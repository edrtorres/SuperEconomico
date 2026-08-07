package com.uth.supereconomico.data.remote.models;

import android.os.Build;
import com.google.gson.annotations.SerializedName;

public class AccessLogRequest {
    @SerializedName("usuario_id")
    private final String usuarioId;
    private final String email;
    private final String rol;
    private final String origen;
    private final String evento;
    @SerializedName("user_agent")
    private final String userAgent;

    public AccessLogRequest(String usuarioId, String email, String rol, String origen, String evento) {
        this.usuarioId = usuarioId;
        this.email = email;
        this.rol = rol;
        this.origen = origen;
        this.evento = evento;
        this.userAgent = Build.MANUFACTURER + " " + Build.MODEL + " (API " + Build.VERSION.SDK_INT + ")";
    }
}
