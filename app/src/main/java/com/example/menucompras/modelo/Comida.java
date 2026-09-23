package com.example.menucompras.modelo;

public class Comida {
    private int id;
    private String nombre;
    private int comensalesBase;
    private TipoComida tipo;
    private CategoriaComida categoria;
    private String preparacion;

    public Comida(int id, String nombre, int comensalesBase, TipoComida tipo,
                  CategoriaComida categoria, String preparacion) {
        this.id = id;
        this.nombre = nombre;
        this.comensalesBase = comensalesBase;
        this.tipo = tipo;
        this.categoria = categoria;
        this.preparacion = preparacion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getComensalesBase() { return comensalesBase; }
    public TipoComida getTipo() { return tipo; }
    public CategoriaComida getCategoria() { return categoria; }
    public String getPreparacion() { return preparacion; }
}