package com.example.menucompras.modelo;

public enum TipoMedida {
    MASA("Masa", "g"),
    VOLUMEN("Volumen", "ml"),
    CONTEO("Conteo", "u");

    private final String etiqueta;
    private final String unidadBase;

    TipoMedida(String etiqueta, String unidadBase) {
        this.etiqueta = etiqueta;
        this.unidadBase = unidadBase;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getUnidadBase() { return unidadBase; }
}