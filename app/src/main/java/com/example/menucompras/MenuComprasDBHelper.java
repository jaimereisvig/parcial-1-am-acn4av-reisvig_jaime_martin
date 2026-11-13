package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.DatosEjemplo;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.LineaDeComida;

public class MenuComprasDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "menucompras.db";
    public static final int DB_VERSION = 1;

    // Tabla ingrediente
    public static final String T_ING = "ingrediente";
    public static final String ING_ID = "id";
    public static final String ING_NOMBRE = "nombre";
    public static final String ING_TIPO_MEDIDA = "tipo_medida";
    public static final String ING_PRES_CANT = "presentacion_cantidad";
    public static final String ING_NOMBRE_PRES = "nombre_presentacion";
    public static final String ING_NOMBRE_UNIDAD = "nombre_unidad";
    public static final String ING_CATEGORIA = "categoria";

    // Tabla comida
    public static final String T_COM = "comida";
    public static final String COM_ID = "id";
    public static final String COM_NOMBRE = "nombre";
    public static final String COM_COMENSALES = "comensales_base";
    public static final String COM_TIPO = "tipo";
    public static final String COM_PREPARACION = "preparacion";
    public static final String COM_CATEGORIA = "categoria";

    // Tabla comida_ingrediente (linea de comida)
    public static final String T_CI = "comida_ingrediente";
    public static final String CI_ID = "id";
    public static final String CI_COMIDA_ID = "comida_id";
    public static final String CI_INGREDIENTE_ID = "ingrediente_id";
    public static final String CI_CANTIDAD = "cantidad";
    public static final String CI_A_GUSTO = "a_gusto";

    private static MenuComprasDBHelper instancia;

    private MenuComprasDBHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static synchronized MenuComprasDBHelper getInstancia(Context context) {
        if (instancia == null) {
            instancia = new MenuComprasDBHelper(context);
        }
        return instancia;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // fuerza las FK (RESTRICT / CASCADE)
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_ING + " (" +
                ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ING_NOMBRE + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                ING_TIPO_MEDIDA + " TEXT NOT NULL, " +
                ING_PRES_CANT + " REAL NOT NULL CHECK (" + ING_PRES_CANT + " > 0), " +
                ING_NOMBRE_PRES + " TEXT, " +
                ING_NOMBRE_UNIDAD + " TEXT, " +
                ING_CATEGORIA + " TEXT)");

        db.execSQL("CREATE TABLE " + T_COM + " (" +
                COM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COM_NOMBRE + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                COM_COMENSALES + " INTEGER NOT NULL CHECK (" + COM_COMENSALES + " > 0), " +
                COM_TIPO + " TEXT NOT NULL, " +
                COM_PREPARACION + " TEXT, " +
                COM_CATEGORIA + " TEXT NOT NULL DEFAULT 'VARIOS')");

        db.execSQL("CREATE TABLE " + T_CI + " (" +
                CI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CI_COMIDA_ID + " INTEGER NOT NULL, " +
                CI_INGREDIENTE_ID + " INTEGER NOT NULL, " +
                CI_CANTIDAD + " REAL, " +
                CI_A_GUSTO + " INTEGER NOT NULL DEFAULT 0 CHECK (" + CI_A_GUSTO + " IN (0, 1)), " +
                "UNIQUE (" + CI_COMIDA_ID + ", " + CI_INGREDIENTE_ID + "), " +
                "CHECK (" + CI_A_GUSTO + " = 1 OR (" + CI_CANTIDAD + " IS NOT NULL AND " + CI_CANTIDAD + " > 0)), " +
                "FOREIGN KEY (" + CI_COMIDA_ID + ") REFERENCES " + T_COM + "(" + COM_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + CI_INGREDIENTE_ID + ") REFERENCES " + T_ING + "(" + ING_ID + ") ON DELETE RESTRICT)");

        sembrar(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Provisorio: al subir de version se recrea la base (todavia no hay datos de usuario que preservar).
        db.execSQL("DROP TABLE IF EXISTS " + T_CI);
        db.execSQL("DROP TABLE IF EXISTS " + T_COM);
        db.execSQL("DROP TABLE IF EXISTS " + T_ING);
        onCreate(db);
    }

    // Siembra inicial desde DatosEjemplo. Se inserta con el id EXPLICITO de cada objeto
    // para preservar las referencias de las lineas (comida_id / ingrediente_id).
    private void sembrar(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (Ingrediente ing : DatosEjemplo.getIngredientes()) {
                ContentValues cv = new ContentValues();
                cv.put(ING_ID, ing.getId());
                cv.put(ING_NOMBRE, ing.getNombre());
                cv.put(ING_TIPO_MEDIDA, ing.getTipoMedida().name());
                cv.put(ING_PRES_CANT, ing.getPresentacionCantidad());
                cv.put(ING_NOMBRE_PRES, ing.getNombrePresentacion());
                cv.put(ING_NOMBRE_UNIDAD, ing.getNombreUnidad());
                cv.put(ING_CATEGORIA, ing.getCategoria().name());
                db.insertOrThrow(T_ING, null, cv);
            }

            for (Comida c : DatosEjemplo.getComidas()) {
                ContentValues cv = new ContentValues();
                cv.put(COM_ID, c.getId());
                cv.put(COM_NOMBRE, c.getNombre());
                cv.put(COM_COMENSALES, c.getComensalesBase());
                cv.put(COM_TIPO, c.getTipo().name());
                cv.put(COM_PREPARACION, c.getPreparacion());
                cv.put(COM_CATEGORIA, c.getCategoria().name());
                db.insertOrThrow(T_COM, null, cv);

                for (LineaDeComida l : c.getLineas()) {
                    ContentValues cvl = new ContentValues();
                    cvl.put(CI_COMIDA_ID, c.getId());
                    cvl.put(CI_INGREDIENTE_ID, l.getIngredienteId());
                    if (l.isAGusto()) {
                        cvl.put(CI_A_GUSTO, 1);
                        cvl.putNull(CI_CANTIDAD);
                    } else {
                        cvl.put(CI_A_GUSTO, 0);
                        cvl.put(CI_CANTIDAD, l.getCantidad());
                    }
                    db.insertOrThrow(T_CI, null, cvl);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}