package com.example.menucompras.modelo;

public class Usuario {

    private final String uid;            // UID de Firebase
    private final String nombreUsuario;
    private final String alias;
    private final String email;

    public Usuario(String uid, String nombreUsuario, String alias, String email) {
        this.uid = uid;
        this.nombreUsuario = nombreUsuario;
        this.alias = alias;
        this.email = email;
    }

    public String getUid() { return uid; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getAlias() { return alias; }
    public String getEmail() { return email; }
}