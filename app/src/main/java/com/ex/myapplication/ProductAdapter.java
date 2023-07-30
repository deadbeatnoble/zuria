package com.ex.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDivider;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> productList;

    private Context context;

    public ProductAdapter(List<ProductModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
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
        holder.tv_productDescription.setText(productModel.getProductDesciption());

        Bitmap imageBitmap = BitmapFactory.decodeByteArray(productModel.getProductImage(), 0, productModel.getProductImage().length);
        holder.iv_productImage.setImageBitmap(imageBitmap);


        Boolean isExpanded = productList.get(position).isExpanded();
        holder.tv_seeMore.setVisibility(isExpanded ? View.GONE :View.VISIBLE);
        holder.cl_hiddenLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.d_seeMoreDivider.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        holder.acb_editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int productId = productList.get(position).getProductId();
                Intent intent = new Intent(context, UpdateProduct.class);
                intent.putExtra("PRODUCT_ID", productId);
                context.startActivity(intent);
            }
        });
        holder.acb_removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHelper dbHelper = new DBHelper(context);
                dbHelper.deleteProduct(productModel);
                productList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView tv_productName;
        TextView tv_productPrice;
        TextView tv_productDescription;
        ImageView iv_productImage;

        ConstraintLayout cl_hiddenLayout;
        TextView tv_seeMore;
        TextView tv_seeLess;
        MaterialDivider d_seeMoreDivider;
        AppCompatButton acb_removeProduct;
        AppCompatButton acb_editProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);
            tv_productDescription = itemView.findViewById(R.id.tv_productDescription);
            iv_productImage = itemView.findViewById(R.id.iv_productImage);

            cl_hiddenLayout = itemView.findViewById(R.id.cl_hiddenLayout);
            tv_seeMore = itemView.findViewById(R.id.tv_seeMore);
            tv_seeLess = itemView.findViewById(R.id.tv_seeLess);
            d_seeMoreDivider = itemView.findViewById(R.id.d_seeMoreDivider);

            acb_removeProduct = itemView.findViewById(R.id.acb_removeProduct);
            acb_editProduct = itemView.findViewById(R.id.acb_editProduct);

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
