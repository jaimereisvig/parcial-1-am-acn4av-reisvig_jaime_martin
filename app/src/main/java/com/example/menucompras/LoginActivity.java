package com.example.menucompras;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.FirebaseNetworkException;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etCorreo, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Auto-entrada: si ya hay sesion de Firebase, salta directo a Home.
        if (mAuth.getCurrentUser() != null) {
            irAHome();
            return;
        }

        setContentView(R.layout.activity_login);
        etCorreo = findViewById(R.id.et_correo);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.tv_ir_registro).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, R.string.error_correo_invalido, Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        irAHome();
                    } else {
                        Exception e = task.getException();
                        int msg;
                        if (e instanceof FirebaseAuthInvalidUserException
                                || e instanceof FirebaseAuthInvalidCredentialsException) {
                            msg = R.string.login_error_credenciales;
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