package com.uth.supereconomico.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.MainActivity;
import com.uth.supereconomico.R;
import com.uth.supereconomico.presentation.viewmodel.LoginViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.UserFriendlyError;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private CheckBox cbAcceptPolicy;
    private CheckBox cbRememberUser;
    private MaterialButton btnLogin;
    private MaterialButton btnGoToRegister;
    private MaterialButton btnRecoverPassword;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbAcceptPolicy = findViewById(R.id.cbAcceptPolicyLogin);
        cbRememberUser = findViewById(R.id.cbRememberUser);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        // Recuperar estado del CheckBox
        android.content.SharedPreferences prefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
        cbAcceptPolicy.setChecked(prefs.getBoolean("policy_accepted", false));
        String remembered = prefs.getString("remembered_identifier", "");
        etEmail.setText(remembered);
        cbRememberUser.setChecked(!remembered.isEmpty());

        btnLogin.setOnClickListener(v -> iniciarSesion());
        btnGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnRecoverPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.user.observe(this, usuario -> {
            Toast.makeText(LoginActivity.this, "Bienvenido, " + usuario.getNombreCompleto(), Toast.LENGTH_SHORT).show();
            if (usuario.getRol() == com.uth.supereconomico.domain.entities.Usuario.Rol.ENCARGADO) {
                Toast.makeText(this, "La cuenta administrativa se usa en el cPanel", Toast.LENGTH_LONG).show();
                com.uth.supereconomico.data.remote.SesionSupabase.cerrarSesion();
                return;
            }
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        });

        viewModel.error.observe(this, mensaje -> Toast.makeText(LoginActivity.this, UserFriendlyError.fromMessage(mensaje), Toast.LENGTH_LONG).show());

        viewModel.isLoading.observe(this, cargando -> btnLogin.setEnabled(!cargando));
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbAcceptPolicy.isChecked()) {
            Toast.makeText(this, getString(R.string.error_policy_not_accepted), Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar estado del CheckBox
        getSharedPreferences("login_prefs", MODE_PRIVATE).edit().putBoolean("policy_accepted", true).apply();
        getSharedPreferences("login_prefs", MODE_PRIVATE).edit()
                .putString("remembered_identifier", cbRememberUser.isChecked() ? email : "").apply();

        viewModel.login(email, password);
    }
}
