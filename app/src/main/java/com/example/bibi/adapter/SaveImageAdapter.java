package com.example.bibi.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.bumptech.glide.Glide;
import com.example.bibi.R;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SaveImageAdapter extends RecyclerView.Adapter<SaveImageAdapter.ViewHodler> {
    Context context;
    List<PostsModel> list;
    FirebaseAuth auth;
    FirebaseUser user;
    public SaveImageAdapter(Context context, List<PostsModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
    @NonNull
    @Override
    public SaveImageAdapter.ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_item_image,parent,false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveImageAdapter.ViewHodler holder, int position) {
        PostsModel postsModel = list.get(position);
        Glide.with(context).load(list.get(position).getPostImage()).into(holder.imageViewSaveImage);
        // thiện hiện việc thêm xóa và sửa bằng dialog
        holder.showDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_item_save_image);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);// đặc nền
                LinearLayout deleteCollection = dialog.findViewById(R.id.delete_collection_image);
                deleteCollection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openShowDialogDelete(Gravity.CENTER,postsModel.getPostId());
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);// đặc kích thức cho phần layout hiện thị
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;// chuyển động của dialog
                dialog.getWindow().setGravity(Gravity.BOTTOM);// thiết lập vị trí mà dialog hiện thị là bên dưới
            }
        });

    }

    private void openShowDialogDelete(int center,String postId) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_deletec_ios);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        // Đặt nền cho dialog từ drawable
        window.setBackgroundDrawableResource(R.drawable.dialog_bg_white);
        WindowManager.LayoutParams windownArr= window.getAttributes();
        windownArr.gravity = center;
        // Tạo MarginLayoutParams và đặt margin
        window.setAttributes(windownArr);
        if (Gravity.CENTER == center) {
            dialog.setCancelable(true);
        }else {
            dialog.setCancelable(false);
        }
        // thực hiện việc xóa
        Button btnDelete = dialog.findViewById(R.id.button_delete);
        Button btnCancel = dialog.findViewById(R.id.button_cancel);
        // truy vấn vào firestore để thực hiện việc sô data
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa dữ liệu trong Firestore
                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("saveImage")
                        .document(user.getUid())
                        .collection("saveImages")
                        .document(postId);
                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Xóa thành công, có thể thực hiện các hành động cần thiết
                                showSuccessAnimation();
                                dialog.dismiss(); // Đóng dialog sau khi xóa thành công
                                
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi xóa thất bại
                                // Ví dụ: hiển thị thông báo lỗi
                                Toast.makeText(context, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Sự kiện click cho nút "Hủy"
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Đóng dialog khi nhấn vào nút "Hủy"
            }
        });

        // Hiển thị dialog
        // Hiển thị dialog
        dialog.show();
    }
    private void showSuccessAnimation() {
        // Tạo dialog
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);

        // Lấy ra LottieAnimationView từ layout của dialog
        LottieAnimationView animationView = dialog.findViewById(R.id.success);

        // Load hoạt cảnh từ tệp raw
        LottieCompositionFactory.fromRawRes(context, R.raw.success)
                .addListener(new LottieListener<LottieComposition>() {
                    @Override
                    public void onResult(LottieComposition result) {
                        // Nạp hoạt cảnh thành công
                        animationView.setComposition(result);
                        animationView.playAnimation();
                    }
                })
                .addFailureListener(new LottieListener<Throwable>() {
                    @Override
                    public void onResult(Throwable throwable) {
                        // Xử lý khi có lỗi xảy ra trong quá trình nạp hoạt cảnh
                        throwable.printStackTrace();
                    }
                });

        // Hiển thị dialog
        dialog.show();

        // Sử dụng Handler để đợi 4 giây
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Sau 4 giây, đóng dialog
                dialog.dismiss();
            }
        }, 2200); // Thời gian đợi là 4 giây (4000 mili giây)
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {
        ImageView imageViewSaveImage,showDialog;
        public ViewHodler(@NonNull View itemView) {
            super(itemView);
            imageViewSaveImage = itemView.findViewById(R.id.matching_image);
            showDialog = itemView.findViewById(R.id.show_item_dialog);
        }
    }
}
