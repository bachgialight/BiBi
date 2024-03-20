package com.example.bibi.adapter;

import static com.example.bibi.fragment.HomeFragment.postsCollection;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bibi.Content_Based_Image_Retrieval.ImageProcessingUtils;
import com.example.bibi.Content_Based_Image_Retrieval.ImageSearchHelper;
import com.example.bibi.R;
import com.example.bibi.activity.SimilarArticlesActivity;
import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.annotations.Nullable;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {
    // phần này hiện danh sách các bài post của người dùng
    Context context;
    private boolean isLiked = false;
    List<PostsModel> list;
    private Interpreter interpreter;
    FirebaseFirestore firestore;
    public PostImageAdapter(Context context, List<PostsModel> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public PostImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_container,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImageAdapter.ViewHolder holder, int position) {
        PostsModel postsModel = list.get(position);
        Glide.with(context).load(postsModel.getPostImage()).into(holder.postImage);
        // Load ảnh từ Firebase Storage và hiển thị bằng Glide
        // hiện chi tiết bài viết
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiện dialog phóng to ảnh
                showZoomDialog(postsModel.getPostImage(), postsModel.getTitle(),postsModel.getPostId(),postsModel.getUid(),postsModel.getLabelMap());
            }
        });
        holder.imageViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_report);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);

                LinearLayout downLoadImage = dialog.findViewById(R.id.down_load_image);
                downLoadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Kiểm tra xem postsModel có phải là đối tượng của lớp PostsModel hay không
                        if (postsModel instanceof PostsModel) {
                            downloadImage(((PostsModel) postsModel).getPostImage());
                        } else {
                            // Xử lý trường hợp không phải là PostsModel
                            // (có thể hiển thị log hoặc thông báo lỗi)
                            Log.e("PostImageAdapter", "Invalid type for postsModel");
                        }
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
        holder.imageViewMatchingSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy postId từ đối tượng Post
                String postId = list.get(position).getPostId();

                // Kiểm tra xem postId có tồn tại không
                if (postId != null) {
                    // Lấy tham chiếu đến tài liệu trong Firestore dựa trên postId
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("posts").document(postId);

                    // Lấy dữ liệu từ Firestore
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Kiểm tra nếu có labelMap
                                if (documentSnapshot.contains("labelMap")) {
                                    Map<String, Double> currentLabelMap = (Map<String, Double>) documentSnapshot.get("labelMap");

                                    // Lấy 3 phần tử có giá trị phần trăm cao nhất từ currentLabelMap
                                    List<Map.Entry<String, Double>> sortedLabels = new ArrayList<>(currentLabelMap.entrySet());
                                    sortedLabels.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
                                    List<Map.Entry<String, Double>> top3Labels = sortedLabels.subList(0, Math.min(3, sortedLabels.size()));

                                    // Tạo danh sách các bài đăng khớp nhãn
                                    List<PostsModel> matchingPosts = new ArrayList<>();

                                    // Lặp qua tất cả các bài đăng
                                    db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot postSnapshot : task.getResult()) {
                                                    // Kiểm tra xem bài đăng có labelMap không
                                                    if (postSnapshot.contains("labelMap")) {
                                                        Map<String, Double> postLabelMap = (Map<String, Double>) postSnapshot.get("labelMap");
                                                        boolean containsTopLabel = false;
                                                        // Kiểm tra xem bài đăng có chứa ít nhất một trong 3 nhãn cao nhất không
                                                        for (Map.Entry<String, Double> entry : top3Labels) {
                                                            if (postLabelMap.containsKey(entry.getKey())) {
                                                                containsTopLabel = true;
                                                                break;
                                                            }
                                                        }
                                                        // Nếu bài đăng chứa ít nhất một trong 3 nhãn cao nhất, thêm vào danh sách matchingPosts
                                                        if (containsTopLabel) {
                                                            matchingPosts.add(postSnapshot.toObject(PostsModel.class));
                                                        }
                                                    }
                                                }
                                                // Hiển thị dialog với danh sách các bài đăng khớp nhãn
                                                showDialogWithMatchingPosts(matchingPosts);
                                            } else {
                                                // Xử lý khi không lấy được dữ liệu từ Firestore
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "Không có labelMap", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Lỗi khi truy xuất dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "Không có postId", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // Show dialog with matching posts
    private void showDialogWithMatchingPosts(List<PostsModel> matchingPosts) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// xóa bỏ tiêu đề của cửa sổ
        dialog.setContentView(R.layout.dialog_matching_posts);// thiết lập nội dung cho layout
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);// đặt nên cho layout
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewMatchingPosts);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        // Tạo adapter và đặt danh sách bài viết trùng khớp
        MatchingPostsAdapter adapter = new MatchingPostsAdapter(context, matchingPosts);
        Collections.reverse(matchingPosts);

        recyclerView.setAdapter(adapter);
        // nút này về
        ImageView imageBack = dialog.findViewById(R.id.back_image);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }


    // Hàm này trả về độ tương thích của một bài viết, sử dụng độ tương thích trung bình của labelMap

    // tải ảnh từ ứng dụng này xuống thiết bị
    private void downloadImage(String imageUrl) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(imageUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "BIBI.jpg");

        downloadManager.enqueue(request);
        Toast.makeText(context, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
    }


    private void showZoomDialog(String imageUrl,String title,String postId,String userId,Map<String,Float> labels) {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_zoom_image);
        // Hiển thị ảnh
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();// tham chiếu đên của sổ
        window.setBackgroundDrawableResource(R.drawable.dialog_bg_white);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.showImageFull);
        Button btnDetailImage = dialog.findViewById(R.id.detail_image_dialog);
        Glide.with(context).load(imageUrl).into(imageView);
        ImageView heart,bookmarkImage;
        ImageView insideHeart;

        // thực hiện việc thích bài viết
        heart = dialog.findViewById(R.id.heart);
        insideHeart = dialog.findViewById(R.id.insideHeard);
        bookmarkImage = dialog.findViewById(R.id.bookmark_save_image);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        like(postId,heart);

        final Animation zoomInAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        final Animation zoomOutAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out);

        imageView.setOnClickListener(new DoubleClickListener() {
            @Override
            void onDoubleClick(View v) {
                heart.setImageResource(R.drawable.baseline_favorite_24);
                heart.startAnimation(zoomInAnim);
                insideHeart.startAnimation(zoomInAnim);
                insideHeart.startAnimation(zoomOutAnim);
                isLiked = true;
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked) {
                    heart.setImageResource(R.drawable.baseline_favorite_border_24);
                } else {
                    heart.setImageResource(R.drawable.baseline_favorite_24);
                    insideHeart.startAnimation(zoomInAnim);
                    insideHeart.startAnimation(zoomOutAnim);
                    if (heart.getTag() == null || heart.getTag().equals("no_liked")) {
                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("likes");
                        Map<String, Object> data = new HashMap<>();
                        data.put(currentUser.getUid(), true);
                        data.put("postId", postId);
                        data.put("postImage", imageUrl);
                        data.put("timestamp", System.currentTimeMillis());
                        data.put("labelMap",labels);
                        data.put("title",title);
                        data.put("uid",userId);
                        collectionReference.document(postId).set(data);
                        heart.setTag("liked");
                    }
                    else {
                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("likes");
                        collectionReference.document(postId).delete();
                        heart.setTag("no_liked");
                    }
                }

                heart.startAnimation(zoomInAnim);
                isLiked = !isLiked;
            }
        });
        // chuyển màn hình chi tiết để hiện chi tiết bài viết ra
        btnDetailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SimilarArticlesActivity.class);
                context.startActivity(intent);
            }
        });
        saveImage(bookmarkImage,postId,currentUser);//thực hiện lưu kiểm tra ảnh đã thích chưa
        bookmarkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // thực hiện animation cho phần lưu ảnh
                bookmarkImage.startAnimation(zoomInAnim);
                if (currentUser != null) {
                    Map<String,Object> data = new HashMap<>();
                    data.put("postImage",imageUrl);
                    data.put("timestamp",System.currentTimeMillis());
                    data.put("title",title);
                    data.put("labelMap",labels);
                    data.put("uid",userId);
                    data.put("postId",postId);
                    FirebaseFirestore.getInstance()
                            .collection("saveImage")
                            .document(currentUser.getUid())
                            .collection("saveImages")
                            .document(postId)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Lưu thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        // chia sẻ bài viết lên trang cá nhân
        Button buttonShareImage = dialog.findViewById(R.id.share_image_for_profile);
        buttonShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lưu dữ liệu lên database
                if (currentUser != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("postImage", imageUrl);
                    data.put("timestamp", System.currentTimeMillis());
                    data.put("title", title);
                    data.put("labelMap", labels);
                    data.put("uid", userId);
                    data.put("postId", postId);
                    FirebaseFirestore.getInstance()
                            .collection("shareImage")
                            .document(currentUser.getUid())
                            .collection("shareImages")
                            .document(postId).set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
// Lấy ra LottieAnimationView từ layout của dialog
                                    LottieAnimationView animationView = dialog.findViewById(R.id.share_congratulation);

                                    // Load hoạt cảnh từ tệp raw
                                    LottieCompositionFactory.fromRawRes(context, R.raw.congratulations)
                                            .addListener(new LottieListener<LottieComposition>() {
                                                @Override
                                                public void onResult(LottieComposition result) {
                                                    // Nạp hoạt cảnh thành công
                                                    animationView.setComposition(result);
                                                    animationView.playAnimation();
                                                    // Ẩn LottieAnimationView sau 3 giây
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            animationView.setVisibility(View.VISIBLE);
                                                        }
                                                    }, 3000); // 3000 milliseconds = 3 giây
                                                }
                                            })
                                            .addFailureListener(new LottieListener<Throwable>() {
                                                @Override
                                                public void onResult(Throwable throwable) {
                                                    // Xử lý khi có lỗi xảy ra trong quá trình nạp hoạt cảnh
                                                    throwable.printStackTrace();
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
        });
    }
    // lấy dữ liệu trong database ra so sánh người dùng đã lưu ảnh này chưa, nếu đã lưu thì hiện icon màu đỏ và ngược lại
    private void saveImage(ImageView imageView, String postId, FirebaseUser user) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("saveImage")
                .document(user.getUid())
                .collection("saveImages")
                .document(postId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle error
                    return;
                }

                if (value.exists()) {
                    imageView.setImageResource(R.drawable.baseline_collections_bookmark_24_red);
                    imageView.setTag("save");
                } else {
                    imageView.setImageResource(R.drawable.baseline_collections_bookmark_24);
                    imageView.setTag("no_save");
                }
            }
        });
    }

    private void like(String postId, ImageView imageView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Tham chiếu đến collection "Likes" trong Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference likeRef = db.collection("likes").document(postId);

        likeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Kiểm tra xem người dùng hiện tại đã thích bài viết này chưa
                    if (snapshot.contains(currentUser.getUid())) {
                        imageView.setImageResource(R.drawable.baseline_favorite_24);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                        imageView.setTag("no_liked");
                    }
                } else {
                }
            }
        });
    }

    public static CollectionReference postsCollection() {
        return FirebaseFirestore.getInstance().collection("posts");
    }
    abstract class DoubleClickListener implements View.OnClickListener {

        private long lastClickTime = 0;

        private static final int DOUBLE_CLICK_TIME_DELTA = 300;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            }
            lastClickTime = clickTime;
        }

        abstract void onDoubleClick(View v);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewReport,imageViewMatchingSearch;
        RoundedImageView postImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.imagePost);
            imageViewReport = itemView.findViewById(R.id.image_report);
            imageViewMatchingSearch = itemView.findViewById(R.id.matching_search);
        }
    }
}
