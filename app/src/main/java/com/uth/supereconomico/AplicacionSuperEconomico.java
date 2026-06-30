package com.uth.supereconomico;

import android.app.Application;

import com.uth.supereconomico.data.remote.SesionSupabase;

public class AplicacionSuperEconomico extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SesionSupabase.inicializar(this);
    }
}
