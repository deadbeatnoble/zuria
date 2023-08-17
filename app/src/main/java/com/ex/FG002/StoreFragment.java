package com.ex.FG002;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;




public class StoreFragment extends Fragment {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("products");
    DatabaseReference databaseReference;
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
    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
        if (NetworkChangeListener.syncStatus) {
            uploadUnsyncedProductsToFirebase();
            offlineDeletedProducts();


            /*List<ProductModel> deletedProducts = dbHelper.getDeletedProducts();
                    for (ProductModel deletedProduct : deletedProducts) {
                        deletedProductFromFirebase(deletedProduct, position);
                    }*/
        }
    }

    private void offlineDeletedProducts() {
        List<ProductModel> deletedProducts = new DBHelper(getActivity()).getDeletedProducts();
        for (ProductModel deletedProduct : deletedProducts) {
            deletedProductFromFirebase(deletedProduct);
        }
    }

    private void deletedProductFromFirebase(ProductModel productModel) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/"+productModel.getOwnerId()+"/"+productModel.getProductId());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("products");
        StorageReference fileReference = storageReference.child(productModel.getOwnerId() + "T" + productModel.getProductId() + "." + getFileExtension(Uri.parse(productModel.getProductImage())));
        Task<Void> task = databaseReference.removeValue();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fileReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                new DBHelper(getActivity()).deleteProduct(productModel);
                                Toast.makeText(getActivity(), "Successfully deleted " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new DBHelper(getActivity()).updateDeletedProducts(productModel.getProductId(), true);
                                Toast.makeText(getActivity(), "Failed to delete " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new DBHelper(getActivity()).updateDeletedProducts(productModel.getProductId(), true);
                Toast.makeText(getActivity(), "Failed to delete " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadUnsyncedProductsToFirebase() {
        DBHelper dbHelper = new DBHelper(getActivity());
        List<ProductModel> unsyncedProducts = dbHelper.getUnsyncedProducts();
        for (ProductModel productModel : unsyncedProducts) {
            // Upload the product to Firebase using Firebase SDK
            // After successful upload, update the sync status to true
            //String firebaseId = "generated_firebase_id"; // Generate a unique ID or use Firebase's auto-generated ID
            //productModel.setSyncStatus(true);
            uploadProduct(productModel, new UploadCallback() {
                @Override
                public void onSuccess(boolean success) {
                    dbHelper.updateProductSyncStatus(productModel.getProductId(), success);
                }
            });
            /*if (uploadProduct(productModel)) {
                dbHelper.updateProductSyncStatus(productModel.getProductId(), true);
            } else {
                dbHelper.updateProductSyncStatus(productModel.getProductId(), false);
            }*/
        }
    }
    @Override
    public void onStop() {
        getContext().unregisterReceiver(networkChangeListener);
        super.onStop();
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadProduct(ProductModel productModel, UploadCallback callback) {
        StorageReference fileReference = storageReference.child(productModel.getOwnerId() + "T" + productModel.getProductId() + "." + getFileExtension(Uri.parse(productModel.getProductImage())));
        fileReference.putFile(Uri.parse(productModel.getProductImage()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /*Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                  //progressBar.setProgress(0);
                              }
                           }, 500);*/

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference = FirebaseDatabase.getInstance().getReference("products/" + productModel.getOwnerId());
                                String productImageUrl = uri.toString();
                                Toast.makeText(getActivity(), "Successfully uploaded product", Toast.LENGTH_SHORT).show();
                                ProductUpload productUpload = new ProductUpload(productModel.getProductId(), productModel.getProductName(),productModel.getProductPrice(), productModel.getProductDesciption(), productImageUrl, productModel.getOwnerId(), productModel.getProductStatus());
                                databaseReference.child(productModel.getProductId()).setValue(productUpload);
                                callback.onSuccess(true);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to upload product!", Toast.LENGTH_SHORT).show();
                        callback.onSuccess(false);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        /*double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                           progress.setProgress((int)progress);*/
                    }
                });
        }

    interface UploadCallback {
        void onSuccess(boolean success);
    }
}