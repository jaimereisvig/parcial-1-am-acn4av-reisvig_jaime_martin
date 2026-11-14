package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                startActivity(new Intent(MainActivity.this, ComidasActivity.class));
            }
        });

        btnArmarMenu.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });

        btnIrMenus.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });

        tvComoFunciona.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mostrarComoFunciona(); }
        });
    }

    private void mostrarComoFunciona() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.home_como_funciona))
                .setMessage(getString(R.string.home_como_funciona_msg))
                .setPositiveButton(getString(R.string.action_got_it), null)
                .show();
    }
}