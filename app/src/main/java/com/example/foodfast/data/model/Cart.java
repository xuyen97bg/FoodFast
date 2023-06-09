package com.example.foodfast.data.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String id;
    private long createAt;
    private long updateAt;
    private int status;
    private String address="";
    private int total;

    private List<CartItem> cartItems = new ArrayList<>();

    public Cart() {
    }
    public Cart(String id,long createAt, long updateAt) {
        this.id = id;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Cart(String id, long createAt, long updateAt, List<CartItem> cartItems) {
        this.id = id;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.cartItems.clear();
        this.cartItems.addAll(cartItems);
    }

    public Cart(String id, long createAt, long updateAt, int status, String address, int total, List<CartItem> cartItems) {
        this.id = id;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.status = status;
        this.address = address;
        this.total = total;
        this.cartItems.clear();
        this.cartItems.addAll(cartItems);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(cartItems);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
