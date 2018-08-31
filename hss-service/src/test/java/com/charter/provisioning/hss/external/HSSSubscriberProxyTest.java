package com.charter.provisioning.hss.external;

import static org.mockito.Mockito.when;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.SoapServiceException;

@RunWith(MockitoJUnitRunner.class)
public class HSSSubscriberProxyTest {

	@InjectMocks
	private HSSSubscriberProxy proxy;
	
	@Mock
	private HssServiceConfig serviceConfig;
	
	@Test
	public void sendAndReceive_CallHSSSubscriber_ExceptsSuccessResponse() throws Exception {
		
		when(serviceConfig.getUrl()).thenReturn("endpoint.url");
		when(serviceConfig.getTxId()).thenReturn("transaction.id");
		when(serviceConfig.getUser()).thenReturn("endpoint.username");
		when(serviceConfig.getPassword()).thenReturn("endpoint.password");
		
		String response = proxy.sendAndReceive(MockObjectCreator.getSoapMessage(), MockObjectCreator.CORRELATION_ID);
		Assert.assertThat(response, CoreMatchers.containsString("result=\"success\""));
	}
	
	@Test
	public void sendAndReceive_CallHSSSubscriberWithoutHttpContext_ExceptsSuccessResponse() throws Exception {
		
		when(serviceConfig.getUrl()).thenReturn("endpoint.url");
		
		String response = proxy.sendAndReceive(MockObjectCreator.getSoapMessage(), MockObjectCreator.CORRELATION_ID);
		Assert.assertThat(response, CoreMatchers.containsString("result=\"success\""));
	}
	
	@Test(expected = SoapServiceException.class)
	public void sendAndReceive_WithInvalidInput_ExceptsSoapServiceException() throws Exception {
		proxy.sendAndReceive(MockObjectCreator.getSoapMessage(), MockObjectCreator.CORRELATION_ID);
	}
}
