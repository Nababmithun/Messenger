package com.example.messenger.OTP;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.R;
import com.example.messenger.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {

    private String phoneNumber;
    private EditText verificationCodeET;
    private Button loginBtn;
    private String verificationId;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);


        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.loginBtn);
        verificationCodeET = findViewById(R.id.verificationET);
        phoneNumber = getIntent().getStringExtra("phone");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = verificationCodeET.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    verificationCodeET.setError("Please Enter valid Code.");
                    verificationCodeET.requestFocus();
                    return;
                } else {
                    verify(code);
                }
            }
        });

        sendOTP();

        colorChangeStatusBar();

    }

    private void sendOTP() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+880" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
           String code = phoneAuthCredential.getSmsCode();
            if (code != null && code.equals(code)) {
                verificationCodeET.setText(code);
                verify(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
    };

    private void verify(String code) {
        if (code!=null){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        }else
            Toast.makeText(this, "Please wait for code!", Toast.LENGTH_SHORT).show();

    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                        Toast.makeText(VerifyActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(VerifyActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void colorChangeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

    public void backBtn(View view) {
        onBackPressed();
        startActivity(new Intent(VerifyActivity.this, PhoneLoginActivity.class));
        finish();
    }
}
