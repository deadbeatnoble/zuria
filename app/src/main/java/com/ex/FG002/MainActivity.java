package com.ex.FG002;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private TextView tv_registerUser;
    private EditText user_phone_number;
    private EditText et_userPassword;
    private Button confirm;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_userPassword = findViewById(R.id.et_userPassword);
        user_phone_number = findViewById(R.id.user_phone_number);

        tv_registerUser = findViewById(R.id.tv_registerUser);
        confirm = findViewById(R.id.confirm);


        tv_registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, sendOTP.class);
                startActivity(intent);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginMobileNumber = user_phone_number.getText().toString();
                String loginPassword = et_userPassword.getText().toString();

                if (!loginMobileNumber.isEmpty() && !loginPassword.isEmpty()) {
                    checkAccountLogIn(loginMobileNumber,loginPassword);
                } else {
                    Toast.makeText(MainActivity.this, "Fill both phone number and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkAccountLogIn(String loginMobileNumber, String loginPassword) {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(loginMobileNumber).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String usernumber = String.valueOf(dataSnapshot.child("mobileNumber").getValue());
                        String userpassword = String.valueOf(dataSnapshot.child("password").getValue());

                        if (usernumber.equals(loginMobileNumber) && userpassword.equals(loginPassword)) {
                            Intent intent = new Intent(MainActivity.this, shopOwnerPOV.class);
                            startActivity(intent);
                        }
                        //????????????????????????????
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}