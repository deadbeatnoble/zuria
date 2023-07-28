package com.ex.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class UpdateProduct extends AppCompatActivity {
    private TextInputLayout til_productName;
    private TextInputLayout til_productPrice;
    private TextInputLayout til_productDescription;
    private Button btn_updateProduct;



    int productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        til_productName = findViewById(R.id.til_productName);
        //til_productName.getEditText().getText();
        til_productPrice = findViewById(R.id.til_productPrice);
        til_productDescription = findViewById(R.id.til_productDescription);

        btn_updateProduct = findViewById(R.id.btn_updateProduct);

        ProductModel productModel = (ProductModel) getIntent().getExtras().getSerializable("PRODUCT");


        productId = productModel.getProductId();
        til_productName.getEditText().setText(productModel.getProductName());
        til_productPrice.getEditText().setText(String.valueOf(productModel.getProductPrice()));
        til_productDescription.getEditText().setText(productModel.getProductDesciption());


        btn_updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(view);
            }
        });


    }

    public void update(View view) {
        String productName = til_productName.getEditText().getText().toString();
        String productPrice = til_productPrice.getEditText().getText().toString();
        String productDescription = til_productDescription.getEditText().getText().toString();

        ProductModel productModel = new ProductModel(productId, productName, Double.parseDouble(productPrice), productDescription, new MyApplication().getOwnerId());

        DBHelper dbHelper = new DBHelper(this);

        Boolean success = dbHelper.updateProduct(productModel);

        Toast.makeText(UpdateProduct.this, "success = " + success, Toast.LENGTH_SHORT).show();
    }
}