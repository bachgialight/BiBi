package com.example.bibi.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.bibi.R;
import com.hbb20.CountryCodePicker;

public class PhoneActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOTPBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        // ánh xạ đến các id

        unitUntil();
    }

    private void unitUntil() {
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.input_phone);
        sendOTPBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!countryCodePicker.isValidFullNumber()) {
                    phoneInput.setError("Số điện thoại này không tồn tại");
                    return;
                }
                Intent intent = new Intent(PhoneActivity.this, OTPActivity.class);
                intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
                startActivity(intent);
            }
        });
    }
}