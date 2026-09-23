package com.example.menucompras;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.DatosEjemplo;

import java.util.ArrayList;
import java.util.List;

public class RepositorioComidas {

    private static RepositorioComidas instancia;

    private final List<Comida> comidas;
    private int siguienteId;

    private RepositorioComidas() {
        comidas = new ArrayList<>(DatosEjemplo.getComidas());
        int max = 0;
        for (Comida c : comidas) {
            if (c.getId() > max) max = c.getId();
        }
        siguienteId = max + 1;
    }

    public static RepositorioComidas getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioComidas();
        }
        return instancia;
    }

    public List<Comida> getTodos() {
        return comidas;
    }

    public Comida buscarPorId(int id) {
        for (Comida c : comidas) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public boolean existeNombre(String nombre, int excluyendoId) {
        String buscado = nombre.trim();
        for (Comida c : comidas) {
            if (c.getId() != excluyendoId && c.getNombre().equalsIgnoreCase(buscado)) {
                return true;
            }
        }
        return false;
    }

    public int nuevoId() {
        return siguienteId++;
    }

    public void agregar(Comida c) {
        comidas.add(c);
    }

    public void actualizar(Comida c) {
        for (int i = 0; i < comidas.size(); i++) {
            if (comidas.get(i).getId() == c.getId()) {
                comidas.set(i, c);
                return;
            }
        }
    }

    public void eliminar(int id) {
        for (int i = 0; i < comidas.size(); i++) {
            if (comidas.get(i).getId() == id) {
                comidas.remove(i);
                return;
            }
        }
    }
}