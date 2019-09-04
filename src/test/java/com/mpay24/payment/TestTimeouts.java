package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mpay24.payment.data.Payment;
import com.mpay24.payment.data.PaymentRequest;
import com.mpay24.payment.type.DirectDebitPaymentType.Brand;

public class TestTimeouts extends AbstractTestCase {
	public final static Logger log = Logger.getLogger(TestTimeouts.class);

	@Test
	public void testVisaPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		Payment response = mpay24.payment(paymentRequest, getVisaTestData(), getCustomer());

		assertEquals("OK", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}

	@Test
	public void testMastercardPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		Payment response = mpay24.payment(paymentRequest, getMastercardTestData(), getCustomer());

		assertEquals("OK", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}


	@Test
	public void testDinersPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		Payment response = mpay24.payment(paymentRequest, getDinersTestData(), getCustomer());

		assertEquals("OK", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}


	@Test
	public void testEpsMinPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(156l);
		Payment response = mpay24.payment(paymentRequest, getEpsData(), getCustomer());

		assertEquals("REDIRECT", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}

	@Test
	public void testEpsPreselectionPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(156l);
		Payment response = mpay24.payment(paymentRequest, getOnlinebankingPayment(com.mpay24.payment.type.OnlineBankingPaymentType.Brand.EPS_STUZZA_BANK_SELECTION), getCustomer());

		assertEquals("REDIRECT", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}

	@Test
	public void testSofortPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(1l);
		Payment response = mpay24.payment(paymentRequest, getSofortData(), getCustomer());

		assertEquals("REDIRECT", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}

	@Test(expected=PaymentException.class)
	public void testPaysafecardPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(121l);
		mpay24.payment(paymentRequest, getPaysafecardData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testPaypalPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(121l);
		mpay24.payment(paymentRequest, getPaypalData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testGiropayPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(121l);
		mpay24.payment(paymentRequest, getGiropayData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testEpsBelowMinPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(120l);
		mpay24.payment(paymentRequest, getEpsData(), getCustomer());
	}

	
	@Test(expected=PaymentException.class)
	public void testAmexPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		mpay24.payment(paymentRequest, getAmexTestData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testJcpPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		mpay24.payment(paymentRequest, getJcbTestData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testMaestroPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		mpay24.payment(paymentRequest, getMaestroTestData(), getCustomer());
	}

	@Test(expected=PaymentException.class)
	public void testDirectDebitPayment() throws PaymentException, ParseException {
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setTimeoutSeconds(2l);
		mpay24.payment(paymentRequest, getDirectDebitTestData(Brand.HOBEX_AT), getCustomer());
	}
}
