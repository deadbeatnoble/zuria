package com.ex.FG002;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout srl_refreshRecyclerView;



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

        productAdapter = new ProductAdapter(productList, getContext());
        storeRecyclerView.setAdapter(productAdapter);

        srl_refreshRecyclerView = rootView.findViewById(R.id.srl_refreshRecyclerView);

        fab_createProduct = rootView.findViewById(R.id.fab_createProduct);

        fab_createProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateProduct.class);
                startActivity(intent);
            }
        });
        srl_refreshRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                productList.addAll(generateProductModel());
                // Notify the adapter that the data set has changed
                productAdapter.notifyDataSetChanged();
                // Stop the refreshing animation
                srl_refreshRecyclerView.setRefreshing(false);
            }
        });

        return rootView;
    }

    private List<ProductModel> generateProductModel() {
        List<ProductModel> productModels = new ArrayList<>();
        productModels.addAll(new DBHelper(this.getActivity()).getOwnerProduct(new MyApplication().getOwnerId()));
        return productModels;
    }

}