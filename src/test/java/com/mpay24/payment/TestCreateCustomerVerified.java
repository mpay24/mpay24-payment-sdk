package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.mpay24.payment.data.Customer;
import com.mpay24.payment.data.Payment;
import com.mpay24.payment.data.PaymentData;
import com.mpay24.payment.data.PaymentRequest;
import com.mpay24.payment.data.Token;

public class TestCreateCustomerVerified extends AbstractSeleniumTestcase {
	public final static Logger logger = LogManager.getLogger(TestCreateCustomerVerified.class);

	@After
	public void tearDown() throws Exception {
		closeFirefox();
	}
	
	@Test
	public void testCreateCustomerCreditCardVerified() throws ParseException, PaymentException, InterruptedException {
		String customerId = "1234567898763";
		Customer customer = getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16");
		deleteProfileForTest(customerId);
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setAmount(null);
		paymentRequest.setCurrency(null);
		Payment payment = mpay24.createCustomer(customer, null, getVisaTestData(), paymentRequest, true);
		assertNotNull(payment);
		assertNotNull(payment.getRedirectLocation());
		
		RemoteWebDriver driver = openFirefoxAtUrl(payment.getRedirectLocation());
		driver.findElement(By.id("right")).click();
		Thread.sleep(1000l);
		
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}

	@Test
	public void testCreateCustomerCreditCardUnVerified() throws ParseException, PaymentException, InterruptedException {
		String customerId = "1234567898763";
		Customer customer = getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16");
		deleteProfileForTest(customerId);
		PaymentRequest paymentRequest = getTestPaymentRequest();
		paymentRequest.setAmount(null);
		paymentRequest.setCurrency(null);
		Payment payment = mpay24.createCustomer(customer, null, getVisaTestData(), paymentRequest, false);
		assertNotNull(payment);
		assertNull(payment.getRedirectLocation());
		
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}

	@Test
	public void testCreateCustomerWithTokenVerified() throws ParseException, PaymentException, InterruptedException {
		String customerId = "1234567898763";
		Customer customer = getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16");
		deleteProfileForTest(customerId);
		
		Token token = mpay24.token(getTestTokenRequest(customerId));
		assertEquals("REDIRECT", token.getReturnCode());
		assertNotNull(token.getApiKey());
		assertNotNull(token.getRedirectLocation());
		assertNotNull(token.getToken());
		
		RemoteWebDriver driver = openFirefoxAtUrl(token.getRedirectLocation());
		driver.findElement(By.name("cardnumber")).sendKeys("4444333322221111");
		driver.findElement(By.name("cardnumber")).sendKeys(Keys.TAB);
		driver.findElement(By.id("expiry")).sendKeys("1225");
		driver.findElement(By.id("expiry")).sendKeys(Keys.TAB);
		driver.findElement(By.name("cvc")).sendKeys("123");
		driver.findElement(By.name("cvc")).sendKeys(Keys.TAB);

		Thread.sleep(1000l);
		closeFirefox();
		
		Payment payment = mpay24.createCustomer(customer, null, getTokenPaymentType(token.getToken()), getTestPaymentRequest(), true);

		assertNotNull(payment);
		assertNotNull(payment.getRedirectLocation());
		
		driver = openFirefoxAtUrl(payment.getRedirectLocation());
		driver.findElement(By.id("right")).click();

		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}

	@Test
	public void testCreateCustomerWithTokenUnVerified() throws ParseException, PaymentException, InterruptedException {
		String customerId = "1234567898763";
		Customer customer = getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16");
		deleteProfileForTest(customerId);
		
		Token token = mpay24.token(getTestTokenRequest(customerId));
		assertEquals("REDIRECT", token.getReturnCode());
		assertNotNull(token.getApiKey());
		assertNotNull(token.getRedirectLocation());
		assertNotNull(token.getToken());
		
		RemoteWebDriver driver = openFirefoxAtUrl(token.getRedirectLocation());
		driver.findElement(By.name("cardnumber")).sendKeys("4444333322221111");
		driver.findElement(By.name("cardnumber")).sendKeys(Keys.TAB);
		driver.findElement(By.id("expiry")).sendKeys("1225");
		driver.findElement(By.id("expiry")).sendKeys(Keys.TAB);
		driver.findElement(By.name("cvc")).sendKeys("123");
		driver.findElement(By.name("cvc")).sendKeys(Keys.TAB);

		Thread.sleep(1000l);
		closeFirefox();
		
		Payment payment = mpay24.createCustomer(customer, null, getTokenPaymentType(token.getToken()), getTestPaymentRequest(), false);

		assertNotNull(payment);
		assertNull(payment.getRedirectLocation());
		
		List<PaymentData> storedPaymentDataList = mpay24.listCustomers(customerId, null, null, null);
		assertNotNull(storedPaymentDataList);
		assertEquals(1, storedPaymentDataList.size());
	}
}
