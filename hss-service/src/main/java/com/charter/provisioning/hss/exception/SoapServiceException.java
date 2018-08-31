package com.charter.provisioning.hss.exception;

@SuppressWarnings("serial")
public class SoapServiceException extends Exception {

	public SoapServiceException() {
		super();
	}

	public SoapServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoapServiceException(String message) {
		super(message);
	}

	public SoapServiceException(Throwable cause) {
		super(cause);
	}
}
