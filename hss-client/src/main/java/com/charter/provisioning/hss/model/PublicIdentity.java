package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PublicIdentity {
	
	public PublicIdentity() {}
    
	private String operation;
    
    private String userId;
    
    private String forward;
    
    private String serviceId;
}
