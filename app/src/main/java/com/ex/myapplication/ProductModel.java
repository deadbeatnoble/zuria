package com.ex.myapplication;

import java.io.Serializable;
import java.sql.Blob;

public class ProductModel implements Serializable {
    private int ProductId;
    private String productName;
    private double productPrice;
    private String productDesciption;
    private byte[] productImage;
    private String ownerId;

    private Boolean expanded;

    public Boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public ProductModel(int productId, String productName, double productPrice, String productDesciption, byte[] productImage,String ownerId) {
        ProductId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesciption = productDesciption;
        this.productImage = productImage;
        this.ownerId = ownerId;
        this.expanded = false;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
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

    public byte[] getProductImage() {
        return productImage;
    }

    public void setProductImage(byte[] productImage) {
        this.productImage = productImage;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
