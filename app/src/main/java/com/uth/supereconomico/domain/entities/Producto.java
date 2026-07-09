package com.uth.supereconomico.domain.entities;

public class Producto {
    private final long id;
    private final String nombre;
    private final String descripcion;
    private final double precio;
    private final String imagenUrl;
    private boolean esOferta;
    private double precioOferta;

    public Producto(long id, String nombre, String descripcion, double precio, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.esOferta = false;
        this.precioOferta = precio;
    }

    public Producto(long id, String nombre, String descripcion, double precio, String imagenUrl, boolean esOferta, double precioOferta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.esOferta = esOferta;
        this.precioOferta = precioOferta;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getImagenUrl() { return imagenUrl; }
    public boolean isEsOferta() { return esOferta; }
    public double getPrecioOferta() { return esOferta ? precioOferta : precio; }
    public void setEsOferta(boolean esOferta) { this.esOferta = esOferta; }
    public void setPrecioOferta(double precioOferta) { this.precioOferta = precioOferta; }
}
