package com.ex.FG002;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StoreFragment extends Fragment {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("products");
    DatabaseReference databaseReference;
    private FloatingActionButton fab_createProduct;
    private FloatingActionButton fab_ShareLocation;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
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
        fab_ShareLocation = rootView.findViewById(R.id.fab_ShareLocation);
        fab_ShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!isLocationEnabled) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Location Is Turned Off");
                        alertDialog.setMessage("Turn on device location to access this feature");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alertDialog.show();
                    }

                    getCurrentLocation();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                }

            }
        });
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

    private void getCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    if (NetworkChangeListener.syncStatus) {

                    } else {

                    }

                    currentLocation = location;
                    Toast.makeText(getActivity(), "latitude is " + currentLocation.getLatitude() + ". longitude is " + currentLocation.getLongitude() + ".", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to get current location", Toast.LENGTH_SHORT).show();
            }
        });
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
            // Assuming `productId` is the unique identifier of the product
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/" + productModel.getOwnerId() + "/" + productModel.getProductId());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The product already exists in Firebase, update it
                        updateProductInFirebase(databaseReference, productModel);
                    } else {
                        // The product does not exist in Firebase, create a new entry
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // An error occurred while querying the product
                }
            });

        }
    }

    private void updateProductInFirebase(DatabaseReference productRef, ProductModel productModel) {

        Map<String, Object> updateData = new HashMap<>();

        updateData.put("ownerId", productModel.getOwnerId());
        updateData.put("productDesciption", productModel.getProductDesciption());
        updateData.put("productId", productModel.getProductId());
        updateData.put("productImage", productModel.getProductImage());
        updateData.put("productName", productModel.getProductName());
        updateData.put("productPrice", productModel.getProductPrice());
        updateData.put("productStatus", productModel.getProductStatus());


        productRef.updateChildren(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // The product update was successful
                        new DBHelper(getActivity()).updateProductSyncStatus(productModel.getProductId(), true);
                        Toast.makeText(getActivity(), "Successfully updated offline changes", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while updating the product
                        new DBHelper(getActivity()).updateProductSyncStatus(productModel.getProductId(), false);
                        Toast.makeText(getActivity(), "Failed to updated offline changes", Toast.LENGTH_SHORT).show();
                    }
                });
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