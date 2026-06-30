package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.domain.repositories.AuthRepository;

import java.util.List;

public class RegisterUseCase {
    private final AuthRepository authRepository;

    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones, AuthRepository.Callback<Void> callback) {
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || nombreCompleto == null || nombreCompleto.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()) {
            callback.onError("Datos incompletos");
            return;
        }

        if (password.length() < 6) {
            callback.onError("La contrasena debe tener al menos 6 caracteres");
            return;
        }

        if (direcciones == null || direcciones.isEmpty()) {
            callback.onError("Agrega al menos una direccion de entrega");
            return;
        }

        authRepository.register(email.trim(), password, nombreCompleto.trim(), telefono.trim(), direcciones, callback);
    }
}
