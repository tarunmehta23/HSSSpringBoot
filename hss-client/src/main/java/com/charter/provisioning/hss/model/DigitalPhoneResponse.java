package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DigitalPhoneResponse {

	public DigitalPhoneResponse() {}
	
	Status status;
	
	String errorCodes;
	
	String description;
	
	public enum Status {
		CREATED,
		SUCCESS,
		FAILURE
	}
}
