package com.example.menucompras.modelo;

import java.util.ArrayList;
import java.util.List;

public class DatosEjemplo {

    public static List<Ingrediente> getIngredientes() {
        List<Ingrediente> lista = new ArrayList<>();
        lista.add(new Ingrediente(1, "Tomate perita", TipoMedida.CONTEO, 1, "unidad", "u", Categoria.VERDULERIA, 4));
        lista.add(new Ingrediente(2, "Cebolla", TipoMedida.CONTEO, 1, "unidad", "u", Categoria.VERDULERIA, 7));
        lista.add(new Ingrediente(3, "Carne picada", TipoMedida.MASA, 1000, "paquete", "g", Categoria.CARNICERIA, 3));
        lista.add(new Ingrediente(4, "Pechuga de pollo", TipoMedida.MASA, 1000, "bandeja", "g", Categoria.CARNICERIA, 5));
        lista.add(new Ingrediente(5, "Queso cremoso", TipoMedida.MASA, 500, "horma", "g", Categoria.FIAMBRERIA, 2));
        lista.add(new Ingrediente(6, "Fideos tirabuzón", TipoMedida.MASA, 500, "paquete", "g", Categoria.ALMACEN, 6));
        lista.add(new Ingrediente(7, "Leche", TipoMedida.VOLUMEN, 1000, "sachet", "ml", Categoria.LACTEOS, 0));
        lista.add(new Ingrediente(8, "Pan lactal", TipoMedida.CONTEO, 1, "paquete", "u", Categoria.PANADERIA, 0));
        return lista;
    }

    public static List<Comida> getComidas() {
        List<Comida> lista = new ArrayList<>();
        lista.add(new Comida("Milanesas con puré"));
        lista.add(new Comida("Tarta de verdura"));
        lista.add(new Comida("Guiso de lentejas"));
        return lista;
    }
}