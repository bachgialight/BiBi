package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bibi.R;
import com.example.bibi.Utils.Utils;
import com.example.bibi.ml.MobilenetV110224Quant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;

public class ImageLabelingActivity extends AppCompatActivity {
    private Button selectImageButton;
    private Button makePredictionButton;
    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;
    private Button cameraButton;
    List<String> labels = null;

    public void checkAndRequestPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);

        selectImageButton = findViewById(R.id.button);
        makePredictionButton = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView2);
        textView = findViewById(R.id.textView);
        cameraButton = findViewById(R.id.camerabtn);

        // Handling permissions
        checkAndRequestPermissions();

        try {
            labels = Utils.loadLabels(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mssg", "button pressed");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 250);
            }
        });

        makePredictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                MobilenetV110224Quant model = null;
                try {
                    model = MobilenetV110224Quant.newInstance(ImageLabelingActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TensorImage tbuffer = TensorImage.fromBitmap(resized);
                ByteBuffer byteBuffer = tbuffer.getBuffer();

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                int max = getMax(outputFeature0.getFloatArray());

                textView.setText(labels.get(max));

                // Releases model resources if no longer used.
                model.close();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 250) {
            imageView.setImageURI(data != null ? data.getData() : null);
            Uri uri = data != null ? data.getData() : null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    private int getMax(float[] arr) {
        int ind = 0;
        float min = 0.0f;

        for (int i = 0; i < 1000; i++) {
            if (arr[i] > min) {
                min = arr[i];
                ind = i;
            }
        }
        return ind;
    }
}