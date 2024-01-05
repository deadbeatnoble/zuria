package com.ex.FG002.CustomerBased;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ex.FG002.OpenSource.MyApplication;
import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.ProductBased.CustomerProductAdapter;
import com.ex.FG002.ProductBased.CustomerProductModel;
import com.ex.FG002.ProductBased.ProductModel;
import com.ex.FG002.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerBasedVendorStoreView extends AppCompatActivity {

    List<CustomerProductModel> customerProducts = new ArrayList<CustomerProductModel>();
    //List<String> selectedProducts = new ArrayList<>();
    //List<String> sharedPreferenceSelectedProducts = new ArrayList<>();
    private ImageView iv_storeImage;
    private RecyclerView customerProductRecyclerView;
    private RecyclerView.Adapter customerProductAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;

    //public static final String SHARED_PREFRS = "productsInCart";
    //public static final String TEXT = "No data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_based_vendor_store_view);


        iv_storeImage = findViewById(R.id.iv_storeView);
        Picasso.get().load(getIntent().getStringExtra("storeImage")).into(iv_storeImage);

        customerProductRecyclerView = findViewById(R.id.rv_storeProductView);
        customerProductRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        customerProductRecyclerView.setLayoutManager(layoutManager);

        customerProductAdapter = new CustomerProductAdapter(this, customerProducts);
        //customerProductAdapter.setOnCustomerSelectedProductListener(this);
        customerProductRecyclerView.setAdapter(customerProductAdapter);

        Intent intent = getIntent();
        String selectedStoreId = intent.getStringExtra("mobileNumber");


        databaseReference = FirebaseDatabase.getInstance().getReference().child("products/" + selectedStoreId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        CustomerProductModel customerProductModel = new CustomerProductModel(
                                String.valueOf(dataSnapshot.child("productId").getValue()),
                                String.valueOf(dataSnapshot.child("productName").getValue()),
                                Double.parseDouble(String.valueOf(dataSnapshot.child("productPrice").getValue())),
                                String.valueOf(dataSnapshot.child("productDesciption").getValue()),
                                String.valueOf(dataSnapshot.child("productImage").getValue()),
                                String.valueOf(dataSnapshot.child("ownerId").getValue()),
                                String.valueOf(dataSnapshot.child("productStatus").getValue()).equals("true") ? true : false
                        );

                        customerProducts.add(customerProductModel);
                    }
                customerProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerBasedVendorStoreView.this, "No Product Available", Toast.LENGTH_LONG).show();
            }
        });


        /*SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRS, MODE_PRIVATE);

        String sharedPreferenceProductsStringForm = sharedPreferences.getString(TEXT, "");
        sharedPreferenceProducts = Arrays.asList(sharedPreferenceProductsStringForm);

        Toast.makeText(this, sharedPreferenceProductsStringForm, Toast.LENGTH_SHORT).show();*/

    }

    /*@Override
    public void onCustomerSelectedProductListener(List<String> selectedProductss) {
        this.selectedProducts = selectedProductss;

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFRS, MODE_PRIVATE);

        String sharedPreferencesSelectedProductStringForm = sharedPreferences.getString(TEXT, "");
        sharedPreferenceSelectedProducts = Arrays.asList(sharedPreferencesSelectedProductStringForm);

        if (sharedPreferencesSelectedProductStringForm.contains(selectedProductss)) {

        }


        //SharedPreferences.Editor editor = sharedPreferences.edit();

        //String sharedPreferenceProductsStringForm = sharedPreferences.getString(TEXT, "");
        //sharedPreferenceSelectedProducts = Arrays.asList(sharedPreferenceProductsStringForm);

        //Toast.makeText(this, sharedPreferenceProductsStringForm, Toast.LENGTH_SHORT).show();

        //editor.clear();
        //editor.putString(TEXT, selectedProductss.toString());
        //editor.apply();




        //Toast.makeText(getApplicationContext(), String.valueOf(selectedProductss.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), selectedProductss.toString(), Toast.LENGTH_LONG).show();
    }*/
}