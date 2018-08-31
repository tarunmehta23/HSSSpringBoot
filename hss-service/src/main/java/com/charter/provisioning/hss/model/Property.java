package com.charter.provisioning.hss.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Property {

	private String name;

    private String value;

    private String type;
}
