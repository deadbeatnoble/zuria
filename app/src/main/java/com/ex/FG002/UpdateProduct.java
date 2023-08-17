package com.ex.FG002;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

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

        if (productModel != null) {
            til_productName.getEditText().setText(productModel.getProductName());
            til_productPrice.getEditText().setText(String.valueOf(productModel.getProductPrice()));
            til_productDescription.getEditText().setText(productModel.getProductDesciption());

            //Bitmap imageBitmap = BitmapFactory.decodeByteArray(productModel.getProductImage(), 0, productModel.getProductImage().length);
            //iv_productImageUP.setImageBitmap(imageBitmap);

            Picasso.get().load(imageUri).into(iv_productImageUP);


        } else {
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
                update(view);
                startActivity(new Intent(UpdateProduct.this, shopOwnerPOV.class));
            }
        });
    }
    public void update(View view) {
        String productName = til_productName.getEditText().getText().toString();
        String productPrice = til_productPrice.getEditText().getText().toString();
        String productDescription = til_productDescription.getEditText().getText().toString();
        String productImage = imageUri.toString();

        ProductModel productModel = new ProductModel(productId, productName, Double.parseDouble(productPrice), productDescription, productImage, new MyApplication().getOwnerId(), NetworkChangeListener.syncStatus, false, false);

        DBHelper dbHelper = new DBHelper(this);

        Boolean success = dbHelper.updateProduct(productModel);

        Toast.makeText(UpdateProduct.this, "success = " + success, Toast.LENGTH_SHORT).show();
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