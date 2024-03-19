package com.example.bibi.Content_Based_Image_Retrieval;

import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class ImageProcessingUtils {
    private final Interpreter interpreter;

    public ImageProcessingUtils(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public float[] extractFeatures(Bitmap bitmap) {
        // Chuyển đổi bitmap thành đối tượng TensorImage
        TensorImage inputImage = TensorImage.fromBitmap(bitmap);

        // Tạo TensorBuffer để lưu trữ đặc trưng
        int batchSize = 1;
        int inputChannels = 3;
        int inputHeight = inputImage.getHeight();
        int inputWidth = inputImage.getWidth();
        int[] inputShape = {batchSize, inputHeight, inputWidth, inputChannels};
        int outputSize = 1000;
        float[][] output = new float[1][outputSize];
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, outputSize}, DataType.FLOAT32);

        // Chạy mô hình để trích xuất đặc trưng từ ảnh
        interpreter.run(inputImage.getBuffer(), outputBuffer.getBuffer());

        // Chuyển đổi TensorBuffer thành mảng float
        float[] features = outputBuffer.getFloatArray();

        return features;
    }

    public static float calculateSimilarity(float[] features1, float[] features2) {
        // Tính toán độ tương đồng sử dụng cosine similarity
        float dotProduct = 0;
        float magnitude1 = 0;
        float magnitude2 = 0;
        for (int i = 0; i < features1.length; i++) {
            dotProduct += features1[i] * features2[i];
            magnitude1 += Math.pow(features1[i], 2);
            magnitude2 += Math.pow(features2[i], 2);
        }
        magnitude1 = (float) Math.sqrt(magnitude1);
        magnitude2 = (float) Math.sqrt(magnitude2);
        float similarity = dotProduct / (magnitude1 * magnitude2);

        return similarity;
    }
}
