package com.example.messenger.OTP;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messenger.R;


public class PhoneLoginActivity extends AppCompatActivity {

  //  private FirebaseAuth firebaseAuth;

    private EditText phoneET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        phoneET = findViewById(R.id.editPhoneNumber);
        colorChangeStatusBar();


    }

    public void nextButton(View view) {

        String phone = phoneET.getText().toString();

        if (phone.isEmpty() || phone.length() <10) {
            phoneET.setError("Please Enter Valid Number");
            phoneET.requestFocus();
            return;
        }else {
            startActivity(new Intent(PhoneLoginActivity.this, VerifyActivity.class).putExtra("phone", phone));
        }
    }

    public void backBtn(View view) {
        onBackPressed();
     //   startActivity(new Intent(PhoneLoginActivity.this, AccountActivity.class));
        finish();
    }

    public void colorChangeStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
    }

}
