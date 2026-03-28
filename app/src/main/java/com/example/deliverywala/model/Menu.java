package com.example.deliverywala.model;

public class Menu {
    private final String foodId;
    private final String foodName;
    private final String foodImage;
    private final String Price;
    private final String restaurantName;

    public Menu(String foodId, String foodName, String foodImage, 
               String Price, String restaurantName) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.Price = Price;
        this.restaurantName = restaurantName;
    }

    public String getFoodId() {
        return foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getPrice() {
        return Price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodImage='" + foodImage + '\'' +
                ", Price='" + Price + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        return foodId.equals(menu.foodId) &&
                foodName.equals(menu.foodName) &&
                foodImage.equals(menu.foodImage) &&
                Price.equals(menu.Price) &&
                restaurantName.equals(menu.restaurantName);
    }


}