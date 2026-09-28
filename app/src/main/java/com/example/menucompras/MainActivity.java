package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnArmarMenu = findViewById(R.id.btn_armar_menu);
        TextView tvComoFunciona = findViewById(R.id.tv_como_funciona);
        Button btnIrIngredientes = findViewById(R.id.btn_ir_ingredientes);
        Button btnIrComidas = findViewById(R.id.btn_ir_comidas);
        Button btnIrMenus = findViewById(R.id.btn_ir_menus);

        btnIrIngredientes.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IngredientesActivity.class));
            }
        });

        btnIrComidas.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComidasActivity.class);
                startActivity(intent);
            }
        });

        btnArmarMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { proximamente(); }
        });

        btnIrMenus.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { proximamente(); }
        });

        tvComoFunciona.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mostrarComoFunciona(); }
        });
    }

    private void proximamente() {
        Toast.makeText(this, "Menús: próxima iteración", Toast.LENGTH_SHORT).show();
    }

    private void mostrarComoFunciona() {
        new AlertDialog.Builder(this)
                .setTitle("¿Cómo funciona?")
                .setMessage("1) Cargás tus ingredientes.\n" +
                        "2) Armás comidas con esos ingredientes.\n" +
                        "3) Planificás el menú de la semana.\n" +
                        "4) La app arma la lista de compras, ordenada por góndola.")
                .setPositiveButton("Entendido", null)
                .show();
    }
}