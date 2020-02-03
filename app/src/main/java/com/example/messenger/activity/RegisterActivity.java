package com.example.messenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    Button registerBtn;
    TextView allReadyHaveAccount;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //For Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_register);

        init();

        allReadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });


    }

    private void CreateNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter your email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Creating new Account");
            progressDialog.setMessage("Please wait , while we are Creating new account for you..");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                rootRef.child("users").child(currentUserId).setValue("");
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created successful", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                            } else {
                                String meg = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error " + meg, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }

    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void init() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        registerBtn = findViewById(R.id.registrationBtn);
        userEmail = findViewById(R.id.registration_email);
        userPassword = findViewById(R.id.registration_password);
        allReadyHaveAccount = findViewById(R.id.alreadyHaveAnAccount);
    }

    private void sendUserToLoginActivity() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

    }
}
