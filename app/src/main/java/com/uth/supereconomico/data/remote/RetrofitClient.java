package com.uth.supereconomico.data.remote;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("apikey", SupabaseConfig.ANON_KEY)
                        .method(original.method(), original.body());

                if (original.header("Authorization") == null) {
                    String token = SupabaseConfig.ANON_KEY;
                    String url = original.url().toString();
                    
                    // Si estamos logueados y NO es una petición de auth/lookup inicial, usamos el token de sesión
                    if (SesionSupabase.haySesionActiva() && !url.contains("/auth/v1/token") && !url.contains("perfiles?telefono=")) {
                        token = SesionSupabase.obtenerTokenAcceso();
                    }
                    
                    requestBuilder.header("Authorization", "Bearer " + token);
                }

                return chain.proceed(requestBuilder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseConfig.URL + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
