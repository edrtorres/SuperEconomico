package com.uth.supereconomico.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionSupabase {
    private static final String NOMBRE_PREFERENCIAS = "sesion_supabase";
    private static final String CLAVE_TOKEN_ACCESO = "token_acceso";

    private static SharedPreferences preferencias;
    private static String tokenAcceso;

    private SesionSupabase() {
    }

    public static void inicializar(Context contexto) {
        if (preferencias == null) {
            preferencias = contexto.getApplicationContext()
                    .getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            tokenAcceso = preferencias.getString(CLAVE_TOKEN_ACCESO, null);
        }
    }

    public static void guardarTokenAcceso(String token) {
        tokenAcceso = token;
        if (preferencias != null) {
            preferencias.edit().putString(CLAVE_TOKEN_ACCESO, token).apply();
        }
    }

    public static String obtenerTokenAcceso() {
        return tokenAcceso;
    }

    public static boolean haySesionActiva() {
        return tokenAcceso != null && !tokenAcceso.trim().isEmpty();
    }

    public static void cerrarSesion() {
        tokenAcceso = null;
        if (preferencias != null) {
            preferencias.edit().remove(CLAVE_TOKEN_ACCESO).apply();
        }
    }
}
