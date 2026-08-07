package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.Pedido;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
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

    public Pedido toDomain() {
        return new Pedido(id, perfilId, estado, total, direccionId, metodoPago, creadoAt, null);
    }

    public static class Item {
        public Long id;

        @SerializedName("pedido_id")
        public Long pedidoId;

        @SerializedName("producto_id")
        public Long productoId;
        
        public Integer cantidad;
        
        @SerializedName("precio_unitario")
        public Double precioUnitario;
        
        public String nombre;
        
        @SerializedName("imagen_url")
        public String imagenUrl;

        @SerializedName("productos")
        public ProductDTO producto;

        public Item() {}

        public Pedido.Item toDomain() {
            String finalNombre = nombre;
            String finalImagen = imagenUrl;
            if (producto != null) {
                if (finalNombre == null || finalNombre.isEmpty()) finalNombre = producto.nombre;
                if (finalImagen == null || finalImagen.isEmpty()) finalImagen = producto.imagenUrl;
            }
            return new Pedido.Item(id, pedidoId, productoId, cantidad, precioUnitario, finalNombre, finalImagen);
        }

        public static Item fromDomain(Pedido.Item domain) {
            Item dto = new Item();
            dto.id = domain.getId();
            dto.pedidoId = domain.getPedidoId();
            dto.productoId = domain.getProductoId();
            dto.cantidad = domain.getCantidad();
            dto.precioUnitario = domain.getPrecioUnitario();
            dto.nombre = domain.getNombre();
            dto.imagenUrl = domain.getImagenUrl();
            return dto;
        }
    }
}
