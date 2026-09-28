package com.example.menucompras;

import com.example.menucompras.modelo.DatosEjemplo;
import com.example.menucompras.modelo.Ingrediente;

import java.util.ArrayList;
import java.util.List;

public class RepositorioIngredientes {

    private static RepositorioIngredientes instancia;

    private final List<Ingrediente> ingredientes;
    private int siguienteId;

    private RepositorioIngredientes() {
        ingredientes = new ArrayList<>(DatosEjemplo.getIngredientes());
        int max = 0;
        for (Ingrediente ing : ingredientes) {
            if (ing.getId() > max) max = ing.getId();
        }
        siguienteId = max + 1;
    }

    public static RepositorioIngredientes getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioIngredientes();
        }
        return instancia;
    }

    public List<Ingrediente> getTodos() {
        return ingredientes;
    }

    public Ingrediente buscarPorId(int id) {
        for (Ingrediente ing : ingredientes) {
            if (ing.getId() == id) return ing;
        }
        return null;
    }

    public boolean existeNombre(String nombre, int excluyendoId) {
        String buscado = nombre.trim();
        for (Ingrediente ing : ingredientes) {
            if (ing.getId() != excluyendoId && ing.getNombre().equalsIgnoreCase(buscado)) {
                return true;
            }
        }
        return false;
    }

    public Ingrediente buscarPorNombre(String nombre) {
        String buscado = nombre.trim();
        for (Ingrediente ing : ingredientes) {
            if (ing.getNombre().equalsIgnoreCase(buscado)) return ing;
        }
        return null;
    }

    public int nuevoId() {
        return siguienteId++;
    }

    public void agregar(Ingrediente ing) {
        ingredientes.add(ing);
    }

    public void actualizar(Ingrediente ing) {
        for (int i = 0; i < ingredientes.size(); i++) {
            if (ingredientes.get(i).getId() == ing.getId()) {
                ingredientes.set(i, ing);
                return;
            }
        }
    }

    public void eliminar(int id) {
        for (int i = 0; i < ingredientes.size(); i++) {
            if (ingredientes.get(i).getId() == id) {
                ingredientes.remove(i);
                return;
            }
        }
    }
}