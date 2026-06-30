package com.uth.supereconomico.data.remote;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.data.remote.models.DireccionRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body SignUpRequest request);

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/v1/verify")
    Call<AuthResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth/v1/verify")
    Call<AuthResponse> verificarRecuperacion(@Body VerificarRecuperacionRequest request);

    @POST("auth/v1/recover")
    Call<Void> recoverPassword(@Query("redirect_to") String redirectTo, @Body RecoverRequest request);

    @PUT("auth/v1/user")
    Call<Void> updateUser(@Header("Authorization") String token, @Body UpdateUserRequest request);

    @POST("rest/v1/aceptaciones_login")
    Call<Void> logLoginAcceptance(@Body LoginAcceptanceRequest request);

    class LoginAcceptanceRequest {
        @SerializedName("usuario_id")
        String usuarioId;
        String email;
        public LoginAcceptanceRequest(String usuarioId, String email) {
            this.usuarioId = usuarioId;
            this.email = email;
        }
    }

    class UpdateUserRequest {
        String password;
        public UpdateUserRequest(String password) {
            this.password = password;
        }
    }

    class SignUpRequest {
        String email;
        String password;
        @SerializedName("data")
        UserData data;

        public SignUpRequest(String email, String password, String nombreCompleto, String telefono, List<DireccionRequest> direcciones) {
            this.email = email;
            this.password = password;
            this.data = new UserData(nombreCompleto, telefono, direcciones);
        }
    }

    class UserData {
        @SerializedName("nombre_completo")
        String nombreCompleto;
        @SerializedName("telefono")
        String telefono;
        @SerializedName("direcciones")
        List<DireccionRequest> direcciones;

        public UserData(String nombreCompleto, String telefono, List<DireccionRequest> direcciones) {
            this.nombreCompleto = nombreCompleto;
            this.telefono = telefono;
            this.direcciones = direcciones;
        }
    }

    class LoginRequest {
        String email;
        String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class VerifyOtpRequest {
        String email;
        String token;
        @SerializedName("type")
        String type = "signup";

        public VerifyOtpRequest(String email, String token) {
            this.email = email;
            this.token = token;
        }
    }

    class VerificarRecuperacionRequest {
        @SerializedName("token_hash")
        String tokenHash;
        @SerializedName("type")
        String type = "recovery";

        public VerificarRecuperacionRequest(String tokenHash) {
            this.tokenHash = tokenHash;
        }
    }

    class RecoverRequest {
        String email;

        public RecoverRequest(String email) {
            this.email = email;
        }
    }

    class AuthResponse {
        @SerializedName("access_token")
        String accessToken;
        @SerializedName("user")
        UserResponse user;

        public String getAccessToken() { return accessToken; }
        public UserResponse getUser() { return user; }
    }

    class UserResponse {
        String id;
        String email;
        public String getId() { return id; }
        public String getEmail() { return email; }
    }
}
