package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.mpay24.payment.data.Payment;
import com.mpay24.payment.data.Token;

public class TestToken extends AbstractSeleniumTestcase {
	public final static Logger logger = LogManager.getLogger(TestToken.class);

	@After
	public void tearDown() throws Exception {
		closeFirefox();
	}


	@Test
	public void testTokenGerman() throws ParseException, PaymentException {
		Token token = mpay24.token(getTestTokenRequest(null));
		assertEquals("REDIRECT", token.getReturnCode());
		assertNotNull(token.getApiKey());
		assertNotNull(token.getRedirectLocation());
		assertNotNull(token.getToken());
		
		RemoteWebDriver driver = openFirefoxAtUrl(token.getRedirectLocation());
		assertEquals("Kartennummer", driver.findElement(By.className("identifierlabel")).getText());
		assertEquals("Gültig bis", driver.findElement(By.className("expirylabel")).getText());
		assertEquals("Prüfnummer (CVN)", driver.findElement(By.className("cvclabel")).getText());
	}

	@Test
	public void testTokenEnglish() throws ParseException, PaymentException {
		Token token = mpay24.token(getTestTokenRequest(null, "EN"));
		assertEquals("REDIRECT", token.getReturnCode());
		assertNotNull(token.getApiKey());
		assertNotNull(token.getRedirectLocation());
		assertNotNull(token.getToken());
		
		RemoteWebDriver driver = openFirefoxAtUrl(token.getRedirectLocation());
		assertEquals("Card Number", driver.findElement(By.className("identifierlabel")).getText());
		assertEquals("Valid thru", driver.findElement(By.className("expirylabel")).getText());
		assertEquals("CVC / CVN", driver.findElement(By.className("cvclabel")).getText());
	}

	@Test
	public void testTokenPaymentWith3DS() throws ParseException, PaymentException, InterruptedException {
		Token token = mpay24.token(getTestTokenRequest(null));
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
		Payment response = mpay24.payment(getTestPaymentRequest(), getTokenPaymentType(token.getToken()));

		assertEquals("REDIRECT", response.getReturnCode());
		assertNotNull(response.getmPayTid());
	}
	
	@Test
	public void testTokenPaymentWithout3DS() throws ParseException, PaymentException, InterruptedException {
		Token token = mpay24.token(getTestTokenRequest(null));
		assertEquals("REDIRECT", token.getReturnCode());
		assertNotNull(token.getApiKey());
		assertNotNull(token.getRedirectLocation());
		assertNotNull(token.getToken());
		
		RemoteWebDriver driver = openFirefoxAtUrl(token.getRedirectLocation());
		driver.findElement(By.name("cardnumber")).sendKeys("4444333322221111");
		driver.findElement(By.name("cardnumber")).sendKeys(Keys.TAB);
		driver.findElement(By.id("expiry")).sendKeys("0520");
		driver.findElement(By.id("expiry")).sendKeys(Keys.TAB);
		driver.findElement(By.name("cvc")).sendKeys("123");
		driver.findElement(By.name("cvc")).sendKeys(Keys.TAB);

//		TimeUnit.SECONDS.sleep(1l);
//		
//		Payment response = mpay24.payment(getTestPaymentRequest(), getTokenPaymentType(token.getToken()));
//
//		assertEquals("OK", response.getReturnCode());
//		assertNotNull(response.getmPayTid());
	}
}
