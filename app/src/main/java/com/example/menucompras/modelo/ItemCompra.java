package com.example.menucompras.modelo;

public class ItemCompra {
    private int ingredienteId;
    private String nombre;
    private double cantidad;
    private String unidad;
    private Categoria categoria;

    public ItemCompra(int ingredienteId, String nombre, double cantidad, String unidad, Categoria categoria) {
        this.ingredienteId = ingredienteId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.categoria = categoria;
    }

    public int getIngredienteId() { return ingredienteId; }
    public String getNombre() { return nombre; }
    public double getCantidad() { return cantidad; }
    public String getUnidad() { return unidad; }
    public Categoria getCategoria() { return categoria; }
}