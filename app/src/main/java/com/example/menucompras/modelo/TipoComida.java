package com.example.menucompras.modelo;

public enum TipoComida {
    ALMUERZO_CENA("Almuerzo/Cena"),
    DESAYUNO_MERIENDA("Desayuno/Merienda"),
    COLACION("Colación"),
    CUALQUIERA("Cualquier momento");

    private final String etiqueta;

    TipoComida(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}