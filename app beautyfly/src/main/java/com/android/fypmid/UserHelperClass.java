package com.android.fypmid;

import android.widget.AutoCompleteTextView;

public class UserHelperClass {
    private String name;
    private String email;
    private String LoginAs;
    private String phoneNo;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String email, String loginAs, String phoneNo) {
        this.name = name;
        this.email = email;
        LoginAs = loginAs;
        this.phoneNo = phoneNo;
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

    public String getLoginAs() {
        return LoginAs;
    }

    public void setLoginAs(String loginAs) {
        LoginAs = loginAs;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
