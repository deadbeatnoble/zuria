package com.ex.FG002.ProductBased;

public class CustomerProductModel {
    private String ProductId;
    private String productName;
    private double productPrice;
    private String productDesciption;
    private String productImageURL;
    private String ownerId;
    private Boolean productStatus;


    public CustomerProductModel(String productId, String productName, double productPrice, String productDesciption, String productImageURL, String ownerId, Boolean productStatus) {
        this.ProductId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesciption = productDesciption;
        this.productImageURL = productImageURL;
        this.ownerId = ownerId;
        this.productStatus = productStatus;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        this.ProductId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesciption() {
        return productDesciption;
    }

    public void setProductDesciption(String productDesciption) {
        this.productDesciption = productDesciption;
    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Boolean productStatus) {
        this.productStatus = productStatus;
    }
}
