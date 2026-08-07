package com.uth.supereconomico.data.repositories;

import com.uth.supereconomico.data.remote.AuthApi;
import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.AccessLogRequest;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import com.uth.supereconomico.data.remote.models.UserDTO;
import com.uth.supereconomico.domain.entities.Usuario;
import com.uth.supereconomico.domain.repositories.AuthRepository;
import com.uth.supereconomico.utils.RemoteLogger;
import com.uth.supereconomico.utils.UserFriendlyError;

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
    public void login(String emailOrPhone, String password, Callback<Usuario> callback) {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            callback.onError("Ingresa tu correo o teléfono registrado");
            return;
        }
        String identifier = emailOrPhone.trim();
        if (identifier.contains("@")) iniciarSesionConCorreo(identifier, password, callback);
        else iniciarSesionConTelefono(identifier, password, callback);
    }

    private void iniciarSesionConTelefono(String telefono, String password, Callback<Usuario> callback) {
        authApi.loginByPhone(new AuthApi.PhoneLoginRequest(telefono, password)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    AuthApi.AuthResponse auth = response.body();
                    SesionSupabase.guardarSesion(auth.getAccessToken(), auth.getRefreshToken(), auth.getExpiresIn(), auth.getUser().getId());
                    validarSesionCliente(callback);
                } else callback.onError(obtenerDetalleError(response, "Correo/teléfono o contraseña incorrectos"));
            }
            @Override public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) { callback.onError(UserFriendlyError.fromThrowable(t)); }
        });
    }


    private void iniciarSesionConCorreo(String email, String password, Callback<Usuario> callback) {
        authApi.login(new AuthApi.LoginRequest(email, password)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApi.AuthResponse auth = response.body();
                    SesionSupabase.guardarSesion(auth.getAccessToken(), auth.getRefreshToken(), auth.getExpiresIn(), auth.getUser().getId());
                    validarSesionCliente(callback);
                } else if (response.code() == 404 || response.code() == 405) {
                    iniciarSesionConCorreoNativo(email, password, callback);
                } else {
                    String errorMsg = obtenerDetalleError(response, "Login fallido");
                    RemoteLogger.log("AuthRepositoryImpl", "login", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                RemoteLogger.log("AuthRepositoryImpl", "login", "Fallo de red", t, null);
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    private void iniciarSesionConCorreoNativo(String email, String password, Callback<Usuario> callback) {
        authApi.loginNative(new AuthApi.LoginRequest(email, password)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApi.AuthResponse auth = response.body();
                    SesionSupabase.guardarSesion(auth.getAccessToken(), auth.getRefreshToken(), auth.getExpiresIn(), auth.getUser().getId());
                    validarSesionCliente(callback);
                } else {
                    String errorMsg = obtenerDetalleError(response, "Login fallido");
                    RemoteLogger.log("AuthRepositoryImpl", "loginNative", errorMsg, null, null);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.AuthResponse> call, Throwable t) {
                RemoteLogger.log("AuthRepositoryImpl", "loginNative", "Fallo de red", t, null);
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void register(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones, Callback<Void> callback) {
        String redirectUrl = "supereconomico://login";
        authApi.signUp(redirectUrl, new AuthApi.SignUpRequest(email, password, nombreCompleto, telefono, direcciones)).enqueue(new retrofit2.Callback<AuthApi.AuthResponse>() {
            @Override
            public void onResponse(Call<AuthApi.AuthResponse> call, Response<AuthApi.AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApi.AuthResponse auth = response.body();
                    if (auth.getAccessToken() != null && auth.getUser() != null) {
                        SesionSupabase.guardarSesion(auth.getAccessToken(), auth.getRefreshToken(), auth.getExpiresIn(), auth.getUser().getId());
                    }
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
                callback.onError(UserFriendlyError.fromThrowable(t));
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

    private void validarSesionCliente(Callback<Usuario> callback) {
        authApi.me().enqueue(new retrofit2.Callback<AuthApi.MeResponse>() {
            @Override
            public void onResponse(Call<AuthApi.MeResponse> call, Response<AuthApi.MeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getProfile() != null) {
                    UserDTO dto = response.body().getProfile();
                    if (!"cliente".equalsIgnoreCase(dto.rol)) {
                        registrarAcceso(dto, "login");
                        SesionSupabase.cerrarSesion();
                        currentUser = null;
                        if (callback != null) callback.onError("Esta cuenta no pertenece a la app de clientes.");
                        return;
                    }
                    currentUser = dto.toDomain();
                    SesionSupabase.actualizarIdUsuario(dto.id);
                    registrarAcceso(dto, "login");
                    if (callback != null) callback.onSuccess(currentUser);
                } else {
                    SesionSupabase.cerrarSesion();
                    currentUser = null;
                    if (callback != null) callback.onError(UserFriendlyError.fromResponse(response, "No se pudo validar tu cuenta"));
                }
            }

            @Override
            public void onFailure(Call<AuthApi.MeResponse> call, Throwable t) {
                SesionSupabase.cerrarSesion();
                currentUser = null;
                if (callback != null) callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    private void registrarAcceso(UserDTO user, String evento) {
        if (user == null || user.id == null) return;
        supabaseApi.logAccess(new AccessLogRequest(user.id, user.email, user.rol, "app_cliente", evento)).enqueue(new retrofit2.Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) { }
            @Override public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    private void fetchUserProfile(String id, Callback<Usuario> callback) {
        supabaseApi.getPerfil("eq." + id, "*").enqueue(new retrofit2.Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserDTO dto = response.body().get(0);
                    currentUser = dto.toDomain();
                    
                    // Asegurar que el ID se guarde en la sesión global
                    SesionSupabase.actualizarIdUsuario(dto.id);

                    if (callback != null) callback.onSuccess(currentUser);
                } else {
                    if (callback != null) callback.onError("No se pudo obtener el perfil");
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                if (callback != null) callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void recoverPassword(String email, Callback<Void> callback) {
        if (email == null || !email.contains("@")) {
            callback.onError("Ingresa el correo electrónico registrado");
            return;
        }
        enviarRecuperacionPorCorreo(email.trim(), callback);
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
                callback.onError(UserFriendlyError.fromThrowable(t));
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
                callback.onError(UserFriendlyError.fromThrowable(t));
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
        String token = (accessToken != null) ? accessToken : SesionSupabase.obtenerTokenAcceso();
        if (token == null) {
            callback.onError("No hay una sesión válida para cambiar la contraseña");
            return;
        }

        String authHeader = "Bearer " + token;
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
                callback.onError(UserFriendlyError.fromThrowable(t));
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
                callback.onError(UserFriendlyError.fromThrowable(t));
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
            return UserFriendlyError.fromResponse(response, mensajeBase);
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

        String friendly = UserFriendlyError.fromMessage(detalle);
        if (!friendly.equals(detalle)) {
            return friendly;
        }
        return UserFriendlyError.fromResponse(response, mensajeBase);
    }

    @Override
    public void updateProfile(String id, String nombreCompleto, String telefono, String direccion, String descripcion, String avatarUrl, Callback<Void> callback) {
        UserDTO updateRequest = new UserDTO();
        updateRequest.nombreCompleto = nombreCompleto;
        updateRequest.telefono = telefono;
        updateRequest.direccion = direccion;
        updateRequest.descripcion = descripcion;
        updateRequest.avatarUrl = avatarUrl;

        supabaseApi.updatePerfil("eq." + id, updateRequest).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Actualizar usuario local si existe
                    if (currentUser != null && currentUser.getId().equals(id)) {
                        currentUser = new Usuario(id, currentUser.getEmail(), nombreCompleto, currentUser.getRol(), avatarUrl, descripcion, currentUser.getLatitud(), currentUser.getLongitud(), telefono, direccion);
                    }
                    callback.onSuccess(null);
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudo actualizar tu perfil"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void updateFcmToken(String id, String token, Callback<Void> callback) {
        UserDTO updateRequest = new UserDTO();
        updateRequest.fcmToken = token;

        supabaseApi.updatePerfil("eq." + id, updateRequest).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudo actualizar la notificación del dispositivo"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void getAddresses(String perfilId, Callback<List<DireccionRequest>> callback) {
        supabaseApi.getDirecciones("eq." + perfilId, "*").enqueue(new retrofit2.Callback<List<DireccionRequest>>() {
            @Override
            public void onResponse(Call<List<DireccionRequest>> call, Response<List<DireccionRequest>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudieron cargar tus direcciones"));
                }
            }

            @Override
            public void onFailure(Call<List<DireccionRequest>> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void addAddress(DireccionRequest address, Callback<Void> callback) {
        supabaseApi.insertDireccion(address).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudo guardar la dirección"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void deleteAddress(long addressId, Callback<Void> callback) {
        supabaseApi.deleteDireccion("eq." + addressId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudo eliminar la dirección"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void getPaymentMethods(String perfilId, Callback<List<com.uth.supereconomico.domain.entities.MetodoPago>> callback) {
        supabaseApi.getMetodosPago("eq." + perfilId, "*").enqueue(new retrofit2.Callback<List<com.uth.supereconomico.data.remote.models.MetodoPagoDTO>>() {
            @Override
            public void onResponse(Call<List<com.uth.supereconomico.data.remote.models.MetodoPagoDTO>> call, Response<List<com.uth.supereconomico.data.remote.models.MetodoPagoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.uth.supereconomico.domain.entities.MetodoPago> list = new java.util.ArrayList<>();
                    for (com.uth.supereconomico.data.remote.models.MetodoPagoDTO dto : response.body()) {
                        list.add(dto.toDomain());
                    }
                    callback.onSuccess(list);
                } else {
                    callback.onError(UserFriendlyError.fromResponse(response, "No se pudieron cargar tus métodos de pago"));
                }
            }
            @Override
            public void onFailure(Call<List<com.uth.supereconomico.data.remote.models.MetodoPagoDTO>> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void addPaymentMethod(com.uth.supereconomico.data.remote.models.MetodoPagoDTO method, Callback<Void> callback) {
        supabaseApi.insertMetodoPago(method).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo guardar el método de pago"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void deletePaymentMethod(long methodId, Callback<Void> callback) {
        supabaseApi.deleteMetodoPago("eq." + methodId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) callback.onSuccess(null);
                else callback.onError(UserFriendlyError.fromResponse(response, "No se pudo eliminar el método de pago"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(UserFriendlyError.fromThrowable(t));
            }
        });
    }

    @Override
    public void logout() {
        String accessToken = SesionSupabase.obtenerTokenAcceso();
        if (currentUser != null) {
            UserDTO user = new UserDTO();
            user.id = currentUser.getId();
            user.email = currentUser.getEmail();
            user.rol = currentUser.getRol().name().toLowerCase();
            registrarAcceso(user, "logout");
        }
        if (accessToken != null) {
            authApi.logout("Bearer " + accessToken).enqueue(new retrofit2.Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) { }
                @Override public void onFailure(Call<Void> call, Throwable t) { }
            });
        }
        SesionSupabase.cerrarSesion();
        currentUser = null;
    }

    @Override
    public Usuario getCurrentUser() {
        if (currentUser == null && SesionSupabase.haySesionActiva()) {
            String id = SesionSupabase.obtenerIdUsuario();
            if (id != null) {
                // Sincrónico para el primer llamado si es necesario, 
                // pero mejor disparar una carga asíncrona y devolver null por ahora
                // para que el ViewModel reintente o observe.
                fetchUserProfile(id, null);
            }
        }
        return currentUser;
    }
}
