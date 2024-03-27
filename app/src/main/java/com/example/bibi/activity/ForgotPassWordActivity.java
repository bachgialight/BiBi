package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bibi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotPassWordActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    EditText inputSendPassWord;
    Button btnSendEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        inputSendPassWord = findViewById(R.id.input_forgot_email);
        btnSendEmail = findViewById(R.id.btn_send_email);
        //bắt sự kiện nhấn để gửi email
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPassWordToEmail();
            }
        });
    }

    private Boolean validateEmail() {
        String email = inputSendPassWord.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.isEmpty()) {
            inputSendPassWord.setError("Bạn chưa nhập Email");
            return false;

        }if (!email.matches(emailPattern)) {
            inputSendPassWord.setError("Định dạng Email của bạn chưa hợp lệ");
            return false;
        }
        return true;

    }
    private void sendPassWordToEmail() {
        if (!validateEmail()) {
            return;
        }
        auth.sendPasswordResetEmail(inputSendPassWord.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(ForgotPassWordActivity.this,LoginActivity.class));
                    finish();
                    Toast.makeText(ForgotPassWordActivity.this, "Vui lòng kiểm tra email của bạn", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ForgotPassWordActivity.this, "Gửi không thành công", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassWordActivity.this, "Gửi không thành công", Toast.LENGTH_SHORT).show();

            }
        });
    }
}