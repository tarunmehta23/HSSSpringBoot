package com.charter.provisioning.hss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "spml")
public class SpmlConfig {

	private String ns2_rrefix;
	
	private String spml_prefix;
	
	private String spml_namespace;
	
	private String xsi_rrefix;

	private String xsi_namespace;
	
	private String subscriber_ns_tag;
	
	private String subscriber_version_tag;
    
	private String object_class;
	
	private String default_language;
	
	private String public_identity_type;
	
	private String private_identity_type;
	
	private String new_generated;
	
	private String return_resulting_object;
	
	private String subscriber_type;
	
	private int subscriber_id_length;
	
	private int irs_suffix_length;
	
	private String delete_scope;
	
	private String delete_scope_string;
	
	private String private_identity_search_name;
	
	private String public_identity_search_name;
	
	private String cdata;
	
	private String spml_ext;
	
	private String ns2_subscriber;
	
	private String xmlns_xsi;
	
	private String xmlns_subscriber;
	
	private String search_status;
	
	private String result;
	
	private String request_id;
	
	private String execution_time;
	
	private String subscribed_media_profile_id;
	
	private String objects;
	
	private String new_generatedString;
	
	private String object;
	
	private String private_user_id;
	
	private String implicit_registered_set;
	
	private String service_profile;
	
	private String public_user_id;
	
	private String subscriber;
	
	private String alias;
	
	private String global_filter_id;
	
	private String modification;
	
	private String global_filter;
	
	private String return_resulting_objectString;
	
	private String name;
	
	private String type;
	
	private String value;
	
	private String xsiType;
	
	private String xsi_type_attr;
	
	private String xmlns_xsi_attr;
	
	private String language;
	
	private String execution;
	
	private String synchronous;
	
	private String spml_search_request;
	
	private String spml_add_request;
	
	private String spml_modify_request;
	
	private String spml_delete_request;
	
	private String spml_delete_response;
	
	private String spml_modify_response;
	
	private String spml_add_response;
	
	private String spml_search_response;

	private String spml_op_setoradd;

	private String spml_op_remove;

	private String subscriber_public_user_id;

	private String subscriber_private_user_id;

	private String subscriber_service_profile;

	private String subscriber_imp_reg_dataSet;

	private String operation;

	private String scope;
}
