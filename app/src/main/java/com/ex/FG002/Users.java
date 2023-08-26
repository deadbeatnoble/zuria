package com.ex.FG002;

public class Users {
    private String mobileNumber, password;
    private String latitude;
    private String longitude;
    private boolean shareUserLocation;
    private boolean locationSyncStatus;

    public Users() {
    }

    public Users(String mobileNumber, String password, String latitude, String longitude, boolean shareUserLocation, boolean locationSyncStatus) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.shareUserLocation = shareUserLocation;
        this.locationSyncStatus = locationSyncStatus;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isShareUserLocation() {
        return shareUserLocation;
    }

    public void setShareUserLocation(boolean shareUserLocation) {
        this.shareUserLocation = shareUserLocation;
    }

    public boolean isLocationSyncStatus() {
        return locationSyncStatus;
    }

    public void setLocationSyncStatus(boolean locationSyncStatus) {
        this.locationSyncStatus = locationSyncStatus;
    }
}
