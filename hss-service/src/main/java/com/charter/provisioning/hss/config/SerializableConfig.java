package com.charter.provisioning.hss.config;

import com.charter.provisioning.network.hss.subscriber.spml.*;
import com.charter.provisioning.network.hss.subscriber.spml.schema.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;

@Component
public class SerializableConfig {

	private XStream xstream;

	private SpmlConfig spmlConfig;

	private HssServiceConfig serviceConfig;

	@Autowired
	public SerializableConfig(SpmlConfig spmlConfig, HssServiceConfig serviceConfig) {
		this.spmlConfig = spmlConfig;
		this.serviceConfig = serviceConfig;
	}

	/**
	 * Marshalling Object to xml string.
	 * @param object
	 * @return String
	 */
	public String marshall(Object object) {
		if (null == xstream) {
			init();
		}
		return xstream.toXML(object);
	}

	/**
	 * Unmarshalling xml string to SpmlResponse.
	 * @param spmlResponseXml
	 * @return SpmlResponse
	 */
	public SpmlResponse unmarshall(String spmlResponseXml) {
		if (null == xstream) {
			init();
		}
		if (!StringUtils.isEmpty(spmlResponseXml))
			return (SpmlResponse) xstream
					.fromXML(spmlResponseXml.replaceAll(spmlConfig.getXsiType(), spmlConfig.getType())
							.replaceAll(spmlConfig.getSpml_ext(), "").replaceAll(spmlConfig.getCdata(), "$1"));
		else {
			return null;
		}
	}

	private void init() {
		
		if (xstream == null) {
            xstream = new XStream(createStaxDriver()) {

                    protected MapperWrapper wrapMapper(MapperWrapper next) {
                        return new MapperWrapper(next) {

                            public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                                return definedIn != Object.class && super.shouldSerializeMember(definedIn, fieldName);
                            }
                        };
                    }
                };
        }
        
		// Adding Xstream Security
		XStream.setupDefaultSecurity(xstream);
		Class<?>[] classes = new Class[] { SpmlResponse.class, Modification.class };
		xstream.allowTypes(classes);
		// Xstream Configurations
		configureAliases(xstream);
		configureAttributes(xstream);
		configureCollections(xstream);
    }
	
	private StaxDriver createStaxDriver() {
       QNameMap qnameMap = new QNameMap();
       // Register Response Mappings
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_search_response(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_search_response());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_add_response(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_add_response());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_modify_response(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_modify_response());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_delete_response(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_delete_response());
       // Register Request Mappings
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_search_request(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_search_request());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_add_request(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_add_request());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_modify_request(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_modify_request());
       qnameMap.registerMapping(new QName(spmlConfig.getSpml_namespace(), spmlConfig.getSpml_delete_request(), spmlConfig.getSpml_prefix()), spmlConfig.getSpml_delete_request());
       // Register Additional Mappings
       qnameMap.registerMapping(new QName(serviceConfig.getDefault_subscriber_ns(), spmlConfig.getNs2_rrefix()), Subscriber.class);
       qnameMap.registerMapping(new QName(spmlConfig.getXsi_namespace(), spmlConfig.getXsi_rrefix()), Subscriber.class);
       qnameMap.registerMapping(new QName(spmlConfig.getNs2_subscriber(), spmlConfig.getObjects()), Subscriber.class);

       
       return new StaxDriver(qnameMap);
   }
   
	private void configureAliases(XStream xstream) {
		xstream.alias(spmlConfig.getSpml_add_response(), SpmlResponse.class);
		xstream.alias(spmlConfig.getSpml_delete_response(), SpmlResponse.class);
		xstream.alias(spmlConfig.getSpml_search_response(), SpmlResponse.class);
		xstream.alias(spmlConfig.getSpml_modify_response(), SpmlResponse.class);
		
		xstream.alias(spmlConfig.getSpml_add_request(), AddRequest.class);
		xstream.alias(spmlConfig.getSpml_delete_request(), DeleteRequest.class);
		xstream.alias(spmlConfig.getSpml_search_request(), SearchRequest.class);
		xstream.alias(spmlConfig.getSpml_modify_request(), ModifyRequest.class);


		xstream.alias(spmlConfig.getPrivate_user_id(), PrivateUserId.class);
        xstream.alias(spmlConfig.getImplicit_registered_set(), ImplicitRegisteredSet.class);
        xstream.alias(spmlConfig.getService_profile(), ServiceProfile.class);
        xstream.alias(spmlConfig.getGlobal_filter_id(), GlobalFilterId.class);
        xstream.alias(spmlConfig.getPublic_user_id(), PublicUserId.class);
        xstream.alias(spmlConfig.getModification(), Modification.class);
        xstream.alias(spmlConfig.getGlobal_filter_id(), GlobalFilterId.class);
        xstream.alias(spmlConfig.getObject(), Subscriber.class);
        xstream.alias(spmlConfig.getAlias(), Alias.class);
        xstream.alias(spmlConfig.getObjects(), Subscriber.class);
        xstream.alias(spmlConfig.getPrivate_user_id(), PrivateUserId.class);
        xstream.alias(spmlConfig.getImplicit_registered_set(), ImplicitRegisteredSet.class);
        xstream.alias(spmlConfig.getPublic_user_id(), PublicUserId.class);
        xstream.alias(spmlConfig.getService_profile(), ServiceProfile.class);
        xstream.alias(spmlConfig.getGlobal_filter_id(), GlobalFilterId.class);
        xstream.alias(spmlConfig.getSubscribed_media_profile_id(), SubscribedMediaProfileID.class);
        
        xstream.aliasField(spmlConfig.getGlobal_filter_id(), ValueObject.class, spmlConfig.getGlobal_filter());
        xstream.aliasField(spmlConfig.getObject(), AddRequest.class, spmlConfig.getSubscriber());
        xstream.aliasField(spmlConfig.getObjects(), SpmlResponse.class, spmlConfig.getSubscriber());
	}

	private void configureAttributes(XStream xstream) {
		
		xstream.aliasAttribute(AddRequest.class, spmlConfig.getSubscriber(), spmlConfig.getXmlns_subscriber());
		xstream.aliasAttribute(ModifyRequest.class, spmlConfig.getSubscriber(), spmlConfig.getXmlns_subscriber());
		xstream.aliasAttribute(ModifyRequest.class, spmlConfig.getXsi_rrefix(), spmlConfig.getXmlns_xsi());
		xstream.aliasAttribute(SearchRequest.class, spmlConfig.getXsi_rrefix(), spmlConfig.getXmlns_xsi());
		xstream.aliasAttribute(Subscriber.class, spmlConfig.getXsi_rrefix(), spmlConfig.getXmlns_xsi());
        xstream.aliasAttribute(Subscriber.class, spmlConfig.getType(), spmlConfig.getXsiType());
		xstream.aliasAttribute(Match.class, spmlConfig.getType(), spmlConfig.getXsiType());
		xstream.aliasAttribute(Match.class, spmlConfig.getXmlns_xsi_attr(), spmlConfig.getXmlns_xsi());
        xstream.aliasAttribute(ValueObject.class, spmlConfig.getType(), spmlConfig.getXsiType());
        xstream.aliasAttribute(ValueObject.class, spmlConfig.getXmlns_xsi_attr(), spmlConfig.getXmlns_xsi());
        
		xstream.useAttributeFor(SearchRequest.class, spmlConfig.getXsi_rrefix());
		
		xstream.useAttributeFor(AddRequest.class, spmlConfig.getNew_generatedString());
        xstream.useAttributeFor(AddRequest.class, spmlConfig.getLanguage());
        xstream.useAttributeFor(AddRequest.class, spmlConfig.getReturn_resulting_objectString());
        xstream.useAttributeFor(AddRequest.class, spmlConfig.getSubscriber());
        
        xstream.useAttributeFor(DeleteRequest.class, spmlConfig.getDelete_scope_string());
        xstream.useAttributeFor(DeleteRequest.class, spmlConfig.getExecution());
        xstream.useAttributeFor(DeleteRequest.class, spmlConfig.getLanguage());
		xstream.useAttributeFor(DeleteRequest.class, spmlConfig.getReturn_resulting_objectString());

        xstream.useAttributeFor(Match.class, spmlConfig.getType());
        xstream.useAttributeFor(Match.class, spmlConfig.getXmlns_xsi_attr());
		xstream.useAttributeFor(ModifyRequest.class, spmlConfig.getLanguage());
		xstream.useAttributeFor(ModifyRequest.class, spmlConfig.getReturn_resulting_objectString());
        xstream.useAttributeFor(Modification.class, spmlConfig.getOperation());
        xstream.useAttributeFor(Modification.class, spmlConfig.getName());
        xstream.useAttributeFor(Modification.class, spmlConfig.getScope());
		
		xstream.useAttributeFor(SearchRequest.class, spmlConfig.getLanguage());
		xstream.useAttributeFor(SpmlResponse.class, spmlConfig.getExecution_time());
        xstream.useAttributeFor(SpmlResponse.class, spmlConfig.getLanguage());
        xstream.useAttributeFor(SpmlResponse.class, spmlConfig.getRequest_id());
        xstream.useAttributeFor(SpmlResponse.class, spmlConfig.getResult());
        xstream.useAttributeFor(SpmlResponse.class, spmlConfig.getSearch_status());
        xstream.useAttributeFor(Subscriber.class, spmlConfig.getType());
        xstream.useAttributeFor(Subscriber.class, spmlConfig.getXsi_rrefix());
        xstream.useAttributeFor(ValueObject.class, spmlConfig.getXmlns_xsi_attr());
        xstream.useAttributeFor(ValueObject.class, spmlConfig.getType());
		xstream.useAttributeFor(Alias.class, spmlConfig.getName());
		xstream.useAttributeFor(Alias.class, spmlConfig.getXsi_type_attr());
		xstream.useAttributeFor(Alias.class, spmlConfig.getValue());
	}

	private void configureCollections(XStream xstream) {
		
		xstream.addImplicitCollection(Hss.class, spmlConfig.getPrivate_user_id(), PrivateUserId.class);
        xstream.addImplicitCollection(Hss.class, spmlConfig.getImplicit_registered_set(), ImplicitRegisteredSet.class);
        xstream.addImplicitCollection(Hss.class, spmlConfig.getPublic_user_id(), PublicUserId.class);
        xstream.addImplicitCollection(Hss.class, spmlConfig.getService_profile(), ServiceProfile.class);
        xstream.addImplicitCollection(ServiceProfile.class, spmlConfig.getGlobal_filter_id(), GlobalFilterId.class);
        xstream.addImplicitCollection(ModifyRequest.class, spmlConfig.getModification(), Modification.class);
        xstream.addImplicitCollection(ValueObject.class, spmlConfig.getGlobal_filter_id(), GlobalFilterId.class);
	}
	
	
}