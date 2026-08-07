package com.uth.supereconomico.presentation.ui;

import android.content.Intent;
import android.net.Uri;
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
import com.uth.supereconomico.presentation.viewmodel.ResetPasswordViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnResetPassword;
    private ImageButton btnBack;
    private TextView tvError;
    private String accessToken;
    private String tokenHashRecuperacion;
    private ResetPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ResetPasswordViewModel.class);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setImageResource(R.drawable.ic_back_curved);
        tvError = findViewById(R.id.tvError);

        manejarIntent(getIntent());

        btnBack.setOnClickListener(v -> finish());
        btnResetPassword.setOnClickListener(v -> actualizarContrasena());

        observarViewModel();
    }

    private void manejarIntent(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            accessToken = data.getQueryParameter("access_token");
            tokenHashRecuperacion = data.getQueryParameter("token_hash");
            if (accessToken == null && data.getFragment() != null) {
                accessToken = obtenerParametroDeFragmento(data.getFragment(), "access_token");
            }
            if (tokenHashRecuperacion == null && data.getFragment() != null) {
                tokenHashRecuperacion = obtenerParametroDeFragmento(data.getFragment(), "token_hash");
            }
            String errorDescription = data.getQueryParameter("error_description");
            if (errorDescription == null && data.getFragment() != null) {
                errorDescription = obtenerParametroDeFragmento(data.getFragment(), "error_description");
            }
            if (errorDescription != null && !errorDescription.trim().isEmpty()) {
                Toast.makeText(this, UserFriendlyError.fromMessage(errorDescription), Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        if ((accessToken == null || accessToken.trim().isEmpty())
                && (tokenHashRecuperacion == null || tokenHashRecuperacion.trim().isEmpty())) {
            Toast.makeText(this, "El enlace para restablecer tu contraseña expiró o no es válido. Solicita uno nuevo.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private String obtenerParametroDeFragmento(String fragmento, String nombre) {
        String[] parametros = fragmento.split("&");
        for (String parametro : parametros) {
            String[] partes = parametro.split("=", 2);
            if (partes.length == 2 && partes[0].equals(nombre)) {
                try {
                    return URLDecoder.decode(partes[1], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return partes[1];
                }
            }
        }
        return null;
    }

    private void observarViewModel() {
        viewModel.isSuccess.observe(this, correcto -> {
            if (correcto) {
                Toast.makeText(ResetPasswordActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        viewModel.error.observe(this, this::mostrarError);

        viewModel.isLoading.observe(this, cargando -> btnResetPassword.setEnabled(!cargando));
    }

    private void actualizarContrasena() {
        String nuevaContrasena = etNewPassword.getText().toString().trim();
        String confirmarContrasena = etConfirmPassword.getText().toString().trim();

        if (nuevaContrasena.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }

        tvError.setVisibility(View.GONE);
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            viewModel.updatePassword(nuevaContrasena, accessToken);
        } else {
            viewModel.actualizarConTokenHash(nuevaContrasena, tokenHashRecuperacion);
        }
    }

    private void mostrarError(String mensaje) {
        tvError.setText(UserFriendlyError.fromMessage(mensaje));
        tvError.setVisibility(View.VISIBLE);
    }
}
