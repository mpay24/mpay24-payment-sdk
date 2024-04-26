package com.mpay24.payment.type;

import com.mpay24.soap.PaymentType;

public abstract class PaymentTypeData {
	private PaymentType paymentType;

	public PaymentTypeData(PaymentType paymentType) {
		super();
		this.setPaymentType(paymentType);
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
}
