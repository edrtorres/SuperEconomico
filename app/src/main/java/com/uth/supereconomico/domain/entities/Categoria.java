package com.uth.supereconomico.domain.entities;

public class Categoria {
    private final long id;
    private final String nombre;
    private final String iconoUrl;

    public Categoria(long id, String nombre, String iconoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.iconoUrl = iconoUrl;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getIconoUrl() { return iconoUrl; }
}
