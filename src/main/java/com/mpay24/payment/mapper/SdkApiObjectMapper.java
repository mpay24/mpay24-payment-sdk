package com.mpay24.payment.mapper;

import com.mpay24.payment.data.Customer;
import com.mpay24.payment.data.PaymentRequest;
import com.mpay24.payment.data.ShoppingCart;
import com.mpay24.payment.data.ShoppingCartItem;
import com.mpay24.payment.type.*;
import com.mpay24.payment.type.InvoicePaymentType.Brand;
import com.mpay24.payment.util.CalendarConverter;
import com.mpay24.soap.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SdkApiObjectMapper {

	ObjectFactory objectFactory = new ObjectFactory();

	public String getCustomerName(Customer customer) {
		if (customer == null)
			return null;
		return customer.getName();
	}

	public String getCustomerId(Customer customer) {
		if (customer == null)
			return null;
		return customer.getCustomerId();
	}

	public PaymentType mapPaymentTypeData(PaymentTypeData paymentType) {
		if (paymentType instanceof DirectDebitPaymentType) {
			return PaymentType.ELV;
		} else if (paymentType instanceof CreditCardPaymentType) {
			return PaymentType.CC;
		} else if (paymentType instanceof TokenPaymentType) {
			return PaymentType.TOKEN;
		} else {
			throw new UnsupportedOperationException("Currently this method only supports the following Payment types: DirectDebit, CreditCard");
		}
		
	}

	public PaymentData mapPaymentData(PaymentTypeData paymentType, String profileId) {
		if (paymentType instanceof DirectDebitPaymentType) {
			return mapPaymentData((DirectDebitPaymentType)paymentType, profileId);
		} else if (paymentType instanceof CreditCardPaymentType) {
			return mapPaymentData((CreditCardPaymentType)paymentType, profileId);
		} else if (paymentType instanceof TokenPaymentType) {
			return mapPaymentData((TokenPaymentType)paymentType, profileId);
		} else {
			throw new UnsupportedOperationException("Currently this method only supports the following Payment types: DirectDebit, CreditCard");
		}
		
	}

	private PaymentData mapPaymentData(DirectDebitPaymentType paymentType, String profileId) {
		PaymentDataELV paymentData = new PaymentDataELV();
		paymentData.setBic(objectFactory.createPaymentDataELVBic(paymentType.getBic()));
		paymentData.setBrand(paymentType.getBrand().toString());
		paymentData.setDateOfSignature(objectFactory.createPaymentDataELVDateOfSignature(CalendarConverter.asXMLGregorianCalendar(paymentType.getDateOfSignature())));
		paymentData.setIban(paymentType.getIban());
		paymentData.setMandateID(objectFactory.createPaymentDataELVMandateID(paymentType.getMandateID()));
		paymentData.setProfileID(objectFactory.createPaymentDataProfileID(profileId));
		return paymentData;
	}

	private PaymentData mapPaymentData(CreditCardPaymentType paymentType, String profileId) {
		PaymentDataCC paymentData = new PaymentDataCC();
		paymentData.setBrand(paymentType.getBrand().toString());
		paymentData.setExpiry(getExpiredateAsLong(paymentType.getExpiry()));
		paymentData.setIdentifier(paymentType.getPan());
		paymentData.setProfileID(objectFactory.createPaymentDataProfileID(profileId));
		return paymentData;
	}

	private PaymentData mapPaymentData(TokenPaymentType paymentType, String profileId) {
		PaymentDataTOKEN paymentData = new PaymentDataTOKEN();
		paymentData.setToken(paymentType.getToken());
		paymentData.setProfileID(objectFactory.createPaymentDataProfileID(profileId));
		return paymentData;
	}

	public Payment mapPaymentSystemData(PaymentRequest paymentRequest, PaymentTypeData paymentType) {
		if (paymentType instanceof DirectDebitPaymentType) {
			return mapPaymentSystemData(paymentRequest, (DirectDebitPaymentType) paymentType);
		} else if (paymentType instanceof CreditCardPaymentType) {
			return mapPaymentSystemData(paymentRequest, (CreditCardPaymentType) paymentType);
		} else if (paymentType instanceof DebitCardPaymentType) {
			return mapPaymentSystemData(paymentRequest, (DebitCardPaymentType) paymentType);
		} else if (paymentType instanceof InvoicePaymentType) {
			return mapPaymentSystemData(paymentRequest, (InvoicePaymentType) paymentType);
		} else if (paymentType instanceof OnlineBankingPaymentType) {
			return mapPaymentSystemData(paymentRequest, (OnlineBankingPaymentType) paymentType);
		} else if (paymentType instanceof PaypalPaymentType) {
			return mapPaymentSystemData(paymentRequest, (PaypalPaymentType) paymentType);
		} else if (paymentType instanceof RecurringCreditCardPaymentType) {
			return mapPaymentSystemData(paymentRequest, (RecurringCreditCardPaymentType) paymentType);
		} else if (paymentType instanceof RecurringDirectDebitPaymentType) {
			return mapPaymentSystemData(paymentRequest, (RecurringDirectDebitPaymentType) paymentType);
		} else if (paymentType instanceof PaysafecardPaymentType) {
			return mapRedirectPaymentSystemData(paymentRequest);
		} else if (paymentType instanceof QuickPaymentType) {
			return mapRedirectPaymentSystemData(paymentRequest);
		} else if (paymentType instanceof TokenPaymentType) {
			return mapRedirectPaymentSystemData(paymentRequest, (TokenPaymentType) paymentType);
		} else {
			return null;
		}
	}

	public Order mapOrder(PaymentRequest paymentRequest, Customer customer, ShoppingCart shoppingCart) {
		if (customer == null && shoppingCart == null)
			return null;
		Order order = new Order();
		order.setBilling(objectFactory.createOrderBilling(mapCustomer(customer)));
		order.setShoppingCart(objectFactory.createOrderShoppingCart(mapShoppingCart(shoppingCart)));
		order.setDescription(objectFactory.createOrderDescription(paymentRequest.getDescription()));
		order.setUserField(objectFactory.createOrderUserField(paymentRequest.getUserField()));
		return order;
	}

	private com.mpay24.soap.ShoppingCart mapShoppingCart(ShoppingCart shoppingCart) {
		if (shoppingCart == null)
			return null;
		com.mpay24.soap.ShoppingCart soapShoppingCart = new com.mpay24.soap.ShoppingCart();
		soapShoppingCart.setDiscount(objectFactory.createShoppingCartDiscount(convertBigDecimalToInteger(shoppingCart.getDiscount())));
		soapShoppingCart.setShippingCosts(objectFactory.createShoppingCartShippingCosts(convertBigDecimalToInteger(shoppingCart.getShippingCost())));
		soapShoppingCart.setShippingTax(objectFactory.createShoppingCartShippingTax(convertBigDecimalToInteger(shoppingCart.getShippingCostTax())));
		soapShoppingCart.setTax(objectFactory.createShoppingCartTax(convertBigDecimalToInteger(shoppingCart.getTax())));
		if (shoppingCart.getItemList() != null) {
			for (ShoppingCartItem shoppingCartItem : shoppingCart.getItemList()) {
				Item item = new Item();
				item.setAmount(convertBigDecimalToInteger(shoppingCartItem.getAmount()));
				item.setDescription(objectFactory.createItemDescription(shoppingCartItem.getDescription()));
				item.setNumber(objectFactory.createItemNumber(shoppingCartItem.getSequenceId()));
				item.setProductNr(objectFactory.createItemProductNr(shoppingCartItem.getProductCode()));
				item.setQuantity(objectFactory.createItemQuantity(shoppingCartItem.getQuantity()));
				item.setTax(objectFactory.createItemTax(convertBigDecimalToInteger(shoppingCartItem.getTax())));
				soapShoppingCart.getItem().add(item);
			}

		}
		return soapShoppingCart;
	}

	private Integer convertBigDecimalToInteger(BigDecimal amount) {
		if (amount == null)
			return null;
		return convertToCentAmount(amount).intValue();
	}

	private Double convertToCentAmount(BigDecimal amount) {
		BigDecimal convertedAmount = new BigDecimal(amount.multiply(new BigDecimal(100)).doubleValue());
		convertedAmount.setScale(2, RoundingMode.HALF_UP);
		return convertedAmount.doubleValue();
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, CreditCardPaymentType paymentTypeData) {
		PaymentCC payment = new PaymentCC();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setAuth3DS(paymentTypeData.getAuth3DS());
		if (paymentTypeData.getBrand() != null) {
			payment.setBrand(paymentTypeData.getBrand().toString());
		}
		payment.setCvc(objectFactory.createPaymentCCCvc(paymentTypeData.getCvc()));
		payment.setExpiry(getExpiredateAsLong(paymentTypeData.getExpiry()));
		payment.setIdentifier(paymentTypeData.getPan());
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private void setManualClearing(PaymentRequest paymentRequest, Payment payment) {
		if (paymentRequest.isCapture() != null) {
			payment.setManualClearing(objectFactory.createPaymentManualClearing(!paymentRequest.isCapture()));
		}
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, DebitCardPaymentType paymentTypeData) {
		PaymentMAESTRO payment = new PaymentMAESTRO();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setExpiry(getExpiredateAsLong(paymentTypeData.getExpiry()));
		payment.setIdentifier(paymentTypeData.getPan());
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, DirectDebitPaymentType paymentTypeData) {
		PaymentELV payment = new PaymentELV();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setIban(paymentTypeData.getIban());
		payment.setBic(objectFactory.createPaymentELVBic(paymentTypeData.getBic()));
		payment.setMandateID(objectFactory.createPaymentELVMandateID(paymentTypeData.getMandateID()));
		payment.setDateOfSignature(objectFactory.createPaymentELVDateOfSignature(CalendarConverter.asXMLGregorianCalendar(paymentTypeData.getDateOfSignature())));
		payment.setBrand(paymentTypeData.getBrand().toString());
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, InvoicePaymentType paymentTypeData) {
		if (paymentTypeData.getBrand() == Brand.BILLPAY) {
			PaymentBILLPAY payment = new PaymentBILLPAY();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setBrand(paymentTypeData.getPaymentType().toString());
			payment.setCurrency(paymentRequest.getCurrency());
			paymentTypeData.setPaymentType(PaymentType.BILLPAY);
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		} else {
			PaymentKLARNA payment = new PaymentKLARNA();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setBrand(paymentTypeData.getPaymentType().toString());
			payment.setCurrency(paymentRequest.getCurrency());
			payment.setPClass(objectFactory.createPaymentKLARNAPClass(paymentTypeData.getKlarnaPclass()));
			payment.setPersonalNumber(paymentTypeData.getKlarnaPersonalNumber());
			paymentTypeData.setPaymentType(PaymentType.KLARNA);
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		}
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, OnlineBankingPaymentType paymentTypeData) {
		if (paymentTypeData.getBrand() == com.mpay24.payment.type.OnlineBankingPaymentType.Brand.EPS) {
			PaymentEPS payment = new PaymentEPS();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setCurrency(paymentRequest.getCurrency());
			payment.setBankID(objectFactory.createPaymentEPSBankID(paymentTypeData.getStuzzaBankId()));
			payment.setBic(objectFactory.createPaymentEPSBic(paymentTypeData.getBic()));
			payment.setBrand(paymentTypeData.getBrand().toString());
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		} else if (paymentTypeData.getBrand() == com.mpay24.payment.type.OnlineBankingPaymentType.Brand.EPS_STUZZA_BANK_SELECTION) {
			PaymentEPS payment = new PaymentEPS();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setCurrency(paymentRequest.getCurrency());
			payment.setBankID(objectFactory.createPaymentEPSBankID(paymentTypeData.getStuzzaBankId()));
			payment.setBic(objectFactory.createPaymentEPSBic(paymentTypeData.getBic()));
			payment.setBrand("INTERNATIONAL");
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		} else if (paymentTypeData.getBrand() == com.mpay24.payment.type.OnlineBankingPaymentType.Brand.SOFORT) {
			Payment payment = new Payment();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setCurrency(paymentRequest.getCurrency());
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		} else if (paymentTypeData.getBrand() == com.mpay24.payment.type.OnlineBankingPaymentType.Brand.GIROPAY) {
			PaymentGIROPAY payment = new PaymentGIROPAY();
			payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
			payment.setCurrency(paymentRequest.getCurrency());
			payment.setBic(paymentTypeData.getBic());
			payment.setIban(objectFactory.createPaymentGIROPAYIban(paymentTypeData.getIban()));
			payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
			payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
			payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
			setManualClearing(paymentRequest, payment);
			return payment;
		}
		return null;
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, PaypalPaymentType paymentTypeData) {
		PaymentPAYPAL payment = new PaymentPAYPAL();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setCustom(objectFactory.createPaymentPAYPALCustom(paymentTypeData.getCustom()));
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		if (paymentTypeData.isExpressCheckout()) {
			payment.setCommit(false);
		}
		return payment;
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, RecurringDirectDebitPaymentType paymentTypeData) {
		Payment payment = new Payment();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
//		payment.setDateOfSignature(paymentTypeData.getDateOfSignature());
//		payment.setMandateID(paymentTypeData.getMandateID());
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private Payment mapPaymentSystemData(PaymentRequest paymentRequest, RecurringCreditCardPaymentType paymentTypeData) {
		Payment payment = new Payment();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
//		payment.setCvc(paymentTypeData.getCvc());
//		payment.setAuth3DS(paymentTypeData.isAuth3DS());
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private Payment mapRedirectPaymentSystemData(PaymentRequest paymentRequest) {
		Payment payment = new Payment();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}

	private Payment mapRedirectPaymentSystemData(PaymentRequest paymentRequest, TokenPaymentType paymentType) {
		PaymentTOKEN payment = new PaymentTOKEN();
		payment.setAmount(convertBigDecimalToInteger(paymentRequest.getAmount()));
		payment.setCurrency(paymentRequest.getCurrency());
		payment.setUseProfile(objectFactory.createPaymentUseProfile(paymentRequest.isSavePaymentData()));
		payment.setProfileID(objectFactory.createPaymentProfileID(paymentRequest.getStoredPaymentDataId()));
		payment.setToken(paymentType.getToken());
		payment.setTimeout(objectFactory.createPaymentTimeout(paymentRequest.getTimeoutSeconds()));
		setManualClearing(paymentRequest, payment);
		return payment;
	}


	protected Long getExpiredateAsLong(Date expiryDate) {
		String expiryDateAsString = new SimpleDateFormat("yyMM").format(expiryDate);
		return Long.valueOf(expiryDateAsString);
	}

	public Address mapCustomer(Customer customer) {
		if (customer == null)
			return null;
		if (customer.getAddress() == null)
			return null;
		Address soapAddress = new Address();
		soapAddress.setMode(AddressMode.READONLY);
		soapAddress.setName(customer.getName());
		soapAddress.setBirthday(objectFactory.createAddressBirthday(CalendarConverter.asXMLGregorianCalendar(customer.getBirthdate())));
		soapAddress.setEmail(objectFactory.createAddressEmail(customer.getEmail()));
		if (customer.getGender() != null) {
			soapAddress.setGender(objectFactory.createAddressGender(Gender.fromValue(customer.getGender().toString().toUpperCase())));
		}
		soapAddress.setPhone(objectFactory.createAddressPhone(customer.getPhoneNumber()));
		mapAddress(soapAddress, customer.getAddress());
		return soapAddress;
	}

	private void mapAddress(Address soapAddress, com.mpay24.payment.data.Address address) {
		if (address == null)
			return;
		if (address.isEditable()) {
			soapAddress.setMode(AddressMode.READWRITE);
		}
		ObjectFactory objectFactory = new ObjectFactory();
		soapAddress.setCity(address.getCity());
		soapAddress.setCountryCode(address.getCountryIso2());
		soapAddress.setState(objectFactory.createAddressState(address.getState()));
		soapAddress.setStreet(address.getStreet());
		soapAddress.setStreet2(objectFactory.createAddressStreet2(address.getStreet2()));
		soapAddress.setZip(address.getZip());
	}

}
