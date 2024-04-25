package com.mpay24.payment.mapper;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class CalendarConverter {

    private static DatatypeFactory df = null;
    static {
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException dce) {
            throw new IllegalStateException(
                    "Exception while obtaining DatatypeFactory instance", dce);
        }
    }

    public static XMLGregorianCalendar asXMLGregorianCalendar(java.util.Date date) {
        if (date == null) {
            return null;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return df.newXMLGregorianCalendar(gc);
        }
    }

    public static java.util.Date asDate(XMLGregorianCalendar xgc) {
        if (xgc == null) {
            return null;
        } else {
            return xgc.toGregorianCalendar().getTime();
        }
    }
}