package com.ex.FG002.OpenSource;

public class Users {
    private String mobileNumber, password;
    private String storeImage;
    private String storeName;
    //private String storeLocation;
    private String storeLatitude;
    private String storeLongitude;
    private boolean shareUserLocation;
    private boolean locationSyncStatus;

    public Users() {
    }

    public Users(String mobileNumber, String password, String storeImage, String storeName, String storeLatitude, String storeLongitude, boolean shareUserLocation, boolean locationSyncStatus) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.storeImage = storeImage;
        this.storeName = storeName;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
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

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /*public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }*/

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
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
