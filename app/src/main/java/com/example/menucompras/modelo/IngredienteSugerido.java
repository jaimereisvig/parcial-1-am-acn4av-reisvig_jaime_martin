package com.example.menucompras.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class IngredienteSugerido implements Parcelable {
    private final String nombre;
    private final String tipoMedida;   // name() de TipoMedida
    private final double presentacionCantidad;
    private final String nombrePresentacion;
    private final String nombreUnidad;
    private final String categoria;    // name() de Categoria (góndola)
    private final double cantidad;
    private final boolean aGusto;

    public IngredienteSugerido(String nombre, String tipoMedida, double presentacionCantidad,
                               String nombrePresentacion, String nombreUnidad, String categoria,
                               double cantidad, boolean aGusto) {
        this.nombre = nombre;
        this.tipoMedida = tipoMedida;
        this.presentacionCantidad = presentacionCantidad;
        this.nombrePresentacion = nombrePresentacion;
        this.nombreUnidad = nombreUnidad;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.aGusto = aGusto;
    }

    protected IngredienteSugerido(Parcel in) {
        nombre = in.readString();
        tipoMedida = in.readString();
        presentacionCantidad = in.readDouble();
        nombrePresentacion = in.readString();
        nombreUnidad = in.readString();
        categoria = in.readString();
        cantidad = in.readDouble();
        aGusto = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(tipoMedida);
        dest.writeDouble(presentacionCantidad);
        dest.writeString(nombrePresentacion);
        dest.writeString(nombreUnidad);
        dest.writeString(categoria);
        dest.writeDouble(cantidad);
        dest.writeByte((byte) (aGusto ? 1 : 0));
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<IngredienteSugerido> CREATOR = new Creator<IngredienteSugerido>() {
        @Override public IngredienteSugerido createFromParcel(Parcel in) { return new IngredienteSugerido(in); }
        @Override public IngredienteSugerido[] newArray(int size) { return new IngredienteSugerido[size]; }
    };

    public String getNombre() { return nombre; }
    public String getTipoMedida() { return tipoMedida; }
    public double getPresentacionCantidad() { return presentacionCantidad; }
    public String getNombrePresentacion() { return nombrePresentacion; }
    public String getNombreUnidad() { return nombreUnidad; }
    public String getCategoria() { return categoria; }
    public double getCantidad() { return cantidad; }
    public boolean isAGusto() { return aGusto; }
}