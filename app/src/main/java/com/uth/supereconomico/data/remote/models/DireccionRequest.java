package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class DireccionRequest {
    private String etiqueta;
    private String direccion;
    private Double latitud;
    private Double longitud;

    public DireccionRequest(String etiqueta, String direccion, Double latitud, Double longitud) {
        this.etiqueta = etiqueta;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getDireccion() { return direccion; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
}
