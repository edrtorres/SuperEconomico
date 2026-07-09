package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.repositories.AuthRepository;

public class UpdateProfileUseCase {
    private final AuthRepository authRepository;

    public UpdateProfileUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String id, String nombreCompleto, String telefono, String direccion, String descripcion, String avatarUrl, AuthRepository.Callback<Void> callback) {
        if (id == null || id.trim().isEmpty()) {
            callback.onError("ID de usuario no válido");
            return;
        }
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            callback.onError("El nombre completo es obligatorio");
            return;
        }
        authRepository.updateProfile(id, nombreCompleto, telefono, direccion, descripcion, avatarUrl, callback);
    }
}
