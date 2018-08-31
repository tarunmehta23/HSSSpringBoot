package com.charter.provisioning.hss.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DigitalPhone {

	public DigitalPhone() {}
	
	private String operation;
    
    private String uuid;
    
    private String name;
    
    private String site;
    
    private String featurePackage;
    
    private List<PublicIdentity> publicIdentity;
    
    private Profile profile;
    
    private List<PrivateIdentity> privateIdentity;
}
