package com.coronation.collections.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import com.coronation.collections.domain.*;
import com.coronation.collections.dto.*;
import com.coronation.collections.exception.ApiException;
import com.coronation.collections.exception.InvalidDataException;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
	Page<Payment> listAll(BooleanExpression expression, Pageable pageable);
    Iterable<Payment> listAll(BooleanExpression expression);
	Payment findById(Long id);
	Payment save(Payment payment, Merchant merchant, Product product,
                 DistributorAccount distributorAccount, Distributor distributor, User createdBy);
    Payment update(Payment prevPayment, Payment newPayment);
    Payment approve(Payment payment, ApprovalDto approvalDto);
    Payment revert(Payment payment, ApprovalDto approvalDto);
    Payment cancelPayment(Payment payment);
    Payment confirmPayment(Payment payment);
    Payment processPayment(Payment payment);
    Payment clearAsPayable(Payment payment);
    Payment getByReferenceCode(String code);
    Payment delete(Payment payment);
    Payment setDistributorAccount(Payment payment, DistributorAccount account);
    Payment validatePayment(InvalidPayment invalidPayment, User user) throws InvalidDataException;
    List<InvalidPayment> uploadPayments(InputStream inputStream, Merchant merchant, User user) throws IOException;
    InvalidPayment findInvalidPaymentById(Long id);
    List<Payment> listAllPendingPayments();
    List<Payment> findAllDuePayments(LocalDateTime dueDate);
    void setMerchantAmountReport(Long merchantId, PaymentReport paymentReport);
    void setDistributorAmountReport
            (Long distributorId, PaymentReport paymentReport);
    List<Payment> findMerchantDuePayments(Long merchantId, LocalDateTime from, LocalDateTime to);
    List<Payment> findDistributorDuePayments(Long merchantId, Long distributorId, LocalDateTime from, LocalDateTime to);
    List<InvalidPayment> merchantInvalidPayments(Long merchantId);
    TransferResponse transfer(TransferRequest transferRequest) throws ApiException;
    List<Payment> findMerchantDistributorPayments(Long merchantId, Long distributorId);

    void sumPayments(List<Payment> payments,
                            final PaymentReport paymentReport);
}
