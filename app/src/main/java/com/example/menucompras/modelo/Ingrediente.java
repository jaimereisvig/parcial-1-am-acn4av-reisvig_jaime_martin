package com.example.menucompras.modelo;

public class Ingrediente {
    private int id;
    private String nombre;
    private TipoMedida tipoMedida;
    private double presentacionCantidad;
    private String nombrePresentacion;
    private String nombreUnidad;
    private Categoria categoria;

    public Ingrediente(int id, String nombre, TipoMedida tipoMedida,
                       double presentacionCantidad, String nombrePresentacion,
                       String nombreUnidad, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.tipoMedida = tipoMedida;
        this.presentacionCantidad = presentacionCantidad;
        this.nombrePresentacion = nombrePresentacion;
        this.nombreUnidad = nombreUnidad;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public TipoMedida getTipoMedida() { return tipoMedida; }
    public double getPresentacionCantidad() { return presentacionCantidad; }
    public String getNombrePresentacion() { return nombrePresentacion; }
    public String getNombreUnidad() { return nombreUnidad; }
    public Categoria getCategoria() { return categoria; }
}