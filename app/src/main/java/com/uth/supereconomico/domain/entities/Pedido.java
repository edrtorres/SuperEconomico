package com.uth.supereconomico.domain.entities;

import java.util.Date;
import java.util.List;

public class Pedido {
    public enum Estado {
        PENDIENTE, COCINA, ENTREGA, CERRADO
    }

    private final String id;
    private final String usuarioId;
    private final Estado estado;
    private final double montoTotal;
    private final double entregaLat;
    private final double entregaLng;
    private final Integer calificacion;
    private final Date creadoAt;
    private final List<DetallePedido> detalles;

    public Pedido(String id, String usuarioId, Estado estado, double montoTotal, double entregaLat, double entregaLng, Integer calificacion, Date creadoAt, List<DetallePedido> detalles) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.montoTotal = montoTotal;
        this.entregaLat = entregaLat;
        this.entregaLng = entregaLng;
        this.calificacion = calificacion;
        this.creadoAt = creadoAt;
        this.detalles = detalles;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public Estado getEstado() { return estado; }
    public double getMontoTotal() { return montoTotal; }
    public double getEntregaLat() { return entregaLat; }
    public double getEntregaLng() { return entregaLng; }
    public Integer getCalificacion() { return calificacion; }
    public Date getCreadoAt() { return creadoAt; }
    public List<DetallePedido> getDetalles() { return detalles; }
}
