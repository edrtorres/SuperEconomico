package com.uth.supereconomico.data.remote.models;

import com.google.gson.annotations.SerializedName;
import com.uth.supereconomico.domain.entities.MetodoPago;

public class MetodoPagoDTO {
    public Long id;
    
    @SerializedName("perfil_id")
    public String perfilId;
    
    public String tipo;
    
    @SerializedName("numero_enmascarado")
    public String numeroEnmascarado;
    
    public String titular;
    
    @SerializedName("fecha_vencimiento")
    public String fechaVencimiento;

    public MetodoPago toDomain() {
        return new MetodoPago(id, perfilId, tipo, numeroEnmascarado, titular, fechaVencimiento);
    }
}
