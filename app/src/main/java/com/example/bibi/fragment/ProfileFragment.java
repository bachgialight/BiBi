package com.example.bibi.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.activity.ImageLabelingActivity;
import com.example.bibi.activity.PostImageActivity;
import com.example.bibi.activity.SettingActivity;
import com.example.bibi.activity.SettingProfileActivity;
import com.example.bibi.activity.UserNameActivity;
import com.example.bibi.model.UsersModel;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ProfileFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imageProfile,zodiacImage;
    ImageView postImage;
    ImageView tab1,tab2,tab3;
    TextView nameUser,zodiacTxt;
    DocumentReference documentReference;
    int selectionTabNumber = 1;
    TextView birthday,location,gender;
    ImageView settingImage,imageGender,AIImage,coverImage,backgroundUser;
    LottieAnimationView progressBar;
    Button btnEditProfile;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView countPosts;
    private Uri backgroundImageUri;  // Đường dẫn của ảnh background

    // Các hằng số để xác định các hành động (Gallery hoặc Camera)
    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    // Thêm StorageReference để định vị Firebase Storage
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        imageProfile = view.findViewById(R.id.image_profile);
        nameUser = view.findViewById(R.id.user_name);
        postImage = view.findViewById(R.id.post_image);
        progressBar = view.findViewById(R.id.progress_circular);
        zodiacImage = view.findViewById(R.id.zodiac_image);
        zodiacTxt =view.findViewById(R.id.zodiac_txt);
        birthday = view.findViewById(R.id.birth_day);
        location = view.findViewById(R.id.location);
        gender = view.findViewById(R.id.gender);
        settingImage = view.findViewById(R.id.setting);
        imageGender = view.findViewById(R.id.gender_image);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        tab1  =view.findViewById(R.id.tabItem1);
        tab2  =view.findViewById(R.id.tabItem2);
        tab3  =view.findViewById(R.id.tabItem3);
        coverImage  = view.findViewById(R.id.background_user);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //AIImage = view.findViewById(R.id.upload_image_profile);
        backgroundUser = view.findViewById(R.id.image_cover_profile);
        countPosts = view.findViewById(R.id.quantity_follower_user_profile);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_cover);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                LinearLayout layoutAddCover = dialog.findViewById(R.id.add_image_cover);
                LinearLayout layoutDeleteCover = dialog.findViewById(R.id.delete_cover_image);

                layoutAddCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= 22) {
                            checkAndRequestForPermission();
                        } else {
                            if (getActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                            } else {
                                openGallery();
                            }
                        }
                    }
                });
                layoutDeleteCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // cậ nhật lại ảnh bìa bằng ""
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(user.getUid())
                                .update("coverImage","")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ProfileFragment.this.getActivity(), "Xóa ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });
        // chuyển động
        final Animation zoomInAnim = AnimationUtils.loadAnimation(ProfileFragment.this.getActivity(), R.anim.zoom_in);
        final Animation zoomOutAnim = AnimationUtils.loadAnimation(ProfileFragment.this.getActivity(), R.anim.zoom_out);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageProfile.startAnimation(zoomInAnim);
                imageProfile.startAnimation(zoomOutAnim);

            }
        });

        // điếm số lượng bài viết của người dùng
        countPostsImage();
//        AIImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ProfileFragment.this.getActivity(), ImageLabelingActivity.class);
//                startActivity(intent);
//            }
//        });
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container,new ImageUserFragment(), null)
                .commit();
        // Hiển thị ProgressBar khi bắt đầu đăng nhập

        getShowProfile();
        //chuyển màn hình qua lại giữa các fragment
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(2);

            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(3);

            }
        });
        // đăng bài viết
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), PostImageActivity.class);
                startActivity(intent);
            }
        });

        // load lại màn hình
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getShowProfile();
            }
        });
        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), SettingProfileActivity.class);
                startActivity(intent);
            }
        });
        // cập nhật ảnh đại hiện cho người dùng

        return view;
    }

    private void countPostsImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereEqualTo("uid",FirebaseUntil.currentUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int numberOfPosts = queryDocumentSnapshots.size();
                        countPosts.setText(numberOfPosts + " ảnh");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }
    private void checkAndRequestForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileFragment.this.getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileFragment.this.getActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
                    // Hiển thị giải thích về lý do cần quyền
                    Toast.makeText(ProfileFragment.this.getActivity()   , "Vui lòng cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
                    // Hiển thị hộp thoại yêu cầu quyền
                    ActivityCompat.requestPermissions(ProfileFragment.this.getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                } else {
                    // Yêu cầu quyền trực tiếp nếu chưa được cấp
                    ActivityCompat.requestPermissions(ProfileFragment.this.getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
                }
            } else {
                // Nếu quyền đã được cấp, mở thư viện ảnh
                openGallery();
            }
        } else {
            if (getActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, PReqCode);
            } else {
                openGallery();
            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            backgroundImageUri = data.getData();
            if (backgroundImageUri != null) {
                backgroundUser.setImageURI(backgroundImageUri);
                uploadImageToFirebase();
            } else {
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

                                Toast.makeText(ProfileFragment.this.getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProfileFragment.this.getActivity(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } catch (Exception e) {
            Log.e("Image upload error", e.getMessage());
        }
    }
    private void updateFirestoreWithImageUrl(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo một Map để cập nhật dữ liệu
        Map<String, Object> updates = new HashMap<>();
        updates.put("coverImage", imageUrl);

        // Cập nhật dữ liệu trong tài liệu của người dùng
        db.collection("users").document(user.getUid()).update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xử lý khi cập nhật thành công
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi cập nhật thất bại
                    }
                });
    }

    private void getShowProfile() {
        progressBar.setVisibility(View.VISIBLE);
        // lấy thông tin của người dùng hiện tại ra và truyền vào avatar
        documentReference = FirebaseUntil.currentUserDetails();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // nếu lấy ra thành cônghue
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        progressBar.setVisibility(View.GONE);
                        // lấy url và tên của người dùng
                        String imageUrl = documentSnapshot.getString("profileImage");
                        String nameProfile = documentSnapshot.getString("name");
                        String sex = documentSnapshot.getString("sex");
                        String background  = documentSnapshot.getString("coverImage");
                        Timestamp timestamp = documentSnapshot.getTimestamp("birthday");
                        if (getActivity() != null && imageUrl != null && !imageUrl.isEmpty() && nameProfile != null && !nameProfile.isEmpty()) {
                            Glide.with(ProfileFragment.this).load(imageUrl).into(imageProfile);
                            nameUser.setText(nameProfile);
                        }
                        gender.setText(sex);
                        // kiểm tra xem nam hay nữ để chọn icon
                        if (sex.equals("Nam")) {
                            imageGender.setImageResource(R.drawable.male);
                        }else {
                            imageGender.setImageResource(R.drawable.female);

                        }
                        // chuyển sang ngày /tháng/ năm
                        Date birthDate = timestamp.toDate();
                        Glide.with(getActivity()).load(background).into(backgroundUser);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate =simpleDateFormat.format(birthDate);
                        birthday.setText(formattedDate);
                        // Lấy Timestamp từ Firestore

                        // Chuyển đổi Timestamp thành Date

                        // Xác định cung hoàng đạo
                        String zodiacSign = getZodiacSign(birthDate);

                        // Thiết lập text và icon tương ứng
                        zodiacTxt.setText(zodiacSign);
                        setZodiacIcon(zodiacSign);
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void selectTab(int position) {
        ImageView selection1;
        ImageView selection2;
        ImageView selection3;
        if (position == 1) {
            selection1 = tab1;
            selection2 = tab2;
            selection3 = tab3;
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, new ImageUserFragment(), null)
                    .commit();
        }else if (position == 2) {
            selection1 = tab2;
            selection2 = tab1;
            selection3 = tab3;
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container,new InfomationUserFragment(), null)
                    .commit();
        }
        else  {
            selection1 = tab3;
            selection2 = tab1;
            selection3 = tab2;
            getChildFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, new HeloFragment(),null)
                    .commit();
        }

        float slideTo = (position - selectionTabNumber) * selection1.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(0,slideTo,0,0);
        translateAnimation.setDuration(100);
        if (selectionTabNumber == 1) {
            tab1.startAnimation(translateAnimation);
        }else if (selectionTabNumber == 2) {
            tab2.startAnimation(translateAnimation);
        }else {
            tab3.startAnimation(translateAnimation);
        }

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                selection1.setBackgroundResource(R.drawable.round_back_100);

                selection2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                selection3.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        selectionTabNumber = position;
    }


    private void setZodiacIcon(String zodiacSign) {
        // Dựa vào tên cung hoàng đạo, đặt icon tương ứng
        // Ví dụ: nếu có 12 icon cho 12 cung hoàng đạo, bạn có thể sử dụng một Switch-Case hoặc Map để ánh xạ tên cung hoàng đạo với resource ID của icon.
        // Sau đó, bạn có thể đặt hình ảnh vào ImageView.
        // Ví dụ:
        int iconResourceId = getIconResourceId(zodiacSign);
        zodiacImage.setImageResource(iconResourceId);
    }
    private int getIconResourceId(String zodiacSign) {
        // Đặt mã nguồn hình ảnh tương ứng với tên cung hoàng đạo
        // Ví dụ: R.drawable.bachduong, R.drawable.kimnguu, ...
        // Thay thế đoạn mã này bằng cách ánh xạ tên cung hoàng đạo với resource ID của icon.
        // Bạn có thể sử dụng Map hoặc điều gì đó tương tự để thực hiện điều này.
        // Đây chỉ là một ví dụ đơn giản.
        switch (zodiacSign) {
            case "Bạch Dương":
                return R.drawable.aries;
            case "Kim Ngưu":
                return R.drawable.taurus;
            case "Thiên Bình":
                return R.drawable.libra;
            case "Sư Tử":
                return R.drawable.leo;
            case "Xử Nữ":
                return R.drawable.virgo;
            case "Bọ Cạp":
                return R.drawable.taurus;
            case "Song Ngư":
                return R.drawable.pisces;
            case "Ma Kết":
                return R.drawable.capricorn;
            case "Nhân Mã":
                return R.drawable.sagittarius;
            case "Bảo Bình":
                return R.drawable.aquarius;
            case "Cự Giải":
                return R.drawable.cancer;
            case "Song Tử":
                return R.drawable.gemini;
            // Thêm các trường hợp khác ở đây...
            default:
                return R.drawable.camera;
        }
    }

    private String getZodiacSign(Date birthDate) {
        // Xác định cung hoàng đạo
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birthDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0

        String zodiacSign = "";

        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) {
            zodiacSign = "Bạch Dương";
        } else if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) {
            zodiacSign = "Kim Ngưu";
        } else if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) {
            zodiacSign = "Song Tử";
        } else if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) {
            zodiacSign = "Cự Giải";
        } else if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) {
            zodiacSign = "Sư Tử";
        } else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) {
            zodiacSign = "Xử Nữ";
        } else if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) {
            zodiacSign = "Thiên Bình";
        } else if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) {
            zodiacSign = "Bọ Cạp";
        } else if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) {
            zodiacSign = "Nhân Mã";
        } else if ((month == 12 && day >= 22) || (month == 1 && day <= 19)) {
            zodiacSign = "Ma Kết";
        } else if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) {
            zodiacSign = "Bảo Bình";
        } else if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) {
            zodiacSign = "Song Ngư";
        }

        return zodiacSign;
    }

}