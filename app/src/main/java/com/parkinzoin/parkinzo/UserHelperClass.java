package com.parkinzoin.parkinzo;

public class UserHelperClass {

    String user,email,mobileNo,
            city,password;

    public UserHelperClass() {
    }

    public UserHelperClass(String user, String email, String mobileNo, String city, String password) {
        this.user = user;
        this.email = email;
        this.mobileNo = mobileNo;
        this.city = city;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
