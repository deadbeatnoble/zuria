package com.ex.FG002.ProductBased;

public class CustomerCartStoreModel {
    private String storeId;
    private String storeName;
    private Double totalPrice;
    private String storeImageURL;

    public CustomerCartStoreModel(String storeId, String storeName, Double totalPrice, String storeImageURL) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.totalPrice = totalPrice;
        this.storeImageURL = storeImageURL;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStoreImageURL() {
        return storeImageURL;
    }

    public void setStoreImageURL(String storeImageURL) {
        this.storeImageURL = storeImageURL;
    }
}
