package com.example.menucompras;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.TipoComida;

public class EditarComidaActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.menucompras.ID_COMIDA";

    private EditText etNombre, etComensales, etPreparacion;
    private Spinner spTipo, spCategoria;
    private TextView tvTitulo;
    private Button btnBorrar, btnCancelar, btnGuardar;

    private RepositorioComidas repo;
    private int idEnEdicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_comida);

        repo = RepositorioComidas.getInstancia();

        tvTitulo = findViewById(R.id.tv_titulo_comida);
        etNombre = findViewById(R.id.et_nombre_comida);
        etComensales = findViewById(R.id.et_comensales);
        etPreparacion = findViewById(R.id.et_preparacion);
        spTipo = findViewById(R.id.sp_tipo_comida);
        spCategoria = findViewById(R.id.sp_categoria_comida);
        btnBorrar = findViewById(R.id.btn_borrar_comida);
        btnCancelar = findViewById(R.id.btn_cancelar_comida);
        btnGuardar = findViewById(R.id.btn_guardar_comida);

        spTipo.setAdapter(adaptador(etiquetasTipos()));
        spCategoria.setAdapter(adaptador(etiquetasCategorias()));

        idEnEdicion = getIntent().getIntExtra(EXTRA_ID, 0);
        if (idEnEdicion != 0) {
            configurarModoEdicion();
        } else {
            configurarModoAlta();
        }

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { guardar(); }
        });
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { confirmarBorrado(); }
        });

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { validarNombreDuplicado(); }
        });
    }

    private String[] etiquetasTipos() {
        TipoComida[] v = TipoComida.values();
        String[] e = new String[v.length];
        for (int i = 0; i < v.length; i++) e[i] = v[i].getEtiqueta();
        return e;
    }

    private String[] etiquetasCategorias() {
        CategoriaComida[] v = CategoriaComida.values();
        String[] e = new String[v.length];
        for (int i = 0; i < v.length; i++) e[i] = v[i].getEtiqueta();
        return e;
    }

    private ArrayAdapter<String> adaptador(String[] etiquetas) {
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, etiquetas);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return a;
    }

    private void configurarModoAlta() {
        tvTitulo.setText("Nueva comida");
        btnBorrar.setVisibility(View.GONE);
        spCategoria.setSelection(CategoriaComida.VARIOS.ordinal()); // default del modelo
    }

    private void configurarModoEdicion() {
        Comida c = repo.buscarPorId(idEnEdicion);
        if (c == null) { finish(); return; }
        tvTitulo.setText("Editar comida");
        etNombre.setText(c.getNombre());
        etComensales.setText(String.valueOf(c.getComensalesBase()));
        spTipo.setSelection(c.getTipo().ordinal());
        spCategoria.setSelection(c.getCategoria().ordinal());
        etPreparacion.setText(c.getPreparacion());
        btnBorrar.setVisibility(View.VISIBLE);
    }

    private void validarNombreDuplicado() {
        String nombre = etNombre.getText().toString().trim();
        if (!nombre.isEmpty() && repo.existeNombre(nombre, idEnEdicion)) {
            etNombre.setError("Ya existe una comida con ese nombre");
        } else {
            etNombre.setError(null);
        }
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("Ingresá un nombre");
            etNombre.requestFocus();
            return;
        }
        if (repo.existeNombre(nombre, idEnEdicion)) {
            etNombre.setError("Ya existe una comida con ese nombre");
            etNombre.requestFocus();
            return;
        }

        int comensales;
        try {
            comensales = Integer.parseInt(etComensales.getText().toString().trim());
        } catch (NumberFormatException e) {
            etComensales.setError("Ingresá un número válido");
            etComensales.requestFocus();
            return;
        }
        if (comensales <= 0) {
            etComensales.setError("Debe ser mayor a 0");
            etComensales.requestFocus();
            return;
        }

        TipoComida tipo = TipoComida.values()[spTipo.getSelectedItemPosition()];
        CategoriaComida categoria = CategoriaComida.values()[spCategoria.getSelectedItemPosition()];
        String preparacion = etPreparacion.getText().toString().trim();

        if (idEnEdicion != 0) {
            repo.actualizar(new Comida(idEnEdicion, nombre, comensales, tipo, categoria, preparacion));
        } else {
            repo.agregar(new Comida(repo.nuevoId(), nombre, comensales, tipo, categoria, preparacion));
        }
        finish();
    }

    private void confirmarBorrado() {
        Comida c = repo.buscarPorId(idEnEdicion);
        if (c == null) { finish(); return; }
        new AlertDialog.Builder(this)
                .setTitle("Eliminar comida")
                .setMessage("¿Eliminar \"" + c.getNombre() + "\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        repo.eliminar(idEnEdicion);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}