package com.coronation.collections.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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
	Payment getByReferenceCode(String code);
    Payment update(Payment prevPayment, Payment newPayment);
    Payment approve(Payment payment, ApprovalDto approvalDto);
    Payment revert(Payment payment, ApprovalDto approvalDto);
    Payment delete(Payment payment);
    List<Payment> listAllPendingPayments();
    Payment processPayment(Payment payment);
    Payment clearAsPayable(Payment payment);
    List<Payment> findAllDuePayments(LocalDate localDate);
    void setMerchantAmountReport(Long merchantId, AmountReport amountReport, CountReport countReport);
    void setDistributorAmountReport
            (Long distributorId, AmountReport amountReport, CountReport countReport);
    List<Payment> findMerchantDuePayments(Long merchantId, LocalDate localDate);
    List<Payment> findDistributorDuePayments(Long merchantId, Long distributorId, LocalDate localDate);
    Payment cancelPayment(Payment payment);
    List<InvalidPayment> uploadPayments(InputStream inputStream, Merchant merchant, User user) throws IOException;
    Payment validatePayment(InvalidPayment invalidPayment, Merchant merchant, User user) throws InvalidDataException;
    Payment confirmPayment(Payment payment);
    List<InvalidPayment> merchantInvalidPayments(Long merchantId);
    TransferResponse transfer(TransferRequest transferRequest) throws ApiException;
    Payment setDistributorAccount(Payment payment, DistributorAccount account);
}
