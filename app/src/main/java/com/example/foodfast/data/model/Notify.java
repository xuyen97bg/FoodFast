package com.example.foodfast.data.model;

public class Notify {
    private String id;
    private String idAccount;
    private long createAt;
    private String title;
    private int status;

    public Notify() {
    }

    public Notify(String idAccount, long createAt, String title, int status) {
        this.idAccount = idAccount;
        this.createAt = createAt;
        this.title = title;
        this.status = status;
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

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
