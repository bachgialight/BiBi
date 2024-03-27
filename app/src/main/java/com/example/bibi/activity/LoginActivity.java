package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {
    EditText editTextInputEmail,editTextInputPassWord;
    TextView forgotPassWord,navigationRegister;
    Button btnLogin;
    LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextInputEmail = findViewById(R.id.input_login_email);
        editTextInputPassWord = findViewById(R.id.input_login_passWord);
        btnLogin = findViewById(R.id.login_btn);
        forgotPassWord = findViewById(R.id.forgot_password);
        navigationRegister = findViewById(R.id.navigation_register);
        progressBar = findViewById(R.id.progressBar);

        // đăng nhập bằng email và password;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inPutLoginEmailAndPassWord();
            }
        });
        forgotPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassWordActivity.class);
                startActivity(intent);
            }
        });
        navigationRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void inPutLoginEmailAndPassWord() {
        progressBar.setVisibility(View.VISIBLE);

        String email = editTextInputEmail.getText().toString();
        String password = editTextInputPassWord.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Đăng nhập địa chỉ email", Toast.LENGTH_SHORT).show();

        }if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Đăng nhập password", Toast.LENGTH_SHORT).show();
        }
        FirebaseUntil.userID().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}