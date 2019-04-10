package com.coronation.collections.repositories;

import java.time.LocalDate;
import java.util.List;

import com.coronation.collections.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.coronation.collections.domain.Payment;
import sun.tools.tree.BooleanExpression;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
		QuerydslPredicateExecutor<Payment> {
	Payment findByReferenceCode(String referenceCode);
	@Query(value = "SELECT p FROM Payment p WHERE p.status = 'APPROVED' " +
			"and p.retryCount < 3 and p.dueDate <= ?1")
	List<Payment> findAllDuePayments(LocalDate dueDate);
	@Query(value = "SELECT p FROM Payment p WHERE p.status <> 'PROCESSED'")
	List<Payment> findAllPendingPayments();
	@Query(value = "SELECT p FROM Payment p WHERE p.merchant.id = ?1 and p.status <> 'PROCESSED' " +
			"and p.dueDate = ?2")
	List<Payment> findMerchantDuePayments(Long merchantId, LocalDate dueDate);
	@Query(value = "SELECT p FROM Payment p WHERE p.distributor.id = ?1 and p.status <> 'PROCESSED' " +
			"and p.dueDate = ?2")
	List<Payment> findDistributorDuePayments(Long distributorId, LocalDate dueDate);
	List<Payment> findByMerchantId(Long id);
	List<Payment> findByMerchantIdAndStatus(Long id, PaymentStatus status);
	List<Payment> findByDistributorId(Long id);
	List<Payment> findByDistributorIdAndStatus(Long id, PaymentStatus status);
	Page<Payment> findAll(BooleanExpression expression, Pageable pageRequest);
	List<Payment> findByMerchantIdAndDueDate(Long id, LocalDate dueDate);
	List<Payment> findByDistributorIdAndDueDate(Long id, LocalDate dueDate);
}
