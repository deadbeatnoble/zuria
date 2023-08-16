package com.ex.FG002;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.divider.MaterialDivider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        holder.s_productStatus.setOnCheckedChangeListener(null);
        ProductModel productModel = productList.get(position);
        holder.tv_productName.setText(productModel.getProductName());
        holder.tv_productPrice.setText(String.valueOf(productModel.getProductPrice()));
        holder.tv_productDescription.setText(productModel.getProductDesciption());
        holder.s_productStatus.setChecked(productModel.getProductStatus());

        //Bitmap imageBitmap = BitmapFactory.decodeByteArray(productModel.getProductImage(), 0, productModel.getProductImage().length);
        //holder.iv_productImage.setImageBitmap(imageBitmap);

        Picasso.get().load(Uri.parse(productModel.getProductImage())).into(holder.iv_productImage);


        Boolean isExpanded = productList.get(position).isExpanded();
        holder.tv_seeMore.setVisibility(isExpanded ? View.GONE :View.VISIBLE);
        holder.cl_hiddenLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.d_seeMoreDivider.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean switchState = sharedPreferences.getBoolean("switchState_" + productModel.getProductId(), productList.get(position).getProductStatus());
        holder.s_productStatus.setChecked(switchState);
        holder.s_productStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (compoundButton.isPressed()) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("switchState_" + productList.get(position).getProductId(), isChecked);
                    editor.apply();
                    if (new DBHelper(context).updateSwitchState(productList.get(position).getProductId(), isChecked)) {
                        if (NetworkChangeListener.syncStatus) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/" + productList.get(position).getOwnerId() + "/" + productList.get(position).getProductId());
                            Map<String, Object> updatedData = new HashMap<>();
                            updatedData.put("productStatus", isChecked);

                            databaseReference.updateChildren(updatedData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            new DBHelper(context).updateProductSyncStatus(productList.get(position).getProductId(), true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            new DBHelper(context).updateProductSyncStatus(productList.get(position).getProductId(), false);
                                        }
                                    });
                        } else {
                            new DBHelper(context).updateProductSyncStatus(productList.get(position).getProductId(), false);
                        }
                    } else {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                    if (isChecked) {
                        Toast.makeText(context, productList.get(position).getProductName() + " is open for order", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, productList.get(position).getProductName() + " is out of stock", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    boolean previousSwitchState = sharedPreferences.getBoolean("switchState_" + productList.get(position).getProductId(), productList.get(position).getProductStatus());
                    holder.s_productStatus.setChecked(previousSwitchState);
                }


            }
        });
        holder.acb_editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productId = productList.get(position).getProductId();
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
        Switch s_productStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);
            tv_productDescription = itemView.findViewById(R.id.tv_productDescription);
            iv_productImage = itemView.findViewById(R.id.iv_productImage);
            s_productStatus = itemView.findViewById(R.id.s_productStatus);

            cl_hiddenLayout = itemView.findViewById(R.id.cl_hiddenLayout);
            tv_seeMore = itemView.findViewById(R.id.tv_seeMore);
            tv_seeLess = itemView.findViewById(R.id.tv_seeLess);
            d_seeMoreDivider = itemView.findViewById(R.id.d_seeMoreDivider);

            acb_removeProduct = itemView.findViewById(R.id.acb_removeProduct);
            acb_editProduct = itemView.findViewById(R.id.acb_editProduct);

            /*s_productStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(context, "Product Available", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
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
