package com.example.menucompras;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.ItemCompra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListaComprasActivity extends AppCompatActivity {

    private static final String PREFS = "compras";
    private static final String KEY_COMPRADOS = "comprados";

    private LinearLayout llLista;
    private TextView tvVacia;
    private LinearLayout llBarra;
    private TextView tvProgreso;
    private Button btnDondeComprar;
    private TextView tvDondeComprarHint;

    // ingredienteId (como String) de los items marcados como comprados.
    private final Set<String> comprados = new HashSet<>();
    // Items de gondola de la lista actual (para contar progreso).
    private List<ItemCompra> itemsActuales = new ArrayList<>();

    // Pide el permiso de ubicación en runtime; si lo conceden, reintenta abrir Maps.
    private final ActivityResultLauncher<String> permisoUbicacionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    concedido -> {
                        if (concedido) {
                            abrirDondeComprar();
                        } else {
                            Toast.makeText(this, R.string.donde_comprar_permiso_denegado,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        llLista = findViewById(R.id.ll_lista);
        tvVacia = findViewById(R.id.tv_lista_vacia);
        llBarra = findViewById(R.id.ll_barra_compra);
        tvProgreso = findViewById(R.id.tv_progreso);
        Button btnReiniciar = findViewById(R.id.btn_reiniciar);
        btnReiniciar.setOnClickListener(v -> reiniciar());
        btnDondeComprar = findViewById(R.id.btn_donde_comprar);
        tvDondeComprarHint = findViewById(R.id.tv_donde_comprar_hint);
        btnDondeComprar.setOnClickListener(v -> abrirDondeComprar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        llLista.removeAllViews();
        cargarComprados();
        CalculadoraCompras.Resultado r = new CalculadoraCompras(this).calcular();
        itemsActuales = r.items;

        if (r.items.isEmpty() && r.chequear.isEmpty()) {
            tvVacia.setVisibility(View.VISIBLE);
            llBarra.setVisibility(View.GONE);
            return;
        }
        tvVacia.setVisibility(View.GONE);
        llBarra.setVisibility(r.items.isEmpty() ? View.GONE : View.VISIBLE);

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
            agregarItemCompra(inflater, contenedorActual, it);
        }

        // Seccion "Chequear" (a gusto): sin checkbox, solo recordatorio.
        if (!r.chequear.isEmpty()) {
            View sec = inflater.inflate(R.layout.item_seccion_compra, llLista, false);
            ((TextView) sec.findViewById(R.id.tv_seccion_titulo)).setText(getString(R.string.compras_chequear));
            LinearLayout cont = sec.findViewById(R.id.ll_items_seccion);
            for (String nombre : r.chequear) agregarLinea(cont, nombre);
            llLista.addView(sec);
        }

        actualizarProgreso();
        actualizarDondeComprar();
    }

    private void agregarItemCompra(LayoutInflater inflater, LinearLayout contenedor, ItemCompra it) {
        CheckBox cb = (CheckBox) inflater.inflate(R.layout.item_compra, contenedor, false);
        final String idStr = String.valueOf(it.getIngredienteId());
        cb.setText(it.getNombre() + " — " + fmt(it.getCantidad()) + " " + it.getUnidad());

        boolean comprado = comprados.contains(idStr);
        cb.setChecked(comprado);              // antes del listener: no lo dispara
        aplicarEstiloComprado(cb, comprado);

        cb.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) comprados.add(idStr); else comprados.remove(idStr);
            guardarComprados();
            aplicarEstiloComprado(cb, isChecked);
            actualizarProgreso();
        });

        contenedor.addView(cb);
    }

    private void aplicarEstiloComprado(CheckBox cb, boolean comprado) {
        if (comprado) {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cb.setTextColor(ContextCompat.getColor(this, R.color.mc_muted));
        } else {
            cb.setPaintFlags(cb.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            cb.setTextColor(ContextCompat.getColor(this, R.color.mc_ink));
        }
    }

    private void actualizarProgreso() {
        int n = itemsActuales.size();
        int x = 0;
        for (ItemCompra it : itemsActuales) {
            if (comprados.contains(String.valueOf(it.getIngredienteId()))) x++;
        }
        tvProgreso.setText(getString(R.string.compras_progreso, x, n));
    }

    private void reiniciar() {
        comprados.clear();
        guardarComprados();
        render();
    }

    private void cargarComprados() {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        comprados.clear();
        // getStringSet devuelve un set que no hay que mutar: se copia.
        comprados.addAll(p.getStringSet(KEY_COMPRADOS, new HashSet<>()));
    }

    private void guardarComprados() {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putStringSet(KEY_COMPRADOS, new HashSet<>(comprados))
                .apply();
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

    // ---- Dónde comprar (Google Maps centrado en tu ubicación) ----

    private void actualizarDondeComprar() {
        boolean mapsOk = hayAppDeMapas();
        boolean ubicacionOk = hayUbicacionActivada();
        boolean habilitado = mapsOk && ubicacionOk;

        btnDondeComprar.setEnabled(habilitado);
        btnDondeComprar.setAlpha(habilitado ? 1f : 0.4f);

        if (habilitado) {
            tvDondeComprarHint.setVisibility(View.GONE);
        } else {
            tvDondeComprarHint.setVisibility(View.VISIBLE);
            tvDondeComprarHint.setText(!mapsOk
                    ? R.string.donde_comprar_sin_maps
                    : R.string.donde_comprar_sin_ubicacion);
        }
    }

    private boolean hayAppDeMapas() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=test"));
        intent.setPackage("com.google.android.apps.maps");
        return intent.resolveActivity(getPackageManager()) != null;
    }

    private boolean hayUbicacionActivada() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null) return false;
        boolean gps = false, red = false;
        try { gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER); } catch (Exception ignored) {}
        try { red = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER); } catch (Exception ignored) {}
        return gps || red;
    }

    private void abrirDondeComprar() {
        // 1) Permiso de ubicación (runtime).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permisoUbicacionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
            return;
        }
        // 2) Última ubicación conocida.
        Location loc = ultimaUbicacion();
        if (loc == null) {
            Toast.makeText(this, R.string.donde_comprar_sin_ubicacion_actual,
                    Toast.LENGTH_SHORT).show();
            return;   // No se abre Maps sin ubicación.
        }
        // 3) Abrir Maps centrado en tu posición, buscando supermercados.
        String query = Uri.encode("supermercado");
        Uri uri = Uri.parse("geo:" + loc.getLatitude() + "," + loc.getLongitude() + "?q=" + query);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri web = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + query);
            startActivity(new Intent(Intent.ACTION_VIEW, web));
        }
    }

    private Location ultimaUbicacion() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null) return null;
        Location mejor = null;
        for (String prov : lm.getProviders(true)) {
            try {
                Location l = lm.getLastKnownLocation(prov);
                if (l != null && (mejor == null || l.getTime() > mejor.getTime())) {
                    mejor = l;
                }
            } catch (SecurityException ignored) {
                // Provider que exige FINE y solo tenemos COARSE: se saltea.
            }
        }
        return mejor;
    }
}