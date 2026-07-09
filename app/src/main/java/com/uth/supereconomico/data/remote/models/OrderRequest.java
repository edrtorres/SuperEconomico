package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderRequest {
    public Long id;

    @SerializedName("perfil_id")
    public String perfilId;
    
    public String estado = "pendiente";
    public Double total;
    
    @SerializedName("direccion_id")
    public Long direccionId;

    @SerializedName("metodo_pago")
    public String metodoPago;

    @SerializedName("creado_at")
    public String creadoAt;

    public static class Item {
        public Long id;

        @SerializedName("pedido_id")
        public Long pedidoId;

        @SerializedName("producto_id")
        public Long productoId;
        
        public Integer cantidad;
        
        @SerializedName("precio_unitario")
        public Double precioUnitario;
        
        // Campos para persistencia visual en carrito
        public String nombre;
        
        @SerializedName("imagen_url")
        public String imagenUrl;

        // Soporte para join con tabla productos al consultar historial
        @SerializedName("productos")
        public ProductDTO producto;

        public Item() {}

        public Item(Long productoId, Integer cantidad, Double precioUnitario) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Item(Long productoId, Integer cantidad, Double precioUnitario, String nombre, String imagenUrl) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.nombre = nombre;
            this.imagenUrl = imagenUrl;
        }
    }
}
