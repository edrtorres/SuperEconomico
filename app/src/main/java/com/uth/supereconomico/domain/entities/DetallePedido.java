package com.uth.supereconomico.domain.entities;

public class DetallePedido {
    private final long productoId;
    private final int cantidad;
    private final double precioCompra;

    public DetallePedido(long productoId, int cantidad, double precioCompra) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
    }

    public long getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
    public double getPrecioCompra() { return precioCompra; }
}
