package com.example.foodfast.model;

public class Account {
    private String id;
    private String username;
    private String password;
    private String fistName;
    private String lastName;
    private long birtday;
    private String phoneNumber;
    private String address;
    private int role; //0-Nữ 1-Nam

    public Account() {
    }

    public Account(String username, String password, String fistName, String lastName, long birtday, String phoneNumber, String address, int role) {
        this.username = username;
        this.password = password;
        this.fistName = fistName;
        this.lastName = lastName;
        this.birtday = birtday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFistName() {
        return fistName;
    }

    public void setFistName(String fistName) {
        this.fistName = fistName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBirtday() {
        return birtday;
    }

    public void setBirtday(long birtday) {
        this.birtday = birtday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
