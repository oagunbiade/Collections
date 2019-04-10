package com.coronation.collections.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.PaymentStatus;
import com.coronation.collections.dto.AmountReport;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.dto.CountReport;
import com.coronation.collections.exception.InvalidDataException;
import com.coronation.collections.repositories.InvalidPaymentRepository;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.PaymentService;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.JsonConverter;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.coronation.collections.repositories.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {
	private PaymentRepository paymentRepository;
	private DistributorService distributorService;
	private ProductService productService;
	private InvalidPaymentRepository invalidPaymentRepository;
	@Autowired
	public PaymentServiceImpl(PaymentRepository paymentRepository, DistributorService distributorService,
							  ProductService productService, InvalidPaymentRepository invalidPaymentRepository) {
		this.paymentRepository = paymentRepository;
		this.distributorService = distributorService;
		this.productService = productService;
		this.invalidPaymentRepository = invalidPaymentRepository;
	}

	@Override
	public Page<Payment> listAll(BooleanExpression expression, Pageable pageable) {
		return paymentRepository.findAll(expression, pageable);
	}

	@Override
	public Iterable<Payment> listAll(BooleanExpression expression) {
		return paymentRepository.findAll(expression);
	}

	@Override
	public Payment findById(Long id) {
		return paymentRepository.findById(id).orElse(null);
	}

	@Override
	public Payment save(Payment payment, Merchant merchant, Product product,
						DistributorAccount distributorAccount, Distributor distributor, User createdBy) {
		payment.setMerchant(merchant);
		payment.setProduct(product);
		payment.setStatus(PaymentStatus.INITIATED);
		payment.setReferenceCode(GenericUtil.generateRandomId());
		payment.setDistributor(distributor);
		payment.setDistributorAccount(distributorAccount);
		payment.setCreatedBy(createdBy);
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public Payment getByReferenceCode(String code) {
		return paymentRepository.findByReferenceCode(code);
	}

	@Override
	public Payment update(Payment prevPayment, Payment newPayment) {
		prevPayment.setEditMode(Boolean.TRUE);
		prevPayment.setStatus(PaymentStatus.INITIATED);
		prevPayment.setUpdateData(JsonConverter.getJson(newPayment));
		return paymentRepository.saveAndFlush(prevPayment);
	}

	@Override
	public Payment approve(Payment payment, ApprovalDto approvalDto) {
		if (approvalDto.getApprove()) {
			if (payment.getEditMode() && payment.getUpdateData() != null) {
				Payment edit = JsonConverter.getElement(payment.getUpdateData(), Payment.class);
				payment.setEditMode(Boolean.FALSE);
				payment.setAmount(edit.getAmount());
				payment.setDueDate(edit.getDueDate());
				payment.setComment(edit.getComment());
				payment.setModifiedAt(LocalDateTime.now());
				payment.setModifiedAt(edit.getCreatedAt());
				payment.setUpdateData(null);
				payment.setRejectReason(null);
			}
			payment.setStatus(PaymentStatus.APPROVED);
		} else {
			payment.setRejectReason(approvalDto.getReason());
			if (payment.getEditMode()) {
				payment.setEditMode(Boolean.FALSE);
			} else {
				payment.setStatus(PaymentStatus.REJECTED);
			}
		}
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public Payment revert(Payment payment, ApprovalDto approvalDto) {
		payment.setRejectReason(approvalDto.getReason());
		payment.setEditMode(Boolean.FALSE);
		payment.setUpdateData(null);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public Payment delete(Payment payment) {
		payment.setDeleted(Boolean.TRUE);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public List<Payment> listAllPendingPayments() {
		return paymentRepository.findAllPendingPayments();
	}

	@Override
	public Payment processPayment(Payment payment) {
		return null;
	}

	@Override
	public Payment clearAsPayable(Payment payment) {
		payment.setStatus(PaymentStatus.APPROVED);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public List<Payment> findAllDuePayments(LocalDate localDate) {
		return paymentRepository.findAllDuePayments(localDate);
	}

	@Override
	public void setMerchantAmountReport(Long merchantId, AmountReport amountReport, CountReport countReport) {
		sumPayments(paymentRepository.findByMerchantId(merchantId), amountReport, countReport);
	}

	@Override
	public void setDistributorAmountReport
			(Long distributorId, AmountReport amountReport, CountReport countReport) {
		sumPayments(paymentRepository.findByDistributorId(distributorId), amountReport, countReport);
	}

	@Override
	public List<Payment> findMerchantDuePayments(Long merchantId, LocalDate localDate) {
		return paymentRepository.findByMerchantIdAndDueDate(merchantId, localDate);
	}

	@Override
	public List<Payment> findDistributorDuePayments(Long distributorId, LocalDate localDate) {
		return paymentRepository.findByDistributorIdAndDueDate(distributorId, localDate);
	}

	@Override
	public Payment cancelPayment(Payment payment) {
		payment.setStatus(PaymentStatus.CANCELED);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public List<InvalidPayment> uploadPayments(InputStream inputStream, Merchant merchant, User user)
			throws IOException {
		List<InvalidPayment> payments = new ArrayList<>();

		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next();
		Row row = null;

		while (rowIterator.hasNext()) {
			row = rowIterator.next();
			if (row.getRowNum() < 1) {
				continue;
			}

			InvalidPayment invalidPayment = new InvalidPayment();
			invalidPayment.setAmount(row.getCell(2).getStringCellValue());
			invalidPayment.setDueDate(row.getCell(4).getStringCellValue());
			invalidPayment.setNumberOfUnits(row.getCell(3).getStringCellValue());
			invalidPayment.setRfpCode(row.getCell(1).getStringCellValue());
			invalidPayment.setProductCode(row.getCell(0).getStringCellValue());
			invalidPayment.setComment(row.getCell(5).getStringCellValue());

			try {
				validatePayment(invalidPayment, merchant, user);
			} catch (InvalidDataException e) {
				invalidPayment.setRejectReason(e.getMessage());
				invalidPayment = invalidPaymentRepository.saveAndFlush(invalidPayment);
				payments.add(invalidPayment);
			}
		}

		return payments;
	}

	@Override
	public Payment validatePayment(InvalidPayment invalidPayment, Merchant merchant, User user) throws InvalidDataException {
		StringBuilder builder = new StringBuilder();
		BigDecimal amount = null;
		Product product = null;
		MerchantDistributor distributor = null;
		int units = 0;
		LocalDate dueDate = null;
		DistributorAccount distributorAccount = null;
		if (invalidPayment.getProductCode() == null || invalidPayment.getProductCode().isEmpty()) {
			builder.append("No product code | ");
		} else {
			product = productService.findByCode(invalidPayment.getProductCode().trim());
			if (product == null) {
				builder.append("Invalid product code | ");
			}
		}
		if (invalidPayment.getRfpCode() == null || invalidPayment.getRfpCode().isEmpty()) {
			builder.append("No rfp code | ");
		} else {
			distributor = distributorService.
					findByMerchantIdAndRfpCode(merchant.getId(), invalidPayment.getRfpCode().trim());
			if (distributor == null) {
				builder.append("Invalid rfp code | ");
			} else {
				distributorAccount = distributorService.getDefaultAccount
						(distributor.getDistributor().getDistributorAccounts()).orElse(null);
				if (distributorAccount == null) {
					builder.append("No account found for distributor | ");
				}
			}
		}
		if (invalidPayment.getAmount() == null || invalidPayment.getAmount().isEmpty()) {
			builder.append("No amount | ");
		} else {
			try {
				amount = new BigDecimal(invalidPayment.getAmount());
				if (product != null && product.getMinAmount() != null &&
						amount.compareTo(product.getMinAmount()) < 0) {
					builder.append("Amount is less than minimum amount | ");
				} else if (amount.compareTo(new BigDecimal("0.00")) <= 0) {
					builder.append("Amount is less than zero | ");
				}
			} catch (NumberFormatException nfe) {
				builder.append("Invalid amount | ");
			}
		}
		if (invalidPayment.getNumberOfUnits() == null || invalidPayment.getNumberOfUnits().isEmpty()) {
			builder.append("No number of units | ");
		} else {
			try {
				units = Integer.parseInt(invalidPayment.getNumberOfUnits());
				if (units < 1) {
					builder.append("Number of units less than 1 | ");
				}
			} catch (NumberFormatException nfe) {
				builder.append("Invalid number of units | ");
			}
		}
		if (invalidPayment.getDueDate() == null || invalidPayment.getDueDate().isEmpty()) {
			builder.append("No due date specified | ");
		} else {
			try {
				dueDate = LocalDate.parse(invalidPayment.getDueDate().trim());
				if (dueDate.isBefore(LocalDate.now())) {
					builder.append("Due date is in the past | ");
				}
			} catch (DateTimeParseException dte) {
				builder.append("Invalid due date specified | ");
			}
		}

		if (builder.length() > 0) {
			throw new InvalidDataException(builder.toString());
		} else {
			Payment payment = new Payment();
			payment.setDueDate(dueDate);
			payment.setAmount(amount);
			payment.setNumberOfUnits(units);
			return save(payment, merchant, product, distributorAccount, distributor.getDistributor(), user);
		}
	}

	@Override
	public Payment confirmPayment(Payment payment) {
		payment.setConfirmed(Boolean.TRUE);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public List<InvalidPayment> merchantInvalidPayments(Long merchantId) {
		return invalidPaymentRepository.findByMerchantIdAndValidatedFalse(merchantId);
	}


	private void sumPayments(List<Payment> payments, final AmountReport amountReport, final CountReport countReport) {
		countReport.setAll(payments.size());
		payments.forEach(p -> {
			amountReport.setAll(amountReport.getApproved().add(p.getAmount()));
			if (p.getStatus().equals(PaymentStatus.APPROVED)) {
				amountReport.setApproved(amountReport.getApproved().add(p.getAmount()));
				countReport.setApproved(countReport.getApproved() + 1);
			} else if (p.getStatus().equals(PaymentStatus.CANCELED)) {
				amountReport.setCanceled(amountReport.getCanceled().add(p.getAmount()));
				countReport.setCanceled(countReport.getCanceled() + 1);
			} else if (p.getStatus().equals(PaymentStatus.FAILED)) {
				amountReport.setFailed(amountReport.getFailed().add(p.getAmount()));
				countReport.setFailed(countReport.getFailed() + 1);
			} else if (p.getStatus().equals(PaymentStatus.INITIATED)) {
				amountReport.setInitiated(amountReport.getInitiated().add(p.getAmount()));
				countReport.setInitiated(countReport.getInitiated() + 1);
			} else if (p.getStatus().equals(PaymentStatus.INVALID)) {
				amountReport.setInvalid(amountReport.getInvalid().add(p.getAmount()));
				countReport.setInvalid(countReport.getInvalid() + 1);
			} else if (p.getStatus().equals(PaymentStatus.PROCESSED)) {
				amountReport.setProcessed(amountReport.getProcessed().add(p.getAmount()));
				countReport.setProcessed(countReport.getProcessed() + 1);
			} else if (p.getStatus().equals(PaymentStatus.REJECTED)) {
				amountReport.setRejected(amountReport.getRejected().add(p.getAmount()));
				countReport.setRejected(countReport.getRejected() + 1);
			}
		});
	}
}
