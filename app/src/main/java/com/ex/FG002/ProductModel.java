package com.ex.FG002;

import java.io.Serializable;

public class ProductModel implements Serializable {
    private String ProductId;
    private String productName;
    private double productPrice;
    private String productDesciption;
    private String productImage;
    private String ownerId;
    private Boolean syncStatus;
    private Boolean productStatus;
    private Boolean isDeleted;

    private Boolean expanded;

    public Boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public ProductModel(String productId, String productName, double productPrice, String productDesciption, String productImage, String ownerId, Boolean syncStatus, Boolean productStatus, Boolean isDeleted) {
        ProductId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesciption = productDesciption;
        this.productImage = productImage;
        this.ownerId = ownerId;
        this.syncStatus = syncStatus;
        this.productStatus = productStatus;
        this.isDeleted = isDeleted;
        this.expanded = false;
    }

    public Boolean getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Boolean productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
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

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesciption() {
        return productDesciption;
    }

    public void setProductDesciption(String productDesciption) {
        this.productDesciption = productDesciption;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Boolean syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
