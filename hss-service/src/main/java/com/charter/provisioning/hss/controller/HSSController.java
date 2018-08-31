package com.charter.provisioning.hss.controller;

import com.charter.provisioning.hss.exception.ErrorResponse;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.service.DigitalPhoneService;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/digital-phones")
@Api(value = "HSS Digital Phone Service")
public class HSSController {

	private DigitalPhoneService digitalPhoneService;
	
	@Autowired
	public HSSController(DigitalPhoneService digitalPhoneService) {
	    this.digitalPhoneService = digitalPhoneService;
	}
	
	@ApiOperation(value = "	Create a subscriber")
	@PostMapping(consumes = "application/json", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Success", response = DigitalPhoneResponse.class, responseContainer = "Return Digital Phone Response"),
			@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error") })
	public ResponseEntity<DigitalPhoneResponse> createSubscriber(
			@RequestHeader(value = "transaction-id", required = false) String transactionId,
			@ApiParam(value = "The DigitalPhone attributes to create digital phone subscriber.", required = true) @RequestBody DigitalPhone digitalPhone) {

		log.info("Create subscriber for Transaction id {}, Telephone Number {}", transactionId, digitalPhone.getPublicIdentity().get(0).getUserId());
		
		return new ResponseEntity<>(digitalPhoneService.createSubscriber(digitalPhone, transactionId), HttpStatus.CREATED);
	}

	@ApiOperation(value = "Delete a subscriber")
	@DeleteMapping(produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Success", response = DigitalPhoneResponse.class, responseContainer = "Return SubscriberResponse"),
			@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error") })
	public ResponseEntity<?> deleteSubscriber(@RequestHeader(value = "audit-user", required = false) String telephoneNumber,
			@RequestHeader(value = "transaction-id", required = false) String transactionId,
			@ApiParam(value = "The DigitalPhone attributes to delete digital phone subscriber.", required = true) @RequestBody DigitalPhone digitalPhone) {

		log.info("Delete subscriber for Transaction id {}, Telephone Number {}", transactionId, telephoneNumber);
		digitalPhoneService.deleteSubscriber(digitalPhone, transactionId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "Retrieving Digital Phone Subscriber")
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Success", response = Subscriber.class, responseContainer = "Return Subscriber"),
			@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error")})
	@GetMapping(produces = "application/json")
	public ResponseEntity<Subscriber> getDigitalPhone(@ApiParam (value = "UUID used to trace a transaction through all systems end to end.") @RequestHeader(value = "transaction-id", required = false) String transactionId,
													  @ApiParam (value = "TelephoneNumber is used to lookup the HSS Subscriber.") @RequestParam(value = "telephone-number", required = false) String telephoneNumber,
													  @ApiParam (value = "ControllerId is used to lookup HSS Subscriber for HG/BG.") @RequestParam(value = "controller-id", required = false) String controllerId,
													  @ApiParam (value = "privateIdentity is used to lookup the HSS Subscriber.") @RequestParam(value = "private-identity", required = false) String privateIdentity) {

		log.info("retrieving digital phone subscriber by Telephone Number {}, Controller Id {}, privateIdentity {}, Transaction id {}", telephoneNumber,controllerId, privateIdentity, transactionId);
		return new ResponseEntity<>(digitalPhoneService.getDigitalPhoneSubscriber(telephoneNumber, controllerId, privateIdentity, transactionId), HttpStatus.OK);
	}

	@ExceptionHandler(Exception.class)
	public ErrorResponse handleException(HttpServletResponse response, Throwable ex) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		if (ex instanceof ServiceException) {
			response.setStatus(((ServiceException) ex).getHttpStatus());
		}

		return ErrorResponse.builder().message(ex.getMessage()).status(String.valueOf(response.getStatus())).build();
	}
	
}