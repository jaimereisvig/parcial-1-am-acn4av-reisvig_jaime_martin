package com.example.menucompras;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.DatosEjemplo;
import com.example.menucompras.modelo.Ingrediente;

import java.util.List;

public class IngredientesActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAJE = "com.example.menucompras.MENSAJE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientes);

        // 1) Recibir el dato de la pantalla anterior y validar que llegó
        TextView txtMensaje = findViewById(R.id.txtMensajeIngredientes);
        String mensaje = getIntent().getStringExtra(EXTRA_MENSAJE);
        if (mensaje != null) {
            txtMensaje.setText(mensaje);
        } else {
            txtMensaje.setText("(sin mensaje)");
        }

        // 2) Cargar datos de ejemplo y armar el texto de la lista
        TextView txtLista = findViewById(R.id.txtListaIngredientes);
        List<Ingrediente> ingredientes = DatosEjemplo.getIngredientes();
        StringBuilder sb = new StringBuilder();
        for (Ingrediente ing : ingredientes) {
            sb.append(ing.getNombre())
                    .append(" - ")
                    .append(ing.getCategoria())
                    .append("\n");
        }
        txtLista.setText(sb.toString());

        // 3) Volver: cierra esta pantalla y vuelve a la Principal
        Button btnVolver = findViewById(R.id.btnVolverIngredientes);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}