package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SexActivity extends AppCompatActivity {
    Button maleButton,femaleButton,otherButton,nextButton;
    String selectedGender;
    Button selectedButton; // Biến để theo dõi nút được chọn
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);

        // ánh xạ trong phần sex.xml
        maleButton = findViewById(R.id.radioButtonMale);
        femaleButton = findViewById(R.id.radioButtonFemale);
        otherButton = findViewById(R.id.radioButtonOther);
        nextButton = findViewById(R.id.nextBtn);

        // Set click listeners for gender buttons
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGender("Nam");
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGender("Nữ");
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGender("Khác");
            }
        });

        // Set click listener for the "Tiếp tục" button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFirestore();
            }
        });
    }

    private void selectGender(String gender) {
        // Reset color for all buttons
        resetButtonColors();

        // Change color for the selected button
        if ("Nam".equals(gender)) {
            maleButton.setBackgroundColor(getResources().getColor(R.color.light_white));
        } else if ("Nữ".equals(gender)) {
            femaleButton.setBackgroundColor(getResources().getColor(R.color.light_white));
        } else if ("Khác".equals(gender)) {
            otherButton.setBackgroundColor(getResources().getColor(R.color.light_white));
        }

        // Save the selected gender
        selectedGender = gender;
    }
    private void resetButtonColors() {
        maleButton.setBackgroundColor(getResources().getColor(R.color.male));
        femaleButton.setBackgroundColor(getResources().getColor(R.color.female));
        otherButton.setBackgroundColor(getResources().getColor(R.color.other));
    }
    private void saveToFirestore() {
        FirebaseUntil.currentUserDetails().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Người dùng đã tồn tại trong Firestore
                        UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                        if (usersModel != null) {
                            // Cập nhật trường "birthday"
                            usersModel.setSex(selectedGender);
                            usersModel.setUid(FirebaseUntil.currentUid());
                            // Lưu thông tin người dùng cập nhật vào Firestore
                            FirebaseUntil.currentUserDetails()
                                    .set(usersModel)
                                    .addOnSuccessListener(aVoid -> {
                                        // Xử lý khi dữ liệu được thêm thành công
                                        Intent intent = new Intent(SexActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();  // Đóng activity sau khi lưu thành công
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý khi có lỗi xảy ra
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi có lỗi xảy ra khi đọc dữ liệu từ Firestore
                });
    }
}