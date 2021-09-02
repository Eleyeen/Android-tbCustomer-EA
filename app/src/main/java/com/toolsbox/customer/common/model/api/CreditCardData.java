package com.toolsbox.customer.common.model.api;

import com.toolsbox.customer.common.model.CreditCardInfo;

import java.io.Serializable;
import java.util.List;

public class CreditCardData implements Serializable {
    public int status;
    public String message;
//    public String default;
    List<CreditCardInfo> data;

}
