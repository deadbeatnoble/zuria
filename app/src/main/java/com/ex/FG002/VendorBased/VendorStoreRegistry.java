package com.ex.FG002.VendorBased;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.OpenSource.sendOTP;
import com.ex.FG002.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class VendorStoreRegistry extends AppCompatActivity {

    private ImageView iv_storePic;
    private FloatingActionButton fab_storePicFromGalery;
    private EditText et_storeName;
    private EditText et_storeLocation;
    private TextView tv_setCurrentLocation;
    private Button btn_storeConfirmed;

    Uri imageUri;

    FusedLocationProviderClient fusedLocationProviderClient;


    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;
    private static final int FINE_PERMISSION_CODE = 1;
    String[]cameraPermission = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[]storagePersmission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_store_registry);

        iv_storePic = findViewById(R.id.iv_storePic);
        fab_storePicFromGalery = findViewById(R.id.fab_storePicFromGallery);
        et_storeName = findViewById(R.id.et_storeName);
        et_storeLocation = findViewById(R.id.et_storeLocation);
        tv_setCurrentLocation = findViewById(R.id.tv_setCurrentLocation);
        btn_storeConfirmed = findViewById(R.id.btn_storeConfirmed);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fab_storePicFromGalery.setOnClickListener(new View.OnClickListener() {
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

        tv_setCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });

        btn_storeConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorStoreRegistry.this, sendOTP.class);
                intent.putExtra("storeImage", imageUri.toString());
                intent.putExtra("storeName", et_storeName.getText().toString());
                intent.putExtra("storeLocation", et_storeLocation.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                try {
                                    et_storeLocation.setText(location.getLatitude() + ", " + location.getLongitude());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(VendorStoreRegistry.this, "Please, turn on Location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(VendorStoreRegistry.this, "Failed to retrieve location try manually", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    private boolean checkCameraPersmission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        return result && result2;
    }
    private void requestCameraPermission() {
        requestPermissions(cameraPermission,CAMERA_REQUEST);
    }
    private void pickFromGallery() {
        CropImage.activity().start(this);
    }
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission() {
        requestPermissions(storagePersmission, STORAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length >= 2) { // Check if grantResults array has at least two elements
                    boolean camera_accept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storage_accept = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (camera_accept && storage_accept) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Unexpected result from permission request", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_REQUEST:
                if (grantResults.length >= 1) { // Check if grantResults array has at least one element
                    boolean storage_accept = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage_accept) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Unexpected result from permission request", Toast.LENGTH_SHORT).show();
                }
                break;
            case FINE_PERMISSION_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    // Location permission denied
                    Toast.makeText(this, "Location permission is required!", Toast.LENGTH_SHORT).show();
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
                Picasso.get().load(imageUri).into(iv_storePic);
            }
        }
    }


}