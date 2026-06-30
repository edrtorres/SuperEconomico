package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.Producto;

public class ProductDTO {
    public long id;
    public String nombre;
    public String descripcion;
    public double precio;
    
    @SerializedName("imagen_url")
    public String imagenUrl;

    public Producto toDomain() {
        return new Producto(id, nombre, descripcion, precio, imagenUrl);
    }
}
