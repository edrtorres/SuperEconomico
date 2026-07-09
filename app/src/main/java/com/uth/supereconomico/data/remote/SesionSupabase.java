package com.uth.supereconomico.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

public class SesionSupabase {
    private static final String NOMBRE_PREFERENCIAS = "sesion_supabase";
    private static final String CLAVE_TOKEN_ACCESO = "token_acceso";
    private static final String CLAVE_ID_USUARIO = "id_usuario";

    private static SharedPreferences preferencias;
    private static String tokenAcceso;
    private static String idUsuario;

    private SesionSupabase() {
    }

    public static void inicializar(Context contexto) {
        if (preferencias == null) {
            preferencias = contexto.getApplicationContext()
                    .getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            tokenAcceso = preferencias.getString(CLAVE_TOKEN_ACCESO, null);
            idUsuario = preferencias.getString(CLAVE_ID_USUARIO, null);
        }
    }

    public static void guardarSesion(String token, String id) {
        tokenAcceso = token;
        idUsuario = id;
        if (preferencias != null) {
            preferencias.edit()
                    .putString(CLAVE_TOKEN_ACCESO, token)
                    .putString(CLAVE_ID_USUARIO, id)
                    .apply();
            android.util.Log.d("Sesion", "Sesion guardada para ID: " + id);
        }
    }

    public static String obtenerTokenAcceso() {
        return tokenAcceso;
    }

    public static String obtenerIdUsuario() {
        return idUsuario;
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
