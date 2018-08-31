package com.charter.provisioning.hss.exception;

import javax.servlet.http.HttpServletResponse;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
	
	private int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(int httpStatus,String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
