package com.ex.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> productList;

    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
    }

    private LinearLayout ll_visibleLayout;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_product_design,parent,false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel productModel = productList.get(position);
        holder.tv_productName.setText(productModel.getProductName());
        holder.tv_productPrice.setText(String.valueOf(productModel.getProductPrice()));

        Boolean isExpanded = productList.get(position).isExpanded();
        holder.cl_hiddenLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView tv_productName;
        TextView tv_productPrice;

        ConstraintLayout cl_hiddenLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);

            cl_hiddenLayout = itemView.findViewById(R.id.cl_hiddenLayout);
            ll_visibleLayout = itemView.findViewById(R.id.ll_visibleLayout);

            ll_visibleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductModel productModel = productList.get(getAdapterPosition());
                    productModel.setExpanded(!productModel.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }
}
