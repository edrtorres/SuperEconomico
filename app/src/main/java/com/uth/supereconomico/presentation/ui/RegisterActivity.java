package com.uth.supereconomico.presentation.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.presentation.viewmodel.RegisterViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;
import com.uth.supereconomico.utils.FormateadorTelefono;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_UBICACION = 1001;
    private static final int CODIGO_SELECCIONAR_UBICACION = 1002;

    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private LinearLayout llAddressesContainer;
    private MaterialButton btnAddAddress;
    private MaterialButton btnRegister;
    private CheckBox cbAcceptPolicy;
    private ImageButton btnBack;

    private RegisterViewModel viewModel;
    private final List<AddressItem> addressItems = new ArrayList<>();
    private AddressItem currentlySettingGps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        llAddressesContainer = findViewById(R.id.llAddressesContainer);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnRegister = findViewById(R.id.btnRegister);
        cbAcceptPolicy = findViewById(R.id.cbAcceptPolicy);

        FormateadorTelefono.aplicar(etPhone);

        agregarCampoDireccion();

        btnBack.setOnClickListener(v -> finish());
        btnAddAddress.setOnClickListener(v -> agregarCampoDireccion());
        btnRegister.setOnClickListener(v -> registrarCliente());
        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.isSuccess.observe(this, correcto -> {
            if (correcto) {
                Toast.makeText(RegisterActivity.this, "Cuenta creada. Revisa tu correo para confirmar", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                intent.putExtra("email", etEmail.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        });

        viewModel.error.observe(this, mensaje -> Toast.makeText(RegisterActivity.this, "Error: " + mensaje, Toast.LENGTH_LONG).show());

        viewModel.isLoading.observe(this, cargando -> btnRegister.setEnabled(!cargando));
    }

    private void agregarCampoDireccion() {
        if (addressItems.size() >= 3) {
            Toast.makeText(this, "Puedes registrar hasta 3 direcciones", Toast.LENGTH_SHORT).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.item_address_input, llAddressesContainer, false);
        AddressItem item = new AddressItem(view);
        item.btnRemove.setOnClickListener(v -> {
            if (addressItems.size() > 1) {
                llAddressesContainer.removeView(view);
                addressItems.remove(item);
            }
        });
        item.btnSetGps.setOnClickListener(v -> {
            currentlySettingGps = item;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODIGO_PERMISO_UBICACION);
            } else {
                abrirMapaUbicacion();
            }
        });
        llAddressesContainer.addView(view);
        addressItems.add(item);
    }

    private void abrirMapaUbicacion() {
        startActivityForResult(new Intent(this, SeleccionarUbicacionActivity.class), CODIGO_SELECCIONAR_UBICACION);
    }

    private void registrarCliente() {
        String nombre = etFullName.getText().toString().trim();
        String telefono = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();
        String confirmar = etConfirmPassword.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!FormateadorTelefono.esTelefonoValido(telefono)) {
            Toast.makeText(this, "El telefono debe tener formato 9999-9999", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 6) {
            Toast.makeText(this, "La contrasena debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, "Las contrasenas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbAcceptPolicy.isChecked()) {
            Toast.makeText(this, "Acepta las politicas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addressItems.isEmpty()) {
            Toast.makeText(this, "Agrega al menos una direccion de entrega", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addressItems.size() > 3) {
            Toast.makeText(this, "Puedes registrar maximo 3 direcciones", Toast.LENGTH_SHORT).show();
            return;
        }

        List<DireccionRequest> direcciones = new ArrayList<>();
        for (AddressItem item : addressItems) {
            String etiqueta = item.etLabel.getText().toString().trim();
            String direccion = item.etAddress.getText().toString().trim();
            if (etiqueta.isEmpty() || direccion.isEmpty() || item.latitude == null || item.longitude == null) {
                Toast.makeText(this, "Falta informacion en direcciones o GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            direcciones.add(new DireccionRequest(etiqueta, direccion, item.latitude, item.longitude));
        }

        viewModel.register(email, contrasena, nombre, FormateadorTelefono.formatear(telefono), direcciones);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_UBICACION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirMapaUbicacion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_SELECCIONAR_UBICACION && resultCode == RESULT_OK && data != null && currentlySettingGps != null) {
            currentlySettingGps.latitude = data.getDoubleExtra("latitud", 0);
            currentlySettingGps.longitude = data.getDoubleExtra("longitud", 0);
            currentlySettingGps.mostrarUbicacionLista();
            Toast.makeText(this, "Ubicacion fijada en el mapa", Toast.LENGTH_SHORT).show();
        }
    }

    private static class AddressItem {
        TextInputEditText etLabel;
        TextInputEditText etAddress;
        MaterialButton btnSetGps;
        MaterialButton btnRemove;
        View contenedorEstadoUbicacion;
        TextView tvEstadoUbicacion;
        Double latitude;
        Double longitude;

        AddressItem(View view) {
            etLabel = view.findViewById(R.id.etLabel);
            etAddress = view.findViewById(R.id.etAddress);
            btnSetGps = view.findViewById(R.id.btnSetLocation);
            btnRemove = view.findViewById(R.id.btnRemove);
            contenedorEstadoUbicacion = view.findViewById(R.id.contenedorEstadoUbicacion);
            tvEstadoUbicacion = view.findViewById(R.id.tvEstadoUbicacion);
        }

        void mostrarUbicacionLista() {
            btnSetGps.setText("Cambiar GPS");
            contenedorEstadoUbicacion.setVisibility(View.VISIBLE);
            tvEstadoUbicacion.setText(String.format(java.util.Locale.US, "Ubicacion fijada: %.5f, %.5f", latitude, longitude));
        }
    }
}
