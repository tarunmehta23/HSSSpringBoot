package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Feature {

	public Feature() {}

	private String name;
    
    private String type;
    
    private String value;
    
    private String operation;
    
    private List<FeatureProperty> featureProperties;
    
}
