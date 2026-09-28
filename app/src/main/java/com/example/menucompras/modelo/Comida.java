package com.example.menucompras.modelo;

import java.util.ArrayList;
import java.util.List;

public class Comida {
    private int id;
    private String nombre;
    private int comensalesBase;
    private TipoComida tipo;
    private CategoriaComida categoria;
    private String preparacion;
    private List<LineaDeComida> lineas;

    // Sin líneas (arranca con lista vacía)
    public Comida(int id, String nombre, int comensalesBase, TipoComida tipo,
                  CategoriaComida categoria, String preparacion) {
        this(id, nombre, comensalesBase, tipo, categoria, preparacion, new ArrayList<LineaDeComida>());
    }

    // Con líneas
    public Comida(int id, String nombre, int comensalesBase, TipoComida tipo,
                  CategoriaComida categoria, String preparacion, List<LineaDeComida> lineas) {
        this.id = id;
        this.nombre = nombre;
        this.comensalesBase = comensalesBase;
        this.tipo = tipo;
        this.categoria = categoria;
        this.preparacion = preparacion;
        this.lineas = lineas;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getComensalesBase() { return comensalesBase; }
    public TipoComida getTipo() { return tipo; }
    public CategoriaComida getCategoria() { return categoria; }
    public String getPreparacion() { return preparacion; }
    public List<LineaDeComida> getLineas() { return lineas; }
}