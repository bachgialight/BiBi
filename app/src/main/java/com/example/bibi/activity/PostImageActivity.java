package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bibi.R;
import com.example.bibi.model.PostsModel;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostImageActivity extends AppCompatActivity {
    ImageView postImage,cancelImage;
    Button ButtonShare;
    EditText editTextNote;


    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    private Uri backgroundImageUri;  // Đường dẫn của ảnh background
    ProgressBar progressBar;
    private DocumentReference imageDocument;  // Thêm biến thành viên
    TextView upload;
    LottieAnimationView progress;

    LinearLayout settingObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);
        postImage = findViewById(R.id.post_image);
        cancelImage = findViewById(R.id.cancel);
        editTextNote = findViewById(R.id.edit_text_note);
        ButtonShare = findViewById(R.id.button_up_load);
        progress = findViewById(R.id.progress_circular);
        settingObject = findViewById(R.id.setting_object);
        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        postImage.setOnClickListener(new View.OnClickListener() {
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
        ButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToFirebase();
            }
        });

        settingObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // chuyển hướng đến cày đặt đối tượng có thể thấy được bài viết bằng cách hiện thị dialog
                final Dialog dialog = new Dialog(PostImageActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.object_can_see);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_diaglog);
                Switch switchEveryone = dialog.findViewById(R.id.switch_everyone);
                Switch switchFollowMe = dialog.findViewById(R.id.switch_follow_me);
                Switch switchMe = dialog.findViewById(R.id.switch_me);
                switchEveryone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // kiểm tra xem nếu switch hiện tại là true(bật) thì những switch khác thì false(tắt)
                            switchFollowMe.setChecked(false);
                            switchMe.setChecked(false);
                        }
                    }
                });

                switchFollowMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            switchEveryone.setChecked(false);
                            switchMe.setChecked(false);
                        }
                    }
                });

                switchMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            switchEveryone.setChecked(false);
                            switchFollowMe.setChecked(false);
                        }
                    }
                });
                dialog.show();

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
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
                postImage.setImageURI(backgroundImageUri);
            } else {
                Toast.makeText(this, "Hình ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Sau đó, trong hàm uploadImageToFirebase:
    private void uploadImageToFirebase() {
        progress.setVisibility(View.VISIBLE);
        try {
            // Ensure an image is selected
            if (backgroundImageUri != null) {
                // Label the image using Firebase ML Kit
                labelImage();
            }
        } catch (Exception e) {
            Log.e("Image upload error", e.getMessage());
        }
    }
    private void labelImage() {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(this, backgroundImageUri);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

            labeler.processImage(image)
                    .addOnSuccessListener(labels -> {
                        processLabels(labels);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PostImageActivity.this, "Labeling failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processLabels(List<FirebaseVisionImageLabel> labels) {
        // Process and save the labels as needed
        Map<String, Float> labelMap = new HashMap<>();

        for (FirebaseVisionImageLabel label : labels) {
            String text = label.getText();
            float confidence = label.getConfidence();

            // Store label and confidence in the map
            labelMap.put(text, confidence);
        }

        // Save the labelMap to Firestore (you can modify this to suit your Firestore structure)
        saveLabelsAndUploadImageToFirestore(labelMap);
    }

    private void saveLabelsAndUploadImageToFirestore(Map<String, Float> labelMap) {
        // Khai báo và khởi tạo backgroundImageUri ở đây
        // Uri backgroundImageUri = ...;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("posts");

        // Create an identifier based on timestamp
        String imageIdentifier = String.valueOf(System.currentTimeMillis());

        // Replace "posts" with your desired collection name
        CollectionReference postsCollection = db.collection("posts");

        // Create a new document for each image
        DocumentReference imageDocument = postsCollection.document();

        // Upload the image to Storage
        uploadImageToStorage(storageReference, imageIdentifier, imageDocument, labelMap, backgroundImageUri);
    }
    // dùng Firebase Machine Learning Kit để gắn nhãn cho hình ảnh bằng labelMap,nhằm xác định được nội dung,
    // khả năng nhận dạng hình ảnh
    private void uploadImageToStorage(StorageReference storageReference, String imageIdentifier, DocumentReference imageDocument, Map<String, Float> labelMap, Uri backgroundImageUri) {
        final StorageReference imageFilePath = storageReference.child(imageIdentifier);

        // Upload the image
        imageFilePath.putFile(backgroundImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Perform image labeling using Firebase ML Kit
                        FirebaseVisionImage image = null;
                        try {
                            image = FirebaseVisionImage.fromFilePath(PostImageActivity.this, backgroundImageUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        FirebaseVision.getInstance()
                                .getOnDeviceImageLabeler()
                                .processImage(image)
                                .addOnSuccessListener(labels -> {
                                    // Process the labels
                                    for (FirebaseVisionImageLabel label : labels) {
                                        labelMap.put(label.getText(), label.getConfidence());
                                    }

                                    // Update the existing document in the "posts" collection with additional fields
                                    imageDocument.set(new HashMap<String, Object>() {{
                                                put("title", editTextNote.getText().toString());
                                                put("uid", FirebaseUntil.currentUid());
                                                put("postImage", imageUrl);
                                                put("labelMap", labelMap);
                                                put("timestamp", System.currentTimeMillis());
                                                // Add other fields as needed
                                            }}, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                // Handle success
                                                String postId = imageDocument.getId();
                                                Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("postId", postId);

                                                // Update the document with postId
                                                imageDocument.update(updateData)
                                                        .addOnSuccessListener(aVoid1 -> {
                                                            // Handle success
                                                            progress.setVisibility(View.GONE);

                                                            Toast.makeText(PostImageActivity.this, "Đăng thành công", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            // Handle failure
                                                            Toast.makeText(PostImageActivity.this, "Cập nhật postId thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure
                                                Toast.makeText(PostImageActivity.this, "Đăng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(PostImageActivity.this, "Error labeling image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(PostImageActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    public static CollectionReference postsCollection() {
        return FirebaseFirestore.getInstance().collection("posts");
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
            if (ContextCompat.checkSelfPermission(PostImageActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(PostImageActivity.this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // Hiển thị giải thích về lý do cần quyền
                    Toast.makeText(this, "Vui lòng cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    // Hiển thị hộp thoại yêu cầu quyền
                    ActivityCompat.requestPermissions(PostImageActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                } else {
                    // Yêu cầu quyền trực tiếp nếu chưa được cấp
                    ActivityCompat.requestPermissions(PostImageActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
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

}