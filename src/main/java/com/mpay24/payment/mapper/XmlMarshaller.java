package com.mpay24.payment.mapper;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.mpay.mdxi.Order;

public class XmlMarshaller {
	public String transformOrderToString(Order order) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(com.mpay.mdxi.Order.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		jaxbMarshaller.marshal(order, writer);
		return writer.toString();
	}
	public Order transformStringToOrder(String xml) throws JAXBException {
		StringReader reader = new StringReader(xml);
		JAXBContext jaxbContext = JAXBContext.newInstance(com.mpay.mdxi.Order.class);
		Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		return (Order) jaxbMarshaller.unmarshal(reader);
	}
}
