package com.example.foodfast.data.model;

public class Food {
    private String id;
    private String title;
    private int price;
    private int discount;
    private String description;
    private String categoryId;
    private String urlImage;
    private String ingredient;

    public Food() {
    }

    public Food(String id, String title, int price, int discount, String description, String categoryId, String urlImage, String ingredient) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.categoryId = categoryId;
        this.urlImage = urlImage;
        this.ingredient = ingredient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
