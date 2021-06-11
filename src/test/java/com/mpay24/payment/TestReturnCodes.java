package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import com.mpay24.payment.Mpay24.Environment;

public class TestReturnCodes extends AbstractTestCase{
	protected Mpay24 mpay24InvalidUser = new Mpay24("12345", getMerchantPassword(), Environment.TEST);
	protected Mpay24 mpay24InvalidPassword = new Mpay24("93975", "xxx", Environment.TEST);

	
	@Test()
	public void testInvalidUser() throws PaymentException, ParseException {
		try {
			mpay24InvalidUser.payment(getTestPaymentRequest(), getMaestroTestData(), getCustomer());
			fail("Exception missing");
		} catch (Exception e) {
			assertEquals("Could not send Message.", e.getMessage());
		}
	}

	@Test
	public void testInvalidPassword() throws PaymentException, ParseException {
		try {
			mpay24InvalidPassword.payment(getTestPaymentRequest(), getMaestroTestData(), getCustomer());
			fail("Exception missing");
		} catch (Exception e) {
			assertEquals("Could not send Message.", e.getMessage());
		}
	}
}
