package com.charter.provisioning.hss.model;

import java.util.List;

import lombok.Data;

@Data
public class Features {

	 private String name;
	    
	 private String type;
	 
	 private String actionName;
	    
	 private List<String> actionValue;
	 
}
