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
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateProduct extends AppCompatActivity {

    private TextInputLayout til_productName;
    private TextInputLayout til_productPrice;
    private TextInputLayout til_productDescription;
    private ImageView iv_productImage;

    Button btn_addProduct;
    Button btn_discardProduct;
    FloatingActionButton fab_imageFromGallery;

    DBHelper dbHelper;


    Uri imageUri;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("products");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products/" + new MyApplication().getOwnerId());

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


        storageReference = FirebaseStorage.getInstance().getReference("products");
        databaseReference = FirebaseDatabase.getInstance().getReference("products/" + new MyApplication().getOwnerId());


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
                    String productId = generateProductId();
                    try {
                        productModel = new ProductModel(productId, til_productName.getEditText().getText().toString(), Double.parseDouble(til_productPrice.getEditText().getText().toString()), til_productDescription.getEditText().getText().toString(), imageUri.toString(), new MyApplication().getOwnerId(), NetworkChangeListener.syncStatus, false, false);
                        Toast.makeText(CreateProduct.this, productModel.toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(CreateProduct.this, "Failed to Add", Toast.LENGTH_SHORT).show();
                    }

                    Boolean success = dbHelper.addProduct(productModel);

                    if (success) {
                        if (NetworkChangeListener.syncStatus == true) {
                            //internet available
                            uploadProduct(productModel);
                        } else {
                            dbHelper.updateProductSyncStatus(productId,false);
                        }
                        //Toast.makeText(CreateProduct.this, "Product Created locally", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateProduct.this, "Failed to create product", Toast.LENGTH_SHORT).show();
                    }

                    //check if online the to firebase uploading starts
                    //uploadProduct(prodcutId);

                    startActivity(new Intent(CreateProduct.this, shopOwnerPOV.class));
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadProduct(ProductModel productModel) {
        if (productModel.getProductImage() == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference fileReference = storageReference.child(new MyApplication().getOwnerId() + "T" + productModel.getProductId() + "." + getFileExtension(Uri.parse(productModel.getProductImage())));
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
                                    String productImageUrl = uri.toString();
                                    Toast.makeText(CreateProduct.this, "Successfully uploaded product", Toast.LENGTH_SHORT).show();
                                    ProductUpload productUpload = new ProductUpload(productModel.getProductId(), productModel.getProductName(), productModel.getProductPrice(), productModel.getProductDesciption(), productImageUrl, productModel.getOwnerId(), productModel.getProductStatus());
                                    databaseReference.child(productModel.getProductId()).setValue(productUpload);

                                    dbHelper.updateProductSyncStatus(productModel.getProductId(), true);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dbHelper.updateProductSyncStatus(productModel.getProductId(), false);
                            Toast.makeText(CreateProduct.this, "Failed to upload product!", Toast.LENGTH_SHORT).show();
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
    }

    private String generateProductId() {
        return UUID.randomUUID().toString();
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
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(iv_productImage);
            }
        }
    }

}