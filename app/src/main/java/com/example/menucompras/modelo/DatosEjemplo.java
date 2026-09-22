package com.example.menucompras.modelo;

import java.util.ArrayList;
import java.util.List;

public class DatosEjemplo {

    public static List<Ingrediente> getIngredientes() {
        List<Ingrediente> lista = new ArrayList<>();
        lista.add(new Ingrediente("Ajo", "Verdulería", "unidad"));
        lista.add(new Ingrediente("Cebolla", "Verdulería", "unidad"));
        lista.add(new Ingrediente("Harina 0000", "Almacén", "g"));
        lista.add(new Ingrediente("Leche", "Lácteos", "ml"));
        lista.add(new Ingrediente("Milanesa de carne", "Carnicería", "unidad"));
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