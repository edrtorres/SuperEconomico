package com.uth.supereconomico.domain.entities;

public class Producto {
    private final long id;
    private final String nombre;
    private final String descripcion;
    private final double precio;
    private final String imagenUrl;

    public Producto(long id, String nombre, String descripcion, double precio, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
}
