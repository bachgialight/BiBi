package com.example.bibi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.activity.ImageLabelingActivity;
import com.example.bibi.activity.PostImageActivity;
import com.example.bibi.activity.SettingActivity;
import com.example.bibi.untils.FirebaseUntil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ProfileFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imageProfile,zodiacImage;
    ImageView postImage;
    ImageView tab1,tab2,tab3;
    TextView nameUser,zodiacTxt;
    DocumentReference documentReference;
    int selectionTabNumber = 1;
    TextView birthday,location,gender;
    ImageView settingImage,imageGender,AIImage;
    LottieAnimationView progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
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
        AIImage = view.findViewById(R.id.upload_image_profile);
        AIImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFragment.this.getActivity(), ImageLabelingActivity.class);
                startActivity(intent);
            }
        });
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
        return view;
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