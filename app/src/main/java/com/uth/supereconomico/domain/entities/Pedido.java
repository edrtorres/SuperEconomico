package com.uth.supereconomico.domain.entities;

import java.util.List;

public class Pedido {
    private final Long id;
    private final String perfilId;
    private final String estado;
    private final Double total;
    private final Long direccionId;
    private final String metodoPago;
    private final String creadoAt;
    private final List<Item> items;

    public Pedido(Long id, String perfilId, String estado, Double total, Long direccionId, String metodoPago, String creadoAt, List<Item> items) {
        this.id = id;
        this.perfilId = perfilId;
        this.estado = estado;
        this.total = total;
        this.direccionId = direccionId;
        this.metodoPago = metodoPago;
        this.creadoAt = creadoAt;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getPerfilId() { return perfilId; }
    public String getEstado() { return estado; }
    public Double getTotal() { return total; }
    public Long getDireccionId() { return direccionId; }
    public String getMetodoPago() { return metodoPago; }
    public String getCreadoAt() { return creadoAt; }
    public List<Item> getItems() { return items; }

    public static class Item {
        private final Long id;
        private final Long pedidoId;
        private final Long productoId;
        private final Integer cantidad;
        private final Double precioUnitario;
        private final String nombre;
        private final String imagenUrl;

        public Item(Long id, Long pedidoId, Long productoId, Integer cantidad, Double precioUnitario, String nombre, String imagenUrl) {
            this.id = id;
            this.pedidoId = pedidoId;
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.nombre = nombre;
            this.imagenUrl = imagenUrl;
        }

        public Long getId() { return id; }
        public Long getPedidoId() { return pedidoId; }
        public Long getProductoId() { return productoId; }
        public Integer getCantidad() { return cantidad; }
        public Double getPrecioUnitario() { return precioUnitario; }
        public String getNombre() { return nombre; }
        public String getImagenUrl() { return imagenUrl; }
    }
}
