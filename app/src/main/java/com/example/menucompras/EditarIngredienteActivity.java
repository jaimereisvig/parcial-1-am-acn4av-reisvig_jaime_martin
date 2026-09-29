package com.example.menucompras;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.TipoMedida;

public class EditarIngredienteActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.menucompras.ID_INGREDIENTE";
    public static final String EXTRA_ID_CREADO = "com.example.menucompras.ID_INGREDIENTE_CREADO";
    public static final String EXTRA_EN_CONTEXTO = "com.example.menucompras.EN_CONTEXTO_COMIDA";

    private EditText etNombre, etPresentacionCantidad, etNombrePresentacion, etNombreUnidad;
    private Spinner spGondola, spTipoMedida;
    private TextView tvTitulo, tvEnUso;
    private Button btnBorrar, btnCancelar, btnGuardar;

    private RepositorioIngredientes repo;
    private int idEnEdicion = 0;
    private boolean enContexto = false;
    private boolean dialogoDuplicadoVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_ingrediente);

        repo = RepositorioIngredientes.getInstancia();

        tvTitulo = findViewById(R.id.tv_titulo_form);
        etNombre = findViewById(R.id.et_nombre);
        spGondola = findViewById(R.id.sp_gondola);
        spTipoMedida = findViewById(R.id.sp_tipo_medida);
        etPresentacionCantidad = findViewById(R.id.et_presentacion_cantidad);
        etNombrePresentacion = findViewById(R.id.et_nombre_presentacion);
        etNombreUnidad = findViewById(R.id.et_nombre_unidad);
        tvEnUso = findViewById(R.id.tv_en_uso_form);
        btnBorrar = findViewById(R.id.btn_borrar);
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnGuardar = findViewById(R.id.btn_guardar);

        spGondola.setAdapter(adaptadorEtiquetas(etiquetasGondolas()));
        spTipoMedida.setAdapter(adaptadorEtiquetas(etiquetasTiposMedida()));

        idEnEdicion = getIntent().getIntExtra(EXTRA_ID, 0);
        enContexto = getIntent().getBooleanExtra(EXTRA_EN_CONTEXTO, false);
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
            @Override public void onClick(View v) { intentarBorrar(); }
        });

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { validarNombreDuplicado(); }
        });

        etNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && enContexto && idEnEdicion == 0) {
                    String nombre = etNombre.getText().toString().trim();
                    if (!nombre.isEmpty() && repo.existeNombre(nombre, idEnEdicion)) {
                        mostrarDialogoUsarExistente(nombre);
                    }
                }
            }
        });
    }

    private String[] etiquetasGondolas() {
        Categoria[] valores = Categoria.values();
        String[] etiquetas = new String[valores.length];
        for (int i = 0; i < valores.length; i++) etiquetas[i] = valores[i].getEtiqueta();
        return etiquetas;
    }

    private String[] etiquetasTiposMedida() {
        TipoMedida[] valores = TipoMedida.values();
        String[] etiquetas = new String[valores.length];
        for (int i = 0; i < valores.length; i++) etiquetas[i] = valores[i].getEtiqueta();
        return etiquetas;
    }

    private ArrayAdapter<String> adaptadorEtiquetas(String[] etiquetas) {
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, etiquetas);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return a;
    }

    private void configurarModoAlta() {
        tvTitulo.setText(getString(R.string.ingrediente_nuevo));
        tvEnUso.setVisibility(View.GONE);
        btnBorrar.setVisibility(View.GONE);
    }

    private void configurarModoEdicion() {
        Ingrediente ing = repo.buscarPorId(idEnEdicion);
        if (ing == null) { finish(); return; }

        tvTitulo.setText(getString(R.string.ingrediente_editar));
        etNombre.setText(ing.getNombre());
        spGondola.setSelection(ing.getCategoria().ordinal());
        spTipoMedida.setSelection(ing.getTipoMedida().ordinal());
        etPresentacionCantidad.setText(String.valueOf(ing.getPresentacionCantidad()));
        etNombrePresentacion.setText(ing.getNombrePresentacion());
        etNombreUnidad.setText(ing.getNombreUnidad());

        tvEnUso.setVisibility(View.VISIBLE);
        btnBorrar.setVisibility(View.VISIBLE);
        int enUso = RepositorioComidas.getInstancia().contarComidasQueUsan(ing.getId());
        if (enUso > 0) {
            tvEnUso.setText(getString(R.string.ingrediente_en_uso_form, enUso));
            btnBorrar.setEnabled(false);
        } else {
            tvEnUso.setText(getString(R.string.ingrediente_sin_uso));
            btnBorrar.setEnabled(true);
        }
    }

    private void validarNombreDuplicado() {
        if (enContexto) {
            etNombre.setError(null);
            return;
        }
        String nombre = etNombre.getText().toString().trim();
        if (!nombre.isEmpty() && repo.existeNombre(nombre, idEnEdicion)) {
            etNombre.setError(getString(R.string.error_ingrediente_duplicado));
        } else {
            etNombre.setError(null);
        }
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError(getString(R.string.error_nombre_requerido));
            etNombre.requestFocus();
            return;
        }
        if (repo.existeNombre(nombre, idEnEdicion)) {
            if (idEnEdicion == 0 && enContexto) {
                mostrarDialogoUsarExistente(nombre);
            } else {
                etNombre.setError(getString(R.string.error_ingrediente_duplicado));
                etNombre.requestFocus();
            }
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(etPresentacionCantidad.getText().toString().trim());
        } catch (NumberFormatException e) {
            etPresentacionCantidad.setError(getString(R.string.error_cantidad_invalida));
            etPresentacionCantidad.requestFocus();
            return;
        }
        if (cantidad <= 0) {
            etPresentacionCantidad.setError(getString(R.string.error_cantidad_mayor_cero));
            etPresentacionCantidad.requestFocus();
            return;
        }

        Categoria categoria = Categoria.values()[spGondola.getSelectedItemPosition()];
        TipoMedida tipoMedida = TipoMedida.values()[spTipoMedida.getSelectedItemPosition()];
        String nombrePresentacion = etNombrePresentacion.getText().toString().trim();
        String nombreUnidad = etNombreUnidad.getText().toString().trim();

        if (idEnEdicion != 0) {
            repo.actualizar(new Ingrediente(idEnEdicion, nombre, tipoMedida, cantidad,
                    nombrePresentacion, nombreUnidad, categoria));
        } else {
            int nuevoId = repo.nuevoId();
            repo.agregar(new Ingrediente(nuevoId, nombre, tipoMedida, cantidad,
                    nombrePresentacion, nombreUnidad, categoria));
            Intent datos = new Intent();
            datos.putExtra(EXTRA_ID_CREADO, nuevoId);
            setResult(RESULT_OK, datos);
        }
        finish();
    }

    private void mostrarDialogoUsarExistente(final String nombre) {
        if (dialogoDuplicadoVisible) return;
        final Ingrediente existente = repo.buscarPorNombre(nombre);
        if (existente == null) return;
        dialogoDuplicadoVisible = true;
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_usar_existente_titulo))
                .setMessage(getString(R.string.dialog_usar_existente_msg, existente.getNombre()))
                .setPositiveButton(getString(R.string.action_use_existing), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int which) {
                        Intent datos = new Intent();
                        datos.putExtra(EXTRA_ID_CREADO, existente.getId());
                        setResult(RESULT_OK, datos);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override public void onDismiss(DialogInterface d) { dialogoDuplicadoVisible = false; }
        });
        dialog.show();
    }

    private void intentarBorrar() {
        Ingrediente ing = repo.buscarPorId(idEnEdicion);
        if (ing == null) { finish(); return; }
        int enUso = RepositorioComidas.getInstancia().contarComidasQueUsan(ing.getId());
        if (enUso > 0) {
            Toast.makeText(this, getString(R.string.toast_ingrediente_en_uso), Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ingrediente_eliminar))
                .setMessage(getString(R.string.dialog_eliminar_msg, ing.getNombre()))
                .setPositiveButton(getString(R.string.action_delete), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        repo.eliminar(idEnEdicion);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }
}