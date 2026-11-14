package com.example.menucompras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.ItemCompra;

public class ListaComprasActivity extends AppCompatActivity {

    private LinearLayout llLista;
    private TextView tvVacia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        llLista = findViewById(R.id.ll_lista);
        tvVacia = findViewById(R.id.tv_lista_vacia);
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        llLista.removeAllViews();
        CalculadoraCompras.Resultado r = new CalculadoraCompras(this).calcular();

        if (r.items.isEmpty() && r.chequear.isEmpty()) {
            tvVacia.setVisibility(View.VISIBLE);
            return;
        }
        tvVacia.setVisibility(View.GONE);

        LayoutInflater inflater = getLayoutInflater();

        // Secciones por gondola (los items ya vienen ordenados por gondola).
        Categoria gondolaActual = null;
        LinearLayout contenedorActual = null;
        for (ItemCompra it : r.items) {
            if (it.getCategoria() != gondolaActual) {
                gondolaActual = it.getCategoria();
                View sec = inflater.inflate(R.layout.item_seccion_compra, llLista, false);
                ((TextView) sec.findViewById(R.id.tv_seccion_titulo)).setText(gondolaActual.getEtiqueta());
                contenedorActual = sec.findViewById(R.id.ll_items_seccion);
                llLista.addView(sec);
            }
            agregarLinea(contenedorActual,
                    it.getNombre() + " — " + fmt(it.getCantidad()) + " " + it.getUnidad());
        }

        // Seccion "Chequear" (a gusto).
        if (!r.chequear.isEmpty()) {
            View sec = inflater.inflate(R.layout.item_seccion_compra, llLista, false);
            ((TextView) sec.findViewById(R.id.tv_seccion_titulo)).setText(getString(R.string.compras_chequear));
            LinearLayout cont = sec.findViewById(R.id.ll_items_seccion);
            for (String nombre : r.chequear) agregarLinea(cont, nombre);
            llLista.addView(sec);
        }
    }

    private void agregarLinea(LinearLayout contenedor, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(ContextCompat.getColor(this, R.color.mc_ink));
        tv.setTextSize(15f);
        tv.setPadding(0, 6, 0, 6);
        contenedor.addView(tv);
    }

    private String fmt(double c) {
        if (c == Math.floor(c)) return String.valueOf((long) c);
        return String.valueOf(c);
    }
}