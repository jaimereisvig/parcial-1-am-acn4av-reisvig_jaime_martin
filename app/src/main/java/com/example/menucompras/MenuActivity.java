package com.example.menucompras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Dia;
import com.example.menucompras.modelo.Franja;
import com.example.menucompras.modelo.MenuItem;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

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

    // Dibuja las 6 franjas del dia elegido, cada una con sus comidas (o "Sin comidas").
    private void renderFranjas() {
        llFranjas.removeAllViews();
        Dia dia = diaSeleccionado();
        LayoutInflater inflater = getLayoutInflater();

        for (Franja franja : Franja.values()) {
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

            // Placeholder: la accion real de agregar llega en M.1b.
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Toast.makeText(MenuActivity.this, "Agregar comida: próximo paso", Toast.LENGTH_SHORT).show();
                }
            });

            llFranjas.addView(seccion);
        }
    }

    private void agregarFilaComida(LinearLayout contenedor, MenuItem it) {
        Comida c = repoComidas.buscarPorId(it.getComidaId());
        String nombre = (c != null) ? c.getNombre() : "(comida eliminada)";
        TextView tv = new TextView(this);
        tv.setText(nombre + "  ×" + formatFactor(it.getFactor()));
        tv.setTextColor(ContextCompat.getColor(this, R.color.mc_ink));
        tv.setTextSize(15f);
        tv.setPadding(0, 8, 0, 8);
        contenedor.addView(tv);
    }

    private String formatFactor(double f) {
        if (f == Math.floor(f)) return String.valueOf((long) f);
        return String.valueOf(f);
    }
}