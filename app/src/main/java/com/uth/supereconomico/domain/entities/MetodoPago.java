package com.uth.supereconomico.domain.entities;

public class MetodoPago {
    private final Long id;
    private final String perfilId;
    private final String tipo; // "tarjeta", "efectivo"
    private final String numeroEnmascarado; // **** **** **** 1234
    private final String titular;
    private final String fechaVencimiento;

    public MetodoPago(Long id, String perfilId, String tipo, String numeroEnmascarado, String titular, String fechaVencimiento) {
        this.id = id;
        this.perfilId = perfilId;
        this.tipo = tipo;
        this.numeroEnmascarado = numeroEnmascarado;
        this.titular = titular;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getId() { return id; }
    public String getPerfilId() { return perfilId; }
    public String getTipo() { return tipo; }
    public String getNumeroEnmascarado() { return numeroEnmascarado; }
    public String getTitular() { return titular; }
    public String getFechaVencimiento() { return fechaVencimiento; }
}
