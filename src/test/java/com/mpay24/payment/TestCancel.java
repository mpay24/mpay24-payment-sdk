package com.mpay24.payment;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mpay24.payment.data.Payment;

public class TestCancel extends AbstractTestCase {
	public final static Logger log = Logger.getLogger(TestCancel.class);

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
