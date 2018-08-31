package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Profile {
	
	public Profile() {}

	private String operation;
    
    private String tas;
    
    private String vm;
    
    private List<Feature> features;
 
}
