package com.himanshu.aicte.common.user;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class User {

    public static final String TYPE_ADMIN = "Admin";
    public static final String TYPE_USER = "Regular User";

    private String firstName, lastName, email, gender, phone, password;
    private String type;

    private List<String> savedNews;

    public User(){
        // required for Firestore.
    }

    public User(String firstName, String lastName, String email, String gender, String phone, String password, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.password = password;
        this.type = type;
        this.savedNews = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSavedNews() {
        return savedNews;
    }

    public void setSavedNews(List<String> savedNews) {
        this.savedNews = savedNews;
    }
}
