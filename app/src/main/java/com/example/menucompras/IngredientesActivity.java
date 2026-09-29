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

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.Ingrediente;

import java.util.ArrayList;
import java.util.List;

public class IngredientesActivity extends AppCompatActivity {

    private RepositorioIngredientes repo;
    private IngredienteAdapter adapter;
    private EditText etBuscar;
    private Spinner spFiltroGondola;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes);

        repo = RepositorioIngredientes.getInstancia();

        etBuscar = findViewById(R.id.et_buscar);
        spFiltroGondola = findViewById(R.id.sp_filtro_gondola);
        tvVacio = findViewById(R.id.tv_vacio);

        RecyclerView rv = findViewById(R.id.rv_ingredientes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredienteAdapter(new ArrayList<Ingrediente>());
        adapter.setOnItemClickListener(new IngredienteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingrediente ingrediente) {
                Intent intent = new Intent(IngredientesActivity.this, EditarIngredienteActivity.class);
                intent.putExtra(EditarIngredienteActivity.EXTRA_ID, ingrediente.getId());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);

        spFiltroGondola.setAdapter(adaptadorFiltro());
        spFiltroGondola.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        ImageButton fab = findViewById(R.id.fab_nuevo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IngredientesActivity.this, EditarIngredienteActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarFiltros();
    }

    private ArrayAdapter<String> adaptadorFiltro() {
        Categoria[] valores = Categoria.values();
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
        int pos = spFiltroGondola.getSelectedItemPosition();
        Categoria gondola = (pos > 0) ? Categoria.values()[pos - 1] : null; // pos 0 = "Todas"

        List<Ingrediente> filtrados = new ArrayList<>();
        for (Ingrediente ing : repo.getTodos()) {
            boolean coincideTexto = texto.isEmpty() || ing.getNombre().toLowerCase().contains(texto);
            boolean coincideGondola = (gondola == null) || (ing.getCategoria() == gondola);
            if (coincideTexto && coincideGondola) {
                filtrados.add(ing);
            }
        }

        adapter.setItems(filtrados);
        tvVacio.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
    }
}