package com.example.menucompras;

import android.content.Context;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.ItemCompra;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.MenuItem;
import com.example.menucompras.modelo.TipoMedida;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CalculadoraCompras {

    // Resultado: items a comprar (ordenados por gondola) + nombres a "chequear" (a gusto).
    public static class Resultado {
        public final List<ItemCompra> items;
        public final List<String> chequear;
        public Resultado(List<ItemCompra> items, List<String> chequear) {
            this.items = items;
            this.chequear = chequear;
        }
    }

    private final RepositorioMenus repoMenus;
    private final RepositorioComidas repoComidas;
    private final RepositorioIngredientes repoIngredientes;

    public CalculadoraCompras(Context context) {
        repoMenus = RepositorioMenus.getInstancia(context);
        repoComidas = RepositorioComidas.getInstancia(context);
        repoIngredientes = RepositorioIngredientes.getInstancia(context);
    }

    public Resultado calcular() {
        // Acumular cantidades (ingredienteId -> suma de cantidad*factor) y marcar los "a gusto".
        Map<Integer, Double> acumulado = new LinkedHashMap<>();
        Set<Integer> aGusto = new TreeSet<>();

        for (MenuItem mi : repoMenus.getItems()) {
            Comida comida = repoComidas.buscarPorId(mi.getComidaId());
            if (comida == null) continue;
            for (LineaDeComida l : comida.getLineas()) {
                if (l.isAGusto()) {
                    aGusto.add(l.getIngredienteId());
                } else {
                    double prev = acumulado.containsKey(l.getIngredienteId())
                            ? acumulado.get(l.getIngredienteId()) : 0.0;
                    acumulado.put(l.getIngredienteId(), prev + l.getCantidad() * mi.getFactor());
                }
            }
        }

        // Items (gondola). CONTEO redondea hacia arriba.
        List<ItemCompra> items = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : acumulado.entrySet()) {
            Ingrediente ing = repoIngredientes.buscarPorId(e.getKey());
            if (ing == null) continue;
            TipoMedida tm = ing.getTipoMedida();
            double cantidad = (tm == TipoMedida.CONTEO) ? Math.ceil(e.getValue()) : e.getValue();
            items.add(new ItemCompra(ing.getId(), ing.getNombre(), cantidad,
                    tm.getUnidadBase(), ing.getCategoria()));
        }
        // Ordenar por gondola (ordinal del enum) y luego por nombre.
        Collections.sort(items, new Comparator<ItemCompra>() {
            @Override public int compare(ItemCompra a, ItemCompra b) {
                int g = a.getCategoria().ordinal() - b.getCategoria().ordinal();
                if (g != 0) return g;
                return a.getNombre().compareToIgnoreCase(b.getNombre());
            }
        });

        // "Chequear": nombres de los ingredientes a gusto, ordenados.
        List<String> chequear = new ArrayList<>();
        for (Integer id : aGusto) {
            Ingrediente ing = repoIngredientes.buscarPorId(id);
            if (ing != null) chequear.add(ing.getNombre());
        }
        Collections.sort(chequear, String.CASE_INSENSITIVE_ORDER);

        return new Resultado(items, chequear);
    }
}