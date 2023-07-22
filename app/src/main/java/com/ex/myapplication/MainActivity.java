package com.ex.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView default_user_phone_number;
    private EditText user_phone_number;
    private EditText admin_code;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        default_user_phone_number = findViewById(R.id.default_user_phone_number);
        user_phone_number = findViewById(R.id.user_phone_number);
        admin_code = findViewById(R.id.admin_code);
        confirm = findViewById(R.id.confirm);

        String[] permission = {
                android.Manifest.permission.READ_PHONE_NUMBERS
        };

        requestPermissions(permission, 102);

        default_user_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPhoneNumberToEditText();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_phone_number.getText().toString().equals("")) {
                    user_phone_number.setError("This field is empty");
                } else if (user_phone_number.getText().toString().equals("0994850480")) {
                    if (admin_code.getText().toString().equals("6994")) {
                        //admin pages
                        //code goes here
                        Intent intent = new Intent(MainActivity.this, shopOwnerPOV.class);
                        startActivity(intent);
                    } else {
                        //code goes here
                        admin_code.setError("Wrong admin code");
                    }
                } else {
                    //user viewing pages
                    //code goes here
                }
            }
        });
    }

    private void setPhoneNumberToEditText() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        user_phone_number.setText(telephonyManager.getLine1Number());
    }
}