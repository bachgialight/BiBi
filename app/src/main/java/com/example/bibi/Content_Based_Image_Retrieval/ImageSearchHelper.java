package com.example.bibi.Content_Based_Image_Retrieval;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bibi.model.PostsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageSearchHelper {

    public interface ImageSearchListener {
        void onImageSearchComplete(List<PostsModel> matchingPosts);
        void onImageSearchError(String errorMessage);
    }

    public static void searchImages(Context context, List<String> imagePaths, String targetImage, ImageSearchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PostsModel> allPosts = queryDocumentSnapshots.toObjects(PostsModel.class);

                    List<PostsModel> matchingPosts = performImageComparison(imagePaths, targetImage, allPosts, context);
                    listener.onImageSearchComplete(matchingPosts);
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Error querying Firestore: " + e.getMessage();
                    listener.onImageSearchError(errorMessage);
                });
    }

    private static List<PostsModel> performImageComparison(List<String> imagePaths, String targetImage, List<PostsModel> allPosts, Context context) {
        List<PostsModel> matchingPosts = new ArrayList<>();

        for (PostsModel post : allPosts) {
            // Kiểm tra xem đường dẫn hình ảnh có hợp lệ không
            if (imagePaths.contains(post.getPostImage())) {
                // Tải hình ảnh từ Firebase Storage và thực hiện so sánh
                loadImageAndCompare(context, post.getPostImage(), targetImage, matchingPosts, post);
            }
        }

        return matchingPosts;
    }

    private static void loadImageAndCompare(Context context, String storageImagePath, String targetImage, List<PostsModel> matchingPosts, PostsModel post) {
        // Sử dụng Firebase Storage để tải hình ảnh về
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageImagePath);

        try {
            File localFile = File.createTempFile("tempImage", "jpg");

            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Đường dẫn địa phương của hình ảnh đã tải về
                        String localImagePath = localFile.getAbsolutePath();

                        // Thực hiện so sánh hình ảnh và thêm vào danh sách nếu khớp
                        if (imageSimilarityCheck(targetImage, localImagePath)) {
                            matchingPosts.add(post);
                        }

                        // Xóa tệp hình ảnh tạm sau khi sử dụng
                        localFile.delete();
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi khi tải hình ảnh từ Firebase Storage
                        e.printStackTrace();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean imageSimilarityCheck(String targetImage, String postImage) {
        // Đọc hình ảnh từ đường dẫn
        Mat img1 = Imgcodecs.imread(targetImage);
        Mat img2 = Imgcodecs.imread(postImage);

        // Chuyển đổi hình ảnh sang định dạng HSV (Hue, Saturation, Value)
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(img2, img2, Imgproc.COLOR_BGR2HSV);

        // Tính histogram của hình ảnh
        Mat histImg1 = new Mat();
        Mat histImg2 = new Mat();

        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(50);
        MatOfInt channels = new MatOfInt(0, 1, 2);

        Imgproc.calcHist(Arrays.asList(img1), channels, new Mat(), histImg1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(img2), channels, new Mat(), histImg2, histSize, ranges);

        // Chuẩn hóa histogram
        Core.normalize(histImg1, histImg1, 0, 1, Core.NORM_MINMAX);
        Core.normalize(histImg2, histImg2, 0, 1, Core.NORM_MINMAX);

        // Tính độ tương tự (tích vô hướng)
        double result = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);

        // Đặt ngưỡng tương tự tùy thuộc vào yêu cầu của bạn
        double similarityThreshold = 0.8;
        // Giải phóng bộ nhớ
        img1.release();
        img2.release();
        return result > similarityThreshold;
    }
}
