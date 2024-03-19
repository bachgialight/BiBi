package com.example.bibi.Content_Based_Image_Retrieval;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageUtils {
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

        return result > similarityThreshold;
    }
}
