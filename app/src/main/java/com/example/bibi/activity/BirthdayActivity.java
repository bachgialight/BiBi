package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BirthdayActivity extends AppCompatActivity {
    private Button btnPickDate;
    private Button btnSaveBirthday;
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSaveBirthday = findViewById(R.id.btnSaveBirthday);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSaveBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBirthdayToFirestore();
            }
        });
    }
    private void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        year = currentDate.get(Calendar.YEAR);
        month = currentDate.get(Calendar.MONTH);
        day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;
                    // Hiển thị ngày đã chọn trên button
                    btnPickDate.setText(day + "/" + (month + 1) + "/" + year);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
    private void saveBirthdayToFirestore() {
        // Tạo Timestamp từ ngày đã chọn
        Calendar birthdayCalendar = Calendar.getInstance();
        birthdayCalendar.set(year, month, day);
        Timestamp birthdayTimestamp = new Timestamp(birthdayCalendar.getTime());

        // Lấy thông tin người dùng hiện tại từ Firestore
        FirebaseUntil.currentUserDetails().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Người dùng đã tồn tại trong Firestore
                        UsersModel usersModel = documentSnapshot.toObject(UsersModel.class);

                        if (usersModel != null) {
                            // Cập nhật trường "birthday"
                            usersModel.setBirthday(birthdayTimestamp);

                            // Lưu thông tin người dùng cập nhật vào Firestore
                            FirebaseUntil.currentUserDetails()
                                    .set(usersModel)
                                    .addOnSuccessListener(aVoid -> {
                                        // Xử lý khi dữ liệu được thêm thành công
                                        Intent intent = new Intent(BirthdayActivity.this, SexActivity.class);
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