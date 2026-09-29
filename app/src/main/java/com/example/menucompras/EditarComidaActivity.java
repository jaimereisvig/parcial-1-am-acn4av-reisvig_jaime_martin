package com.example.menucompras;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.TipoComida;

import android.content.Intent;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.ArrayList;
import java.util.List;

public class EditarComidaActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.menucompras.ID_COMIDA";

    private EditText etNombre, etComensales, etPreparacion;
    private Spinner spTipo, spCategoria;
    private TextView tvTitulo;
    private Button btnBorrar, btnCancelar, btnGuardar, btnAgregarIngrediente;
    private LinearLayout llIngredientes;

    private RepositorioComidas repo;
    private RepositorioIngredientes repoIng;
    private int idEnEdicion = 0;
    private List<LineaDeComida> lineasActuales = new ArrayList<>();
    private ActivityResultLauncher<Intent> lanzadorCrearIngrediente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_comida);

        lanzadorCrearIngrediente = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        int idCreado = resultado.getData()
                                .getIntExtra(EditarIngredienteActivity.EXTRA_ID_CREADO, 0);
                        if (idCreado != 0) {
                            agregarIngredienteResuelto(idCreado);
                        }
                    }
                });

        repo = RepositorioComidas.getInstancia();
        repoIng = RepositorioIngredientes.getInstancia();

        tvTitulo = findViewById(R.id.tv_titulo_comida);
        etNombre = findViewById(R.id.et_nombre_comida);
        etComensales = findViewById(R.id.et_comensales);
        etPreparacion = findViewById(R.id.et_preparacion);
        spTipo = findViewById(R.id.sp_tipo_comida);
        spCategoria = findViewById(R.id.sp_categoria_comida);
        btnBorrar = findViewById(R.id.btn_borrar_comida);
        btnCancelar = findViewById(R.id.btn_cancelar_comida);
        btnGuardar = findViewById(R.id.btn_guardar_comida);
        btnAgregarIngrediente = findViewById(R.id.btn_agregar_ingrediente);
        llIngredientes = findViewById(R.id.ll_ingredientes);

        spTipo.setAdapter(adaptador(etiquetasTipos()));
        spCategoria.setAdapter(adaptador(etiquetasCategorias()));

        idEnEdicion = getIntent().getIntExtra(EXTRA_ID, 0);
        if (idEnEdicion != 0) {
            configurarModoEdicion();
        } else {
            configurarModoAlta();
        }
        renderLineas();

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { guardar(); }
        });
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { confirmarBorrado(); }
        });
        btnAgregarIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { mostrarDialogoElegirIngrediente(); }
        });

        Button btnCrear = findViewById(R.id.btn_crear_ingrediente);
        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarIngredienteActivity.class);
            // Sin EXTRA_ID => modo ALTA. Con EN_CONTEXTO => habilita "usar existente" ante duplicado.
            intent.putExtra(EditarIngredienteActivity.EXTRA_EN_CONTEXTO, true);
            lanzadorCrearIngrediente.launch(intent);
        });

        etNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { validarNombreDuplicado(); }
        });
    }

    // ---------- Spinners ----------
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

    // ---------- Modos ----------
    private void configurarModoAlta() {
        tvTitulo.setText(getString(R.string.comida_nueva));
        btnBorrar.setVisibility(View.GONE);
        spCategoria.setSelection(CategoriaComida.VARIOS.ordinal());
        lineasActuales = new ArrayList<>();
    }

    private void configurarModoEdicion() {
        Comida c = repo.buscarPorId(idEnEdicion);
        if (c == null) { finish(); return; }
        tvTitulo.setText(getString(R.string.comida_editar));
        etNombre.setText(c.getNombre());
        etComensales.setText(String.valueOf(c.getComensalesBase()));
        spTipo.setSelection(c.getTipo().ordinal());
        spCategoria.setSelection(c.getCategoria().ordinal());
        etPreparacion.setText(c.getPreparacion());
        btnBorrar.setVisibility(View.VISIBLE);
        lineasActuales = new ArrayList<>(c.getLineas()); // copia editable
    }

    // ---------- Ingredientes de la comida ----------
    private void renderLineas() {
        llIngredientes.removeAllViews();
        for (int i = 0; i < lineasActuales.size(); i++) {
            final int idx = i;
            LineaDeComida linea = lineasActuales.get(i);
            View fila = getLayoutInflater().inflate(R.layout.item_linea_comida, llIngredientes, false);
            TextView tv = fila.findViewById(R.id.tv_linea_ingrediente);
            TextView btnQuitar = fila.findViewById(R.id.btn_quitar_linea);
            tv.setText(textoLinea(linea));
            btnQuitar.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    lineasActuales.remove(idx);
                    renderLineas();
                }
            });
            llIngredientes.addView(fila);
        }
    }

    private String textoLinea(LineaDeComida linea) {
        Ingrediente ing = repoIng.buscarPorId(linea.getIngredienteId());
        String nombre = (ing != null) ? ing.getNombre() : getString(R.string.linea_ingrediente_eliminado);
        if (linea.isAGusto()) {
            return getString(R.string.linea_a_gusto, nombre);
        }
        String unidad = (ing != null) ? ing.getTipoMedida().getUnidadBase() : "";
        return getString(R.string.linea_cantidad, nombre, formatCantidad(linea.getCantidad()), unidad);
    }

    private String formatCantidad(double c) {
        if (c == Math.floor(c)) return String.valueOf((long) c);
        return String.valueOf(c);
    }

    private boolean yaAgregado(int ingredienteId) {
        for (LineaDeComida l : lineasActuales) {
            if (l.getIngredienteId() == ingredienteId) return true;
        }
        return false;
    }

    private void agregarIngredienteResuelto(int ingredienteId) {
        Ingrediente ing = repoIng.buscarPorId(ingredienteId);
        if (ing == null) return;
        if (yaAgregado(ingredienteId)) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_ya_en_comida_titulo))
                    .setMessage(getString(R.string.dialog_ya_en_comida_msg, ing.getNombre()))
                    .setPositiveButton(getString(R.string.action_accept), null)
                    .show();
            return;
        }
        mostrarDialogoCantidad(ing);
    }

    private void mostrarDialogoElegirIngrediente() {
        final List<Ingrediente> disponibles = new ArrayList<>();
        for (Ingrediente ing : repoIng.getTodos()) {
            if (!yaAgregado(ing.getId())) disponibles.add(ing);
        }
        if (disponibles.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_sin_ingredientes), Toast.LENGTH_SHORT).show();
            return;
        }
        String[] nombres = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) nombres[i] = disponibles.get(i).getNombre();

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_elegir_ingrediente_titulo))
                .setItems(nombres, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mostrarDialogoCantidad(disponibles.get(which));
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void mostrarDialogoCantidad(final Ingrediente ing) {
        View vista = getLayoutInflater().inflate(R.layout.dialog_cantidad, null);
        final EditText etCantidad = vista.findViewById(R.id.et_cantidad_dialog);
        final CheckBox cbAGusto = vista.findViewById(R.id.cb_a_gusto);

        cbAGusto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton b, boolean isChecked) {
                etCantidad.setEnabled(!isChecked);
                if (isChecked) etCantidad.setError(null);
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(ing.getNombre())
                .setView(vista)
                .setPositiveButton(getString(R.string.action_add), null)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface d) {
                Button positivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positivo.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        boolean aGusto = cbAGusto.isChecked();
                        double cantidad = 0;
                        if (!aGusto) {
                            try {
                                cantidad = Double.parseDouble(etCantidad.getText().toString().trim());
                            } catch (NumberFormatException e) {
                                etCantidad.setError(getString(R.string.error_cantidad_requerida));
                                return;
                            }
                            if (cantidad <= 0) {
                                etCantidad.setError(getString(R.string.error_mayor_cero));
                                return;
                            }
                        }
                        lineasActuales.add(new LineaDeComida(ing.getId(), cantidad, aGusto));
                        renderLineas();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    // ---------- Validación / guardar / borrar ----------
    private void validarNombreDuplicado() {
        String nombre = etNombre.getText().toString().trim();
        if (!nombre.isEmpty() && repo.existeNombre(nombre, idEnEdicion)) {
            etNombre.setError(getString(R.string.error_comida_duplicada));
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
            etNombre.setError(getString(R.string.error_comida_duplicada));
            etNombre.requestFocus();
            return;
        }

        int comensales;
        try {
            comensales = Integer.parseInt(etComensales.getText().toString().trim());
        } catch (NumberFormatException e) {
            etComensales.setError(getString(R.string.error_comensales_invalido));
            etComensales.requestFocus();
            return;
        }
        if (comensales <= 0) {
            etComensales.setError(getString(R.string.error_mayor_cero));
            etComensales.requestFocus();
            return;
        }

        TipoComida tipo = TipoComida.values()[spTipo.getSelectedItemPosition()];
        CategoriaComida categoria = CategoriaComida.values()[spCategoria.getSelectedItemPosition()];
        String preparacion = etPreparacion.getText().toString().trim();

        if (idEnEdicion != 0) {
            repo.actualizar(new Comida(idEnEdicion, nombre, comensales, tipo, categoria, preparacion, lineasActuales));
        } else {
            repo.agregar(new Comida(repo.nuevoId(), nombre, comensales, tipo, categoria, preparacion, lineasActuales));
        }
        finish();
    }

    private void confirmarBorrado() {
        Comida c = repo.buscarPorId(idEnEdicion);
        if (c == null) { finish(); return; }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.comida_eliminar))
                .setMessage(getString(R.string.dialog_eliminar_msg, c.getNombre()))
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