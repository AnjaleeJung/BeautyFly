package com.android.fypmid;

import java.util.ArrayList;
import java.util.HashMap;

public class BusinessService {
    private String serviceTitle;
    private String serviceCharges;
    private String serviceRatings;
    private String waitingTime;
    private String image;
    private String pushKey;
    private String gender;
    private String uId;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BusinessService() {
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(String serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public String getServiceRatings() {
        return serviceRatings;
    }

    public void setServiceRatings(String serviceRatings) {
        this.serviceRatings = serviceRatings;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }

}
