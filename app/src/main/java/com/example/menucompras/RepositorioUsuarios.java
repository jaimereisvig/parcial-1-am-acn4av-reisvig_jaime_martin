package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.menucompras.modelo.Usuario;

public class RepositorioUsuarios {

    private static RepositorioUsuarios instancia;
    private final MenuComprasDBHelper dbHelper;

    private RepositorioUsuarios(Context context) {
        dbHelper = MenuComprasDBHelper.getInstancia(context);
    }

    public static synchronized RepositorioUsuarios getInstancia(Context context) {
        if (instancia == null) {
            instancia = new RepositorioUsuarios(context);
        }
        return instancia;
    }

    // Guarda el perfil local de un usuario recien registrado en Firebase.
    public void agregar(Usuario u) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MenuComprasDBHelper.USER_UID, u.getUid());
        cv.put(MenuComprasDBHelper.USER_NOMBRE, u.getNombreUsuario());
        cv.put(MenuComprasDBHelper.USER_ALIAS, u.getAlias());
        cv.put(MenuComprasDBHelper.USER_EMAIL, u.getEmail());
        db.insertOrThrow(MenuComprasDBHelper.T_USER, null, cv);
    }

    public boolean existe(String uid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_USER,
                new String[]{MenuComprasDBHelper.USER_UID},
                MenuComprasDBHelper.USER_UID + " = ?",
                new String[]{uid}, null, null, null);
        boolean hay = c.moveToFirst();
        c.close();
        return hay;
    }

    public Usuario buscarPorUid(String uid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_USER, null,
                MenuComprasDBHelper.USER_UID + " = ?",
                new String[]{uid}, null, null, null);
        Usuario u = null;
        if (c.moveToFirst()) {
            u = new Usuario(
                    c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.USER_UID)),
                    c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.USER_NOMBRE)),
                    c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.USER_ALIAS)),
                    c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.USER_EMAIL)));
        }
        c.close();
        return u;
    }
}