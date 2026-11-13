package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.TipoComida;

import java.util.ArrayList;
import java.util.List;

public class RepositorioComidas {

    private static RepositorioComidas instancia;
    private final MenuComprasDBHelper dbHelper;

    private RepositorioComidas(Context context) {
        dbHelper = MenuComprasDBHelper.getInstancia(context);
    }

    public static synchronized RepositorioComidas getInstancia(Context context) {
        if (instancia == null) {
            instancia = new RepositorioComidas(context);
        }
        return instancia;
    }

    // Arma una Comida (con sus lineas) desde la fila actual del cursor.
    private Comida desde(SQLiteDatabase db, Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_ID));
        String nombre = c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_NOMBRE));
        int comensales = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_COMENSALES));
        TipoComida tipo = TipoComida.valueOf(
                c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_TIPO)));
        CategoriaComida categoria = CategoriaComida.valueOf(
                c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_CATEGORIA)));
        String preparacion = c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.COM_PREPARACION));
        List<LineaDeComida> lineas = lineasDe(db, id);
        return new Comida(id, nombre, comensales, tipo, categoria, preparacion, lineas);
    }

    // Carga las lineas de una comida. En la BD "a gusto" => cantidad NULL;
    // el modelo usa cantidad 0 cuando aGusto es true (la ignora).
    private List<LineaDeComida> lineasDe(SQLiteDatabase db, int comidaId) {
        List<LineaDeComida> lineas = new ArrayList<>();
        Cursor c = db.query(MenuComprasDBHelper.T_CI, null,
                MenuComprasDBHelper.CI_COMIDA_ID + " = ?", new String[]{String.valueOf(comidaId)},
                null, null, MenuComprasDBHelper.CI_ID + " ASC");
        try {
            while (c.moveToNext()) {
                int ingId = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.CI_INGREDIENTE_ID));
                boolean aGusto = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.CI_A_GUSTO)) == 1;
                double cantidad = aGusto ? 0
                        : c.getDouble(c.getColumnIndexOrThrow(MenuComprasDBHelper.CI_CANTIDAD));
                lineas.add(new LineaDeComida(ingId, cantidad, aGusto));
            }
        } finally {
            c.close();
        }
        return lineas;
    }

    // Inserta las lineas de una comida (BD: aGusto => cantidad NULL).
    private void insertarLineas(SQLiteDatabase db, int comidaId, List<LineaDeComida> lineas) {
        for (LineaDeComida l : lineas) {
            ContentValues cv = new ContentValues();
            cv.put(MenuComprasDBHelper.CI_COMIDA_ID, comidaId);
            cv.put(MenuComprasDBHelper.CI_INGREDIENTE_ID, l.getIngredienteId());
            if (l.isAGusto()) {
                cv.put(MenuComprasDBHelper.CI_A_GUSTO, 1);
                cv.putNull(MenuComprasDBHelper.CI_CANTIDAD);
            } else {
                cv.put(MenuComprasDBHelper.CI_A_GUSTO, 0);
                cv.put(MenuComprasDBHelper.CI_CANTIDAD, l.getCantidad());
            }
            db.insertOrThrow(MenuComprasDBHelper.T_CI, null, cv);
        }
    }

    private ContentValues valores(Comida c) {
        ContentValues cv = new ContentValues();
        cv.put(MenuComprasDBHelper.COM_NOMBRE, c.getNombre());
        cv.put(MenuComprasDBHelper.COM_COMENSALES, c.getComensalesBase());
        cv.put(MenuComprasDBHelper.COM_TIPO, c.getTipo().name());
        cv.put(MenuComprasDBHelper.COM_PREPARACION, c.getPreparacion());
        cv.put(MenuComprasDBHelper.COM_CATEGORIA, c.getCategoria().name());
        return cv;
    }

    public List<Comida> getTodos() {
        List<Comida> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_COM, null, null, null, null, null,
                MenuComprasDBHelper.COM_ID + " ASC");
        try {
            while (c.moveToNext()) lista.add(desde(db, c));
        } finally {
            c.close();
        }
        return lista;
    }

    public Comida buscarPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_COM, null,
                MenuComprasDBHelper.COM_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (c.moveToNext()) return desde(db, c);
            return null;
        } finally {
            c.close();
        }
    }

    public boolean existeNombre(String nombre, int excluyendoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_COM, new String[]{MenuComprasDBHelper.COM_ID},
                MenuComprasDBHelper.COM_NOMBRE + " = ? COLLATE NOCASE AND "
                        + MenuComprasDBHelper.COM_ID + " <> ?",
                new String[]{nombre.trim(), String.valueOf(excluyendoId)},
                null, null, null);
        try {
            return c.getCount() > 0;
        } finally {
            c.close();
        }
    }

    // Cuantas comidas usan un ingrediente (cada comida cuenta una vez).
    public int contarComidasQueUsan(int ingredienteId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(DISTINCT " + MenuComprasDBHelper.CI_COMIDA_ID + ") FROM "
                        + MenuComprasDBHelper.T_CI + " WHERE "
                        + MenuComprasDBHelper.CI_INGREDIENTE_ID + " = ?",
                new String[]{String.valueOf(ingredienteId)});
        try {
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            c.close();
        }
    }

    // Inserta la comida y sus lineas (transaccion). Devuelve el id asignado por la BD.
    public int agregar(Comida c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            long comidaId = db.insertOrThrow(MenuComprasDBHelper.T_COM, null, valores(c));
            insertarLineas(db, (int) comidaId, c.getLineas());
            db.setTransactionSuccessful();
            return (int) comidaId;
        } finally {
            db.endTransaction();
        }
    }

    // Actualiza la comida y reemplaza todas sus lineas (transaccion).
    public void actualizar(Comida c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.update(MenuComprasDBHelper.T_COM, valores(c),
                    MenuComprasDBHelper.COM_ID + " = ?", new String[]{String.valueOf(c.getId())});
            db.delete(MenuComprasDBHelper.T_CI,
                    MenuComprasDBHelper.CI_COMIDA_ID + " = ?", new String[]{String.valueOf(c.getId())});
            insertarLineas(db, c.getId(), c.getLineas());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MenuComprasDBHelper.T_COM, MenuComprasDBHelper.COM_ID + " = ?",
                new String[]{String.valueOf(id)});
        // Las lineas de la comida caen por ON DELETE CASCADE.
    }
}