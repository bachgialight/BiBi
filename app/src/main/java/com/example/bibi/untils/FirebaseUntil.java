package com.example.bibi.untils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUntil {
    // hàm bên dưới là lấy uid của người dùng hiện tại
    public static String currentUid() {
        return FirebaseAuth.getInstance().getUid();
    }
    public static FirebaseAuth userID() {
        return FirebaseAuth.getInstance();
    }
    // code bên dười là để hàm dùng để gọi nhiều lần và lưu vào firebase có tên là users và uid của người dùng hiện tại
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUid());
    }

    public static DocumentReference currentPostDetails() {
        return FirebaseFirestore.getInstance().collection("posts").document(currentUid());
    }
    // hàm bên dưới là dùng để kiểm tra xem người dùng đã đăng nhập tồn tại trong ứng dụng hay chưa
    // thường được kiểm tra ở phần Splash
    public static boolean isLoggedIn() {
        if (currentUid() != null) {
            return true;
        }
        return false;
    }
}
