package com.example.bibi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bibi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    String phone;
    EditText inputOTP;
    Button nextBtn;
    TextView resendOTP;
    Long timeOutSecond = 60L;
    ProgressBar progressBar;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        phone = getIntent().getExtras().getString("phone");
        Toast.makeText(this, phone, Toast.LENGTH_SHORT).show();
        // cập nhật dữ liệu trong firebase
        inputOTP = findViewById(R.id.input_otp);
        nextBtn = findViewById(R.id.button_confirm_otp);
        resendOTP = findViewById(R.id.resend_otp);
        progressBar = findViewById(R.id.login_progress_bar);

        // gửi mã OTP
        sendOTP(phone,false);

        //chuyển màn hình
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredOTP = inputOTP.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enteredOTP);
                sighIn(credential);
                setInProgress(true);
            }
        });
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP(phone,true);
            }
        });
    }
    public void sendOTP(String phone,boolean isResend) {
        //điếm ngược thời gian gửi thông báo có còn giá trị
        startResendTimer();
        // ẩn và hiện progress
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(timeOutSecond, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                // đăng nhập
                                // Kiểm tra xem người dùng đã nhập OTP thủ công hay không
                                if (inputOTP.getText().toString().isEmpty()) {
                                    // Người dùng chưa nhập OTP, không cần đăng nhập tự động
                                    // Hiển thị thông báo hoặc thực hiện các xử lý khác ở đây
                                } else {
                                    // Người dùng đã nhập mã OTP, thực hiện đăng nhập tự động
                                    sighIn(phoneAuthCredential);
                                }
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPActivity.this, "Gửi OTP không thành công", Toast.LENGTH_SHORT).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(OTPActivity.this, "Gửi OTP thành công", Toast.LENGTH_SHORT).show();
                                setInProgress(false);

                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startResendTimer() {
        // ẩn resendOTP lại khi đang chạy
        resendOTP.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeOutSecond--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resendOTP.setText("Gửi lại OTP sau " + timeOutSecond);
                    }
                });
                if (timeOutSecond <= 0) {
                    timeOutSecond = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendOTP.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }


    private void sighIn(PhoneAuthCredential phoneAuthCredential) {
        setInProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTPActivity.this, UserNameActivity.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                }else {
                    Toast.makeText(OTPActivity.this, "Gửi verification thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);

        }
    }
}