package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        TextView tvSaludo = findViewById(R.id.tv_saludo);
        Button btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);

        // Saludo con el alias del perfil local (si existe en este dispositivo).
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario u = RepositorioUsuarios.getInstancia(this).buscarPorUid(user.getUid());
            if (u != null) {
                tvSaludo.setText(getString(R.string.home_saludo, u.getAlias()));
            }
        }

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

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { cerrarSesion(); }
        });
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void mostrarComoFunciona() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.home_como_funciona))
                .setMessage(getString(R.string.home_como_funciona_msg))
                .setPositiveButton(getString(R.string.action_got_it), null)
                .show();
    }
}