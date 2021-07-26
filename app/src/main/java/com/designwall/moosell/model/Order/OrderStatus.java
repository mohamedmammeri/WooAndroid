package com.designwall.moosell.model.Order;

import android.content.Context;
import android.content.res.Resources;

public enum OrderStatus {

    PENDING("pending"),
    PROCESSING("processing"),
    ONHOLD("onhold"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    REFUNDED("refunded"),
    FAILED("failed"),
    TRASH("trash");

    private final String value;

    OrderStatus(String value){ this.value = value; }
    @Override
    public String toString() { return this.value; }

    public static String getLocalized(String status, Context context){
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(OrderStatus.valueOf(status.toUpperCase().replace("-", "")).value,
                "string", context.getPackageName()));
    }

}
