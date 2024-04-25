package com.mpay24.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class AbstractSeleniumTestcase extends AbstractTestCase {

	FirefoxDriver driver;
//	ChromeDriver chromeDriver;

	protected RemoteWebDriver openFirefoxAtUrl(String url) {
		System.setProperty("webdriver.gecko.driver", getGeckodriverPath());
		driver = new FirefoxDriver();
		driver.get(url);
		return driver;

//		driver = new ChromeDriver();
//		driver.get(url);
//		return driver;
	}

	private String getGeckodriverPath () {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "selenium/geckodriver/geckodriver.exe";
		} else if (os.contains("mac")) {
			return "selenium/geckodriver/geckodriver";
		} else {
			return null;
		}
	}

	protected void closeFirefox() {
		if (driver != null) driver.quit();
	}

	protected void assertUIElement(String url, String id, String value) {
		RemoteWebDriver driver = openFirefoxAtUrl(url);
		assertEquals(value, driver.findElement(By.id(id)).getText());
	}

	protected void assertNotExistent(RemoteWebDriver driver, By by) {
		try {
			driver.findElement(by);
			fail("Element with name '" + by + "' found");
		} catch (NoSuchElementException e) {
			; // That is what we expect
		}
	}


}
