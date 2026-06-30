package com.uth.supereconomico.utils;

import android.util.Log;
import com.uth.supereconomico.data.remote.RetrofitClient;
import com.uth.supereconomico.data.remote.SupabaseApi;
import com.uth.supereconomico.data.remote.models.ErrorLogRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteLogger {
    private static final String TAG = "RemoteLogger";
    private static SupabaseApi supabaseApi;

    private static SupabaseApi getApi() {
        if (supabaseApi == null) {
            supabaseApi = RetrofitClient.getClient().create(SupabaseApi.class);
        }
        return supabaseApi;
    }

    public static void log(String className, String method, String errorMessage, Throwable t, String userId) {
        String stackTrace = "";
        if (t != null) {
            stackTrace = Log.getStackTraceString(t);
        }

        ErrorLogRequest logRequest = new ErrorLogRequest(className, method, errorMessage, stackTrace, userId);
        
        getApi().logError(logRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Error log enviado a Supabase exitosamente");
                } else {
                    Log.e(TAG, "Fallo al enviar log a Supabase: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error de red al enviar log: " + t.getMessage());
            }
        });
        
        // También imprimir en Logcat local
        Log.e(className, method + ": " + errorMessage, t);
    }
}
