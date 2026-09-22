package com.example.menucompras;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.DatosEjemplo;
import com.example.menucompras.modelo.Ingrediente;

import java.util.List;

public class IngredientesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes);

        List<Ingrediente> ingredientes = DatosEjemplo.getIngredientes();

        RecyclerView rv = findViewById(R.id.rv_ingredientes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new IngredienteAdapter(ingredientes));

        // FAB visible pero sin acción en esta iteración (se conecta al Alta en la Iteración 2)
    }
}