package com.example.foodfast.data.model;

public class Account {
    private String id;
    private String username;
    private String password;
    private String fistName;
    private String lastName;
    private long birthday;
    private String phoneNumber;
    private String address;
    private int gender;//0-Nữ 1-Nam
    private int role; //0-customer, 1-owner

    private String imageUrl;

    public Account() {
    }


    public Account(String id, String username, String password, String fistName, String lastName, long birthday, String phoneNumber, String address, int gender, int role, String imageUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fistName = fistName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public Account(String username, String password, String fistName, String lastName, long birthday, String phoneNumber, String address, int gender, int role, String imageUrl) {
        this.username = username;
        this.password = password;
        this.fistName = fistName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public Account(String username, String password, String fistName, String lastName, long birthday,
                   String phoneNumber, String address, int role) {
        this.username = username;
        this.password = password;
        this.fistName = fistName;
        this.lastName = lastName;
        this.birthday = birthday;
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

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
