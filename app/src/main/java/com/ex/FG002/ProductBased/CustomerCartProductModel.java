package com.ex.FG002.ProductBased;

public class CustomerCartProductModel {
    private String productOwnerId;
    private String productId;
    private String productName;
    private int productAmount;
    private double productPrice;
    private String productImageURL;

    public CustomerCartProductModel(String productOwnerId, String productId, String productName, int productAmount, double productPrice, String productImageURL) {
        this.productOwnerId = productOwnerId;
        this.productId = productId;
        this.productName = productName;
        this.productAmount = productAmount;
        this.productPrice = productPrice;
        this.productImageURL = productImageURL;
    }

    public String getProductOwnerId() {
        return productOwnerId;
    }

    public void setProductOwnerId(String productOwnerId) {
        this.productOwnerId = productOwnerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }
}