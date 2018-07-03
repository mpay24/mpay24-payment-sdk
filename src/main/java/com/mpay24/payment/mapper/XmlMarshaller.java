package com.mpay24.payment.mapper;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.mpay.mdxi.Order;
import com.mpay.mdxi.Profile;

public class XmlMarshaller {
	
	public String transformToString(Object object) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		jaxbMarshaller.marshal(object, writer);
		return writer.toString();
	}
	
	public Order transformStringToOrder(String xml) throws JAXBException {
		StringReader reader = new StringReader(xml);
		JAXBContext jaxbContext = JAXBContext.newInstance(com.mpay.mdxi.Order.class);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		return (Order) jaxbMarshaller.unmarshal(reader);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T transformString(String xml, Class<T> clazz) throws JAXBException {
		StringReader reader = new StringReader(xml);
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		return (T) jaxbMarshaller.unmarshal(reader);
	}
}
