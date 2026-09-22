package com.example.menucompras.modelo;

public enum Categoria {
    VERDULERIA("Verdulería"),
    CARNICERIA("Carnicería"),
    FIAMBRERIA("Fiambrería"),
    LACTEOS("Lácteos"),
    PANADERIA("Panadería"),
    ALMACEN("Almacén"),
    LIMPIEZA("Limpieza"),
    OTROS("Otros");

    private final String etiqueta;

    Categoria(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
