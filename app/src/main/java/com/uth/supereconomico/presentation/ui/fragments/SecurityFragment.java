package com.uth.supereconomico.presentation.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.uth.supereconomico.R;
import com.uth.supereconomico.presentation.viewmodel.SecurityViewModel;
import com.uth.supereconomico.presentation.viewmodel.ViewModelFactory;

public class SecurityFragment extends Fragment {

    private SecurityViewModel viewModel;
    private MaterialButton btnChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security, container, false);

        ViewModelFactory factory = new ViewModelFactory();
        viewModel = new ViewModelProvider(this, factory).get(SecurityViewModel.class);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.error.observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            btnChangePassword.setEnabled(!loading);
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etNewPass = dialogView.findViewById(R.id.etDialogNewPassword);
        TextInputEditText etConfirmPass = dialogView.findViewById(R.id.etDialogConfirmPassword);

        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar Contraseña")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String newPass = etNewPass.getText().toString().trim();
                    String confirm = etConfirmPass.getText().toString().trim();
                    if (newPass.length() >= 6 && newPass.equals(confirm)) {
                        viewModel.updatePassword(newPass);
                    } else {
                        Toast.makeText(getContext(), "Las contraseñas no coinciden o son cortas", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
