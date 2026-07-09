package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class DireccionRequest {
    public Long id;
    
    @SerializedName("perfil_id")
    public String perfilId;
    
    public String etiqueta;
    
    @SerializedName("direccion_texto")
    public String direccion;
    
    public Double latitud;
    public Double longitud;

    public DireccionRequest() {}

    public DireccionRequest(String etiqueta, String direccion, Double latitud, Double longitud) {
        this.etiqueta = etiqueta;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
