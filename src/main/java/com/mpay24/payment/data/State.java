package com.mpay24.payment.data;

public enum State {
    INIT,
    CREATED,
    AUTHORIZE,
    SUSPENDED,
    REDIRECTED,
    CALLBACK,
    RESERVED,
    RESERVED_REVERSAL,
    EXECUTE,
    BILLED,
    BILLED_REVERSAL,
    REVOKE,
    CREDITED,
    CREDITED_REVERSAL,
    REJECT,
    ARCHIVED,
    WITHDRAW,
    WITHDRAWN,
    ERROR,
	
    REVERSED,
    NOTFOUND,
    FAILED
}
