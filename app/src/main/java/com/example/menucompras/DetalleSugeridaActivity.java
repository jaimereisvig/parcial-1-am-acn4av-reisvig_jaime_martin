package com.example.menucompras;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.menucompras.modelo.ComidaSugerida;
import com.example.menucompras.modelo.IngredienteSugerido;

public class DetalleSugeridaActivity extends AppCompatActivity {

    public static final String EXTRA_COMIDA = "comida_sugerida";

    private ComidaSugerida comida;
    private Button btnImportar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_sugerida);

        comida = getIntent().getParcelableExtra(EXTRA_COMIDA);
        if (comida == null) { finish(); return; }

        ImageView iv = findViewById(R.id.iv_imagen_detalle);
        TextView tvNombre = findViewById(R.id.tv_nombre_detalle);
        TextView tvMeta = findViewById(R.id.tv_meta_detalle);
        TextView tvPrep = findViewById(R.id.tv_preparacion_detalle);
        LinearLayout llIngredientes = findViewById(R.id.ll_ingredientes_detalle);
        btnImportar = findViewById(R.id.btn_importar);

        tvNombre.setText(comida.getNombre());
        tvMeta.setText(getString(R.string.detalle_sugerida_comensales, comida.getComensales()));

        String url = comida.getImagenUrl();
        if (url != null && !url.trim().isEmpty()) {
            Glide.with(this).load(url).centerCrop()
                    .placeholder(R.drawable.bg_chip_neutral)
                    .error(R.drawable.ic_imagen_rota)
                    .into(iv);
        }

        for (IngredienteSugerido is : comida.getIngredientes()) {
            llIngredientes.addView(lineaIngrediente(is));
        }

        String prep = comida.getPreparacion();
        tvPrep.setVisibility((prep == null || prep.trim().isEmpty()) ? View.GONE : View.VISIBLE);
        tvPrep.setText(prep);

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { importar(); }
        });
    }

    private TextView lineaIngrediente(IngredienteSugerido is) {
        TextView tv = new TextView(this);
        if (is.isAGusto()) {
            tv.setText(getString(R.string.linea_sugerida_agusto, is.getNombre()));
        } else {
            tv.setText(getString(R.string.linea_sugerida_cantidad,
                    is.getNombre(), fmt(is.getCantidad()), is.getNombreUnidad()));
        }
        tv.setTextColor(ContextCompat.getColor(this, R.color.mc_ink));
        tv.setTextSize(15f);
        tv.setPadding(0, 6, 0, 6);
        return tv;
    }

    private void importar() {
        btnImportar.setEnabled(false);
        ImportadorComidas.Resultado r = ImportadorComidas.importar(this, comida);
        switch (r) {
            case OK:
                Toast.makeText(this, R.string.sugerida_importada, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case YA_EXISTE:
                btnImportar.setEnabled(true);
                Toast.makeText(this, R.string.sugerida_ya_existe, Toast.LENGTH_LONG).show();
                break;
            default:
                btnImportar.setEnabled(true);
                Toast.makeText(this, R.string.sugerida_error_importar, Toast.LENGTH_LONG).show();
        }
    }

    private String fmt(double c) {
        if (c == Math.floor(c)) return String.valueOf((long) c);
        return String.valueOf(c);
    }
}