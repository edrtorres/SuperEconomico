package com.uth.supereconomico.domain.usecases;

import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;

public class VerifyOtpUseCase {
    private final AuthRepository authRepository;

    public VerifyOtpUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String otp, AuthRepository.Callback<Usuario> callback) {
        if (email == null || otp == null || otp.length() < 6) {
            callback.onError("Datos inválidos");
            return;
        }
        authRepository.verifyOtp(email, otp, new AuthRepository.Callback<Usuario>() {
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
