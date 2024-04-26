package com.mpay24.payment;

import com.mpay24.payment.data.Customer;
import com.mpay24.payment.data.PaymentData;
import com.mpay24.payment.type.DirectDebitPaymentType.Brand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestCreateCustomer extends AbstractTestCase {
	public final static Logger logger = LogManager.getLogger(TestCreateCustomer.class);

	@Test
	public void testCreateCreditCardCustomerWithoutAddress() throws ParseException, PaymentException {
		String customerId = "12345678987622";
		deleteProfileForTest(customerId);
		Customer customer = getCustomer(customerId, "Xenia Wiesbauer");
		customer.setBirthdate(formatDate("1970-01-31"));
		mpay24.createCustomer(customer, getVisaTestData());
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}
	
	@Test
	public void testCreateCreditCardCustomerWithAddress() throws ParseException, PaymentException {
		String customerId = "1234567898763";
		deleteProfileForTest(customerId);
		mpay24.createCustomer(getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16"), getVisaTestData());
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}

	@Test
	public void testCreateTwoCustomerProfilesWithAddress() throws ParseException, PaymentException {
		String customerId = "max.mustermann@gmail.com";
		deleteProfileForTest(customerId);
		mpay24.createCustomer(getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16"), "x", getVisaTestData());
		mpay24.createCustomer(getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16"), "y", getDirectDebitTestData(Brand.HOBEX_AT));
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(2, storedPaymentDataList.size());
	}

}
