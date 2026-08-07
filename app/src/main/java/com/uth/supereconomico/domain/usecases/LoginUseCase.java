package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;

public class LoginUseCase {
    private final AuthRepository authRepository;

    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String usuario, String password, AuthRepository.Callback<Usuario> callback) {
        if (usuario == null || usuario.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            callback.onError("Correo y contrasena son obligatorios");
            return;
        }

        authRepository.login(usuario.trim(), password, new AuthRepository.Callback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                authRepository.logLoginAcceptance(usuario.getId(), usuario.getEmail());
                callback.onSuccess(usuario);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
