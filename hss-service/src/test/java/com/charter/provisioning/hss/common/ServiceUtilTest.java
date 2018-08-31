package com.charter.provisioning.hss.common;

import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class ServiceUtilTest {

	@InjectMocks
	private ServiceUtil serviceUtil;
	
	@Test
	public void testAppendRandom_GenerateRandomNumber_ExpectsNotNull32CharLengthString() throws Exception {
		
		String random = serviceUtil.appendRandom(32, false);
		
		Assert.assertThat(random, is(IsNull.notNullValue()));
		Assert.assertThat(random.length(), is(32));
	}
	
	@Test
	public void testAppendRandom_GenerateRandomNumberUsingHex_ExpectsNotNull32CharLengthString() throws Exception {
		
		String random = serviceUtil.appendRandom(32, true);
		
		Assert.assertThat(random, is(IsNull.notNullValue()));
		Assert.assertThat(random.length(), is(32));
	}
	
	@Test
	public void testAppendRandom_GenerateRandomNumberUsingHex_ExpectsNull() throws Exception {
		
		String random = serviceUtil.appendRandom(0, true);
		Assert.assertThat(random, is(nullValue()));
	}
	
	@Test
	public void testAppendTimeStamp_GenerateRandomTimeStamp_ExpectsNotNull24CharLengthString() throws Exception {
		
		String random = serviceUtil.appendTimestamp(32, true);
		
		Assert.assertThat(random, is(IsNull.notNullValue()));
		Assert.assertThat(random.length(), is(24));
	}
	
	@Test
	public void testAppendTimeStamp_GenerateRandomTimeStamp_ExpectsNotNull26CharLengthString() throws Exception {
		
		String random = serviceUtil.appendTimestamp(32, false);
		
		Assert.assertThat(random, is(IsNull.notNullValue()));
		Assert.assertThat(random.length(), is(26));
	}
	
	@Test
	public void testAppendTimeStamp_GenerateRandomTimeStamp_ExpectsNull() throws Exception {
		
		String random = serviceUtil.appendTimestamp(0, false);
		Assert.assertThat(random, is(nullValue()));
	}
}
