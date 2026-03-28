package com.example.deliverywala.model;

public class Restaurants {
    private String foodId;
    private String foodName;
    private String foodAmount;
    private String foodDescription;
    private String foodImage;

    public Restaurants(String foodId, String foodName, String foodAmount, 
                      String foodDescription, String foodImage) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.foodDescription = foodDescription;
        this.foodImage = foodImage;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(String foodAmount) {
        this.foodAmount = foodAmount;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    @Override
    public String toString() {
        return "Restaurants{" +
                "foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodAmount='" + foodAmount + '\'' +
                ", foodDescription='" + foodDescription + '\'' +
                ", foodImage='" + foodImage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurants that = (Restaurants) o;
        return foodId.equals(that.foodId) &&
                foodName.equals(that.foodName) &&
                foodAmount.equals(that.foodAmount) &&
                foodDescription.equals(that.foodDescription) &&
                foodImage.equals(that.foodImage);
    }


}