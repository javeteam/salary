package com.aspect.salary.entity;

public class AppUser {

    private int userId;
    private String login;
    private String password;
    private boolean active;

    public AppUser(int userId, String login, String password, boolean active) {
        this.userId = userId;
        this.login = login;
        this.password = password;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(Character active) {
        this.active = (active == 'Y');
    }
}
