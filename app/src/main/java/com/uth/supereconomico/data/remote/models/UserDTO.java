package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.Usuario;

public class UserDTO {
    public String id;
    public String email;
    public String telefono;
    public String direccion;
    
    @SerializedName("nombre_completo")
    public String nombreCompleto;
    
    public String rol;
    
    @SerializedName("avatar_url")
    public String avatarUrl;

    @SerializedName("fcm_token")
    public String fcmToken;
    
    public String descripcion;
    public Double latitud;
    public Double longitud;

    public Usuario toDomain() {
        Usuario.Rol domainRol = Usuario.Rol.CLIENTE;
        if ("encargado".equalsIgnoreCase(rol)) {
            domainRol = Usuario.Rol.ENCARGADO;
        } else if ("repartidor".equalsIgnoreCase(rol)) {
            domainRol = Usuario.Rol.REPARTIDOR;
        }
        return new Usuario(id, email, nombreCompleto, domainRol, avatarUrl, descripcion, latitud, longitud, telefono, direccion);
    }
}
