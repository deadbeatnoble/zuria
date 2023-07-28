package com.ex.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class CreateProduct extends AppCompatActivity {

    private TextInputLayout til_productName;
    private TextInputLayout til_productPrice;
    private TextInputLayout til_productDescription;

    Button btn_addProduct;
    Button btn_discardProduct;

    DBHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        til_productName = findViewById(R.id.til_productName);
        //til_productName.getEditText().getText();
        til_productPrice = findViewById(R.id.til_productPrice);
        til_productDescription = findViewById(R.id.til_productDescription);


        btn_addProduct = findViewById(R.id.btn_addProduct);
        btn_discardProduct = findViewById(R.id.btn_discardProduct);

        dbHelper = new DBHelper(CreateProduct.this);



        btn_addProduct.setOnClickListener(new View.OnClickListener() {
            ProductModel productModel;

            @Override
            public void onClick(View view) {
                try {
                    productModel = new ProductModel(1, til_productName.getEditText().getText().toString(), Integer.parseInt(til_productPrice.getEditText().getText().toString()), til_productDescription.getEditText().getText().toString(), new MyApplication().getOwnerId());
                    Toast.makeText(CreateProduct.this, productModel.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(CreateProduct.this, "Faild to Add", Toast.LENGTH_SHORT).show();
                    ProductModel productModel = new ProductModel(-1, "error", 0, "error occured", "error");
                }

                DBHelper databaseHelper = new DBHelper(CreateProduct.this);
                Boolean success = databaseHelper.addProduct(productModel);

                Toast.makeText(CreateProduct.this, "success = " + success, Toast.LENGTH_SHORT).show();
            }
        });
    }

}