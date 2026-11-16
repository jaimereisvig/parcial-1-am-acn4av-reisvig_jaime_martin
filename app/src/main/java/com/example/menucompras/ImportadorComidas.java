package com.example.menucompras;

import android.content.Context;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.ComidaSugerida;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.IngredienteSugerido;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.TipoComida;
import com.example.menucompras.modelo.TipoMedida;

import java.util.ArrayList;
import java.util.List;

public class ImportadorComidas {

    public enum Resultado { OK, YA_EXISTE, ERROR }

    private ImportadorComidas() {}

    public static Resultado importar(Context ctx, ComidaSugerida s) {
        RepositorioComidas repoCom = RepositorioComidas.getInstancia(ctx);
        RepositorioIngredientes repoIng = RepositorioIngredientes.getInstancia(ctx);

        // 1) Si ya existe una comida con ese nombre, no se importa.
        if (repoCom.existeNombre(s.getNombre(), -1)) {
            return Resultado.YA_EXISTE;
        }
        try {
            // 2) Resolver cada ingrediente: reutilizar por nombre o crear.
            List<LineaDeComida> lineas = new ArrayList<>();
            for (IngredienteSugerido is : s.getIngredientes()) {
                Ingrediente local = repoIng.buscarPorNombre(is.getNombre());
                int ingId;
                if (local != null) {
                    ingId = local.getId();
                } else {
                    Ingrediente nuevo = new Ingrediente(
                            0,
                            is.getNombre(),
                            TipoMedida.valueOf(is.getTipoMedida()),
                            is.getPresentacionCantidad(),
                            is.getNombrePresentacion(),
                            is.getNombreUnidad(),
                            Categoria.valueOf(is.getCategoria()));
                    ingId = repoIng.agregar(nuevo);
                }
                lineas.add(new LineaDeComida(ingId, is.isAGusto() ? 0 : is.getCantidad(), is.isAGusto()));
            }

            // 3) Crear la comida con esas líneas.
            Comida comida = new Comida(
                    0,
                    s.getNombre(),
                    s.getComensales(),
                    TipoComida.valueOf(s.getTipo()),
                    CategoriaComida.valueOf(s.getCategoria()),
                    s.getPreparacion(),
                    s.getImagenUrl(),
                    lineas);
            repoCom.agregar(comida);
            return Resultado.OK;
        } catch (Exception e) {
            return Resultado.ERROR;
        }
    }
}