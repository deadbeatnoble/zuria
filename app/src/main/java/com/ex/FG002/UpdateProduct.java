package com.ex.FG002;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProduct extends AppCompatActivity {
    private TextInputLayout til_productName;
    private TextInputLayout til_productPrice;
    private TextInputLayout til_productDescription;
    private ImageView iv_productImageUP;
    private Button btn_updateProduct;

    private FloatingActionButton fab_imageFromGallery;


    String productId;
    Uri imageUri;


    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;
    String[]cameraPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[]storagePersmission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        til_productName = findViewById(R.id.til_productName);
        til_productPrice = findViewById(R.id.til_productPrice);
        til_productDescription = findViewById(R.id.til_productDescription);
        iv_productImageUP = findViewById(R.id.iv_productImageUP);

        btn_updateProduct = findViewById(R.id.btn_updateProduct);
        fab_imageFromGallery = findViewById(R.id.fab_imageFromGallery);

        // Initialize the productId class variable with the value of the PRODUCT_ID extra
        productId = getIntent().getStringExtra("PRODUCT_ID");

        DBHelper dbHelper = new DBHelper(this);
        ProductModel productModel = dbHelper.getProductById(productId);
        boolean constantProductStatus;

        if (productModel != null) {
            til_productName.getEditText().setText(productModel.getProductName());
            til_productPrice.getEditText().setText(String.valueOf(productModel.getProductPrice()));
            til_productDescription.getEditText().setText(productModel.getProductDesciption());
            constantProductStatus = productModel.getProductStatus();
            //Bitmap imageBitmap = BitmapFactory.decodeByteArray(productModel.getProductImage(), 0, productModel.getProductImage().length);
            //iv_productImageUP.setImageBitmap(imageBitmap);

            Picasso.get().load(Uri.parse(productModel.getProductImage())).into(iv_productImageUP);


        } else {
            constantProductStatus = false;
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        fab_imageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int productImage = 0;
                if (productImage == 0) {
                    if (!checkCameraPersmission()) {
                        requestCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                } else if (productImage == 1){
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        btn_updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(view, constantProductStatus);
                startActivity(new Intent(UpdateProduct.this, shopOwnerPOV.class));
            }
        });
    }
    public void update(View view, boolean constantProductStatus) {
        String productName = til_productName.getEditText().getText().toString();
        String productPrice = til_productPrice.getEditText().getText().toString();
        String productDescription = til_productDescription.getEditText().getText().toString();
        String productImage = String.valueOf(getUriFromDrawable(iv_productImageUP.getDrawable()));

        ProductModel productModel = new ProductModel(productId, productName, Double.parseDouble(productPrice), productDescription, productImage, new MyApplication().getOwnerId(), NetworkChangeListener.syncStatus, constantProductStatus, false);

        DBHelper dbHelper = new DBHelper(this);



        if (NetworkChangeListener.syncStatus) {
            //connected
            updateFromFirebase(productModel);
        } else {
            //not connected
            dbHelper.updateProduct(productModel);
            dbHelper.updateProductSyncStatus(productModel.getProductId(), false);
        }




        Boolean success = dbHelper.updateProduct(productModel);

        Toast.makeText(UpdateProduct.this, "success = " + success, Toast.LENGTH_SHORT).show();
    }

    private Uri getUriFromDrawable(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Title", null);
        return Uri.parse(path);
    }

    private void updateFromFirebase(ProductModel productModel) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("products");
        StorageReference fileReference = storageReference.child(productModel.getOwnerId() + "T" + productModel.getProductId() + "." + getFileExtension(Uri.parse(productModel.getProductImage())));
        fileReference.putFile(Uri.parse(productModel.getProductImage()))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> map = new HashMap<>();

                                map.put("ownerId", productModel.getOwnerId());
                                map.put("productDesciption", productModel.getProductDesciption());
                                map.put("productId", productModel.getProductId());
                                map.put("productImage", String.valueOf(uri));
                                map.put("productName", productModel.getProductName());
                                map.put("productPrice", productModel.getProductPrice());
                                map.put("productStatus", productModel.getProductStatus());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/" + productModel.getOwnerId());
                                databaseReference.child(productModel.getProductId())
                                        .updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                new DBHelper(UpdateProduct.this).updateProduct(productModel);
                                                new DBHelper(UpdateProduct.this).updateProductSyncStatus(productModel.getProductId(), true);
                                                Toast.makeText(UpdateProduct.this, "Successfully updated " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                new DBHelper(UpdateProduct.this).updateProduct(productModel);
                                                new DBHelper(UpdateProduct.this).updateProductSyncStatus(productModel.getProductId(), false);
                                                Toast.makeText(UpdateProduct.this, "Failed to update " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new DBHelper(UpdateProduct.this).updateProduct(productModel);
                        new DBHelper(UpdateProduct.this).updateProductSyncStatus(productModel.getProductId(), false);
                        Toast.makeText(UpdateProduct.this, "Failed to update Image of " + productModel.getProductName(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri parse) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(parse));
    }
    private byte[] imageToByteArray(ImageView iv_productImage) {
        Bitmap bitmap = ((BitmapDrawable) iv_productImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] bytes = byteArray.toByteArray();

        return bytes;
    }


    private void requestStoragePermission() {
        requestPermissions(storagePersmission, STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromGallery() {
        CropImage.activity().start(this);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission,CAMERA_REQUEST);
    }

    private boolean checkCameraPersmission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        return result && result2;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0) {
                    boolean camera_accept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storage_accept = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (camera_accept && storage_accept) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "enable camera and storage permission", Toast.LENGTH_SHORT);
                    }
                }
                break;
            case STORAGE_REQUEST:
                if (grantResults.length > 0) {
                    boolean storage_accept = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage_accept) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "enable storage permission", Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(iv_productImageUP);
            }
        }
    }
}