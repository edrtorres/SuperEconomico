package com.uth.supereconomico.data.repositories;

import com.uth.supereconomico.data.remote.AuthApi;
import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.data.remote.models.UserDTO;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.utils.RemoteLogger;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthApi authApi;
    private final SupabaseApi supabaseApi;
    private Usuario currentUser;

    public AuthRepositoryImpl(AuthApi authApi, SupabaseApi supabaseApi) {
        this.authApi = authApi;
        this.supabaseApi = supabaseApi;
    }

    @Override
    public void login(String email, String password, Callback<Usuario> callback) {
        if (esTelefono(email)) {
            resolverCorreoPorTelefono(email, new Callback<String>() {
                @Override
                public void onSuccess(String correo) {
                    iniciarSesionConCorreo(correo, password, callback);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
            return;
        }

        iniciarSesionConCorreo(email, password, callback);
    }

    private void iniciarSesionConCorreo(String email, String password, Callback<Usuario> callback) {
        authApi.login(new AuthApi.LoginRequest(email, password)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SesionSupabase.guardarTokenAcceso(response.body().getAccessToken());
                    fetchUserProfile(response.body().getUser().getId(), callback);
                } else {
                    String errorMsg = obtenerDetalleError(response, "Login fallido");
                    RemoteLogger.log("AuthRepositoryImpl", "login", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                RemoteLogger.log("AuthRepositoryImpl", "login", "Fallo de red", t, null);
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void register(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones, Callback<Void> callback) {
        authApi.signUp(new AuthApi.SignUpRequest(email, password, nombreCompleto, telefono, direcciones)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMsg = obtenerDetalleError(response, "Error en registro");
                    RemoteLogger.log("AuthRepositoryImpl", "register", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                RemoteLogger.log("AuthRepositoryImpl", "register", "Fallo de red", t, null);
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void verifyOtp(String email, String otp, Callback<Usuario> callback) {
        authApi.verifyOtp(new AuthApi.VerifyOtpRequest(email, otp)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SesionSupabase.guardarTokenAcceso(response.body().getAccessToken());
                    fetchUserProfile(response.body().getUser().getId(), callback);
                } else {
                    callback.onError(obtenerDetalleError(response, "Codigo invalido"));
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void logLoginAcceptance(String usuarioId, String email) {
        authApi.logLoginAcceptance(new AuthApi.LoginAcceptanceRequest(usuarioId, email)).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("AuthRepository", "Aceptacion de login registrada");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("AuthRepository", "Error al registrar aceptacion: " + t.getMessage());
            }
        });
    }

    private void fetchUserProfile(String id, Callback<Usuario> callback) {
        supabaseApi.getPerfil("eq." + id, "*").enqueue(new retrofit2.Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentUser = response.body().get(0).toDomain();
                    callback.onSuccess(currentUser);
                } else {
                    callback.onError("No se pudo obtener el perfil");
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void recoverPassword(String email, Callback<Void> callback) {
        if (esTelefono(email)) {
            resolverCorreoPorTelefono(email, new Callback<String>() {
                @Override
                public void onSuccess(String correo) {
                    enviarRecuperacionPorCorreo(correo, callback);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
            return;
        }

        enviarRecuperacionPorCorreo(email, callback);
    }

    private void enviarRecuperacionPorCorreo(String email, Callback<Void> callback) {
        String redirectUrl = "supereconomico://reset-password";
        authApi.recoverPassword(redirectUrl, new AuthApi.RecoverRequest(email)).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMsg = obtenerDetalleError(response, "Error al enviar correo");
                    RemoteLogger.log("AuthRepositoryImpl", "recoverPassword", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                RemoteLogger.log("AuthRepositoryImpl", "recoverPassword", "Fallo de red", t, null);
                callback.onError(t.getMessage());
            }
        });
    }

    private void resolverCorreoPorTelefono(String telefono, Callback<String> callback) {
        String telefonoFormateado = formatearTelefono(telefono);
        buscarCorreoPorTelefono(telefonoFormateado, new Callback<String>() {
            @Override
            public void onSuccess(String correo) {
                callback.onSuccess(correo);
            }

            @Override
            public void onError(String message) {
                buscarCorreoPorTelefono(limpiarTelefono(telefono), callback);
            }
        });
    }

    private void buscarCorreoPorTelefono(String telefono, Callback<String> callback) {
        supabaseApi.getPerfilPorTelefono("eq." + telefono, "email,telefono").enqueue(new retrofit2.Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String correo = response.body().get(0).email;
                    if (correo != null && !correo.trim().isEmpty()) {
                        callback.onSuccess(correo.trim());
                        return;
                    }
                }
                callback.onError("No encontramos una cuenta con ese telefono");
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private boolean esTelefono(String valor) {
        return limpiarTelefono(valor).length() == 8 && !valor.contains("@");
    }

    private String limpiarTelefono(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^0-9]", "");
    }

    private String formatearTelefono(String valor) {
        String limpio = limpiarTelefono(valor);
        if (limpio.length() <= 4) {
            return limpio;
        }
        if (limpio.length() > 8) {
            limpio = limpio.substring(0, 8);
        }
        return limpio.substring(0, 4) + "-" + limpio.substring(4);
    }

    @Override
    public void updatePassword(String newPassword, String accessToken, Callback<Void> callback) {
        String authHeader = "Bearer " + accessToken;
        SesionSupabase.guardarTokenAcceso(accessToken);
        authApi.updateUser(authHeader, new AuthApi.UpdateUserRequest(newPassword)).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorMsg = obtenerDetalleError(response, "No se pudo actualizar la contrasena");
                    RemoteLogger.log("AuthRepositoryImpl", "updatePassword", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    @Override
    public void actualizarContrasenaConTokenHash(String newPassword, String tokenHash, Callback<Void> callback) {
        authApi.verificarRecuperacion(new AuthApi.VerificarRecuperacionRequest(tokenHash)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getAccessToken() != null) {
                    updatePassword(newPassword, response.body().getAccessToken(), callback);
                } else {
                    String errorMsg = obtenerDetalleError(response, "No se pudo validar el enlace de recuperacion");
                    RemoteLogger.log("AuthRepositoryImpl", "actualizarContrasenaConTokenHash", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    private String obtenerDetalleError(Response<?> response, String mensajeBase) {
        String detalle = null;
        try {
            if (response.errorBody() != null) {
                detalle = response.errorBody().string();
            }
        } catch (IOException ignored) {
            detalle = null;
        }

        if (detalle == null || detalle.trim().isEmpty()) {
            return mensajeBase + ". Codigo: " + response.code();
        }

        String detalleMinuscula = detalle.toLowerCase();
        if (detalleMinuscula.contains("same_password")) {
            return "La nueva contrasena debe ser diferente a la anterior.";
        }
        if (detalleMinuscula.contains("weak_password") || detalleMinuscula.contains("password should")) {
            return "La contrasena no cumple los requisitos de seguridad.";
        }
        if (detalleMinuscula.contains("expired") || detalleMinuscula.contains("invalid")) {
            return "El enlace de recuperacion expiro o no es valido. Solicita uno nuevo.";
        }

        return mensajeBase + ": " + detalle;
    }

    @Override
    public void logout() {
        SesionSupabase.cerrarSesion();
        currentUser = null;
    }

    @Override
    public Usuario getCurrentUser() {
        return currentUser;
    }
}
