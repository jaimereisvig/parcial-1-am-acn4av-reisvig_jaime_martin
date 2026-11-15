package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menucompras.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.FirebaseNetworkException;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etUsuario, etAlias, etCorreo, etPassword;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth = FirebaseAuth.getInstance();

        etUsuario = findViewById(R.id.et_usuario);
        etAlias = findViewById(R.id.et_alias);
        etCorreo = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);
        btnRegistrar = findViewById(R.id.btn_registrar);

        btnRegistrar.setOnClickListener(v -> registrar());
        findViewById(R.id.tv_ir_login).setOnClickListener(v -> finish()); // vuelve al Login
    }

    private void registrar() {
        String usuario = etUsuario.getText().toString().trim();
        String alias = etAlias.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(alias)
                || TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, R.string.error_correo_invalido, Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, R.string.error_pass_corta, Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrar.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        // Guarda el perfil local (usuario + alias) asociado al uid de Firebase.
                        RepositorioUsuarios.getInstancia(this)
                                .agregar(new Usuario(uid, usuario, alias, correo));
                        irAHome();
                    } else {
                        btnRegistrar.setEnabled(true);
                        Exception e = task.getException();
                        int msg;
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            msg = R.string.registro_error_correo_en_uso;
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            msg = R.string.registro_error_pass_debil;
                        } else if (e instanceof FirebaseNetworkException) {
                            msg = R.string.error_sin_internet;
                        } else {
                            msg = R.string.auth_error_generico;
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irAHome() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}