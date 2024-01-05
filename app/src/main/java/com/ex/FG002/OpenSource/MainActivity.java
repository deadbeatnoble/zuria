package com.ex.FG002.OpenSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.CustomerBased.CustomerPOV;
import com.ex.FG002.R;
import com.ex.FG002.VendorBased.VendorPOV;
import com.ex.FG002.VendorBased.VendorStoreRegistry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView tv_registerUser;
    private EditText user_phone_number;
    private TextInputLayout til_userPassword;
    private Button confirm;
    private ProgressBar pb_progressBar;
    DatabaseReference reference;
    private TextView userPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        til_userPassword = findViewById(R.id.til_userPassword);
        user_phone_number = findViewById(R.id.user_phone_number);

        tv_registerUser = findViewById(R.id.tv_registerUser);
        confirm = findViewById(R.id.confirm);
        pb_progressBar = findViewById(R.id.pb_progressBar);
        userPage = findViewById(R.id.bio);
        userPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CustomerPOV.class);
                startActivity(intent);
            }
        });
        tv_registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VendorStoreRegistry.class);
                startActivity(intent);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb_progressBar.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.GONE);

                String loginMobileNumber = user_phone_number.getText().toString();
                String loginPassword = til_userPassword.getEditText().getText().toString();

                MyApplication.OwnerId = loginMobileNumber;

                if (validatePhoneNumber(loginMobileNumber)) {
                    if (!loginMobileNumber.isEmpty() && !loginPassword.isEmpty()) {
                        //pbv
                        checkAccountLogIn(loginMobileNumber,loginPassword);
                    } else {
                        pb_progressBar.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Fill both phone number and password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pb_progressBar.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validatePhoneNumber(String userInput) {
        Pattern p = Pattern.compile("[9][0-9]{8}");
        Matcher matcher = p.matcher(userInput);
        return matcher.matches();
    }

    private void checkAccountLogIn(String loginMobileNumber, String loginPassword) {
        UserDBHelper userDBHelper = new UserDBHelper(this);
        Users user = new Users();
        if (userDBHelper.checkUserExists(loginMobileNumber)) {
            user = userDBHelper.getUserData(loginMobileNumber);
            if (loginPassword.equals(user.getPassword())) {
                pb_progressBar.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);

                MyApplication.OwnerId = loginMobileNumber;
                MyApplication.OwnerPassword = loginPassword;


                Intent intent = new Intent(MainActivity.this, VendorPOV.class);
                startActivity(intent);
            } else {
                pb_progressBar.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }

        } else {
            reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(loginMobileNumber).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    pb_progressBar.setVisibility(View.GONE);
                    confirm.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        //pbg
                        if (task.getResult().exists()) {
                            DataSnapshot dataSnapshot = task.getResult();
                            String usernumber = String.valueOf(dataSnapshot.child("mobileNumber").getValue());
                            String userpassword = String.valueOf(dataSnapshot.child("password").getValue());

                            if (usernumber.equals(loginMobileNumber) && userpassword.equals(loginPassword)) {
                                pb_progressBar.setVisibility(View.GONE);
                                confirm.setVisibility(View.VISIBLE);

                                MyApplication.OwnerId = loginMobileNumber;
                                MyApplication.OwnerPassword = loginPassword;


                                //i commented the lower 2 lines because they save the login data when logged in once but currently we dont have enough data to fetch when logging in for the frist time hence its removed
                                //Users user = new Users(loginMobileNumber, loginPassword, null, null, false, false);
                                //userDBHelper.addUserData(user);

                                Intent intent = new Intent(MainActivity.this, VendorPOV.class);
                                startActivity(intent);
                            } else {
                                pb_progressBar.setVisibility(View.GONE);
                                confirm.setVisibility(View.VISIBLE);

                                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            pb_progressBar.setVisibility(View.GONE);
                            confirm.setVisibility(View.VISIBLE);

                            Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pb_progressBar.setVisibility(View.GONE);
                        confirm.setVisibility(View.VISIBLE);

                        Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}