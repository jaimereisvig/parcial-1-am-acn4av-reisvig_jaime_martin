package com.example.menucompras;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.ComidaSugerida;

import java.util.ArrayList;
import java.util.List;

public class SugeridasActivity extends AppCompatActivity {

    private SugeridaAdapter adapter;
    private ProgressBar progreso;
    private TextView tvMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugeridas);

        progreso = findViewById(R.id.pb_sugeridas);
        tvMensaje = findViewById(R.id.tv_mensaje_sugeridas);

        RecyclerView rv = findViewById(R.id.rv_sugeridas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SugeridaAdapter(new ArrayList<ComidaSugerida>());
        adapter.setOnItemClickListener(new SugeridaAdapter.OnItemClickListener() {
            @Override public void onItemClick(ComidaSugerida comida) {
                Intent i = new Intent(SugeridasActivity.this, DetalleSugeridaActivity.class);
                i.putExtra(DetalleSugeridaActivity.EXTRA_COMIDA, comida);
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);

        cargar();
    }

    private void cargar() {
        progreso.setVisibility(View.VISIBLE);
        tvMensaje.setVisibility(View.GONE);
        ServicioSugeridas.descargar(this, new ServicioSugeridas.Callback() {
            @Override public void onExito(List<ComidaSugerida> comidas) {
                progreso.setVisibility(View.GONE);
                adapter.setItems(comidas);
                if (comidas.isEmpty()) {
                    tvMensaje.setText(R.string.sugeridas_vacias);
                    tvMensaje.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onError(String mensaje) {
                progreso.setVisibility(View.GONE);
                tvMensaje.setText(R.string.sugeridas_error);
                tvMensaje.setVisibility(View.VISIBLE);
            }
        });
    }
}