package com.example.menucompras.modelo;

public enum Dia {
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado"),
    DOMINGO("Domingo");

    private final String etiqueta;

    Dia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}