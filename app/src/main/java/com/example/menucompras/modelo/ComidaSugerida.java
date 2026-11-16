package com.example.menucompras.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ComidaSugerida implements Parcelable {
    private final String nombre;
    private final int comensales;
    private final String tipo;        // name() de TipoComida
    private final String categoria;   // name() de CategoriaComida
    private final String preparacion;
    private final String imagenUrl;
    private final List<IngredienteSugerido> ingredientes;

    public ComidaSugerida(String nombre, int comensales, String tipo, String categoria,
                          String preparacion, String imagenUrl, List<IngredienteSugerido> ingredientes) {
        this.nombre = nombre;
        this.comensales = comensales;
        this.tipo = tipo;
        this.categoria = categoria;
        this.preparacion = preparacion;
        this.imagenUrl = imagenUrl;
        this.ingredientes = ingredientes;
    }

    protected ComidaSugerida(Parcel in) {
        nombre = in.readString();
        comensales = in.readInt();
        tipo = in.readString();
        categoria = in.readString();
        preparacion = in.readString();
        imagenUrl = in.readString();
        ingredientes = in.createTypedArrayList(IngredienteSugerido.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeInt(comensales);
        dest.writeString(tipo);
        dest.writeString(categoria);
        dest.writeString(preparacion);
        dest.writeString(imagenUrl);
        dest.writeTypedList(ingredientes);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<ComidaSugerida> CREATOR = new Creator<ComidaSugerida>() {
        @Override public ComidaSugerida createFromParcel(Parcel in) { return new ComidaSugerida(in); }
        @Override public ComidaSugerida[] newArray(int size) { return new ComidaSugerida[size]; }
    };

    public String getNombre() { return nombre; }
    public int getComensales() { return comensales; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public String getPreparacion() { return preparacion; }
    public String getImagenUrl() { return imagenUrl; }
    public List<IngredienteSugerido> getIngredientes() {
        return ingredientes != null ? ingredientes : new ArrayList<>();
    }
}