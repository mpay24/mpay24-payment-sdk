package com.mpay24.payment.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.mpay.mdxi.AddressModeType;
import com.mpay.mdxi.AddressType;
import com.mpay.mdxi.AddressType.Country;
import com.mpay.mdxi.AddressType.Name;
import com.mpay.mdxi.AddressType.Street;
import com.mpay.mdxi.LanguageType;
import com.mpay.mdxi.Order.BillingAddr;
import com.mpay.mdxi.Order.Currency;
import com.mpay.mdxi.Order.PaymentTypes;
import com.mpay.mdxi.Order.PaymentTypes.Payment;
import com.mpay.mdxi.Order.ShippingAddr;
import com.mpay.mdxi.Order.ShoppingCart.Item;
import com.mpay.mdxi.Order.ShoppingCart.Item.Description;
import com.mpay.mdxi.Order.ShoppingCart.Item.ItemPrice;
import com.mpay.mdxi.Order.ShoppingCart.Item.Number;
import com.mpay.mdxi.Order.ShoppingCart.Item.Price;
import com.mpay.mdxi.Order.ShoppingCart.Item.ProductNr;
import com.mpay.mdxi.Order.ShoppingCart.Item.Quantity;
import com.mpay.mdxi.Order.TemplateSet;
import com.mpay.mdxi.PaymentBrandType;
import com.mpay.mdxi.PaymentTypeType;
import com.mpay.mdxi.Profile;
import com.mpay.mdxi.Profile.CustomerID;
import com.mpay.mdxi.Profile.URL;
import com.mpay24.payment.data.Address;
import com.mpay24.payment.data.Customer;
import com.mpay24.payment.data.Customer.Gender;
import com.mpay24.payment.data.PageStyle;
import com.mpay24.payment.data.PaymentRequest;
import com.mpay24.payment.data.PaymentType;
import com.mpay24.payment.data.ShoppingCart;
import com.mpay24.payment.data.ShoppingCartHeaders;
import com.mpay24.payment.data.ShoppingCartItem;
import com.mpay24.payment.data.ShoppingCartStyle;
import com.mpay24.payment.data.StylingOptions;

public class SdkMdxiMapper {
	public final static Logger logger = Logger.getLogger(SdkMdxiMapper.class);

	private XmlMarshaller xmlMarshaller = new XmlMarshaller();

	public String constructAndMarshalOrder(PaymentRequest paymentRequest, Customer customer, ShoppingCart shoppingCart, StylingOptions stylingOptions) {
		try {
			com.mpay.mdxi.Order order = getOrder(paymentRequest);
			order.setURL(getOrderUrl(paymentRequest));
			order.setPrice(getPrice(paymentRequest));
			order.setCurrency(getCurrency(paymentRequest.getCurrency()));
			order.setUserField(paymentRequest.getUserField());
			order.setTemplateSet(getTemplateSet(paymentRequest, stylingOptions));
			addPageStyle(order, paymentRequest);
			constructShoppingCart(shoppingCart, order);
			order.setPaymentTypes(getPaymentTypes(paymentRequest));
			constructCustomerData(paymentRequest, customer, order);
			return xmlMarshaller.transformToString(order);
		} catch (JAXBException e) {
			logger.error(e.getMessage() + " " + e.getLinkedException());
		}
		return null;
	}
	
	public String constructAndMarshalProfile(Customer customer, String profileId,  ShoppingCart shoppingCart, StylingOptions stylingOptions, PaymentRequest paymentRequest) {
		try {
			com.mpay.mdxi.Profile profile = new Profile();
			constructCustomerData(customer, profileId, profile);
			URL url = new URL();
			if(paymentRequest != null) {
				profile.setPaymentTypes(getProfilePaymentTypes(paymentRequest));
				url.setSuccess(paymentRequest.getSuccessUrl());
				url.setCancel(paymentRequest.getCancelUrl());
			}			
			profile.setURL(url);
			return xmlMarshaller.transformToString(profile);
		} catch (JAXBException e) {
			logger.error(e.getMessage() + " " + e.getLinkedException());
		}
		return null;
	}

	private TemplateSet getTemplateSet(PaymentRequest paymentRequest, StylingOptions stylingOptions) {
		TemplateSet templateSet = new TemplateSet();
		setLanguage(paymentRequest, templateSet);
		setTemplate(stylingOptions, templateSet);
		return templateSet;
	}

	private void setTemplate(StylingOptions stylingOptions, TemplateSet templateSet) {
		if (stylingOptions != null && stylingOptions.getTemplate() != null) {
			templateSet.setCSSName(stylingOptions.getTemplate().toString());
			templateSet.setValue("WEB");
		}
	}

	private void setLanguage(PaymentRequest paymentRequest, TemplateSet templateSet) {
		if (paymentRequest.getLanguage() != null) {
			templateSet.setLanguage(LanguageType.fromValue(paymentRequest.getLanguage().toString()));
		}
	}

	private void constructShoppingCart(ShoppingCart shoppingCart, com.mpay.mdxi.Order order) {
		order.setShoppingCart(getShoppingCart(shoppingCart));
	}

	private void constructCustomerData(PaymentRequest paymentRequest, Customer customer, com.mpay.mdxi.Order order) {
		if (customer == null)
			return;
		order.setClientIP(customer.getClientIp());
		order.setCustomer(getCustomer(paymentRequest, customer));
		order.setBillingAddr(getBillingAddress(customer));
		order.setShippingAddr(getShippingAddress(customer));
	}
	
	private void constructCustomerData(Customer customer, String profileId, com.mpay.mdxi.Profile profile) {
		if (profile == null)
			return;
		profile.setClientIP(customer.getClientIp());
		CustomerID customerID = new CustomerID();
		customerID.setValue(customer.getCustomerId());
		customerID.setProfileID(profileId);
		profile.setCustomerID(customerID);
		profile.setBillingAddr(getBillingAddress(customer));
	}

	private BillingAddr getBillingAddress(Customer customer) {
		BillingAddr billingAddr = (BillingAddr) getAddress(customer, customer.getAddress(), new BillingAddr());
		if (billingAddr == null) return null;
		if (customer.getAddress().isEditable()) {
			billingAddr.setMode(AddressModeType.READ_WRITE);
		} else {
			billingAddr.setMode(AddressModeType.READ_ONLY);
		}
		return billingAddr;
	}

	private ShippingAddr getShippingAddress(Customer customer) {
		ShippingAddr shippingAddr = (ShippingAddr) getAddress(customer, customer.getShippingAddress(), new ShippingAddr());
		if (shippingAddr == null) return null;
		if (customer.getAddress().isEditable()) {
			shippingAddr.setMode(AddressModeType.READ_WRITE);
		} else {
			shippingAddr.setMode(AddressModeType.READ_ONLY);
		}
		return shippingAddr;
	}

	private PaymentTypes getPaymentTypes(PaymentRequest paymentRequest) {
		if (paymentRequest.getPaymentTypeInclusionList() == null)
			return null;
		if (paymentRequest.getPaymentTypeInclusionList().size() == 0)
			return null;
		PaymentTypes paymentTypes = new PaymentTypes();
		paymentTypes.setEnable(paymentRequest.isEnableForRestrictions());
		for (PaymentType paymentType : paymentRequest.getPaymentTypeInclusionList()) {
			paymentTypes.getPayment().add(getPaymentType(paymentType));
		}
		return paymentTypes;
	}
	
	private com.mpay.mdxi.Profile.PaymentTypes getProfilePaymentTypes(PaymentRequest paymentRequest) {
		if (paymentRequest.getPaymentTypeInclusionList() == null)
			return null;
		if (paymentRequest.getPaymentTypeInclusionList().size() == 0)
			return null;
		com.mpay.mdxi.Profile.PaymentTypes paymentTypes = new com.mpay.mdxi.Profile.PaymentTypes();
		paymentTypes.setEnable(paymentRequest.isEnableForRestrictions());
		for (PaymentType paymentType : paymentRequest.getPaymentTypeInclusionList()) {
			paymentTypes.getPayment().add(getProfilePaymentType(paymentType));
		}
		return paymentTypes;
	}

	private Payment getPaymentType(PaymentType paymentType) {
		Payment payment = new Payment();
		payment.setType(PaymentTypeType.fromValue(paymentType.getPaymentType()));
		if (paymentType.getBrand() != null) {
			payment.setBrand(PaymentBrandType.fromValue(paymentType.getBrand()));
		}
		return payment;
	}
	
	private com.mpay.mdxi.Profile.PaymentTypes.Payment getProfilePaymentType(PaymentType paymentType) {
		com.mpay.mdxi.Profile.PaymentTypes.Payment payment = new com.mpay.mdxi.Profile.PaymentTypes.Payment();
		payment.setType(PaymentTypeType.fromValue(paymentType.getPaymentType()));
		if (paymentType.getBrand() != null) {
			payment.setBrand(PaymentBrandType.fromValue(paymentType.getBrand()));
		}
		return payment;
	}

	private com.mpay.mdxi.Order getOrder(PaymentRequest paymentsRequest) {
		com.mpay.mdxi.Order order = new com.mpay.mdxi.Order();
		order.setTid(paymentsRequest.getTransactionID());
		return order;
	}
	private void addPageStyle(com.mpay.mdxi.Order order, PaymentRequest paymentRequest) {
		if(paymentRequest.getPageStyle() != null) {
			PageStyle style = paymentRequest.getPageStyle();
			order.setStyle(style.getStyle());
			order.setPageStyle(style.getPageStyle());
			order.setFooterStyle(style.getFooterStyle());
			order.setInputFieldsStyle(style.getInputFieldsStyle());
			order.setLogoStyle(style.getLogoStyle());
			order.setErrorsStyle(style.getErrorsStyle());
			order.setPageCaptionStyle(style.getCaptionStyle());
			order.setPageHeaderStyle(style.getHeaderStyle());
			order.setButtonsStyle(style.getButtonsStyle());
			order.setSuccessTitleStyle(style.getSuccessTitleStyle());
			order.setErrorsHeaderStyle(style.getErrorsHeaderStyle());
			order.setErrorsStyle(style.getErrorsStyle());
			order.setErrorTitleStyle(style.getErrorTitleStyle());
		}
	}
	private com.mpay.mdxi.Order.URL getOrderUrl(PaymentRequest paymentsRequest) {
		com.mpay.mdxi.Order.URL url = new com.mpay.mdxi.Order.URL();
		url.setSuccess(paymentsRequest.getSuccessUrl());
		url.setError(paymentsRequest.getErrorUrl());
		url.setConfirmation(paymentsRequest.getConfirmationUrl());
		url.setCancel(paymentsRequest.getCancelUrl());
		return url;
	}
	
	private com.mpay.mdxi.Profile.URL getProfileUrl(PaymentRequest paymentsRequest) {
		com.mpay.mdxi.Profile.URL url = new com.mpay.mdxi.Profile.URL();
		url.setSuccess(paymentsRequest.getSuccessUrl());
		url.setCancel(paymentsRequest.getCancelUrl());
		return url;
	}

	private com.mpay.mdxi.Order.Price getPrice(PaymentRequest paymentsRequest) {
		com.mpay.mdxi.Order.Price price = new com.mpay.mdxi.Order.Price();
		price.setValue(paymentsRequest.getAmount().floatValue());
		price.setHeader(paymentsRequest.getPriceHeader());
		if(paymentsRequest.getPageStyle() != null) {
			price.setHeaderStyle(paymentsRequest.getPageStyle().getPriceHeaderStyle());
			price.setStyle(paymentsRequest.getPageStyle().getPriceStyle());
		}
		return price;
	}

	private Currency getCurrency(String currencyString) {
		Currency currency = new Currency();
		currency.setValue(currencyString);
		return currency;
	}

	private com.mpay.mdxi.Order.Customer getCustomer(PaymentRequest paymentRequest, Customer customer) {
		com.mpay.mdxi.Order.Customer mdxiCustomer = new com.mpay.mdxi.Order.Customer();
		mdxiCustomer.setId(customer.getCustomerId());
		mdxiCustomer.setValue(customer.getName());
		mdxiCustomer.setUseProfile(paymentRequest.isSavePaymentData());
		return mdxiCustomer;
	}

	private AddressType getAddress(Customer customer, Address address, AddressType addressType) {
		if (customer == null || address == null) return null;
		if (address.getName() != null) {
			addressType.setName(getName(address.getName(), customer.getGender().getShort(), customer.getBirthdate()));
		} else {
			addressType.setName(getName(customer.getName(), getGender(customer.getGender()), customer.getBirthdate()));
		}
		addressType.setEmail(customer.getEmail());
		addressType.setStreet(getStreet(address.getStreet()));
		addressType.setStreet2(address.getStreet2());
		addressType.setZip(address.getZip());
		addressType.setCity(address.getCity());
		addressType.setState(address.getState());
		addressType.setCountry(getCountry(address.getCountryIso2(), address.getCountryName()));
		addressType.setPhone(customer.getPhoneNumber());
		return addressType;
	}

	private String getGender(Gender gender) {
		if (gender == null) return null;
		else return gender.getShort();
	}

	private Name getName(String nameString, String gender, Date birthdate) {
		Name name = new Name();
		name.setValue(nameString);
		name.setGender(gender);
		name.setBirthday(getCalendarDate(birthdate));
		return name;
	}

	private XMLGregorianCalendar getCalendarDate(Date date) {
		if (date == null)
			return null;
		try {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (Exception e) {
			logger.error("Invalid date '" + date.toString() + "'. Exception: " + e.getMessage());
			return null;
		}
	}

	private Street getStreet(String street) {
		if (street == null)
			return null;
		Street mdxiStreet = new Street();
		mdxiStreet.setValue(street);
		return mdxiStreet;
	}

	private Country getCountry(String countryIso2, String countryName) {
		Country country = new Country();
		country.setCode(countryIso2);
		country.setValue(countryName);
		return country;
	}

	private com.mpay.mdxi.Order.ShoppingCart getShoppingCart(ShoppingCart shoppingCart) {
		if (shoppingCart == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart mdxi = new com.mpay.mdxi.Order.ShoppingCart();
		mdxi.setDescription(shoppingCart.getDescription());
		addShoppingCartStyle(mdxi, shoppingCart);
		addShoppingCartHeaders(mdxi, shoppingCart);
		addDiscount(shoppingCart, mdxi);
		addShippingCost(shoppingCart, mdxi);
		addSubTotal(shoppingCart, mdxi);
		addTax(shoppingCart, mdxi);
		addShoppingItems(shoppingCart, mdxi);
		return mdxi;
	}
	private void addShoppingCartStyle(com.mpay.mdxi.Order.ShoppingCart mdxi, ShoppingCart shoppingCart) {
		if(shoppingCart.getCartStyle() != null) {
			ShoppingCartStyle style = shoppingCart.getCartStyle();
			mdxi.setStyle(style.getStyle());
			mdxi.setCaptionStyle(style.getCaptionStyle());
			mdxi.setDescriptionStyle(style.getDescriptionStyle());
			mdxi.setHeaderStyle(style.getHeaderStyle());
			mdxi.setItemPriceStyle(style.getItemPriceStyle());
			mdxi.setNumberStyle(style.getNumberStyle());
			mdxi.setPackageStyle(style.getPackageStyle());
			mdxi.setPriceStyle(style.getPriceStyle());
			mdxi.setProductNrStyle(style.getProductNrStyle());
			mdxi.setQuantityStyle(style.getQuantityStyle());
		}
	}
	private void addShoppingCartHeaders(com.mpay.mdxi.Order.ShoppingCart mdxi, ShoppingCart shoppingCart) {
		if(shoppingCart.getCartHeaders() != null) {
			ShoppingCartHeaders header = shoppingCart.getCartHeaders();
			mdxi.setHeader(header.getHeader());
			mdxi.setDescriptionHeader(header.getDescriptionHeader());
			mdxi.setItemPriceHeader(header.getItemPriceHeader());
			mdxi.setNumberHeader(header.getNumberHeader());
			mdxi.setPackageHeader(header.getPackageHeader());
			mdxi.setPriceHeader(header.getPriceHeader());
			mdxi.setProductNrHeader(header.getProductNrHeader());
			mdxi.setQuantityHeader(header.getQuantityHeader());
		}
	}
	
	private void addShoppingItems(ShoppingCart shoppingCart, com.mpay.mdxi.Order.ShoppingCart mdxi) {
		if (shoppingCart.getItemList() == null)
			return;
		List<Item> itemList = mdxi.getItem();
		for (ShoppingCartItem shoppingCartItem : shoppingCart.getItemList()) {
			com.mpay.mdxi.Order.ShoppingCart.Item item = new com.mpay.mdxi.Order.ShoppingCart.Item();
			item.setDescription(getDescription(shoppingCartItem));
			item.setItemPrice(getItemPrice(shoppingCartItem));
			item.setNumber(getNumber(shoppingCartItem));
			item.setPrice(getPrice(shoppingCartItem));
			item.setProductNr(getProductNumber(shoppingCartItem));
			item.setQuantity(getQuantity(shoppingCartItem));
			itemList.add(item);
		}
	}

	private Quantity getQuantity(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getQuantity() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.Quantity q = new com.mpay.mdxi.Order.ShoppingCart.Item.Quantity();
		q.setValue(shoppingCartItem.getQuantity());
		if(shoppingCartItem.getItemStyle() != null) {
			q.setStyle(shoppingCartItem.getItemStyle().getQuantityStyle());
		}
		return q;
	}

	private ProductNr getProductNumber(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getProductCode() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.ProductNr productNumber = new com.mpay.mdxi.Order.ShoppingCart.Item.ProductNr();
		productNumber.setValue(shoppingCartItem.getProductCode());
		if(shoppingCartItem.getItemStyle() != null) {
			productNumber.setStyle(shoppingCartItem.getItemStyle().getProductNrStyle());
		}
		return productNumber;
	}

	private Price getPrice(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getAmount() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.Price p = new com.mpay.mdxi.Order.ShoppingCart.Item.Price();
		p.setValue(shoppingCartItem.getAmount().floatValue());
		if(shoppingCartItem.getItemStyle() != null) {
			p.setStyle(shoppingCartItem.getItemStyle().getPriceStyle());
		}
		return p;
	}

	private Number getNumber(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getSequenceId() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.Number number = new com.mpay.mdxi.Order.ShoppingCart.Item.Number();
		number.setValue(shoppingCartItem.getSequenceId());
		if(shoppingCartItem.getItemStyle() != null) {
			number.setStyle(shoppingCartItem.getItemStyle().getNumberStyle());
		}
		return number;
	}

	private ItemPrice getItemPrice(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getItemAmount() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.ItemPrice price = new com.mpay.mdxi.Order.ShoppingCart.Item.ItemPrice();
		price.setValue(shoppingCartItem.getItemAmount().floatValue());
		if(shoppingCartItem.getItemStyle() != null) {
			price.setStyle(shoppingCartItem.getItemStyle().getItemPriceStyle());
		}
		return price;
	}

	private Description getDescription(ShoppingCartItem shoppingCartItem) {
		if (shoppingCartItem.getDescription() == null)
			return null;
		com.mpay.mdxi.Order.ShoppingCart.Item.Description desc = new com.mpay.mdxi.Order.ShoppingCart.Item.Description();
		desc.setValue(shoppingCartItem.getDescription());
		if(shoppingCartItem.getItemStyle() != null) {
			desc.setStyle(shoppingCartItem.getItemStyle().getDescriptionStyle());
		}
		return desc;
	}

	private void addDiscount(ShoppingCart shoppingCart, com.mpay.mdxi.Order.ShoppingCart mdxi) {
		if (shoppingCart.getDiscount() != null) {
			com.mpay.mdxi.Order.ShoppingCart.Discount discount = new com.mpay.mdxi.Order.ShoppingCart.Discount();
			discount.setValue(shoppingCart.getDiscount().floatValue());
			if(shoppingCart.getCartHeaders() != null) discount.setHeader(shoppingCart.getCartHeaders().getDiscountHeader());
			if(shoppingCart.getCartStyle() != null) {
				discount.setHeaderStyle(shoppingCart.getCartStyle().getDiscountHeaderStyle());
				discount.setStyle(shoppingCart.getCartStyle().getDiscountStyle());
			}
			mdxi.getSubTotalOrDiscountOrShippingCosts().add(discount);
		}
	}

	private void addShippingCost(ShoppingCart shoppingCart, com.mpay.mdxi.Order.ShoppingCart mdxi) {
		if (shoppingCart.getShippingCost() != null) {
			com.mpay.mdxi.Order.ShoppingCart.ShippingCosts shippingCosts = new com.mpay.mdxi.Order.ShoppingCart.ShippingCosts();
			shippingCosts.setValue(shoppingCart.getShippingCost().floatValue());
			if (shoppingCart.getShippingCostTax() != null) {
				shippingCosts.setTax(shoppingCart.getShippingCostTax().floatValue());
			}
			if(shoppingCart.getCartHeaders() != null) shippingCosts.setHeader(shoppingCart.getCartHeaders().getShippingCostsHeader());
			if(shoppingCart.getCartStyle() != null) {
				shippingCosts.setHeaderStyle(shoppingCart.getCartStyle().getShippingCostsHeaderStyle());
				shippingCosts.setStyle(shoppingCart.getCartStyle().getShippingCostsStyle());
			}
			mdxi.getSubTotalOrDiscountOrShippingCosts().add(shippingCosts);
		}
	}

	private void addSubTotal(ShoppingCart shoppingCart, com.mpay.mdxi.Order.ShoppingCart mdxi) {
		if (shoppingCart.getSubTotal() != null) {
			com.mpay.mdxi.Order.ShoppingCart.SubTotal subTotal = new com.mpay.mdxi.Order.ShoppingCart.SubTotal();
			subTotal.setValue(shoppingCart.getSubTotal().floatValue());
			if(shoppingCart.getCartHeaders() != null) subTotal.setHeader(shoppingCart.getCartHeaders().getSubTotalHeader());
			if(shoppingCart.getCartStyle() != null) {
				subTotal.setHeaderStyle(shoppingCart.getCartStyle().getSubTotalHeaderStyle());
				subTotal.setStyle(shoppingCart.getCartStyle().getSubTotalStyle());
			}
			mdxi.getSubTotalOrDiscountOrShippingCosts().add(subTotal);
		}
	}

	private void addTax(ShoppingCart shoppingCart, com.mpay.mdxi.Order.ShoppingCart mdxi) {
		if (shoppingCart.getTax() != null) {
			com.mpay.mdxi.Order.ShoppingCart.Tax tax = new com.mpay.mdxi.Order.ShoppingCart.Tax();
			tax.setValue(shoppingCart.getTax().floatValue());
			tax.setPercent(shoppingCart.getTaxPercentage().floatValue());
			if(shoppingCart.getCartHeaders() != null) tax.setHeader(shoppingCart.getCartHeaders().getTaxHeader());
			if(shoppingCart.getCartStyle() != null) {
				tax.setHeaderStyle(shoppingCart.getCartStyle().getTaxHeaderStyle());
				tax.setStyle(shoppingCart.getCartStyle().getTaxStyle());
			}
			mdxi.getSubTotalOrDiscountOrShippingCosts().add(tax);
		}
	}

}
