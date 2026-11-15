package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.TipoMedida;

import java.util.ArrayList;
import java.util.List;

public class RepositorioIngredientes {

    private static RepositorioIngredientes instancia;
    private final MenuComprasDBHelper dbHelper;

    private RepositorioIngredientes(Context context) {
        dbHelper = MenuComprasDBHelper.getInstancia(context);
    }

    public static synchronized RepositorioIngredientes getInstancia(Context context) {
        if (instancia == null) {
            instancia = new RepositorioIngredientes(context);
        }
        return instancia;
    }

    private Ingrediente desde(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_ID));
        String nombre = c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_NOMBRE));
        TipoMedida tipoMedida = TipoMedida.valueOf(
                c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_TIPO_MEDIDA)));
        double presCant = c.getDouble(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_PRES_CANT));
        String nombrePres = c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_NOMBRE_PRES));
        String nombreUnidad = c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_NOMBRE_UNIDAD));
        Categoria categoria = Categoria.valueOf(
                c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.ING_CATEGORIA)));
        return new Ingrediente(id, nombre, tipoMedida, presCant, nombrePres, nombreUnidad, categoria);
    }

    // Incluye el usuario_uid del usuario logueado.
    private ContentValues valores(Ingrediente ing) {
        ContentValues cv = new ContentValues();
        cv.put(MenuComprasDBHelper.ING_USUARIO, SesionActual.uid());
        cv.put(MenuComprasDBHelper.ING_NOMBRE, ing.getNombre());
        cv.put(MenuComprasDBHelper.ING_TIPO_MEDIDA, ing.getTipoMedida().name());
        cv.put(MenuComprasDBHelper.ING_PRES_CANT, ing.getPresentacionCantidad());
        cv.put(MenuComprasDBHelper.ING_NOMBRE_PRES, ing.getNombrePresentacion());
        cv.put(MenuComprasDBHelper.ING_NOMBRE_UNIDAD, ing.getNombreUnidad());
        cv.put(MenuComprasDBHelper.ING_CATEGORIA, ing.getCategoria().name());
        return cv;
    }

    public List<Ingrediente> getTodos() {
        List<Ingrediente> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_ING, null,
                MenuComprasDBHelper.ING_USUARIO + " = ?", new String[]{SesionActual.uid()},
                null, null, MenuComprasDBHelper.ING_ID + " ASC");
        try {
            while (c.moveToNext()) lista.add(desde(c));
        } finally {
            c.close();
        }
        return lista;
    }

    public Ingrediente buscarPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_ING, null,
                MenuComprasDBHelper.ING_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (c.moveToNext()) return desde(c);
            return null;
        } finally {
            c.close();
        }
    }

    public boolean existeNombre(String nombre, int excluyendoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_ING, new String[]{MenuComprasDBHelper.ING_ID},
                MenuComprasDBHelper.ING_USUARIO + " = ? AND "
                        + MenuComprasDBHelper.ING_NOMBRE + " = ? COLLATE NOCASE AND "
                        + MenuComprasDBHelper.ING_ID + " <> ?",
                new String[]{SesionActual.uid(), nombre.trim(), String.valueOf(excluyendoId)},
                null, null, null);
        try {
            return c.getCount() > 0;
        } finally {
            c.close();
        }
    }

    public Ingrediente buscarPorNombre(String nombre) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(MenuComprasDBHelper.T_ING, null,
                MenuComprasDBHelper.ING_USUARIO + " = ? AND "
                        + MenuComprasDBHelper.ING_NOMBRE + " = ? COLLATE NOCASE",
                new String[]{SesionActual.uid(), nombre.trim()}, null, null, null);
        try {
            if (c.moveToNext()) return desde(c);
            return null;
        } finally {
            c.close();
        }
    }

    public int agregar(Ingrediente ing) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insertOrThrow(MenuComprasDBHelper.T_ING, null, valores(ing));
        return (int) id;
    }

    public void actualizar(Ingrediente ing) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.update(MenuComprasDBHelper.T_ING, valores(ing),
                MenuComprasDBHelper.ING_ID + " = ?", new String[]{String.valueOf(ing.getId())});
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(MenuComprasDBHelper.T_ING, MenuComprasDBHelper.ING_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLiteConstraintException e) {
            // Referenciado por alguna comida (FK RESTRICT): no se borra. La app ya lo bloquea antes.
        }
    }
}