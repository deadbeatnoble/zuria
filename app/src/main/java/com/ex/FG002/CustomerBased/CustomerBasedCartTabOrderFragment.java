package com.ex.FG002.CustomerBased;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ex.FG002.OpenSource.Users;
import com.ex.FG002.ProductBased.CustomerCartProductAdapter;
import com.ex.FG002.ProductBased.CustomerCartProductModel;
import com.ex.FG002.ProductBased.CustomerCartStoreAdapter;
import com.ex.FG002.ProductBased.CustomerCartStoreModel;
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
    private List<CustomerCartStoreModel> customerCartStoreModelList = new ArrayList<>();

    private RecyclerView customerCartProductRecyclerView;
    private RecyclerView customerCartStoreRecyclerView;

    private CustomerCartProductAdapter customerCartProductAdapter;
    private CustomerCartStoreAdapter customerCartStoreAdapter;

    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_based_cart_tab_order, container, false);

        //customerCartProductRecyclerView = rootView.findViewById(R.id.rv_customerCartStore);
        customerCartStoreRecyclerView = rootView.findViewById(R.id.rv_customerCartStore);

        //customerCartProductRecyclerView.setHasFixedSize(true);
        customerCartStoreRecyclerView.setHasFixedSize(true);

        //customerCartProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        customerCartStoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //customerCartProductAdapter = new CustomerCartProductAdapter(customerCartProductModelList, getActivity());
        customerCartStoreAdapter = new CustomerCartStoreAdapter(customerCartStoreModelList, customerCartProductModelList, getActivity());

        //customerCartProductRecyclerView.setAdapter(customerCartProductAdapter) ;
        customerCartStoreRecyclerView.setAdapter(customerCartStoreAdapter);


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                customerCartStoreModelList.remove(viewHolder.getAdapterPosition());
                customerCartStoreAdapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(customerCartStoreRecyclerView);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("productsInCart", getContext().MODE_PRIVATE);
                Map<String, String> allValues = (Map<String, String>) sharedPreferences.getAll();

                List<String> storeIdLists = new ArrayList<>();

                for (Map.Entry<String, ?> entry : allValues.entrySet()) {
                    String storeId = entry.getKey();

                    storeIdLists.add(storeId);
                }

                for (DataSnapshot snapshot : dataSnapshot.child("Users").getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    if (storeIdLists.contains(user.getMobileNumber())) {
                        CustomerCartStoreModel customerCartStoreModel = new CustomerCartStoreModel(
                                user.getMobileNumber(),
                                user.getStoreName(),
                                58.0,
                                user.getStoreImage()
                        );
                        customerCartStoreModelList.add(customerCartStoreModel);
                    }
                }

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

                customerCartStoreAdapter.notifyDataSetChanged();
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