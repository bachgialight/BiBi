package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bibi.R;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class UserNameActivity extends AppCompatActivity {
    EditText userNameInput;
    Button letMeBtn;
    ProgressBar progressBar;
    UsersModel usersModel;
    String phoneNumber;
    ImageView imageProfile;

    String email,password;
    private Uri backgroundImageUri;  // Đường dẫn của ảnh background

    // Các hằng số để xác định các hành động (Gallery hoặc Camera)
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    // Thêm StorageReference để định vị Firebase Storage
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        // Khởi tạo storageReference
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImage");

        userNameInput = findViewById(R.id.input_user_name);
        letMeBtn = findViewById(R.id.button_confirm_otp);
        progressBar = findViewById(R.id.login_progress_bar);
        imageProfile = findViewById(R.id.roundedImageViewProfile);

        // truyền số điện thoạt từ phần OTP
        //phoneNumber = getIntent().getExtras().getString("phone");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");

        getUserName();
        letMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserName();
            }
        });
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                    } else {
                        openGallery();
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            backgroundImageUri = data.getData();
            if (backgroundImageUri != null) {
                imageProfile.setImageURI(backgroundImageUri);
                uploadImageToFirebase();
            } else {
                Toast.makeText(this, "Hình ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadImageToFirebase() {
        try {
            // Ensure an image is selected
            if (backgroundImageUri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_image");

                // Create an identifier based on timestamp
                String imageIdentifier = String.valueOf(System.currentTimeMillis());

                final StorageReference imageFilePath = storageReference.child(imageIdentifier);

                // Upload the image
                imageFilePath.putFile(backgroundImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get the download URL
                            imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();

                                // Update the Firestore document with the image URL
                                updateFirestoreWithImageUrl(imageUrl);

                                Toast.makeText(UserNameActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserNameActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } catch (Exception e) {
            Log.e("Image upload error", e.getMessage());
        }
    }

    private void updateFirestoreWithImageUrl(String imageUrl) {
        if (usersModel != null) {
            usersModel.setProfileImage(imageUrl);
        } else {
            usersModel = new UsersModel();
            usersModel.setEmail(email);
            usersModel.setPassword(password);
            usersModel.setProfileImage(imageUrl);
            usersModel.setCoverImage("");
            // Add other fields as needed

        }

        // Update the Firestore document
        FirebaseUntil.currentUserDetails().set(usersModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {

                        Toast.makeText(this, "" + "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle the case where updating the Firestore document fails
                        Toast.makeText(UserNameActivity.this, "Error updating Firestore document: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PReqCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, mở thư viện ảnh
                openGallery();
            } else {
                // Quyền bị từ chối, hiển thị thông báo
                Toast.makeText(this, "Quyền bị từ chối, bạn không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkAndRequestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(UserNameActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UserNameActivity.this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Hiển thị giải thích về lý do cần quyền
                    Toast.makeText(this, "Vui lòng cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    // Hiển thị hộp thoại yêu cầu quyền
                    ActivityCompat.requestPermissions(UserNameActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                } else {
                    // Yêu cầu quyền trực tiếp nếu chưa được cấp
                    ActivityCompat.requestPermissions(UserNameActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                }
            } else {
                // Nếu quyền đã được cấp, mở thư viện ảnh
                openGallery();
            }
        } else {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
            } else {
                openGallery();
            }
        }
    }


    public void setUserName() {
        String username = userNameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            userNameInput.setError("Tên người dùng cần ít nhất 3 ký tự");
            return;
        }
        setInProgress(true);

        if (usersModel != null) {
            usersModel.setName(username);
        }else  {
            usersModel = new UsersModel();
            usersModel.setName(username);
            //usersModel.setPhone(phoneNumber);

            usersModel.setCreatedTimestamp(Timestamp.now());
        }
        // truyền thông tin dữ liệu của người dùng vào (users của là uid trong auth)
        FirebaseUntil.currentUserDetails().set(usersModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(UserNameActivity.this, BirthdayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    public void getUserName() {
        setInProgress(true);
        FirebaseUntil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(true);
                if (task.isSuccessful()) {
                    UsersModel usersModel = task.getResult().toObject(UsersModel.class);
                    if (usersModel != null) {
                        userNameInput.setText(usersModel.getName());
                    }
                }
            }
        });
    }
    public void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);

        }
    }
}