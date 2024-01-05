package com.ex.FG002.OpenSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.ProductBased.CreateProduct;
import com.ex.FG002.ProductBased.ProductUpload;
import com.ex.FG002.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterUser extends AppCompatActivity {
    private TextInputLayout til_newPassword;
    private TextInputLayout til_confirmPassword;
    private Button btn_register;
    private TextView tv_loginUser;

    String mobileNumber, password;
    FirebaseDatabase db;
    DatabaseReference reference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        til_newPassword = findViewById(R.id.til_newPassword);
        til_confirmPassword = findViewById(R.id.til_confirmPassword);

        tv_loginUser = findViewById(R.id.tv_loginUser);
        btn_register = findViewById(R.id.btn_register);

        tv_loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNumber = getIntent().getStringExtra("usermobile");

                //9.006955417367214, 38.84637434342064

                String storeName = getIntent().getStringExtra("storeName");
                String storeLocation = getIntent().getStringExtra("storeLocation");
                String storeImage = getIntent().getStringExtra("storeImage");

                String[] locationArray = storeLocation.split(",");

                String storeLatitude = locationArray[0].trim();
                String storeLongitude = locationArray[1].trim();

                password = til_confirmPassword.getEditText().getText().toString();

                if (!til_newPassword.getEditText().getText().toString().equals(til_confirmPassword.getEditText().getText().toString())) {
                    til_confirmPassword.setError("Password don't match");
                } else {
                    /*UserDBHelper userDBHelper = new UserDBHelper(RegisterUser.this);
                    Users users = new Users(mobileNumber, password, storeImage, storeName,storeLatitude, storeLongitude, false, false);
                    userDBHelper.addUserData(users);*/




                    storageReference = FirebaseStorage.getInstance().getReference("stores");
                    StorageReference fileReference = storageReference.child(mobileNumber + "T" + storeName + "." + getFileExtension(Uri.parse(storeImage)));
                    fileReference.putFile(Uri.parse(storeImage))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            db = FirebaseDatabase.getInstance();
                                            reference = db.getReference("Users");

                                            UserDBHelper userDBHelper = new UserDBHelper(RegisterUser.this);
                                            Users users = new Users(mobileNumber, password, uri.toString(), storeName, storeLatitude, storeLongitude, false, false);
                                            userDBHelper.addUserData(users);

                                            reference.child(mobileNumber).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RegisterUser.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    userDBHelper.addUserData(users);
                                                    startActivity(intent);
                                                }
                                            });

                                            //Toast.makeText(RegisterUser.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterUser.this, "Failed to Register", Toast.LENGTH_SHORT).show();
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
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}