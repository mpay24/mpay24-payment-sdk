package com.mpay24.payment.mapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class SdkApiObjectMapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetExpiredateAsLong() throws ParseException {
		assertEquals(Long.valueOf(1604), new SdkApiObjectMapper().getExpiredateAsLong(getDate("04/2016")));
	}

	private Date getDate(String dateString) throws ParseException {
		return new SimpleDateFormat("MM/yyyy").parse(dateString);
	}

}
