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

public class CreateProduct extends AppCompatActivity {

    private TextInputLayout til_productName;
    private TextInputLayout til_productPrice;
    private TextInputLayout til_productDescription;
    private ImageView iv_productImage;

    Button btn_addProduct;
    Button btn_discardProduct;
    FloatingActionButton fab_imageFromGallery;

    DBHelper dbHelper;



    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;
    String[]cameraPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[]storagePersmission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        til_productName = findViewById(R.id.til_productName);
        til_productPrice = findViewById(R.id.til_productPrice);
        til_productDescription = findViewById(R.id.til_productDescription);
        iv_productImage = findViewById(R.id.iv_productImageCP);


        fab_imageFromGallery = findViewById(R.id.fab_imageFromGallery);
        btn_addProduct = findViewById(R.id.btn_addProduct);
        btn_discardProduct = findViewById(R.id.btn_discardProduct);

        dbHelper = new DBHelper(CreateProduct.this);
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

        btn_addProduct.setOnClickListener(new View.OnClickListener() {
            ProductModel productModel;

            @Override
            public void onClick(View view) {
                if(til_productName.getEditText().getText().toString().isEmpty() || til_productPrice.getEditText().getText().toString().isEmpty() || til_productDescription.getEditText().getText().toString().isEmpty() || iv_productImage.getDrawable() == null) {
                    Toast.makeText(CreateProduct.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        productModel = new ProductModel(1, til_productName.getEditText().getText().toString(), Double.parseDouble(til_productPrice.getEditText().getText().toString()), til_productDescription.getEditText().getText().toString(), imageToByteArray(iv_productImage), new MyApplication().getOwnerId());
                        Toast.makeText(CreateProduct.this, productModel.toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CreateProduct.this, "Failed to Add", Toast.LENGTH_SHORT).show();
                    }

                    DBHelper databaseHelper = new DBHelper(CreateProduct.this);
                    Boolean success = databaseHelper.addProduct(productModel);

                    if (success) {
                        Toast.makeText(CreateProduct.this, "Product Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateProduct.this, "Failed to Add", Toast.LENGTH_SHORT).show();
                    }


                    startActivity(new Intent(CreateProduct.this, shopOwnerPOV.class));
                }
            }
        });
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
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromGallery() {
        CropImage.activity().start(this);
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission,CAMERA_REQUEST);
    }

    private boolean checkCameraPersmission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
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
                Uri resultUri = result.getUri();
                Picasso.get().load(resultUri).into(iv_productImage);
            }
        }
    }

}