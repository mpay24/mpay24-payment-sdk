package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.mpay.soap.client.Status;
import com.mpay24.payment.data.Payment;
import com.mpay24.payment.data.Refund;
import com.mpay24.payment.data.State;

public class TestRefund extends AbstractTestCase {
	public final static Logger logger = LogManager.getLogger(TestRefund.class);

	@Test
	public void testRefundPaymentWithPaymentObject() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(), getVisaTestData());
		Refund refund = mpay24.refund(payment);
		assertEquals("OK", refund.getReturnCode());
		assertEquals("CREDITED", refund.getState().toString());
		assertNotNull(refund.getmPayTid());
	}

	@Test
	public void testRefundPaymentWithMpayTid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(), getVisaTestData());
		Refund refund = mpay24.refund(payment.getmPayTid());
		assertEquals("OK", refund.getReturnCode());
		assertEquals("CREDITED", refund.getState().toString());
		assertNotNull(refund.getmPayTid());
	}

	@Test
	public void testPartialRefundPaymentWithMpayTid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(), getVisaTestData());
		Refund refund = mpay24.refund(payment.getmPayTid(), new BigDecimal(0.1));
		assertEquals("OK", refund.getReturnCode());
		assertEquals("CREDITED", refund.getState().toString());
		assertNotNull(refund.getmPayTid());
	}

	@Test
	public void testPartialRefundPartialPaymentWithMpayTid() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(), getVisaTestData());
		Refund refund = mpay24.refund(payment.getmPayTid(), payment.getStateID(), payment.getAmount());
		assertEquals("OK", refund.getReturnCode());
		assertEquals("CREDITED", refund.getState().toString());
		assertNotNull(refund.getmPayTid());
	}
	
	@Test
	public void testRefundExceedsPaymentAmount() throws ParseException, PaymentException {
		Payment payment = mpay24.payment(getTestPaymentRequest(), getVisaTestData());
		try {
			Refund refund = mpay24.refund(payment.getmPayTid(), payment.getStateID(), new BigDecimal(2.0));
		} catch (PaymentException e) {
			assertEquals(Status.ERROR, e.getStatus());
			assertEquals("CREDIT_LIMIT_EXCEEDED", e.getErrorCode());
		}
		Payment response = mpay24.paymentDetails(payment.getmPayTid());
		assertEquals(State.BILLED, response.getState());
	}
}
