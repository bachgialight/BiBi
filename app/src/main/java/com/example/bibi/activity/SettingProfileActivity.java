package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingProfileActivity extends AppCompatActivity {
    ImageView imageViewProfile,btnBack;
    EditText editName,editSex,editBio,editEmail,editWebUrl;
    FirebaseAuth auth;
    FirebaseUser user;
    Button btnUpdateFinish;
    LottieAnimationView progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);
        progress = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.back_image);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        imageViewProfile = findViewById(R.id.edit_profile);
        editName = findViewById(R.id.edit_name);
        editSex = findViewById(R.id.edit_sex);
        editBio = findViewById(R.id.edit_bio);
        editEmail = findViewById(R.id.edit_email);
        btnUpdateFinish = findViewById(R.id.btn_update_finish);
        editWebUrl = findViewById(R.id.edit_web_url);
        // phần này là lấy ra phần dữ liệu của người dùng hiện tại
        showProfile();
        btnUpdateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showProfile() {
        progress.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progress.setVisibility(View.GONE);
                        String name = documentSnapshot.getString("name");
                        String imageUrl = documentSnapshot.getString("profileImage");
                        String email  = documentSnapshot.getString("email");
                        String sex = documentSnapshot.getString("sex");

                        editSex.setText(sex);
                        editEmail.setText(email);
                        editName.setText(name);
                        Glide.with(getApplicationContext()).load(imageUrl).into(imageViewProfile);
                    }
                });
    }

    private void updateProfile() {
        progress.setVisibility(View.VISIBLE);

        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String sex = editSex.getText().toString();
        String website = editWebUrl.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .update("name",name,"sex",sex,"website",website)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progress.setVisibility(View.GONE);

                                Toast.makeText(SettingProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

}