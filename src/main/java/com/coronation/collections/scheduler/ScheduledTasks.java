package com.coronation.collections.scheduler;

import java.io.IOException;
import java.time.LocalDate;
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
	public String schedulePaymentTask() {
		List<Payment> payments = this.paymentService.findAllDuePayments(LocalDate.now());
		
		int listSize = payments.size();
		
		String retVal="Empty";

		logger.info("list size : " + listSize);
		
		if(!payments.isEmpty()) {
			
		
		int i = 0;
		for(Payment payment : payments) {

		int retry = payment.getTryCount();

		logger.info("Processing payment for: " +payment.getReferenceCode());

			
			if (retry >= 3) {
				

				payment.setStatus(PaymentStatus.FAILED);
			
				retVal = "Retry complete";
				break;
				
			}
			
			//Check Balance
			float bal = 0;
			try {
				bal = Utilities.getBalRequest(payment.getDebitAccount(), balanceUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
				break;
			}
			
			logger.info("comparing balance: "+bal+" and transaction amount: " +payment.getAmount());
			if (bal <= payment.getAmount()) {
				logger.info("insufficient balance : ");
				payment.setStatus("COMPLETED");
				payment.setTryCount(retry+1);
			
				 retVal = "done";				
			}
			
			logger.info("About to do transfer from  : " + payment.getDebitAccount());

			String responseCode = null;
			try {
				responseCode = Utilities.localTransferRequest(payment.getDebitAccount(), payment.getCreditAccount(),
						payment.getAmount(), transferUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				 retVal = "failed";	
			}

			//Checking status of transfer
			logger.info("Checking the status of transaction : " +responseCode);
			if (responseCode != "000") {
				logger.info("Transfer failed : ");

				payment.setTryCount(retry + 1);
				++i;
				 retVal = "done";	
			}

		
			payment.setStatus(PaymentStatus.PROCESSED);
			try {
				logger.info("Before payment edit 1 : ");
				paymentService.update(payment);
				logger.info("after payment edit 1 : ");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	
		}
		
		}
		
		return retVal;

	}
}
