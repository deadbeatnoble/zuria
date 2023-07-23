package com.ex.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;




public class StoreFragment extends Fragment {


    private FloatingActionButton fab_createProduct;
    //add fab_createProduct = getActivity().findViewById(R.id.fab_createProduct);





    private List<ProductModel> productList;
    private RecyclerView storeRecyclerView;
    private ProductAdapter productAdapter;



    public StoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_store, container, false);
        productList = generateProductModel();

        storeRecyclerView = rootView.findViewById(R.id.rv_product);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productAdapter = new ProductAdapter(productList);
        storeRecyclerView.setAdapter(productAdapter);

        return rootView;
    }

    private List<ProductModel> generateProductModel() {
        List<ProductModel> productModels = new ArrayList<>();
        productModels.add(new ProductModel("Special Erteb", 60.50));
        productModels.add(new ProductModel("Fasting Erteb", 35.00));
        productModels.add(new ProductModel("Normal Erteb", 40.50));
        productModels.add(new ProductModel("Erteb with Ketchup", 45.50));
        productModels.add(new ProductModel("Erteb without Felafillllllllllllll", 50.50));

        return productModels;
    }

}