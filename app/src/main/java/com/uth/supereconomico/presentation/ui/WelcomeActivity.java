package com.uth.supereconomico.presentation.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.uth.supereconomico.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final long DURACION_SPLASH_MS = 8000L;
    private static final long INTERVALO_PROGRESO_MS = 80L;

    private ProgressBar progresoSplash;
    private TextView tvPorcentajeSplash;
    private CountDownTimer temporizadorSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_welcome);

        progresoSplash = findViewById(R.id.progresoSplash);
        tvPorcentajeSplash = findViewById(R.id.tvPorcentajeSplash);

        iniciarSplash();
    }

    private void iniciarSplash() {
        temporizadorSplash = new CountDownTimer(DURACION_SPLASH_MS, INTERVALO_PROGRESO_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                long transcurrido = DURACION_SPLASH_MS - millisUntilFinished;
                int porcentaje = (int) Math.min(100, (transcurrido * 100) / DURACION_SPLASH_MS);
                actualizarProgreso(porcentaje);
            }

            @Override
            public void onFinish() {
                actualizarProgreso(100);
                abrirLogin();
            }
        };
        temporizadorSplash.start();
    }

    private void actualizarProgreso(int porcentaje) {
        progresoSplash.setProgress(porcentaje);
        tvPorcentajeSplash.setText(porcentaje + "%");
    }

    private void abrirLogin() {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (temporizadorSplash != null) {
            temporizadorSplash.cancel();
        }
        super.onDestroy();
    }
}
