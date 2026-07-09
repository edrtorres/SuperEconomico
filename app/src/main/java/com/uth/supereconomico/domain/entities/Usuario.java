package com.uth.supereconomico.domain.entities;

public class Usuario {
    public enum Rol {
        CLIENTE,
        ENCARGADO
    }

    private final String id;
    private final String email;
    private final String nombreCompleto;
    private final Rol rol;
    private final String avatarUrl;
    private final String descripcion;
    private final Double latitud;
    private final Double longitud;
    private final String telefono;
    private final String direccion;

    public Usuario(String id, String email, String nombreCompleto, Rol rol, String avatarUrl, String descripcion, Double latitud, Double longitud, String telefono, String direccion) {
        this.id = id;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.avatarUrl = avatarUrl;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getNombreCompleto() { return nombreCompleto; }
    public Rol getRol() { return rol; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getDescripcion() { return descripcion; }
    public Double getLatitud() { return latitud; }
    public Double getLongitud() { return longitud; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
}
