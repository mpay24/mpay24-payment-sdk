package com.mpay24.payment.type;

import com.mpay24.soap.PaymentType;

public class TokenPaymentType extends PaymentTypeData {

	private String token;
	
	public TokenPaymentType(String token) {
		super(PaymentType.TOKEN);
		setToken(token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
