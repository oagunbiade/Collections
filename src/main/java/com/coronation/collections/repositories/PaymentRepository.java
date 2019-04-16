package com.coronation.collections.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.coronation.collections.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>,
		QuerydslPredicateExecutor<Payment> {
	Payment findByReferenceCode(String referenceCode);
	@Query(value = "SELECT p FROM Payment p WHERE p.status = 'APPROVED' " +
			"and p.tryCount < 3 and p.dueDate <= ?1")
	List<Payment> findAllDuePayments(LocalDateTime dueDate);

	@Query(value = "SELECT p FROM Payment p WHERE p.status <> 'PROCESSED' and p.status <> 'CANCELED' " +
			"and p.status <> 'REJECTED'")
	List<Payment> findAllPendingPayments();

	@Query(value = "SELECT p FROM Payment p WHERE p.merchant.id = ?1 and p.status <> 'PROCESSED' " +
			"and p.status <> 'CANCELED' and p.status <> 'REJECTED'" +
			"and p.dueDate between ?2 and ?3")
	List<Payment> findMerchantDuePayments(Long merchantId, LocalDateTime from, LocalDateTime to);

	@Query(value = "SELECT p FROM Payment p WHERE p.merchant.id = ?1 and p.distributor.id = ?2" +
			" and p.status <> 'PROCESSED' " +
			"and p.dueDate between ?3 and ?4")
	List<Payment> findDistributorDuePayments(Long merchantId, Long distributorId,
											 LocalDateTime from, LocalDateTime to);
	List<Payment> findByMerchantId(Long id);
	List<Payment> findByDistributorId(Long id);
	List<Payment> findByDistributorIdAndMerchantId(Long distributorId, Long merchantId);
}
