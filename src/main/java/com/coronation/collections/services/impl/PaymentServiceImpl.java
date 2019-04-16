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
import com.coronation.collections.dto.*;
import com.coronation.collections.exception.ApiException;
import com.coronation.collections.exception.InvalidDataException;
import com.coronation.collections.repositories.InvalidPaymentRepository;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.PaymentService;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.util.Constants;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.JsonConverter;
import com.coronation.collections.util.Utilities;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.coronation.collections.repositories.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {
	private PaymentRepository paymentRepository;
	private DistributorService distributorService;
	private ProductService productService;
	private InvalidPaymentRepository invalidPaymentRepository;
	private Utilities utilities;
	@Autowired
	public PaymentServiceImpl(PaymentRepository paymentRepository, DistributorService distributorService,
							  ProductService productService, InvalidPaymentRepository invalidPaymentRepository,
							  Utilities utilities) {
		this.paymentRepository = paymentRepository;
		this.distributorService = distributorService;
		this.productService = productService;
		this.invalidPaymentRepository = invalidPaymentRepository;
		this.utilities = utilities;
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
		TransferRequest request = new TransferRequest();
		request.setUniqueIdentifier(GenericUtil.generateRandomId());
		request.setCreditAccountNumber(payment.getProduct().getAccount().getAccount().getAccountNumber());
		request.setDebitAccountNumber(payment.getDistributorAccount().getAccount().getAccountNumber());
		request.setTranAmount(payment.getAmount());
		request.setNaration(payment.getNarration());
		ResponseEntity<TransferResponse> response = utilities.postTransfer(request);

		if (response.getStatusCode().is2xxSuccessful()) {
			if (Constants.TRANSFER_RESPONSE_CODE.equals(response.getBody().getResponseCode())) {
				payment.setStatus(PaymentStatus.PROCESSED);
			} else {
				payment.setRejectReason("Transaction failed with response: " +
						response.getBody().getResponseCode() + " " + response.getBody().getResponseDescription());
				payment.setStatus(PaymentStatus.FAILED);
			}
		} else {
			payment.setStatus(PaymentStatus.FAILED);
			payment.setRejectReason("Error occurred during processing with: " + response.getStatusCode().toString());
		}

		payment.setTryCount(payment.getTryCount() + 1);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public Payment clearAsPayable(Payment payment) {
		payment.setStatus(PaymentStatus.APPROVED);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public List<Payment> findAllDuePayments(LocalDateTime dueDate) {
		return paymentRepository.findAllDuePayments(dueDate);
	}

	@Override
	public void setMerchantAmountReport(Long merchantId, PaymentReport paymentReport) {
		sumPayments(paymentRepository.findByMerchantId(merchantId), paymentReport);
	}

	@Override
	public void setDistributorAmountReport
			(Long distributorId, PaymentReport paymentReport) {
		sumPayments(paymentRepository.findByDistributorId(distributorId), paymentReport);
	}

	@Override
	public List<Payment> findMerchantDuePayments(Long merchantId, LocalDateTime from, LocalDateTime to) {
		return paymentRepository.findMerchantDuePayments(merchantId, from, to);
	}

	@Override
	public List<Payment> findDistributorDuePayments(Long merchantId, Long distributorId,
													LocalDateTime from, LocalDateTime to) {
		return paymentRepository.findDistributorDuePayments(merchantId, distributorId, from, to);
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
			invalidPayment.setMerchant(merchant);

			try {
				validatePayment(invalidPayment, user);
			} catch (InvalidDataException e) {
				invalidPayment.setRejectReason(e.getMessage());
				invalidPayment = invalidPaymentRepository.saveAndFlush(invalidPayment);
				payments.add(invalidPayment);
			}
		}

		return payments;
	}

	@Transactional
	@Override
	public Payment validatePayment(InvalidPayment invalidPayment, User user) throws InvalidDataException {
		StringBuilder builder = new StringBuilder();
		BigDecimal amount = null;
		Product product = null;
		MerchantDistributor distributor = null;
		int units = 0;
		LocalDateTime dueDate = null;
		DistributorAccount distributorAccount = null;
		if (invalidPayment.getProductCode() == null || invalidPayment.getProductCode().isEmpty()) {
			builder.append("No product code | ");
		} else {
			product = productService.findByCode(invalidPayment.getProductCode().trim());
			if (product == null) {
				builder.append("Invalid product code | ");
			} else if (!product.getMerchant().equals(invalidPayment.getMerchant())) {
				builder.append("Product belongs to a different merchant");
			}
		}
		if (invalidPayment.getRfpCode() == null || invalidPayment.getRfpCode().isEmpty()) {
			builder.append("No rfp code | ");
		} else {
			distributor = distributorService.
					findByMerchantIdAndRfpCode(invalidPayment.getMerchant().getId(), invalidPayment.getRfpCode().trim());
			if (distributor == null) {
				builder.append("Invalid rfp code | ");
			} else {
				List<DistributorAccount> distributorAccounts =
						distributorService.distributorAccounts(distributor.getId());
				distributorAccount = distributorService.getDefaultAccount
						(distributorAccounts).orElse(null);
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
				dueDate = GenericUtil.dateTimeFromString(invalidPayment.getDueDate().trim());
				if (dueDate.isBefore(LocalDateTime.now())) {
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
			payment = save(payment, invalidPayment.getMerchant(), product, distributorAccount,
					distributor.getDistributor(), user);
			invalidPayment.setValidated(Boolean.TRUE);
			invalidPayment.setModifiedAt(LocalDateTime.now());
			invalidPaymentRepository.saveAndFlush(invalidPayment);
			return payment;
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

	@Override
	public TransferResponse transfer(TransferRequest transferRequest) throws ApiException {
		ResponseEntity<TransferResponse> response = utilities.postTransfer(transferRequest);
		if (response.getStatusCode() != HttpStatus.OK) {
			ApiException exception = new ApiException("An error occurred while processing payment");
			exception.setStatusCode(response.getStatusCode().value());
			throw exception;
		} else {
			return response.getBody();
		}
	}

	@Override
	public List<Payment> findMerchantDistributorPayments(Long merchantId, Long distributorId) {
		return paymentRepository.findByDistributorIdAndMerchantId(distributorId, merchantId);
	}

	@Override
	public Payment setDistributorAccount(Payment payment, DistributorAccount account) {
		payment.setDistributorAccount(account);
		payment.setModifiedAt(LocalDateTime.now());
		return paymentRepository.saveAndFlush(payment);
	}

	@Override
	public InvalidPayment findInvalidPaymentById(Long id) {
		return invalidPaymentRepository.findById(id).orElse(null);
	}

	public void sumPayments(List<Payment> payments, final PaymentReport paymentReport) {
		CountReport countReport = paymentReport.getCountReport();
		AmountReport amountReport = paymentReport.getAmountReport();
		countReport.setAll(payments.size());
		List<PaymentStatus> excludeStatuses =
				Arrays.asList(PaymentStatus.PROCESSED, PaymentStatus.CANCELED, PaymentStatus.REJECTED);
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
			if (p.getDueDate().toLocalDate().isEqual(LocalDate.now()) && !excludeStatuses.contains(p.getStatus())) {
				countReport.setDueToday(countReport.getDueToday() + 1);
				amountReport.setDueToday(amountReport.getDueToday().add(p.getAmount()));
			}
		});
	}
}
