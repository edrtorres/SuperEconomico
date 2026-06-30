package com.uth.supereconomico.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.MainActivity;
import com.uth.supereconomico.R;
import com.uth.supereconomico.presentation.viewmodel.VerifyOtpViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;

public class VerifyOtpActivity extends AppCompatActivity {

    private TextInputEditText etOtp;
    private Button btnVerify;
    private ImageButton btnBack;
    private String email;
    private VerifyOtpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(VerifyOtpViewModel.class);

        email = getIntent().getStringExtra("email");
        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(this, "Correo no disponible para verificacion", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setImageResource(R.drawable.ic_back_custom);
        btnBack.setOnClickListener(v -> finish());

        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(v -> verificarCodigo());

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.user.observe(this, usuario -> {
            Toast.makeText(VerifyOtpActivity.this, "Bienvenido, " + usuario.getNombreCompleto(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
            finishAffinity();
        });

        viewModel.error.observe(this, mensaje -> Toast.makeText(VerifyOtpActivity.this, mensaje, Toast.LENGTH_LONG).show());

        viewModel.isLoading.observe(this, cargando -> btnVerify.setEnabled(!cargando));
    }

    private void verificarCodigo() {
        String otp = etOtp.getText().toString().trim();
        if (otp.length() < 6) {
            Toast.makeText(this, "Ingresa el codigo de 6 digitos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.verifyOtp(email, otp);
    }
}
