package com.designwall.moosell.model.Order;

public enum OrderStatus {

    Pending("pending"),
    Processing("processing"),
    OnHold("on-hold"),
    Completed("completed"),
    Cancelled("cancelled"),
    Refunded("refunded"),
    Failed("failed");

    private final String value;
    OrderStatus(String value){ this.value = value; }
    @Override
    public String toString() { return this.value; }

}
