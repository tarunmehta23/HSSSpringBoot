package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PrivateIdentity {

	public PrivateIdentity() {}

	private String operation;
    
    private String userId;
    
    private String publicId;
    
    private String password;

}
