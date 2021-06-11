package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mpay24.payment.data.Payment;

public class TestCaptureAfterAuthorize extends AbstractTestCase {
	public final static Logger log = Logger.getLogger(TestCaptureAfterAuthorize.class);

	@Test
	public void testCapturePayment() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequestUncaptured(1.0), getVisaTestData());
		payment = mpay24.capture(payment);
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}

	@Test
	public void testCapturePaymentWithMpaytid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequestUncaptured(1.0), getVisaTestData());
		payment = mpay24.capture(payment.getmPayTid());
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}

	@Test
	public void testPartialCapturePaymentWithMpaytid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequestUncaptured(1.0), getVisaTestData());
		payment = mpay24.capture(payment.getmPayTid(), new BigDecimal(0.1));
		assertEquals("OK", payment.getReturnCode());
		assertEquals("BILLED", payment.getState().toString());
		assertNotNull(payment.getmPayTid());
	}



}
