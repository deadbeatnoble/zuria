package com.ex.FG002.ProductBased;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.FG002.R;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerCartProductAdapter extends RecyclerView.Adapter<CustomerCartProductAdapter.MyViewHolder> {

    private List<CustomerCartProductModel> customerCartProductModelList = new ArrayList<>();
    Context context;

    List<String> sharedPreferenceSelectedProductsIdsAndAmounts = new ArrayList<>();
    List<String> _sharedPreferenceSelectedProducts = new ArrayList<>();

    public static final String SHARED_PREFRS = "productsInCart";
    public static String TEXT = "storeProducts";

    public CustomerCartProductAdapter(List<CustomerCartProductModel> customerCartProductModelList, Context context) {
        this.customerCartProductModelList = customerCartProductModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerCartProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_cart_product_design, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerCartProductAdapter.MyViewHolder holder, int position) {
        CustomerCartProductModel customerCartProductModel = customerCartProductModelList.get(position);
        holder.tv_cartProductName.setText(customerCartProductModel.getProductName());
        holder.tv_cartProductPrice.setText(String.valueOf(customerCartProductModel.getProductPrice()));
        holder.tv_cartProductAmount.setText(String.valueOf(customerCartProductModel.getProductAmount()));

        Picasso.get().load(customerCartProductModel.getProductImageURL()).into(holder.civ_cartProductImage);

        holder.ib_reduceAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newProductAmount = Integer.parseInt(holder.tv_cartProductAmount.getText().toString()) - 1;
                String productOwnerId = customerCartProductModel.getProductOwnerId();
                String productId = customerCartProductModel.getProductId();

                SharedPreferences sharedPreferences = context.getSharedPreferences("productsInCart", Context.MODE_PRIVATE);
                String sharedPreferencesProductIdsAndNames = sharedPreferences.getString(productOwnerId, "");
                List<String> sharedPreferencesProductIdsAndNamesArrayForm = new ArrayList<>(Arrays.asList(sharedPreferencesProductIdsAndNames.split(",")));

                for (int i = 0; i < sharedPreferencesProductIdsAndNamesArrayForm.size(); i++) {
                    String[] productInfo = sharedPreferencesProductIdsAndNamesArrayForm.get(i).split(":");
                    String currentProductId = productInfo[0];
                    int currentProductAmount = Integer.parseInt(productInfo[1]);

                    if (productId.equals(currentProductId)) {
                        currentProductAmount--;

                        if (currentProductAmount <= 0) {
                            sharedPreferencesProductIdsAndNamesArrayForm.remove(i);
                        } else {
                            sharedPreferencesProductIdsAndNamesArrayForm.set(i, currentProductId + ":" + currentProductAmount);
                        }

                        break; // Exit the loop after finding the matching product
                    }
                }

                if (sharedPreferencesProductIdsAndNamesArrayForm.isEmpty()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(customerCartProductModel.getProductOwnerId());
                    editor.apply();
                } else {
                    String newSharedPreferencesProductIdsAndNames = TextUtils.join(",", sharedPreferencesProductIdsAndNamesArrayForm);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(productOwnerId, newSharedPreferencesProductIdsAndNames);
                    editor.apply();
                }


                holder.tv_cartProductAmount.setText(String.valueOf(newProductAmount));

                if (newProductAmount <= 0) {
                    // Remove the item from the list if the amount reaches 0 or below
                    customerCartProductModelList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            }
        });

        holder.ib_addAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newProductAmount = Integer.parseInt(holder.tv_cartProductAmount.getText().toString()) + 1;

                SharedPreferences sharedPreferences = context.getSharedPreferences("productsInCart", context.MODE_PRIVATE);

                String sharedPreferencesProductIdsAndNames = sharedPreferences.getString(customerCartProductModel.getProductOwnerId(), "");
                List<String> sharedPreferencesProductIdsAndNamesArrayForm = new ArrayList<>(Arrays.asList(sharedPreferencesProductIdsAndNames.split(",")));

                for (int i = 0; i < sharedPreferencesProductIdsAndNamesArrayForm.size(); i++) {
                    String productId = sharedPreferencesProductIdsAndNamesArrayForm.get(i).split(":")[0];
                    Integer productAmount = Integer.parseInt(sharedPreferencesProductIdsAndNamesArrayForm.get(i).split(":")[1]);

                    if (customerCartProductModel.getProductId().equals(productId)) {
                        productAmount++;
                    }

                    //Toast.makeText(context, productId + ":" + productAmount, Toast.LENGTH_SHORT).show();
                    sharedPreferencesProductIdsAndNamesArrayForm.set(i, productId + ":" + productAmount);

                }

                String newSharedPreferencesProductIdsAndNames = TextUtils.join(",", sharedPreferencesProductIdsAndNamesArrayForm);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(customerCartProductModel.getProductOwnerId());
                editor.apply();

                editor.putString(customerCartProductModel.getProductOwnerId(), newSharedPreferencesProductIdsAndNames);
                editor.apply();

                holder.tv_cartProductAmount.setText(String.valueOf(newProductAmount));
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerCartProductModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_cartProductName;
        private TextView tv_cartProductPrice;
        private TextView tv_cartProductAmount;
        private CircleImageView civ_cartProductImage;
        private ImageButton ib_reduceAmount;
        private ImageButton ib_addAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_cartProductName = itemView.findViewById(R.id.tv_cartProductName);
            tv_cartProductPrice = itemView.findViewById(R.id.tv_cartProductPrice);
            tv_cartProductAmount = itemView.findViewById(R.id.tv_cartProductAmount);
            civ_cartProductImage = itemView.findViewById(R.id.civ_cartProductImage);
            ib_reduceAmount = itemView.findViewById(R.id.ib_reduceAmount);
            ib_addAmount = itemView.findViewById(R.id.ib_addAmount);
        }
    }
}
