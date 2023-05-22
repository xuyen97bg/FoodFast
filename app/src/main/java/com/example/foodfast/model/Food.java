package com.example.foodfast.model;

public class Food {
    private String id;
    private String title;
    private int price;
    private int discount;
    private String description;
    private int category;
    private String urlImage;

    public Food() {
    }

    public Food(String id, String title, int price, int discount, String description, int category, String urlImage) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.category = category;
        this.urlImage = urlImage;
    }

    public Food(String title, int price, int discount, String description, int category, String urlImage) {
        this.title = title;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.category = category;
        this.urlImage = urlImage;
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
