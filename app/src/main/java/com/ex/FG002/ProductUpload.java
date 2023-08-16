package com.ex.FG002;

public class ProductUpload {
    private String ProductId;
    private String productName;
    private double productPrice;
    private String productDesciption;
    private String productImage;
    private String ownerId;
    private boolean productStatus;
    //private Boolean expanded;


    public ProductUpload() {
    }

    public ProductUpload(String productId, String productName, double productPrice, String productDesciption, String productImage, String ownerId, boolean productStatus) {
        ProductId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesciption = productDesciption;
        this.productImage = productImage;
        this.ownerId = ownerId;
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

    public void setProductPrice(double productPrice) {
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

    public boolean getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(boolean productStatus) {
        this.productStatus = productStatus;
    }
}
