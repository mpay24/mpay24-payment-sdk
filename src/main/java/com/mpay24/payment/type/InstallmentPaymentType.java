package com.mpay24.payment.type;

import com.mpay24.soap.PaymentType;

public class InstallmentPaymentType extends InvoicePaymentType {

	public InstallmentPaymentType() {
		super(PaymentType.HP);
	}

}
