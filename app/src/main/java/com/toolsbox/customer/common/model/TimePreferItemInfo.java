package com.toolsbox.customer.common.model;

import java.io.Serializable;

public class TimePreferItemInfo implements Serializable {
    public String title;
    public boolean isSelected;

    public TimePreferItemInfo(){

    }

    public TimePreferItemInfo(String title, boolean isSelected) {
        this.title = title;
        this.isSelected = isSelected;
    }


}
