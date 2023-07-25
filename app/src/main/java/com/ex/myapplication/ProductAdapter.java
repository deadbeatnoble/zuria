package com.ex.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDivider;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> productList;

    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
    }

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
        holder.tv_seeMore.setVisibility(isExpanded ? View.GONE :View.VISIBLE);
        holder.cl_hiddenLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.d_seeMoreDivider.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView tv_productName;
        TextView tv_productPrice;

        ConstraintLayout cl_hiddenLayout;
        TextView tv_seeMore;
        TextView tv_seeLess;
        MaterialDivider d_seeMoreDivider;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);

            cl_hiddenLayout = itemView.findViewById(R.id.cl_hiddenLayout);
            tv_seeMore = itemView.findViewById(R.id.tv_seeMore);
            tv_seeLess = itemView.findViewById(R.id.tv_seeLess);
            d_seeMoreDivider = itemView.findViewById(R.id.d_seeMoreDivider);

            tv_seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductModel productModel = productList.get(getAdapterPosition());
                    productModel.setExpanded(!productModel.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            tv_seeLess.setOnClickListener(new View.OnClickListener() {
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
