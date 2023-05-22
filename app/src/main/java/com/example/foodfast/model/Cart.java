package com.example.foodfast.model;

public class Cart {
    private String id;
    private String idAccount;
    private String idCartItem;
    private int status; //0-Giỏ hàng 1-Lịch sử mua hàng
    private String address;
    private long createAt;
    private long updateAt;

    public Cart() {
    }

    public Cart(String idAccount, String idCartItem, int status, String address, long createAt, long updateAt) {
        this.idAccount = idAccount;
        this.idCartItem = idCartItem;
        this.status = status;
        this.address = address;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getIdCartItem() {
        return idCartItem;
    }

    public void setIdCartItem(String idCartItem) {
        this.idCartItem = idCartItem;
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
