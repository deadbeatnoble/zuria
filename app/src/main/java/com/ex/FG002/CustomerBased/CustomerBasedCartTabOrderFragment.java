package com.ex.FG002.CustomerBased;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.ProductBased.CustomerCartProductAdapter;
import com.ex.FG002.ProductBased.CustomerCartProductModel;
import com.ex.FG002.ProductBased.CustomerProductAdapter;
import com.ex.FG002.ProductBased.CustomerProductModel;
import com.ex.FG002.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomerBasedCartTabOrderFragment extends Fragment {

    private List<CustomerCartProductModel> customerCartProductModelList = new ArrayList<>();
    private RecyclerView customerCartProductRecyclerView;
    private CustomerCartProductAdapter customerCartProductAdapter;

    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_based_cart_tab_order, container, false);

        customerCartProductRecyclerView = rootView.findViewById(R.id.rv_customerCartProduct);
        customerCartProductRecyclerView.setHasFixedSize(true);
        customerCartProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        customerCartProductAdapter = new CustomerCartProductAdapter(customerCartProductModelList, getActivity());
        customerCartProductRecyclerView.setAdapter(customerCartProductAdapter);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                customerCartProductModelList.remove(viewHolder.getAdapterPosition());
                customerCartProductAdapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(customerCartProductRecyclerView);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("productsInCart", getContext().MODE_PRIVATE);
                Map<String, String> allValues = (Map<String, String>) sharedPreferences.getAll();


                for (Map.Entry<String, String> entry : allValues.entrySet()) {
                    String ownerId = entry.getKey();
                    String productId = entry.getValue();

                    //productId was not just a simple string. it had many ids in it separated with "," so we separet them and assign them to an array and when next time the code tries to fetch the data of a product under its respective owner id it would loop as much as the size of the array. meaning it will fetch the data of more than 1 product within the same owner id

                    List<String> productIds = new ArrayList<>(Arrays.asList(productId.split(",")));
                    int sizeOfProductId = productIds.size();

                    // Assuming the database structure is /products/{ownerId}/{productId}
                    DataSnapshot ownerSnapshot = dataSnapshot.child("products").child(ownerId);
                    if (ownerSnapshot.exists()) {

                        for (int i = 0; i < sizeOfProductId; i++) {
                            DataSnapshot productSnapshot = ownerSnapshot.child(productIds.get(i).split(":")[0]);
                            if (productSnapshot.exists()) {
                                // Retrieve the product data

                                String productPriceString = String.valueOf(productSnapshot.child("productPrice").getValue());
                                double productPrice = 0.0; // Default value if the string is null

                                if (productPriceString != null) {
                                    try {
                                        productPrice = Double.parseDouble(productPriceString);
                                    } catch (NumberFormatException e) {
                                        // Handle the case where the value is not a valid double
                                        e.printStackTrace();
                                    }
                                }

                                CustomerProductModel customerProductModel = new CustomerProductModel(
                                        String.valueOf(productSnapshot.child("productId").getValue()),
                                        String.valueOf(productSnapshot.child("productName").getValue()),
                                        productPrice,
                                        String.valueOf(productSnapshot.child("productDesciption").getValue()),
                                        String.valueOf(productSnapshot.child("productImage").getValue()),
                                        String.valueOf(productSnapshot.child("ownerId").getValue()),
                                        String.valueOf(productSnapshot.child("productStatus").getValue()).equals("true")
                                );


// Create a CustomerCartProductModel object and add it to the list
                                CustomerCartProductModel customerCartProductModel = new CustomerCartProductModel(
                                        customerProductModel.getOwnerId(),
                                        customerProductModel.getProductId(),
                                        customerProductModel.getProductName(),
                                        Integer.parseInt(productIds.get(i).split(":")[1]),
                                        customerProductModel.getProductPrice(),
                                        customerProductModel.getProductImageURL()
                                );
                                customerCartProductModelList.add(customerCartProductModel);
                            }
                        }


                    }
                }

                customerCartProductAdapter.notifyDataSetChanged();
                // Use the customerCartProductModelList as needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        return rootView;
    }
}