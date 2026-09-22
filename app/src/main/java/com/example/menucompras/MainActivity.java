package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIngredientes = findViewById(R.id.btnIngredientes);
        Button btnComidas = findViewById(R.id.btnComidas);

        btnIngredientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IngredientesActivity.class);
                intent.putExtra(IngredientesActivity.EXTRA_MENSAJE, "Bienvenido a Ingredientes");
                startActivity(intent);
            }
        });

        btnComidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComidasActivity.class);
                intent.putExtra(ComidasActivity.EXTRA_MENSAJE, "Bienvenido a Comidas");
                startActivity(intent);
            }
        });
    }
}