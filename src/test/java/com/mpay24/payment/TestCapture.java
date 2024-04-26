package com.mpay24.payment;

import com.mpay24.payment.data.Payment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestCapture extends AbstractTestCase {
	public final static Logger logger = LogManager.getLogger(TestCapture.class);

	@Test
	public void testCapturePayment() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(false), getVisaTestData());
		payment = mpay24.capture(payment);
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}

	@Test
	public void testCapturePaymentWithMpaytid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(false), getVisaTestData());
		payment = mpay24.capture(payment.getmPayTid());
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}

	@Test
	public void testPartialCapturePaymentWithMpaytid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(false), getVisaTestData());
		payment = mpay24.capture(payment.getmPayTid(), new BigDecimal(0.1));
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}



}
