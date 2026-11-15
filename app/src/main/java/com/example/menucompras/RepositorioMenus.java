package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.menucompras.modelo.Dia;
import com.example.menucompras.modelo.Franja;
import com.example.menucompras.modelo.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class RepositorioMenus {

    private static RepositorioMenus instancia;
    private final MenuComprasDBHelper dbHelper;

    private RepositorioMenus(Context context) {
        dbHelper = MenuComprasDBHelper.getInstancia(context);
    }

    public static synchronized RepositorioMenus getInstancia(Context context) {
        if (instancia == null) {
            instancia = new RepositorioMenus(context);
        }
        return instancia;
    }

    // id del menu del usuario logueado; si no existe (defensivo), lo crea.
    private int menuIdDelUsuario(SQLiteDatabase db) {
        String uid = SesionActual.uid();
        Cursor c = db.query(MenuComprasDBHelper.T_MENU,
                new String[]{MenuComprasDBHelper.MENU_ID},
                MenuComprasDBHelper.MENU_USUARIO + " = ?", new String[]{uid},
                null, null, null);
        try {
            if (c.moveToFirst()) return c.getInt(0);
        } finally {
            c.close();
        }
        ContentValues cv = new ContentValues();
        cv.put(MenuComprasDBHelper.MENU_USUARIO, uid);
        cv.put(MenuComprasDBHelper.MENU_ETIQUETA, "Menú semanal");
        cv.putNull(MenuComprasDBHelper.MENU_FECHA_INICIO);
        return (int) db.insertOrThrow(MenuComprasDBHelper.T_MENU, null, cv);
    }

    private MenuItem desde(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.MI_ID));
        int comidaId = c.getInt(c.getColumnIndexOrThrow(MenuComprasDBHelper.MI_COMIDA_ID));
        Dia dia = Dia.valueOf(c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.MI_DIA)));
        Franja franja = Franja.valueOf(c.getString(c.getColumnIndexOrThrow(MenuComprasDBHelper.MI_FRANJA)));
        double factor = c.getDouble(c.getColumnIndexOrThrow(MenuComprasDBHelper.MI_FACTOR));
        return new MenuItem(id, comidaId, dia, franja, factor);
    }

    private ContentValues valores(MenuItem it, int menuId) {
        ContentValues cv = new ContentValues();
        cv.put(MenuComprasDBHelper.MI_MENU_ID, menuId);
        cv.put(MenuComprasDBHelper.MI_COMIDA_ID, it.getComidaId());
        cv.put(MenuComprasDBHelper.MI_DIA, it.getDia().name());
        cv.put(MenuComprasDBHelper.MI_FRANJA, it.getFranja().name());
        cv.put(MenuComprasDBHelper.MI_FACTOR, it.getFactor());
        return cv;
    }

    public List<MenuItem> getItems() {
        List<MenuItem> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int menuId = menuIdDelUsuario(db);
        Cursor c = db.query(MenuComprasDBHelper.T_MI, null,
                MenuComprasDBHelper.MI_MENU_ID + " = ?",
                new String[]{String.valueOf(menuId)},
                null, null, MenuComprasDBHelper.MI_ID + " ASC");
        try {
            while (c.moveToNext()) lista.add(desde(c));
        } finally {
            c.close();
        }
        return lista;
    }

    public List<MenuItem> getItemsDe(Dia dia, Franja franja) {
        List<MenuItem> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int menuId = menuIdDelUsuario(db);
        Cursor c = db.query(MenuComprasDBHelper.T_MI, null,
                MenuComprasDBHelper.MI_MENU_ID + " = ? AND "
                        + MenuComprasDBHelper.MI_DIA + " = ? AND "
                        + MenuComprasDBHelper.MI_FRANJA + " = ?",
                new String[]{String.valueOf(menuId), dia.name(), franja.name()},
                null, null, MenuComprasDBHelper.MI_ID + " ASC");
        try {
            while (c.moveToNext()) lista.add(desde(c));
        } finally {
            c.close();
        }
        return lista;
    }

    public int agregar(MenuItem it) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int menuId = menuIdDelUsuario(db);
        long id = db.insertOrThrow(MenuComprasDBHelper.T_MI, null, valores(it, menuId));
        return (int) id;
    }

    public void actualizar(MenuItem it) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int menuId = menuIdDelUsuario(db);
        db.update(MenuComprasDBHelper.T_MI, valores(it, menuId),
                MenuComprasDBHelper.MI_ID + " = ?", new String[]{String.valueOf(it.getId())});
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MenuComprasDBHelper.T_MI, MenuComprasDBHelper.MI_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int contarItemsQueUsan(int comidaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM " + MenuComprasDBHelper.T_MI + " WHERE "
                        + MenuComprasDBHelper.MI_COMIDA_ID + " = ?",
                new String[]{String.valueOf(comidaId)});
        try {
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            c.close();
        }
    }
}