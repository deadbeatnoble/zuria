package com.ex.FG002;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {
    private TextInputLayout til_newPassword;
    private TextInputLayout til_confirmPassword;
    private Button btn_register;
    private TextView tv_loginUser;

    String mobileNumber, password;
    FirebaseDatabase db;
    DatabaseReference reference;

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
                password = til_confirmPassword.getEditText().getText().toString();

                if (!til_newPassword.getEditText().getText().toString().equals(til_confirmPassword.getEditText().getText().toString())) {
                    til_confirmPassword.setError("Password don't match");
                } else {
                    Users users = new Users(mobileNumber,password);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("Users");
                    reference.child(mobileNumber).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterUser.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}