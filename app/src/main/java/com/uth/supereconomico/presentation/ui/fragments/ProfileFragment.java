package com.uth.supereconomico.presentation.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.R;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.presentation.ui.SeleccionarUbicacionActivity;
import com.uth.supereconomico.presentation.ui.WelcomeActivity;
import com.uth.supereconomico.domain.entities.MetodoPago;
import com.uth.supereconomico.presentation.ui.adapters.AddressAdapter;
import com.uth.supereconomico.presentation.ui.adapters.PaymentAdapter;
import com.uth.supereconomico.presentation.viewmodel.ProfileViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import com.yalantis.ucrop.UCrop;
import androidx.core.content.FileProvider;

public class ProfileFragment extends Fragment implements 
        AddressAdapter.OnAddressClickListener,
        PaymentAdapter.OnPaymentClickListener {

    private ProfileViewModel viewModel;
    private ShapeableImageView ivProfilePic;
    private TextView tvNameHeader, tvStatus;
    private MaterialCardView cvPayment, cvAddresses;
    private MaterialButton btnNotif, btnHelp, btnAccount, btnSecurity, btnLogout;
    
    private String base64Image = null;
    private Double tempLat = 0.0, tempLng = 0.0;
    private TextView tvGpsStatus;
    private Uri cameraImageUri;

    // Selector de Imagen desde Galería
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        startCrop(imageUri);
                    }
                }
            }
    );

    // Captura de Foto desde Cámara
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (cameraImageUri != null) {
                        startCrop(cameraImageUri);
                    }
                }
            }
    );

    // Recorte de Imagen (UCrop)
    private final ActivityResultLauncher<Intent> cropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        processImage(resultUri);
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                    Intent data = result.getData();
                    if (data != null) {
                        Throwable cropError = UCrop.getError(data);
                        if (cropError != null) {
                            Toast.makeText(getContext(), "Error al recortar: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    // Permiso de Cámara
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Mapa para Direcciones
    private final ActivityResultLauncher<Intent> mapLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    tempLat = result.getData().getDoubleExtra("latitud", 0.0);
                    tempLng = result.getData().getDoubleExtra("longitud", 0.0);
                    if (tvGpsStatus != null) {
                        tvGpsStatus.setText(String.format(Locale.US, "Ubicación fijada: %.5f, %.5f", tempLat, tempLng));
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvNameHeader = view.findViewById(R.id.tvProfileNameHeader);
        tvStatus = view.findViewById(R.id.tvProfileStatus);
        cvPayment = view.findViewById(R.id.cvPaymentMethods);
        cvAddresses = view.findViewById(R.id.cvMyAddresses);
        btnNotif = view.findViewById(R.id.btnNotifSettings);
        btnHelp = view.findViewById(R.id.btnHelpSupport);
        btnAccount = view.findViewById(R.id.btnAccountSettings);
        btnSecurity = view.findViewById(R.id.btnSecurityProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        setupListeners();
        observeViewModel();

        return view;
    }

    private void setupListeners() {
        ivProfilePic.setOnClickListener(v -> showImageSourceDialog());
        
        cvPayment.setOnClickListener(v -> mostrarDialogoGestionPagos());

        cvAddresses.setOnClickListener(v -> mostrarDialogoGestionDirecciones());

        btnNotif.setOnClickListener(v -> mostrarDialogoConfiguracionNotificaciones());

        btnHelp.setOnClickListener(v -> Toast.makeText(getContext(), "Soporte Técnico", Toast.LENGTH_SHORT).show());
        btnAccount.setOnClickListener(v -> mostrarDialogoAjustesCuenta());
        
        btnSecurity.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.navigation_security);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void showImageSourceDialog() {
        String[] options = {"Cámara", "Galería"};
        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar foto de perfil")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            File photoFile = new File(requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), 
                    "profile_" + System.currentTimeMillis() + ".jpg");
            cameraImageUri = FileProvider.getUriForFile(requireContext(), 
                    "com.uth.supereconomico.fileprovider", photoFile);
            
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al abrir cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void startCrop(Uri sourceUri) {
        String destinationFileName = "cropped_profile_" + System.currentTimeMillis() + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), destinationFileName));
        
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setToolbarColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary));
        options.setStatusBarColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_dark));
        options.setActiveControlsWidgetColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary));
        
        Intent intent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(800, 800)
                .withOptions(options)
                .getIntent(requireContext());
        
        cropLauncher.launch(intent);
    }

    private void processImage(Uri uri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            // Redimensionar para no saturar la DB
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            
            ivProfilePic.setImageBitmap(resized);
            
            // Guardar automáticamente
            Usuario user = viewModel.user.getValue();
            if (user != null) {
                viewModel.updateProfile(user.getNombreCompleto(), user.getTelefono(), user.getDireccion(), user.getDescripcion(), base64Image);
            }
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvNameHeader.setText("Hola, " + user.getNombreCompleto().split(" ")[0]);
                tvStatus.setText(user.getRol() == Usuario.Rol.ENCARGADO ? "Encargado de Tienda" : "Cliente Premium - 450 puntos");
                
                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(user.getAvatarUrl(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivProfilePic.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        Glide.with(this).load(user.getAvatarUrl()).into(ivProfilePic);
                    }
                }
            }
        });

        viewModel.isUpdateSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });
    }

    private void mostrarDialogoGestionPagos() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_manage_payments, null);
        RecyclerView rv = dialogView.findViewById(R.id.rvManagePayments);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAddNewPayment);
        
        PaymentAdapter payAdapter = new PaymentAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(payAdapter);
        
        viewModel.payments.observe(getViewLifecycleOwner(), payAdapter::setPayments);

        btnAdd.setOnClickListener(v -> showAddPaymentDialog());

        new AlertDialog.Builder(getContext())
                .setTitle("Mis Métodos de Pago")
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAddPaymentDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_payment_method, null);
        TextInputEditText etTitular = dialogView.findViewById(R.id.etCardTitular);
        TextInputEditText etNumber = dialogView.findViewById(R.id.etCardNumber);
        TextInputEditText etExpiry = dialogView.findViewById(R.id.etCardExpiry);

        new AlertDialog.Builder(getContext())
                .setTitle("Nueva Tarjeta")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String tit = etTitular.getText().toString().trim();
                    String num = etNumber.getText().toString().trim();
                    String exp = etExpiry.getText().toString().trim();
                    
                    if (!tit.isEmpty() && num.length() >= 13 && !exp.isEmpty()) {
                        viewModel.addPaymentMethod(tit, num, exp);
                    } else {
                        Toast.makeText(getContext(), "Datos de tarjeta inválidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDeleteClick(MetodoPago payment) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Tarjeta")
                .setMessage("¿Deseas eliminar esta tarjeta?")
                .setPositiveButton("Eliminar", (dialog, which) -> viewModel.deletePaymentMethod(payment.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoGestionDirecciones() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_manage_addresses, null);
        RecyclerView rv = dialogView.findViewById(R.id.rvManageAddresses);
        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAddNewAddress);
        
        AddressAdapter addrAdapter = new AddressAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(addrAdapter);
        
        viewModel.addresses.observe(getViewLifecycleOwner(), addrAdapter::setAddresses);

        btnAdd.setOnClickListener(v -> showAddAddressDialog());

        new AlertDialog.Builder(getContext())
                .setTitle("Mis Direcciones")
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAddAddressDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_address, null);
        TextInputEditText etLabel = dialogView.findViewById(R.id.etDialogLabel);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etDialogAddress);
        MaterialButton btnSetGps = dialogView.findViewById(R.id.btnDialogSetGps);
        tvGpsStatus = dialogView.findViewById(R.id.tvDialogGpsStatus);

        tempLat = 0.0; tempLng = 0.0;

        btnSetGps.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SeleccionarUbicacionActivity.class);
            mapLauncher.launch(intent);
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Nueva Dirección")
                .setView(dialogView)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    String label = etLabel.getText().toString().trim();
                    String addr = etAddress.getText().toString().trim();
                    if (!label.isEmpty() && !addr.isEmpty()) {
                        viewModel.addAddress(label, addr, tempLat, tempLng);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onDeleteClick(DireccionRequest address) {
        viewModel.deleteAddress(address.id);
    }

    private void mostrarDialogoConfiguracionNotificaciones() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_notification_frequency, null);

        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE);

        com.google.android.material.switchmaterial.SwitchMaterial swEnable = view.findViewById(R.id.switchEnableNotif);
        com.google.android.material.switchmaterial.SwitchMaterial swSound = view.findViewById(R.id.switchSound);
        com.google.android.material.switchmaterial.SwitchMaterial swVib = view.findViewById(R.id.switchVibration);
        android.widget.AutoCompleteTextView spinner = view.findViewById(R.id.spinnerFrequency);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveNotifSettings);

        // Cargar valores actuales
        swEnable.setChecked(prefs.getBoolean("notif_enabled", true));
        swSound.setChecked(prefs.getBoolean("notif_sound", true));
        swVib.setChecked(prefs.getBoolean("notif_vibration", true));

        String[] opciones = {"Cada 10 segundos", "Cada 30 segundos", "Cada 1 minuto", "Cada 5 minutos"};
        int[] valores = {10, 30, 60, 300};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, opciones);
        spinner.setAdapter(adapter);

        int actualVal = prefs.getInt("sync_interval_seconds", 30);
        for(int i=0; i<valores.length; i++) {
            if(valores[i] == actualVal) {
                spinner.setText(opciones[i], false);
                break;
            }
        }

        btnSave.setOnClickListener(v -> {
            boolean enabled = swEnable.isChecked();
            prefs.edit()
                    .putBoolean("notif_enabled", enabled)
                    .putBoolean("notif_sound", swSound.isChecked())
                    .putBoolean("notif_vibration", swVib.isChecked())
                    .putInt("sync_interval_seconds", getValorDeOpcion(spinner.getText().toString(), opciones, valores))
                    .apply();

            if (enabled) {
                reiniciarServicio();
            } else {
                requireActivity().stopService(new Intent(getContext(), com.uth.supereconomico.utils.OrderRealtimeService.class));
            }

            Toast.makeText(getContext(), "Configuración guardada", Toast.LENGTH_SHORT).show();
            bottomSheet.dismiss();
        });

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    private int getValorDeOpcion(String seleccion, String[] opciones, int[] valores) {
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(seleccion)) return valores[i];
        }
        return 30;
    }

    private void reiniciarServicio() {
        requireActivity().stopService(new Intent(getContext(), com.uth.supereconomico.utils.OrderRealtimeService.class));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(new Intent(getContext(), com.uth.supereconomico.utils.OrderRealtimeService.class));
        } else {
            requireActivity().startService(new Intent(getContext(), com.uth.supereconomico.utils.OrderRealtimeService.class));
        }
    }

    private void mostrarDialogoAjustesCuenta() {
        Usuario user = viewModel.user.getValue();
        if (user == null) return;

        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheet = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etName = view.findViewById(R.id.etEditProfileName);
        TextInputEditText etPhone = view.findViewById(R.id.etEditProfilePhone);
        TextInputEditText etAddress = view.findViewById(R.id.etEditProfileAddress);
        TextInputEditText etDesc = view.findViewById(R.id.etEditProfileDesc);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveProfileChanges);

        etName.setText(user.getNombreCompleto());
        etPhone.setText(user.getTelefono());
        etAddress.setText(user.getDireccion());
        etDesc.setText(user.getDescripcion());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String addr = etAddress.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.updateProfile(name, phone, addr, desc, user.getAvatarUrl());
            bottomSheet.dismiss();
        });

        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    private void logout() {
        viewModel.logout();
        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
