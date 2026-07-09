package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.Categoria;

public class CategoriaDTO {
    public long id;
    public String nombre;
    
    @SerializedName("imagen_url")
    public String imagenUrl;

    public Categoria toDomain() {
        return new Categoria(id, nombre, imagenUrl);
    }
}
