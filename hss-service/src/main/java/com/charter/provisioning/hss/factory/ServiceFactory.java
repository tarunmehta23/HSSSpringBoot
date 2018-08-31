package com.charter.provisioning.hss.factory;

import com.charter.provisioning.hss.exception.ServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ServiceFactory {
	
    private List<ServiceInterface> services;
    
    @Autowired
	public ServiceFactory(List<ServiceInterface> services) {
		this.services = services;
	}

    private static final Map<ServiceType, ServiceInterface> myServiceCache = new HashMap<>();
	
	@Getter(AccessLevel.PRIVATE)
	public enum ServiceType {
		CreateSubscriberHandler,
		DeleteSubscriberHandler,
		CreateHGSubscriberHandler,
		CreateBGSubscriberHandler,
		CreateRCFSubscriberHandler,
        DeleteHGSubscriberHandler,
        DeleteBGSubscriberHandler;
		
		private Supplier<ServiceInterface> constructor;
	}

    @PostConstruct
    public void initMyServiceCache() {
		for (ServiceInterface service : services) {
			
			List<ServiceType> serviceTypes = Arrays.asList(ServiceType.values());
			ServiceType serviceType = serviceTypes.stream()
					.filter(x -> ClassUtils.getShortName(ClassUtils.getUserClass(service)).equals(x.name())).findAny()
					.orElse(null);
			
			if (null == serviceType)
				serviceType = serviceTypes.stream().filter(x -> service.toString().equals(x.name()))
						.findAny().orElse(null);
			
			if (null != serviceType) {
				myServiceCache.put(serviceType, service);
			}
		}
    }

    public static ServiceInterface getService(ServiceType type) {
    	ServiceInterface service = myServiceCache.get(type);
        if(service == null) throw new ServiceException("Unknown service type: " + type);
        return service;
    }
}
