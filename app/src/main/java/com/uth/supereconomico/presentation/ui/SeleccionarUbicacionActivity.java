package com.uth.supereconomico.presentation.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.uth.supereconomico.R;

public class SeleccionarUbicacionActivity extends AppCompatActivity {

    private static final int CODIGO_PERMISO_UBICACION = 2101;
    private static final double LATITUD_DEFECTO = 14.0818;
    private static final double LONGITUD_DEFECTO = -87.2068;

    private WebView webViewMapa;
    private FusedLocationProviderClient clienteUbicacion;
    private Double latitudSeleccionada;
    private Double longitudSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_ubicacion);

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this);
        webViewMapa = findViewById(R.id.webViewMapa);

        ImageButton btnBack = findViewById(R.id.btnBack);
        MaterialButton btnConfirmarUbicacion = findViewById(R.id.btnConfirmarUbicacion);

        btnBack.setOnClickListener(v -> finish());
        btnConfirmarUbicacion.setOnClickListener(v -> confirmarUbicacion());

        prepararMapa();
        prepararUbicacionActual();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void prepararMapa() {
        webViewMapa.getSettings().setJavaScriptEnabled(true);
        webViewMapa.getSettings().setDomStorageEnabled(true);
        webViewMapa.getSettings().setDatabaseEnabled(true);
        webViewMapa.getSettings().setGeolocationEnabled(true);
        webViewMapa.getSettings().setLoadWithOverviewMode(true);
        webViewMapa.getSettings().setUseWideViewPort(true);
        webViewMapa.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.99 Mobile Safari/537.36");
        webViewMapa.addJavascriptInterface(new PuenteMapa(), "Android");
        
        // Evitar que el sistema abra enlaces fuera del WebView
        webViewMapa.setWebViewClient(new android.webkit.WebViewClient());
        webViewMapa.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    private void prepararUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODIGO_PERMISO_UBICACION);
            cargarMapa(LATITUD_DEFECTO, LONGITUD_DEFECTO, 13);
            return;
        }

        clienteUbicacion.getLastLocation().addOnSuccessListener(this::usarUltimaUbicacion);
    }

    private void usarUltimaUbicacion(Location location) {
        if (location != null) {
            cargarMapa(location.getLatitude(), location.getLongitude(), 16);
        } else {
            cargarMapa(LATITUD_DEFECTO, LONGITUD_DEFECTO, 13);
            Toast.makeText(this, "Toca el mapa para marcar tu ubicacion", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarMapa(double latitud, double longitud, int zoom) {
        latitudSeleccionada = latitud;
        longitudSeleccionada = longitud;
        webViewMapa.loadDataWithBaseURL(
                "https://unpkg.com/",
                crearHtmlMapa(latitud, longitud, zoom),
                "text/html",
                "UTF-8",
                null
        );
    }

    private String crearHtmlMapa(double latitud, double longitud, int zoom) {
        return "<!doctype html>"
                + "<html><head>"
                + "<meta charset='utf-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                + "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'>"
                + "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>"
                + "<style>"
                + "  html, body, #mapa { height: 100%; margin: 0; padding: 0; width: 100%; }"
                + "  .leaflet-control-attribution { font-size: 8px; }"
                + "</style>"
                + "</head><body>"
                + "<div id='mapa'></div>"
                + "<script>"
                + "  var lat = " + latitud + ";"
                + "  var lng = " + longitud + ";"
                + "  var mapa = L.map('mapa', { zoomControl: false }).setView([lat, lng], " + zoom + ");"
                + "  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {"
                + "    maxZoom: 19,"
                + "    attribution: '&copy; OpenStreetMap'"
                + "  }).addTo(mapa);"
                + "  var marcador = L.marker([lat, lng], { draggable: true }).addTo(mapa);"
                + "  function enviar(p) {"
                + "    if(window.Android && Android.seleccionarUbicacion) {"
                + "      Android.seleccionarUbicacion(p.lat, p.lng);"
                + "    }"
                + "  }"
                + "  marcador.on('dragend', function(e) { enviar(marcador.getLatLng()); });"
                + "  mapa.on('click', function(e) { marcador.setLatLng(e.latlng); enviar(e.latlng); });"
                + "  setTimeout(function() { mapa.invalidateSize(); }, 500);"
                + "</script>"
                + "</body></html>";
    }

    private void confirmarUbicacion() {
        if (latitudSeleccionada == null || longitudSeleccionada == null) {
            Toast.makeText(this, "Selecciona una ubicacion en el mapa", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra("latitud", latitudSeleccionada);
        data.putExtra("longitud", longitudSeleccionada);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISO_UBICACION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            prepararUbicacionActual();
        }
    }

    private class PuenteMapa {
        @JavascriptInterface
        public void seleccionarUbicacion(double latitud, double longitud) {
            runOnUiThread(() -> {
                latitudSeleccionada = latitud;
                longitudSeleccionada = longitud;
            });
        }
    }
}
