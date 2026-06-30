package com.uth.supereconomico.data.remote.models;

import android.os.Build;
import com.google.gson.annotations.SerializedName;

public class ErrorLogRequest {
    @SerializedName("clase_origen")
    private String claseOrigen;
    
    @SerializedName("metodo")
    private String metodo;
    
    @SerializedName("mensaje_error")
    private String mensajeError;
    
    @SerializedName("stack_trace")
    private String stackTrace;
    
    @SerializedName("dispositivo")
    private String dispositivo;
    
    @SerializedName("version_app")
    private String versionApp;
    
    @SerializedName("usuario_id")
    private String usuarioId;

    public ErrorLogRequest(String claseOrigen, String metodo, String mensajeError, String stackTrace, String usuarioId) {
        this.claseOrigen = claseOrigen;
        this.metodo = metodo;
        this.mensajeError = mensajeError;
        this.stackTrace = stackTrace;
        this.usuarioId = usuarioId;
        this.dispositivo = Build.MANUFACTURER + " " + Build.MODEL + " (API " + Build.VERSION.SDK_INT + ")";
        this.versionApp = "1.0.0";
    }
}
