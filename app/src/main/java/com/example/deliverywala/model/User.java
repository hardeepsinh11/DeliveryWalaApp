package com.example.deliverywala.model;

public class User {
    private String uid;
    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private String firebaseToken;

    public User() {
    }

    public User(String uid, String name, String email, String mobileNumber, 
                String address, String firebaseToken) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.firebaseToken = firebaseToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", address='" + address + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return (uid != null ? uid.equals(user.uid) : user.uid == null) &&
               (name != null ? name.equals(user.name) : user.name == null) &&
               (email != null ? email.equals(user.email) : user.email == null) &&
               (mobileNumber != null ? mobileNumber.equals(user.mobileNumber) : user.mobileNumber == null) &&
               (address != null ? address.equals(user.address) : user.address == null) &&
               (firebaseToken != null ? firebaseToken.equals(user.firebaseToken) : user.firebaseToken == null);
    }


}