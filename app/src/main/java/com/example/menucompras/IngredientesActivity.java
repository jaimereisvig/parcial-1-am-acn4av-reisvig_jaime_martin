package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.Ingrediente;

public class IngredientesActivity extends AppCompatActivity {

    private IngredienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes);

        RepositorioIngredientes repo = RepositorioIngredientes.getInstancia();

        RecyclerView rv = findViewById(R.id.rv_ingredientes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IngredienteAdapter(repo.getTodos());
        adapter.setOnItemClickListener(new IngredienteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ingrediente ingrediente) {
                Intent intent = new Intent(IngredientesActivity.this, EditarIngredienteActivity.class);
                intent.putExtra(EditarIngredienteActivity.EXTRA_ID, ingrediente.getId());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);

        ImageButton fab = findViewById(R.id.fab_nuevo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sin EXTRA_ID => modo alta
                Intent intent = new Intent(IngredientesActivity.this, EditarIngredienteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al volver del alta/edición/borrado, refrescar la lista
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}