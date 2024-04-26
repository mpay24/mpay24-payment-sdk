package com.mpay24.payment;

import com.mpay24.payment.data.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.text.ParseException;

public class TestCancel extends AbstractTestCase {
	public final static Logger logger = LogManager.getLogger(TestCancel.class);

	@Test
	public void testCancelPayment() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(false), getVisaTestData());
		mpay24.cancel(payment);
	}

	@Test
	public void testCancelPaymentWithMpaytid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(false), getVisaTestData());
		mpay24.cancel(payment.getmPayTid());
	}



}
