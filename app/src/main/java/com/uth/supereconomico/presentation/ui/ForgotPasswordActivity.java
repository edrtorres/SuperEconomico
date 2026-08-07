package com.uth.supereconomico.presentation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.R;
import com.uth.supereconomico.presentation.viewmodel.ForgotPasswordViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnSendInstructions;
    private ImageButton btnBack;
    private TextView tvError;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ForgotPasswordViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        btnSendInstructions = findViewById(R.id.btnSendInstructions);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setImageResource(R.drawable.ic_back_curved);
        tvError = findViewById(R.id.tvError);

        btnBack.setOnClickListener(v -> finish());
        btnSendInstructions.setOnClickListener(v -> enviarInstrucciones());

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.isSuccess.observe(this, correcto -> {
            if (correcto) {
                Toast.makeText(ForgotPasswordActivity.this, "Te enviamos un correo para restablecer tu contraseña.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.error.observe(this, this::mostrarError);

        viewModel.isLoading.observe(this, cargando -> btnSendInstructions.setEnabled(!cargando));
    }

    private void enviarInstrucciones() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            mostrarError("Ingresa tu correo o telefono");
            return;
        }

        tvError.setVisibility(View.GONE);
        viewModel.recoverPassword(email);
    }

    private void mostrarError(String mensaje) {
        tvError.setText(UserFriendlyError.fromMessage(mensaje));
        tvError.setVisibility(View.VISIBLE);
    }
}
