package com.tdc.vlxdonline.Model;

public class Users {
    private String email, pass;
    private String type;

    public Users(String email, String pass, String type) {
        this.email = email;
        this.pass = pass;
        this.type = type;
    }

    public Users() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
