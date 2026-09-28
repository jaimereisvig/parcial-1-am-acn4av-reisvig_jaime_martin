package com.example.menucompras.modelo;

public class LineaDeComida {
    private int ingredienteId;
    private double cantidad;
    private boolean aGusto;

    public LineaDeComida(int ingredienteId, double cantidad, boolean aGusto) {
        this.ingredienteId = ingredienteId;
        this.cantidad = cantidad;
        this.aGusto = aGusto;
    }

    public int getIngredienteId() { return ingredienteId; }
    public double getCantidad() { return cantidad; }
    public boolean isAGusto() { return aGusto; }
}