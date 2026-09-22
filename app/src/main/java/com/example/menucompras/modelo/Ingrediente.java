package com.example.menucompras.modelo;
public class Ingrediente {
    private String nombre;
    private String categoria;
    private String unidad;

    public Ingrediente(String nombre, String categoria, String unidad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.unidad = unidad;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUnidad() { return unidad; }
}
