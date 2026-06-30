package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.repositories.AuthRepository;

public class UpdatePasswordUseCase {
    private final AuthRepository authRepository;

    public UpdatePasswordUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String nuevaContrasena, String tokenAcceso, AuthRepository.Callback<Void> callback) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 6 || tokenAcceso == null || tokenAcceso.trim().isEmpty()) {
            callback.onError("Datos invalidos");
            return;
        }
        authRepository.updatePassword(nuevaContrasena, tokenAcceso, callback);
    }

    public void ejecutarConTokenHash(String nuevaContrasena, String tokenHash, AuthRepository.Callback<Void> callback) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 6 || tokenHash == null || tokenHash.trim().isEmpty()) {
            callback.onError("Datos invalidos");
            return;
        }
        authRepository.actualizarContrasenaConTokenHash(nuevaContrasena, tokenHash, callback);
    }
}
