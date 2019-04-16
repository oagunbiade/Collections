package com.coronation.collections.scheduler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.coronation.collections.domain.enums.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.coronation.collections.domain.Payment;
import com.coronation.collections.services.PaymentService;
import com.coronation.collections.util.Utilities;

@Component
public class ScheduledTasks {


    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
	@Autowired
	PaymentService paymentService;

	@Value("${app.balanceUrl}")
	private String balanceUrl;

	@Value("${app.transferUrl}")
	private String transferUrl;

	@Scheduled(fixedRate = 1000000)
	public void schedulePaymentTask() {
		List<Payment> payments = this.paymentService.findAllDuePayments(LocalDateTime.now());
		payments.forEach(payment -> paymentService.processPayment(payment));
	}
}
