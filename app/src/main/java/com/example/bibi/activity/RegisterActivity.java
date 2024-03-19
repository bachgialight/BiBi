package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText editTextEmail,editTextPassWord;
    private FirebaseAuth mAuth;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        Button nextBtn = findViewById(R.id.nextBtn);
        editTextEmail = findViewById(R.id.input_email);
        editTextPassWord = findViewById(R.id.inputPassWord);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void registerUser() {
        email = editTextEmail.getText().toString();
        password = editTextPassWord.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUntil.userID().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng ký thành công
                            Toast.makeText(RegisterActivity.this, "Đã đăng ký", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, UserNameActivity.class);
                            intent.putExtra("email",email);
                            intent.putExtra("password",password);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();  // Đóng activity sau khi lưu thành công
                            // Lưu thông tin người dùng vào Firestore
                            //saveUserToFirestore(email,password);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void saveUserToFirestore(String email, String password) {
//        FirebaseUntil.currentUserDetails().get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        // Người dùng đã tồn tại trong Firestore
//                        UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);
//
//                        if (usersModel != null) {
//                            // Cập nhật trường "email" và "password"
//                            Map<String, Object> updateData = new HashMap<>();
//                            updateData.put("email", email);
//                            updateData.put("password", password);
//
//                            // Lưu thông tin người dùng cập nhật vào Firestore
//                            FirebaseUntil.currentUserDetails()
//                                    .update(updateData)
//                                    .addOnSuccessListener(aVoid -> {
//                                        // Xử lý khi dữ liệu được cập nhật thành công
//                                        Log.d("RegisterActivity", "Thông tin người dùng đã được cập nhật thành công");
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Xử lý khi có lỗi xảy ra khi cập nhật dữ liệu
//                                        Log.e("RegisterActivity", "Lỗi khi cập nhật thông tin người dùng", e);
//                                    });
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Xử lý khi có lỗi xảy ra khi đọc dữ liệu từ Firestore
//                    Log.e("RegisterActivity", "Lỗi khi đọc thông tin người dùng từ Firestore", e);
//                });
//    }


}