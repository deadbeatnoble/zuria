package com.ex.myapplication;

public class ProductModel {
    private String productName;
    private double productPrice;

    private Boolean expanded;

    public Boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public ProductModel(String productName, double productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.expanded = false;
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
}
