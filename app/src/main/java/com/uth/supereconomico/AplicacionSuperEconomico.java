package com.uth.supereconomico;

import android.app.Application;

import com.uth.supereconomico.data.remote.SesionSupabase;
import com.uth.supereconomico.utils.CartPersistence;

public class AplicacionSuperEconomico extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SesionSupabase.inicializar(this);
        CartPersistence.init(this);
    }
}
