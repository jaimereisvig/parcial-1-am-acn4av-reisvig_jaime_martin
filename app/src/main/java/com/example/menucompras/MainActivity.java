package com.example.menucompras;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnRespaldar;
    private Button btnRestaurar;
    private TextView tvUltimoRespaldo;

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
        TextView tvSalir = findViewById(R.id.tv_salir);
        btnRespaldar = findViewById(R.id.btn_respaldar);
        tvUltimoRespaldo = findViewById(R.id.tv_ultimo_respaldo);

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

        btnRespaldar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { respaldar(); }
        });

        btnRestaurar = findViewById(R.id.btn_restaurar);
        btnRestaurar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { restaurar(); }
        });

        tvSalir.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { cerrarSesion(); }
        });

        cargarUltimoRespaldo();
    }

    private void respaldar() {
        if (!hayInternet()) {
            Toast.makeText(this, R.string.respaldo_sin_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        btnRespaldar.setEnabled(false);
        Toast.makeText(this, R.string.respaldo_en_curso, Toast.LENGTH_SHORT).show();
        ServicioRespaldo.respaldar(this, new ServicioRespaldo.Callback() {
            @Override public void onExito() {
                btnRespaldar.setEnabled(true);
                Toast.makeText(MainActivity.this, R.string.respaldo_ok, Toast.LENGTH_SHORT).show();
                mostrarFecha(new Date()); // feedback inmediato con la hora local
            }
            @Override public void onError(String mensaje) {
                btnRespaldar.setEnabled(true);
                Toast.makeText(MainActivity.this, R.string.respaldo_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cargarUltimoRespaldo() {
        ServicioRespaldo.ultimaFecha(fecha -> mostrarFecha(fecha));
    }

    private void restaurar() {
        if (!hayInternet()) {
            Toast.makeText(this, R.string.respaldo_sin_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        ServicioRespaldo.ultimaFecha(fecha -> {
            if (fecha == null) {
                Toast.makeText(this, R.string.restaurar_sin_respaldo, Toast.LENGTH_SHORT).show();
                return;
            }
            String f = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(fecha);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.restaurar_confirmar_titulo)
                    .setMessage(getString(R.string.restaurar_confirmar_msg, f))
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_accept, (d, w) -> ejecutarRestauracion())
                    .show();
        });
    }

    private void ejecutarRestauracion() {
        btnRestaurar.setEnabled(false);
        Toast.makeText(this, R.string.restaurar_en_curso, Toast.LENGTH_SHORT).show();
        ServicioRespaldo.restaurar(this, new ServicioRespaldo.CallbackRestaurar() {
            @Override public void onExito() {
                Toast.makeText(MainActivity.this, R.string.restaurar_ok, Toast.LENGTH_SHORT).show();
                recreate(); // recarga saludo y fecha
            }
            @Override public void onSinRespaldo() {
                btnRestaurar.setEnabled(true);
                Toast.makeText(MainActivity.this, R.string.restaurar_sin_respaldo, Toast.LENGTH_SHORT).show();
            }
            @Override public void onError(String mensaje) {
                btnRestaurar.setEnabled(true);
                Toast.makeText(MainActivity.this, R.string.restaurar_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarFecha(Date fecha) {
        if (fecha == null) {
            tvUltimoRespaldo.setText(R.string.home_sin_respaldo);
        } else {
            String f = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(fecha);
            tvUltimoRespaldo.setText(getString(R.string.home_ultimo_respaldo, f));
        }
    }

    private boolean hayInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network red = cm.getActiveNetwork();
        if (red == null) return false;
        NetworkCapabilities cap = cm.getNetworkCapabilities(red);
        return cap != null && cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
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