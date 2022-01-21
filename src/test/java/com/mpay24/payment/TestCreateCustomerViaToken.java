package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.mpay24.payment.data.Token;

public class TestCreateCustomerViaToken extends AbstractSeleniumTestcase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		closeFirefox();
	}

	@Test
	public void testCreateCustomerViaToken() throws PaymentException, InterruptedException {
		String customerId = "12345678987633";
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
		mpay24.createCustomer(getCustomerWithAddress(customerId, "Xenia Wiesbauer", "Grüngasse 16"), "x", getTokenPaymentType(token.getToken()));

	}
	
}
