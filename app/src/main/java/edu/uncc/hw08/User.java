package edu.uncc.hw08;

import java.io.Serializable;

public class User implements Serializable {
    String name, email, userId;
    Boolean isOnline;

    public User() {
    }

    public User(String name, String email, String userId,Boolean isOnline) {
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
