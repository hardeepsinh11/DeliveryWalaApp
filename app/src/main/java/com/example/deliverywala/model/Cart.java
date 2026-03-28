package com.example.deliverywala.model;

public class Cart {
    private String foodId;
    private String foodName;
    private String Price;
    private String restaurantName;
    private String restaurantId;
    private String quantity;
    private String orderId;
    private String onPaymentSuccess;
    private String userId;

    public Cart(String foodId, String foodName, String Price, String restaurantName, 
               String restaurantId, String quantity, String orderId, 
               String onPaymentSuccess, String userId) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.Price = Price;
        this.restaurantName = restaurantName;
        this.restaurantId = restaurantId;
        this.quantity = quantity;
        this.orderId = orderId;
        this.onPaymentSuccess = onPaymentSuccess;
        this.userId = userId;
    }

    // Getters
    public String getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public String getPrice() { return Price; }
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantId() { return restaurantId; }
    public String getQuantity() { return quantity; }
    public String getOrderId() { return orderId; }
    public String getOnPaymentSuccess() { return onPaymentSuccess; }
    public String getUserId() { return userId; }

    // Setters
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setPrice(String Price) { this.Price = Price; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setOnPaymentSuccess(String onPaymentSuccess) { this.onPaymentSuccess = onPaymentSuccess; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Cart{" +
                "foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", Price='" + Price + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", orderId='" + orderId + '\'' +
                ", onPaymentSuccess='" + onPaymentSuccess + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}