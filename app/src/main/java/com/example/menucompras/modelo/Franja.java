package com.example.menucompras.modelo;

public enum Franja {
    DESAYUNO("Desayuno"),
    COLACION_MANANA("Colación mañana"),
    ALMUERZO("Almuerzo"),
    MERIENDA("Merienda"),
    COLACION_TARDE("Colación tarde"),
    CENA("Cena");

    private final String etiqueta;

    Franja(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}