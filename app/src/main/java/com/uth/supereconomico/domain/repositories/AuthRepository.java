package com.uth.supereconomico.domain.repositories;

import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.domain.entities.Usuario;
import java.util.List;

public interface AuthRepository {
    interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void login(String email, String password, Callback<Usuario> callback);
    void register(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones, Callback<Void> callback);
    void verifyOtp(String email, String otp, Callback<Usuario> callback);
    void logLoginAcceptance(String usuarioId, String email);
    void recoverPassword(String email, Callback<Void> callback);
    void updatePassword(String newPassword, String accessToken, Callback<Void> callback);
    void actualizarContrasenaConTokenHash(String newPassword, String tokenHash, Callback<Void> callback);
    void updateProfile(String id, String nombreCompleto, String telefono, String direccion, String descripcion, String avatarUrl, Callback<Void> callback);
    void updateFcmToken(String id, String token, Callback<Void> callback);
    void getAddresses(String perfilId, Callback<List<DireccionRequest>> callback);
    void addAddress(DireccionRequest address, Callback<Void> callback);
    void deleteAddress(long addressId, Callback<Void> callback);
    
    void getPaymentMethods(String perfilId, Callback<List<com.uth.supereconomico.domain.entities.MetodoPago>> callback);
    void addPaymentMethod(com.uth.supereconomico.data.remote.models.MetodoPagoDTO method, Callback<Void> callback);
    void deletePaymentMethod(long methodId, Callback<Void> callback);

    void logout();
    Usuario getCurrentUser();
}
