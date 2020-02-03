package com.example.messenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.OTP.PhoneLoginActivity;
import com.example.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {
    private TextView chatUserActiveStatus, ChatConnectionTV;

    private FirebaseUser currentUser;
    Button loginButton , phoneLoginButton;
    private EditText userEmail, userPassword;
    TextView needNewAccount, forgetPaw;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For all full screen then .

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_login);

        init();

        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });


        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, PhoneLoginActivity.class));
            }
        });
    }

    private void AllowUserToLogin() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Enter your email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle("Sign In");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        sendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Login is Successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String meg = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error " + meg, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();
        loginButton = findViewById(R.id.loginBtn);
        phoneLoginButton = findViewById(R.id.phone_using);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        needNewAccount = findViewById(R.id.needNewAccount);
        forgetPaw = findViewById(R.id.forgetPassword);
        progressDialog = new ProgressDialog(this);

        ChatConnectionTV = findViewById(R.id.ChatConnectionTV);

    }

  /*  @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }*/

    private void sendUserToMainActivity() {
        Intent intent =new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        startActivity(new Intent(this,RegisterActivity.class));
    }



    // Broadcast receiver for network checking
    public class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            ChatConnectionTV.setVisibility(View.GONE);
            if (networkInfo != null && networkInfo.isConnected()){
                ChatConnectionTV.setText("Internet connected");
                ChatConnectionTV.setTextColor(Color.WHITE);
                ChatConnectionTV.setVisibility(View.VISIBLE);

                // LAUNCH activity after certain time period
                new Timer().schedule(new TimerTask(){
                    public void run() {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                ChatConnectionTV.setVisibility(View.GONE);
                            }
                        });
                    }
                }, 1200);
            } else {
                ChatConnectionTV.setText("No internet connection! ");
                ChatConnectionTV.setTextColor(Color.WHITE);
                ChatConnectionTV.setBackgroundColor(Color.RED);
                ChatConnectionTV.setVisibility(View.VISIBLE);
            }
        }
    }

}


