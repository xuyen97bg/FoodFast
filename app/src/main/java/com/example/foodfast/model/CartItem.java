package com.example.foodfast.model;

public class CartItem {
    private String id;
    private String idFood;
    private int amountBuy;
    private int price;
    private long createAt;
    private long updateAt;

    public CartItem() {
    }

    public CartItem( String idFood, int amountBuy, int price, long createAt, long updateAt) {
        this.idFood = idFood;
        this.amountBuy = amountBuy;
        this.price = price;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdFood() {
        return idFood;
    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
    }

    public int getAmountBuy() {
        return amountBuy;
    }

    public void setAmountBuy(int amountBuy) {
        this.amountBuy = amountBuy;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }
}
