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
        lista.add(new Comida(1, "Milanesas con puré", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.CARNES_ROJAS,
                "Freír o al horno las milanesas; hacer puré con papas, leche y manteca.",
                lineas(new LineaDeComida(3, 500, false), new LineaDeComida(7, 200, false))));
        lista.add(new Comida(2, "Tarta de verdura", 6, TipoComida.ALMUERZO_CENA,
                CategoriaComida.TARTAS,
                "Rehogar la acelga con cebolla, mezclar con huevo y queso, hornear en masa.",
                lineas(new LineaDeComida(2, 2, false), new LineaDeComida(5, 200, false), new LineaDeComida(7, 100, false))));
        lista.add(new Comida(3, "Guiso de lentejas", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.GUISOS_ESTOFADOS,
                "Hervir las lentejas con verduras, chorizo y panceta.",
                lineas(new LineaDeComida(2, 1, false), new LineaDeComida(1, 3, false))));
        lista.add(new Comida(4, "Ensalada César", 2, TipoComida.ALMUERZO_CENA,
                CategoriaComida.ENSALADAS,
                "Lechuga, croutons, pollo y aderezo césar.",
                lineas(new LineaDeComida(4, 300, false), new LineaDeComida(8, 1, false))));
        lista.add(new Comida(5, "Panqueques con dulce de leche", 4, TipoComida.DESAYUNO_MERIENDA,
                CategoriaComida.POSTRES, "",
                lineas(new LineaDeComida(7, 500, false))));
        return lista;
    }

    private static List<LineaDeComida> lineas(LineaDeComida... items) {
        List<LineaDeComida> l = new ArrayList<>();
        for (LineaDeComida x : items) l.add(x);
        return l;
    }
}