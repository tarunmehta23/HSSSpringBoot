package com.charter.provisioning.hss.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.Text;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.charter.provisioning.hss.exception.SoapServiceException;
import com.charter.provisioning.hss.model.Property;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@Configuration
public class SoapMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Value("${soap.body}")
	private String soapBody;

	@Value("${soap.action}")
	private String soapAction;

	@Value("${soap.header}")
	private String soapHeader;

	public SOAPMessage message;

	protected Map<String, Property> properties = new HashMap<>();

	private transient TransformerFactory transformerFactory = null;
	private transient DocumentBuilderFactory documentBuilderFactory = null;

	/**
	 * Constructor creates soap message instance and creates messageFactory instance 
	 * @throws SoapServiceException
	 */
	public SoapMessage() throws SoapServiceException {
		try {
			createSoapMessage();
		} catch (SOAPException e) {
			log.error("Exception occurred while creating soap Message instance ", e);
			throw new SoapServiceException(e);
		}
	}

	/**
	 * Creates Soap Messages and populate message body contents for Hss Subscriber.
	 * @param payload
	 * @throws SoapServiceException
	 */
	public void populateMessageBody(String payload) throws SoapServiceException {
		try {
			Document payloadDoc = buildDom(payload);

			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			soapEnvelope.getBody().detachNode();
			soapEnvelope.addBody();

			Document soapPartDoc = getSoapPartDom();
			Node payloadNode = soapPartDoc.importNode(payloadDoc.getDocumentElement(), true);
			Node soapBodyNode = soapPartDoc.getFirstChild().getLastChild();
			soapBodyNode.appendChild(payloadNode);
			soapPart.setContent(new DOMSource(soapPartDoc));

		} catch (SOAPException e) {
			log.error("Exception occurred while populating Message body ", e);
			throw new SoapServiceException(e);
		}
	}

	/**
	 * Returns Property Values for Soap Message.
	 * @param name
	 * @return String
	 */
	public String getPropertyValue(String name) {
		Property property = getProperty(name);
		if (property != null)
			return property.getValue();
		return null;
	}

	/**
	 * Returns XML string Contents of soap Message.
	 * @return String
	 * @throws SoapServiceException
	 */
	public String getSoapXml() throws SoapServiceException {

		if (message == null)
			return null;

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			message.writeTo(bos);
			return bos.toString();

		} catch (SOAPException | IOException e) {
			log.error("Exception occurred while retrieving soap XML ", e);
			throw new SoapServiceException(e);
		}
	}

	/**
	 * Create soap message from input Bytes array.
	 * @param bytes
	 * @throws SoapServiceException
	 */
	public void setMessage(byte[] bytes) throws SoapServiceException {

		if (bytes == null)
			return;

		createSoapMessage(bytes);
	}

	/**
	 * Get Soap payload in XML format with namespace Parsing.
	 * @return String
	 * @throws SoapServiceException
	 */
	public String getPayload() throws SoapServiceException {
		try {
			SOAPBody soapBody = getMessageSoapBody();
			List<Object> bodyElements = getChildElements(soapBody);
			if (bodyElements.size() == 0)
				return null;

			Object bodyElement = bodyElements.get(0);
			if (bodyElement instanceof Text) {
				return ((Text) bodyElement).getValue();
			} else {
				return getSoapBodyNodeContent();
			}

		} catch (SOAPException e) {
			log.error("Exception occurred while retrieving soap xml payload ", e);
			throw new SoapServiceException(e);
		}
	}

	void addProperty(String name, String value) {
		properties.put(name, Property.builder().name(name).value(value).build());
	}

	Property getProperty(String name) {
		return properties.get(name);
	}

	private void createSoapMessage() throws SOAPException {
		MessageFactory messageFactory = MessageFactory.newInstance();
		message = messageFactory.createMessage();
	}

	private Document buildDom(byte[] bytes) throws SoapServiceException {

		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {

			DocumentBuilderFactory factory = getDocumentBuilderFactory();
			DocumentBuilder builder = factory.newDocumentBuilder();

			return builder.parse(bis);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Exception occurred while building Document Builder ", e);
			throw new SoapServiceException(e);
		}
	}

	private Document buildDom(String xml) throws SoapServiceException {
		return buildDom(xml.getBytes());
	}

	private Document getSoapPartDom() throws SoapServiceException {
		
		try {
			Source soapPartSource = message.getSOAPPart().getContent();
			return (Document) transformToDom(soapPartSource);

		} catch (SOAPException | TransformerException e) {
			log.error("Exception occurred while building Document ", e);
			throw new SoapServiceException(e);
		}
	}

	private Node transformToDom(Source source) throws TransformerException {

		Transformer transformer = getTransformerFactory().newTransformer();
		DOMResult domResult = new DOMResult();
		transformer.transform(source, domResult);
		return domResult.getNode();
	}

	private TransformerFactory getTransformerFactory() {
		if (transformerFactory == null) {
			transformerFactory = TransformerFactory.newInstance();
		}
		return transformerFactory;
	}

	private DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
		}
		return documentBuilderFactory;
	}

	private void createSoapMessage(byte[] bytes) throws SoapServiceException {

		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
			MessageFactory messageFactory = MessageFactory.newInstance();

			message = messageFactory.createMessage();
			StreamSource streamSource = new StreamSource(bis);
			message.getSOAPPart().setContent(streamSource);

		} catch (SOAPException | IOException e) {
			log.error("Exception occurred while creating soap message ", e);
			throw new SoapServiceException(e);
		}
	}

	private SOAPBody getMessageSoapBody() throws SoapServiceException {
		if (message == null)
			return null;

		try {
			SOAPPart soapPart = message.getSOAPPart();

			if (soapPart != null) {
				SOAPEnvelope soapEnv = soapPart.getEnvelope();
				if (soapEnv != null) {
					return soapEnv.getBody();
				}
			}
			return null;

		} catch (SOAPException e) {
			log.error("Exception occurred while retrieving soap body ", e);
			throw new SoapServiceException(e);
		}
	}

	private List<Object> getChildElements(SOAPElement soapElement) throws SOAPException {

		List<Object> childElements = new ArrayList<>();
		if (soapElement == null)
			return childElements;

		Iterator<?> it = soapElement.getChildElements();
		Object childElement;

		while (it.hasNext()) {
			childElement = it.next();
			if (childElement instanceof org.w3c.dom.Comment)
				continue;
			if (childElement instanceof Text) {
				Text soapText = (Text) childElement;
				if (soapText.isComment() || soapText.getValue().trim().length() == 0)
					continue;
			}
			if (childElement instanceof org.w3c.dom.Text) {
				org.w3c.dom.Text domText = (org.w3c.dom.Text) childElement;
				if (domText.getData().trim().length() == 0)
					continue;
			}
			childElements.add(childElement);
		}

		return childElements;
	}

	private String getSoapBodyNodeContent() throws SoapServiceException {
		try {
			Node node = getSoapBodyNode();
			byte[] soapBodyContent = transformToByteArray(new DOMSource(node));
			return new String(soapBodyContent);

		} catch (TransformerException | IOException e) {
			log.error("Exception occurred while retrieving soap body node content ", e);
			throw new SoapServiceException(e);
		}
	}

	private Node getSoapBodyNode() throws SoapServiceException {
		try {
			byte[] soapPartBytes = getSoapPart();
			Document document = buildDom(soapPartBytes);

			DOMXPath xpath = new DOMXPath("/soap:Envelope/soap:Body/*");
			xpath.addNamespace("soap", SOAPConstants.URI_NS_SOAP_ENVELOPE);
			return (Node) xpath.selectSingleNode(document);

		} catch (JaxenException e) {
			log.error("Exception occurred while retrieving soap body node ", e);
			throw new SoapServiceException(e);
		}
	}

	private byte[] getSoapPart() throws SoapServiceException {
		try {
			Source soapPartSource = message.getSOAPPart().getContent();
			return transformToByteArray(soapPartSource);

		} catch (SOAPException | TransformerException | IOException e) {
			log.error("Exception occurred while retrieving soap part ", e);
			throw new SoapServiceException(e);
		}
	}

	private byte[] transformToByteArray(Source source) throws TransformerException, IOException {

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			Transformer transformer = getTransformerFactory().newTransformer();
			transformer.setOutputProperty("omit-xml-declaration", "yes");

			StreamResult result = new StreamResult(bos);
			transformer.transform(source, result);

			return bos.toByteArray();
		}
	}
}
