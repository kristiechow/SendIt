package com.example.kristie.sendit;

/**
 * Created by Kristie on 11/20/17.
 */

public class SMSObject {
    public String Phone;
    public String SMS;

    public SMSObject() {
    }

    public SMSObject(String Phone, String SMS) {
        this.Phone = Phone;
        this.SMS = SMS;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getSMS() {
        return SMS;
    }

    public void setSMS(String SMS) {
        this.SMS = SMS;
    }
}
