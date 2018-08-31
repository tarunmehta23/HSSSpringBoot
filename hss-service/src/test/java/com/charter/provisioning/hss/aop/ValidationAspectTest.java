package com.charter.provisioning.hss.aop;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhone;
import org.apache.http.HttpStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationAspectTest {

	@Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ProceedingJoinPoint joinPoint;
    
    @Mock
	private HssServiceConfig serviceConfig;
    
	@InjectMocks
	private ValidationAspect aspect;
	
	@Test
	public void inputValidation_ValidateDigitalPhone_Success() throws Exception {
		
		Object obj = MockObjectCreator.getDigitalPhone();
		Object[] objArray = new Object[1];
		objArray[0] = obj;
		
		when(joinPoint.getArgs()).thenReturn(objArray);
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(serviceConfig.getHssPackage()).thenReturn("package");
		when(serviceConfig.getValidFeatureLength()).thenReturn(1);
		aspect.inputValidation(joinPoint);
	}
	
	@Test(expected = ServiceException.class)
	public void inputValidation_ValidateDigitalPhoneWithNullInput_ExpectsServiceException() throws Exception {

		Object[] objArray = new Object[1];
		objArray[0] = DigitalPhone.builder().operation(MockObjectCreator.OPERATION_CREATE).build();
		
		when(joinPoint.getArgs()).thenReturn(objArray);
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		aspect.inputValidation(joinPoint);
	}
	
	@Test(expected = ServiceException.class)
	public void inputValidation_ValidateDigitalPhoneWithNullPublicId_ExpectsServiceException() throws Exception {
		
		Object obj = MockObjectCreator.getDigitalPhoneWithoutPublicAndPrivateIdentity();
		Object[] objArray = new Object[1];
		objArray[0] = obj;
		
		when(joinPoint.getArgs()).thenReturn(objArray);
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		aspect.inputValidation(joinPoint);
	}

	@Test
	public void inputValidation_ValidateEmptyDigitalPhoneName_ExpectsServiceException() throws Exception {

		try {
			Object obj = MockObjectCreator.getDigitalPhoneWithEmptyName();
			Object[] objArray = new Object[1];
			objArray[0] = obj;

			when(joinPoint.getArgs()).thenReturn(objArray);
			aspect.inputValidation(joinPoint);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void inputValidation_ValidateForInvalidFeatureList_ExpectsServiceException() throws Exception {

		try {
			Object obj = MockObjectCreator.getDigitalPhoneWithInvalidInput();
			Object[] objArray = new Object[1];
			objArray[0] = obj;

			when(joinPoint.getArgs()).thenReturn(objArray);
			when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
			when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
			when(serviceConfig.getHssPackage()).thenReturn("package");
			aspect.inputValidation(joinPoint);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}
}
