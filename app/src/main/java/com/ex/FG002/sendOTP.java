package com.ex.FG002;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sendOTP extends AppCompatActivity {

    private EditText et_userPhoneNumber;
    private Button btn_receiveVerification;
    private ProgressBar pb_progressBar;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        et_userPhoneNumber = findViewById(R.id.et_userPhoneNumber);
        btn_receiveVerification = findViewById(R.id.btn_receiveVerification);
        pb_progressBar = findViewById(R.id.pb_progressBar);

        et_userPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (validatePhoneNumber(et_userPhoneNumber.getText().toString())) {
                    btn_receiveVerification.setEnabled(true);
                } else {
                    btn_receiveVerification.setEnabled(false);
                    et_userPhoneNumber.setError("Invalid Phone Number");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_receiveVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb_progressBar.setVisibility(View.VISIBLE);
                btn_receiveVerification.setVisibility(View.INVISIBLE);

                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(et_userPhoneNumber.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                Toast.makeText(sendOTP.this, "User already exists", Toast.LENGTH_SHORT).show();
                                pb_progressBar.setVisibility(View.GONE);
                                btn_receiveVerification.setVisibility(View.VISIBLE);
                            } else {

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+251" + et_userPhoneNumber.getText().toString(),
                                        60,
                                        TimeUnit.SECONDS,
                                        sendOTP.this,
                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                pb_progressBar.setVisibility(View.GONE);
                                                btn_receiveVerification.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                pb_progressBar.setVisibility(View.GONE);
                                                btn_receiveVerification.setVisibility(View.VISIBLE);
                                                Toast.makeText(sendOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                pb_progressBar.setVisibility(View.GONE);
                                                btn_receiveVerification.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent(getApplicationContext(), verifyOTP.class);
                                                intent.putExtra("number", et_userPhoneNumber.getText().toString());
                                                intent.putExtra("verificationId", verificationId);
                                                startActivity(intent);
                                            }
                                        }
                                );

                            }
                        } else {
                            Toast.makeText(sendOTP.this, "Connection error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private boolean validatePhoneNumber(String userInput) {
        Pattern p = Pattern.compile("[9][0-9]{8}");
        Matcher matcher = p.matcher(userInput);
        return matcher.matches();
    }

}