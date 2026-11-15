package com.example.menucompras.modelo;

import java.util.ArrayList;
import java.util.List;

// Seed de datos de ejemplo (fuente: seed_ingredientes.json + seed_comidas.json).
// Ingredientes sin usar por ninguna comida: 3 (Carne picada), 6 (Fideos tirabuzon), 8 (Pan lactal).
public class DatosEjemplo {

    public static List<Ingrediente> getIngredientes() {
        List<Ingrediente> lista = new ArrayList<>();
        lista.add(new Ingrediente(1,  "Tomate perita",    TipoMedida.CONTEO,  1,    "unidad",  "u",  Categoria.VERDULERIA));
        lista.add(new Ingrediente(2,  "Cebolla",          TipoMedida.CONTEO,  1,    "unidad",  "u",  Categoria.VERDULERIA));
        lista.add(new Ingrediente(3,  "Carne picada",     TipoMedida.MASA,    1000, "paquete", "g",  Categoria.CARNICERIA));
        lista.add(new Ingrediente(4,  "Pechuga de pollo", TipoMedida.CONTEO,  1,    "unidad",  "u",  Categoria.CARNICERIA));
        lista.add(new Ingrediente(5,  "Queso cremoso",    TipoMedida.MASA,    500,  "horma",   "g",  Categoria.FIAMBRERIA));
        lista.add(new Ingrediente(6,  "Fideos tirabuzón", TipoMedida.MASA,    500,  "paquete", "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(7,  "Leche",            TipoMedida.VOLUMEN, 1000, "sachet",  "ml", Categoria.LACTEOS));
        lista.add(new Ingrediente(8,  "Pan lactal",       TipoMedida.CONTEO,  1,    "paquete", "u",  Categoria.PANADERIA));
        lista.add(new Ingrediente(9,  "Papas",            TipoMedida.CONTEO,  1,    "unidad",  "u",  Categoria.VERDULERIA));
        lista.add(new Ingrediente(10, "Sal",              TipoMedida.MASA,    500,  "paquete", "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(11, "Pimienta",         TipoMedida.MASA,    25,   "sobre",   "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(12, "Nuez moscada",     TipoMedida.MASA,    25,   "sobre",   "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(13, "Acelga",           TipoMedida.CONTEO,  1,    "atado",   "u",  Categoria.VERDULERIA));
        lista.add(new Ingrediente(14, "Huevo",            TipoMedida.CONTEO,  12,   "docena",  "u",  Categoria.ALMACEN));
        lista.add(new Ingrediente(15, "Aceite",           TipoMedida.VOLUMEN, 1000, "botella", "ml", Categoria.ALMACEN));
        lista.add(new Ingrediente(17, "Lentejas",         TipoMedida.MASA,    500,  "paquete", "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(18, "Pan rallado",       TipoMedida.MASA,    1000, "paquete", "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(19, "Harina",           TipoMedida.MASA,    1000, "paquete", "g",  Categoria.ALMACEN));
        lista.add(new Ingrediente(20, "Dulce de leche",   TipoMedida.MASA,    500,  "pote",    "g",  Categoria.ALMACEN));
        return lista;
    }

    public static List<Comida> getComidas() {
        List<Comida> lista = new ArrayList<>();

        lista.add(new Comida(1, "Puré de papa", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.VARIOS, "",
                "https://commons.wikimedia.org/wiki/Special:FilePath/Sous_vide_mashed_potatoes.jpg",
                lineas(new LineaDeComida(9, 5, false),
                        new LineaDeComida(7, 0, true),
                        new LineaDeComida(10, 0, true),
                        new LineaDeComida(11, 0, true),
                        new LineaDeComida(12, 0, true))));

        lista.add(new Comida(2, "Tarta de Acelga", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.TARTAS, "",
                "https://commons.wikimedia.org/wiki/Special:FilePath/Torta_Pasqualina_%28Ostertorte_aus_Ligurien%29.jpg",
                lineas(new LineaDeComida(2, 2, false),
                        new LineaDeComida(13, 1, false),
                        new LineaDeComida(14, 3, false),
                        new LineaDeComida(5, 200, false),
                        new LineaDeComida(10, 0, true),
                        new LineaDeComida(11, 0, true),
                        new LineaDeComida(12, 0, true))));

        lista.add(new Comida(3, "Guiso de lentejas", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.GUISOS_ESTOFADOS, "",
                "https://commons.wikimedia.org/wiki/Special:FilePath/Guiso_de_lentejas_argentino_con_chorizo.jpg",
                lineas(new LineaDeComida(17, 250, false),
                        new LineaDeComida(2, 1, false),
                        new LineaDeComida(1, 3, false),
                        new LineaDeComida(10, 0, true),
                        new LineaDeComida(11, 0, true),
                        new LineaDeComida(12, 0, true),
                        new LineaDeComida(15, 0, true))));

        lista.add(new Comida(4, "Milanesa de pollo", 2, TipoComida.ALMUERZO_CENA,
                CategoriaComida.AVES, "",
                "https://commons.wikimedia.org/wiki/Special:FilePath/Chicken_milanesa_of_Argentina.jpg",
                lineas(new LineaDeComida(4, 1, false),
                        new LineaDeComida(18, 0, true),
                        new LineaDeComida(14, 1, false),
                        new LineaDeComida(10, 0, true),
                        new LineaDeComida(11, 0, true))));

        lista.add(new Comida(5, "Panqueques con dulce de leche", 4, TipoComida.ALMUERZO_CENA,
                CategoriaComida.POSTRES, "",
                "https://commons.wikimedia.org/wiki/Special:FilePath/Captura_de_pantalla_2022-08-25_135241.png",
                lineas(new LineaDeComida(7, 250, false),
                        new LineaDeComida(14, 2, false),
                        new LineaDeComida(19, 125, false),
                        new LineaDeComida(20, 0, true))));

        return lista;
    }

    private static List<LineaDeComida> lineas(LineaDeComida... items) {
        List<LineaDeComida> l = new ArrayList<>();
        for (LineaDeComida x : items) l.add(x);
        return l;
    }
}