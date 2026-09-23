package com.example.menucompras.modelo;

public enum CategoriaComida {
    ARROCES("Arroces"),
    AVES("Aves"),
    CARNES_ROJAS("Carnes Rojas"),
    EMPANADAS("Empanadas"),
    ENSALADAS("Ensaladas"),
    GUISOS_ESTOFADOS("Guisos y Estofados"),
    PESCADOS_MARISCOS("Pescados y Mariscos"),
    PASTAS("Pastas"),
    POSTRES("Postres"),
    SANDWICHES("Sándwiches"),
    SOPAS("Sopas"),
    TARTAS("Tartas"),
    VARIOS("Varios");

    private final String etiqueta;

    CategoriaComida(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}