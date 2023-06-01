package com.example.foodfast.data.model;

public class CartItem {
    private String idFood;
    private int amountBuy;
    private int price;

    public CartItem() {
    }

    public CartItem( String idFood, int amountBuy, int price) {
        this.idFood = idFood;
        this.amountBuy = amountBuy;
        this.price = price;
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

    public void minus(){
        amountBuy--;
    }

    public void plus(){
        amountBuy++;
    }

}
