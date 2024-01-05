package com.ex.FG002.ProductBased;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.FG002.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.MyViewHolder>{

    List<CustomerProductModel> customerProductModels = new ArrayList<CustomerProductModel>();
    /*private OnCustomerSelectedProductListener onCustomerSelectedProductListener;
    List<String> customerSelectedProducts = new ArrayList<String>();*/
    List<String> sharedPreferenceSelectedProductsIdsAndAmounts = new ArrayList<>();
    List<String> newSharedPreferenceSelectedProductsIdsAndAmounts = new ArrayList<>();
    List<String> _sharedPreferenceSelectedProducts = new ArrayList<>();
    Context context;


    public static final String SHARED_PREFRS = "productsInCart";
    public static String TEXT = "storeProducts";

    public CustomerProductAdapter(Context context, List<CustomerProductModel> customerProductModels) {
        this.context = context;
        this.customerProductModels = customerProductModels;
        //this.onCustomerSelectedProductListener = (OnCustomerSelectedProductListener) context;
    }

    /*public interface OnCustomerSelectedProductListener {
        void onCustomerSelectedProductListener(List<String> selectedProducts);
    }

    public void setOnCustomerSelectedProductListener(OnCustomerSelectedProductListener listener) {
        this.onCustomerSelectedProductListener = listener;
    }*/

    @NonNull
    @Override
    public CustomerProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_customer_product_design, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductAdapter.MyViewHolder holder, int position) {
        CustomerProductModel customerProductModel = customerProductModels.get(position);
        holder.tv_productName.setText(customerProductModel.getProductName());
        holder.tv_productPrice.setText(String.valueOf(customerProductModel.getProductPrice()));
        holder.s_productStatus.setChecked(customerProductModel.getProductStatus());


        //holder.iv_productImage.setText(customerProductModel.getProductImage());
        //Picasso.get().load(Uri.parse(productModel.getProductImage())).into(holder.iv_productImage);

        Picasso.get().load(customerProductModel.getProductImageURL()).into(holder.iv_productImage);

        holder.ll_selectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TEXT = customerProductModel.getOwnerId();

                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFRS, Context.MODE_PRIVATE);
                String sharedPreferencesSelectedProductStringForm = sharedPreferences.getString(TEXT, "");

                if (sharedPreferencesSelectedProductStringForm.isEmpty()) {
                    sharedPreferenceSelectedProductsIdsAndAmounts = new ArrayList<>();
                } else {
                    sharedPreferenceSelectedProductsIdsAndAmounts = new ArrayList<>(Arrays.asList(sharedPreferencesSelectedProductStringForm.split(",")));
                }

                _sharedPreferenceSelectedProducts.clear();

                for (int i = 0; i < sharedPreferenceSelectedProductsIdsAndAmounts.size(); i++) {
                    String[] productIdAndAmount = sharedPreferenceSelectedProductsIdsAndAmounts.get(i).split(":");
                    String productId = productIdAndAmount[0];
                    String productAmount = productIdAndAmount[1];

                    _sharedPreferenceSelectedProducts.add(productId + ":" + productAmount);
                }

                // Check if the clicked product is already in the list
                String clickedProductId = customerProductModel.getProductId();
                boolean isProductAlreadySelected = false;

                for (int i = 0; i < _sharedPreferenceSelectedProducts.size(); i++) {
                    String[] productIdAndAmount = _sharedPreferenceSelectedProducts.get(i).split(":");
                    String productId = productIdAndAmount[0];

                    if (productId.equals(clickedProductId)) {
                        isProductAlreadySelected = true;
                        break;
                    }
                }

                // If the product is already selected, remove it from the list
                if (isProductAlreadySelected) {
                    for (int i = 0; i < _sharedPreferenceSelectedProducts.size(); i++) {
                        String[] productIdAndAmount = _sharedPreferenceSelectedProducts.get(i).split(":");
                        String productId = productIdAndAmount[0];

                        if (productId.equals(clickedProductId)) {
                            _sharedPreferenceSelectedProducts.remove(i);
                            break;
                        }
                    }
                } else {
                    // If the product is not selected, add it to the list with a default amount of 1
                    _sharedPreferenceSelectedProducts.add(clickedProductId + ":1");
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(TEXT);

                if (!_sharedPreferenceSelectedProducts.isEmpty()) {
                    String newSharedPreferencesSelectedProductStringForm = TextUtils.join(",", _sharedPreferenceSelectedProducts);
                    editor.putString(TEXT, newSharedPreferencesSelectedProductStringForm);
                }

                editor.apply();

                Toast.makeText(context, sharedPreferences.getString(TEXT, ""), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerProductModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Switch s_productStatus;
        TextView tv_productName;
        TextView tv_productPrice;
        //TextView tv_productDescription;
        ImageView iv_productImage;
        LinearLayout ll_selectProduct;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            s_productStatus = itemView.findViewById(R.id.s_productStatus);
            tv_productName = itemView.findViewById(R.id.tv_productName);
            tv_productPrice = itemView.findViewById(R.id.tv_productPrice);
            iv_productImage = itemView.findViewById(R.id.iv_productImage);
            ll_selectProduct = itemView.findViewById(R.id.ll_selectProduct);
        }
    }
}
