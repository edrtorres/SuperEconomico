package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.Producto;

public class ProductDTO {
    public long id;
    
    @SerializedName("categoria_id")
    public Long categoriaId;

    public String nombre;
    public String descripcion;
    public double precio;
    
    @SerializedName("imagen_url")
    public String imagenUrl;

    @SerializedName("es_oferta")
    public boolean esOferta;

    @SerializedName("precio_oferta")
    public Double precioOferta;

    public Producto toDomain() {
        return new Producto(id, nombre, descripcion, precio, imagenUrl, esOferta, precioOferta != null ? precioOferta : precio);
    }
}
