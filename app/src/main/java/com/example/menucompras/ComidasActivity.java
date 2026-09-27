package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.Comida;

public class ComidasActivity extends AppCompatActivity {

    private ComidaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        RepositorioComidas repo = RepositorioComidas.getInstancia();

        RecyclerView rv = findViewById(R.id.rv_comidas);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ComidaAdapter(repo.getTodos());
        adapter.setOnItemClickListener(new ComidaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comida comida) {
                Intent intent = new Intent(ComidasActivity.this, EditarComidaActivity.class);
                intent.putExtra(EditarComidaActivity.EXTRA_ID, comida.getId());
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);

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
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}