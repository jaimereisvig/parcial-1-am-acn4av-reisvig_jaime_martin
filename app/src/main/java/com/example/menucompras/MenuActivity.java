package com.example.menucompras;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Dia;
import com.example.menucompras.modelo.Franja;
import com.example.menucompras.modelo.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    // Factores validos (etiqueta <-> valor). Todos exactos en double.
    private static final String[] FACTOR_ETIQUETAS = {"¼", "½", "1", "1½", "2", "3", "4"};
    private static final double[] FACTOR_VALORES = {0.25, 0.5, 1.0, 1.5, 2.0, 3.0, 4.0};

    private RepositorioMenus repoMenus;
    private RepositorioComidas repoComidas;
    private Spinner spDia;
    private LinearLayout llFranjas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        repoMenus = RepositorioMenus.getInstancia(this);
        repoComidas = RepositorioComidas.getInstancia(this);

        spDia = findViewById(R.id.sp_dia);
        llFranjas = findViewById(R.id.ll_franjas);

        spDia.setAdapter(adaptadorDias());
        spDia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renderFranjas();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderFranjas();
    }

    private ArrayAdapter<String> adaptadorDias() {
        Dia[] dias = Dia.values();
        String[] etiquetas = new String[dias.length];
        for (int i = 0; i < dias.length; i++) etiquetas[i] = dias[i].getEtiqueta();
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, etiquetas);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return a;
    }

    private Dia diaSeleccionado() {
        return Dia.values()[spDia.getSelectedItemPosition()];
    }

    private void renderFranjas() {
        llFranjas.removeAllViews();
        final Dia dia = diaSeleccionado();
        LayoutInflater inflater = getLayoutInflater();

        for (final Franja franja : Franja.values()) {
            View seccion = inflater.inflate(R.layout.item_franja_menu, llFranjas, false);
            TextView tvTitulo = seccion.findViewById(R.id.tv_franja_titulo);
            LinearLayout llComidas = seccion.findViewById(R.id.ll_comidas_franja);
            TextView tvVacia = seccion.findViewById(R.id.tv_franja_vacia);
            TextView btnAgregar = seccion.findViewById(R.id.btn_agregar_franja);

            tvTitulo.setText(franja.getEtiqueta());

            List<MenuItem> items = repoMenus.getItemsDe(dia, franja);
            if (items.isEmpty()) {
                tvVacia.setVisibility(View.VISIBLE);
            } else {
                tvVacia.setVisibility(View.GONE);
                for (MenuItem it : items) {
                    agregarFilaComida(llComidas, it);
                }
            }

            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mostrarDialogoElegirComida(dia, franja);
                }
            });

            llFranjas.addView(seccion);
        }
    }

    private void agregarFilaComida(LinearLayout contenedor, final MenuItem it) {
        Comida c = repoComidas.buscarPorId(it.getComidaId());
        String nombre = (c != null) ? c.getNombre() : "(comida eliminada)";

        View fila = getLayoutInflater().inflate(R.layout.item_menu_comida, contenedor, false);
        TextView tv = fila.findViewById(R.id.tv_menu_comida);
        TextView btnQuitar = fila.findViewById(R.id.btn_quitar_menu_comida);

        tv.setText(nombre + "  ×" + etiquetaFactor(it.getFactor()));

        // Tap en el nombre -> editar el factor.
        tv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mostrarDialogoEditarFactor(it); }
        });
        btnQuitar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                repoMenus.eliminar(it.getId());
                renderFranjas();
            }
        });

        contenedor.addView(fila);
    }

    // Catalogo de comidas, excluyendo las que ya estan en esta (dia, franja).
    private void mostrarDialogoElegirComida(final Dia dia, final Franja franja) {
        List<Integer> yaEnFranja = new ArrayList<>();
        for (MenuItem it : repoMenus.getItemsDe(dia, franja)) {
            yaEnFranja.add(it.getComidaId());
        }

        final List<Comida> disponibles = new ArrayList<>();
        for (Comida c : repoComidas.getTodos()) {
            if (!yaEnFranja.contains(c.getId())) disponibles.add(c);
        }
        if (disponibles.isEmpty()) {
            Toast.makeText(this, getString(R.string.menu_sin_comidas), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nombres = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) nombres[i] = disponibles.get(i).getNombre();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_elegir_comida))
                .setItems(nombres, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        mostrarDialogoFactor(dia, franja, disponibles.get(which));
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void mostrarDialogoFactor(final Dia dia, final Franja franja, final Comida comida) {
        new AlertDialog.Builder(this)
                .setTitle(comida.getNombre())
                .setItems(FACTOR_ETIQUETAS, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        double factor = FACTOR_VALORES[which];
                        repoMenus.agregar(new MenuItem(0, comida.getId(), dia, franja, factor));
                        renderFranjas();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void mostrarDialogoEditarFactor(final MenuItem it) {
        Comida c = repoComidas.buscarPorId(it.getComidaId());
        String titulo = (c != null) ? c.getNombre() : getString(R.string.menu_elegir_comida);
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setItems(FACTOR_ETIQUETAS, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        double factor = FACTOR_VALORES[which];
                        repoMenus.actualizar(new MenuItem(
                                it.getId(), it.getComidaId(), it.getDia(), it.getFranja(), factor));
                        renderFranjas();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private String etiquetaFactor(double f) {
        for (int i = 0; i < FACTOR_VALORES.length; i++) {
            if (FACTOR_VALORES[i] == f) return FACTOR_ETIQUETAS[i];
        }
        return String.valueOf(f); // fallback
    }
}