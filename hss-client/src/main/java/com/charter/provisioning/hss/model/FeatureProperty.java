
package com.charter.provisioning.hss.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeatureProperty {

	public FeatureProperty() {}

	private String name;
    
    private String type;
    
    private String value;
    
    private String operation;
    
}
