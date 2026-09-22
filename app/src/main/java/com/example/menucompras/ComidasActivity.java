package com.example.menucompras;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.DatosEjemplo;

import java.util.List;

public class ComidasActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAJE = "com.example.menucompras.MENSAJE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        // 1) Recibir el dato de la pantalla anterior y validar que llegó
        TextView txtMensaje = findViewById(R.id.txtMensajeComidas);
        String mensaje = getIntent().getStringExtra(EXTRA_MENSAJE);
        if (mensaje != null) {
            txtMensaje.setText(mensaje);
        } else {
            txtMensaje.setText("(sin mensaje)");
        }

        // 2) Cargar comidas de ejemplo y armar el texto
        TextView txtLista = findViewById(R.id.txtListaComidas);
        List<Comida> comidas = DatosEjemplo.getComidas();
        StringBuilder sb = new StringBuilder();
        for (Comida c : comidas) {
            sb.append(c.getNombre()).append("\n");
        }
        txtLista.setText(sb.toString());

        // 3) Volver
        Button btnVolver = findViewById(R.id.btnVolverComidas);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}