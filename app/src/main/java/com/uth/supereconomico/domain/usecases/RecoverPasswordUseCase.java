package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.repositories.AuthRepository;

public class RecoverPasswordUseCase {
    private final AuthRepository authRepository;

    public RecoverPasswordUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String usuario, AuthRepository.Callback<Void> callback) {
        if (usuario == null || usuario.trim().isEmpty()) {
            callback.onError("El correo o telefono es obligatorio");
            return;
        }
        authRepository.recoverPassword(usuario.trim(), callback);
    }
}
