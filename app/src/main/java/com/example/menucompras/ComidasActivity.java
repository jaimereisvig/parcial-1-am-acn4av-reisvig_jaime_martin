package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;

import java.util.ArrayList;
import java.util.List;

public class ComidasActivity extends AppCompatActivity {

    private RepositorioComidas repo;
    private ComidaAdapter adapter;
    private EditText etBuscar;
    private Spinner spFiltroCategoria;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        repo = RepositorioComidas.getInstancia();

        etBuscar = findViewById(R.id.et_buscar_comida);
        spFiltroCategoria = findViewById(R.id.sp_filtro_categoria_comida);
        tvVacio = findViewById(R.id.tv_vacio_comida);

        RecyclerView rv = findViewById(R.id.rv_comidas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ComidaAdapter(new ArrayList<Comida>());
        adapter.setOnItemClickListener(new ComidaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comida comida) {
                Intent intent = new Intent(ComidasActivity.this, EditarComidaActivity.class);
                intent.putExtra(EditarComidaActivity.EXTRA_ID, comida.getId());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);

        spFiltroCategoria.setAdapter(adaptadorFiltro());
        spFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltros();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { aplicarFiltros(); }
        });

        ImageButton fab = findViewById(R.id.fab_nuevo_comida);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ComidasActivity.this, EditarComidaActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarFiltros();
    }

    private ArrayAdapter<String> adaptadorFiltro() {
        CategoriaComida[] valores = CategoriaComida.values();
        String[] opciones = new String[valores.length + 1];
        opciones[0] = getString(R.string.filtro_todas);
        for (int i = 0; i < valores.length; i++) {
            opciones[i + 1] = valores[i].getEtiqueta();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return a;
    }

    private void aplicarFiltros() {
        String texto = etBuscar.getText().toString().trim().toLowerCase();
        int pos = spFiltroCategoria.getSelectedItemPosition();
        CategoriaComida categoria = (pos > 0) ? CategoriaComida.values()[pos - 1] : null; // pos 0 = "Todas"

        List<Comida> filtrados = new ArrayList<>();
        for (Comida c : repo.getTodos()) {
            boolean coincideTexto = texto.isEmpty() || c.getNombre().toLowerCase().contains(texto);
            boolean coincideCategoria = (categoria == null) || (c.getCategoria() == categoria);
            if (coincideTexto && coincideCategoria) {
                filtrados.add(c);
            }
        }

        adapter.setItems(filtrados);
        tvVacio.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
    }
}