package com.ex.FG002.OpenSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.FG002.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verifyOTP extends AppCompatActivity {
    private EditText et_code1, et_code2, et_code3, et_code4, et_code5, et_code6;
    private TextView tv_resendCode;
    private TextView tv_userPhoneNumber;
    private Button btn_receiveVerification;
    private ProgressBar pb_progressBar;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        tv_userPhoneNumber = findViewById(R.id.tv_userPhoneNumber);
        tv_userPhoneNumber.setText(String.format(
                "+251-%s", getIntent().getStringExtra("number")
        ));

        et_code1 =findViewById(R.id.et_code1);
        et_code2 =findViewById(R.id.et_code2);
        et_code3 =findViewById(R.id.et_code3);
        et_code4 =findViewById(R.id.et_code4);
        et_code5 =findViewById(R.id.et_code5);
        et_code6 =findViewById(R.id.et_code6);

        setupOTPInputs();

        tv_resendCode = findViewById(R.id.tv_resendCode);
        btn_receiveVerification = findViewById(R.id.btn_receiveVerification);
        pb_progressBar = findViewById(R.id.pb_progressBar);

        verificationId = getIntent().getStringExtra("verificationId");

        btn_receiveVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_code1.getText().toString().trim().isEmpty() ||
                        et_code2.getText().toString().trim().isEmpty() ||
                        et_code3.getText().toString().trim().isEmpty() ||
                        et_code4.getText().toString().trim().isEmpty() ||
                        et_code5.getText().toString().trim().isEmpty() ||
                        et_code6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(verifyOTP.this, "Enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = et_code1.getText().toString() +
                        et_code2.getText().toString() +
                        et_code3.getText().toString() +
                        et_code4.getText().toString() +
                        et_code5.getText().toString() +
                        et_code6.getText().toString();
                if (verificationId != null) {
                    pb_progressBar.setVisibility(View.VISIBLE);
                    btn_receiveVerification.setVisibility(View.GONE);

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pb_progressBar.setVisibility(View.GONE);
                                    btn_receiveVerification.setVisibility(View.VISIBLE);
                                    String mobile = getIntent().getStringExtra("number");
                                    if (task.isSuccessful()) {

                                        Intent intent = getIntent();
                                        String storeName = intent.getStringExtra("storeName");
                                        String storeLocation = intent.getStringExtra("storeLocation");
                                        String storeImage = intent.getStringExtra("storeImage");


                                        intent = new Intent(getApplicationContext(), RegisterUser.class);

                                        intent.putExtra("storeImage", storeImage);
                                        intent.putExtra("storeName", storeName);
                                        intent.putExtra("storeLocation", storeLocation);

                                        intent.putExtra("usermobile", mobile);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(verifyOTP.this, "Verification code inserted is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        tv_resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+251" + getIntent().getStringExtra("number"),
                        60,
                        TimeUnit.SECONDS,
                        verifyOTP.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(verifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(verifyOTP.this, "New verification code sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

    }

    public void setupOTPInputs() {
        et_code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    et_code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    et_code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    et_code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    et_code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().trim().isEmpty()) {
                    et_code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}