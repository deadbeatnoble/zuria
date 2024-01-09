package com.ex.FG002.ProductBased;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.FG002.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerCartStoreAdapter extends RecyclerView.Adapter<CustomerCartStoreAdapter.MyViewHolder> {

    private List<CustomerCartStoreModel> customerCartStoreModelList = new ArrayList<>();
    private List<CustomerCartProductModel> customerCartProductModelList = new ArrayList<>();
    private List<CustomerCartProductModel> FILTEREDCustomerCartProductModelList = new ArrayList<>();
    Double totalPrice = 0.0;
    Context context;

    public CustomerCartStoreAdapter(List<CustomerCartStoreModel> customerCartStoreModelList, List<CustomerCartProductModel> customerCartProductModelList, Context context) {
        this.customerCartStoreModelList = customerCartStoreModelList;
        this.customerCartProductModelList = customerCartProductModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_customer_store_design, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CustomerCartStoreModel customerCartStoreModel = customerCartStoreModelList.get(position);
        holder.tv_storeName.setText(customerCartStoreModel.getStoreName());
        //holder.tv_totalPrice.setText(String.valueOf(customerCartStoreModel.getTotalPrice()));

        Picasso.get().load(customerCartStoreModel.getStoreImageURL()).into(holder.civ_storeImage);

        FILTEREDCustomerCartProductModelList.clear();

        for (CustomerCartProductModel customerCartProductModel : customerCartProductModelList) {
            if (customerCartProductModel.getProductOwnerId().equals(customerCartStoreModel.getStoreId())) {
                FILTEREDCustomerCartProductModelList.add(customerCartProductModel);
                totalPrice += customerCartProductModel.getProductPrice()*customerCartProductModel.getProductAmount();
            }
        }

        holder.tv_totalPrice.setText(String.valueOf(totalPrice));

        totalPrice = 0.0;

        CustomerCartProductAdapter customerCartProductAdapter = new CustomerCartProductAdapter(FILTEREDCustomerCartProductModelList, context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.rv_customerCartProduct.setLayoutManager(linearLayoutManager);
        holder.rv_customerCartProduct.setAdapter(customerCartProductAdapter);


    }

    @Override
    public int getItemCount() {
        return customerCartStoreModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civ_storeImage;
        TextView tv_storeName;
        TextView tv_totalPrice;
        RecyclerView rv_customerCartProduct;
        Button b_checkout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_storeImage = itemView.findViewById(R.id.civ_storeImage);
            tv_storeName = itemView.findViewById(R.id.tv_storeName);
            tv_totalPrice = itemView.findViewById(R.id.tv_totalPrice);
            rv_customerCartProduct = itemView.findViewById(R.id.rv_customerCartProduct);
            b_checkout = itemView.findViewById(R.id.b_checkout);
        }
    }
}
