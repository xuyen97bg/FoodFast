package com.example.foodfast.data.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String id;
    private long createAt;
    private long updateAt;

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
        this.cartItems = cartItems;
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
}
