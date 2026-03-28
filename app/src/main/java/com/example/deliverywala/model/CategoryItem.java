package com.example.deliverywala.model;

public class CategoryItem {
    private String categoryName;
    private int categoryImageRes;

    public CategoryItem(String categoryName, int categoryImageRes) {
        this.categoryName = categoryName;
        this.categoryImageRes = categoryImageRes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryImageRes() {
        return categoryImageRes;
    }
}
