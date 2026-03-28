package com.example.deliverywala.model;

import java.util.ArrayList;

public class OrderHistory {
    private String restaurantName;
    private String orderDate;
    private String orderTime;
    private String totalCost;
    private ArrayList<String> foodName;
    private ArrayList<String> qty;
    private ArrayList<String> foodPrice;
    private ArrayList<String> orderId;
    private String deliveryAddress;
    private String contact;
    private String orderStatus;
    private String struserId;

    public OrderHistory(String restaurantName, String orderDate, String orderTime,
                      String totalCost, ArrayList<String> foodName, ArrayList<String> qty,
                      ArrayList<String> foodPrice, ArrayList<String> orderId,
                      String deliveryAddress, String contact, String orderStatus,
                      String struserId) {
        this.restaurantName = restaurantName;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.totalCost = totalCost;
        this.foodName = foodName;
        this.qty = qty;
        this.foodPrice = foodPrice;
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.contact = contact;
        this.orderStatus = orderStatus;
        this.struserId = struserId;
    }

    // Getters
    public String getRestaurantName() { return restaurantName; }
    public String getOrderDate() { return orderDate; }
    public String getOrderTime() { return orderTime; }
    public String getTotalCost() { return totalCost; }
    public ArrayList<String> getFoodName() { return foodName; }
    public ArrayList<String> getQty() { return qty; }
    public ArrayList<String> getFoodPrice() { return foodPrice; }
    public ArrayList<String> getOrderId() { return orderId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getContact() { return contact; }
    public String getOrderStatus() { return orderStatus; }
    public String getStruserId() { return struserId; }

    // Setters
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }
    public void setTotalCost(String totalCost) { this.totalCost = totalCost; }
    public void setFoodName(ArrayList<String> foodName) { this.foodName = foodName; }
    public void setQty(ArrayList<String> qty) { this.qty = qty; }
    public void setFoodPrice(ArrayList<String> foodPrice) { this.foodPrice = foodPrice; }
    public void setOrderId(ArrayList<String> orderId) { this.orderId = orderId; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setContact(String contact) { this.contact = contact; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setStruserId(String struserId) { this.struserId = struserId; }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "restaurantName='" + restaurantName + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", totalCost='" + totalCost + '\'' +
                ", foodName=" + foodName +
                ", qty=" + qty +
                ", foodPrice=" + foodPrice +
                ", orderId=" + orderId +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", contact='" + contact + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", struserId='" + struserId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistory that = (OrderHistory) o;
        return restaurantName.equals(that.restaurantName) &&
                orderDate.equals(that.orderDate) &&
                orderTime.equals(that.orderTime) &&
                totalCost.equals(that.totalCost) &&
                foodName.equals(that.foodName) &&
                qty.equals(that.qty) &&
                foodPrice.equals(that.foodPrice) &&
                orderId.equals(that.orderId) &&
                deliveryAddress.equals(that.deliveryAddress) &&
                contact.equals(that.contact) &&
                orderStatus.equals(that.orderStatus) &&
                struserId.equals(that.struserId);
    }


}