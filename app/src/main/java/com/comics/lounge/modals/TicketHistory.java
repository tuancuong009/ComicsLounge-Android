package com.comics.lounge.modals;

public class TicketHistory {
    String orderId, name, orderDate, orderTime;
    int showCount, mealCount;

    public TicketHistory(String orderId, String name, String orderDate, String orderTime, int showCount, int mealCount) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.showCount = showCount;
        this.mealCount = mealCount;
    }

    public String getName() {
        return name;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public int getShowCount() {
        return showCount;
    }

    public int getMealCount() {
        return mealCount;
    }

    public String getOrderId() {
        return orderId;
    }
}
