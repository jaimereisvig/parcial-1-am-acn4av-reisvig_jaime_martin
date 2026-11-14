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
    public static final int DB_VERSION = 2;

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

    // Tabla menu (en esta etapa hay un unico menu, con id MENU_UNICO_ID)
    public static final String T_MENU = "menu";
    public static final String MENU_ID = "id";
    public static final String MENU_ETIQUETA = "etiqueta";
    public static final String MENU_FECHA_INICIO = "fecha_inicio";
    public static final int MENU_UNICO_ID = 1;

    // Tabla menu_item (una comida asignada a un dia + franja, con factor)
    public static final String T_MI = "menu_item";
    public static final String MI_ID = "id";
    public static final String MI_MENU_ID = "menu_id";
    public static final String MI_COMIDA_ID = "comida_id";
    public static final String MI_DIA = "dia";
    public static final String MI_FRANJA = "franja";
    public static final String MI_FACTOR = "factor";

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

        // Dominio de Menus (v2)
        crearTablasMenu(db);
        sembrarMenu(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migracion aditiva: NO se recrea la base (se preservan ingredientes/comidas del usuario).
        if (oldVersion < 2) {
            // v1 -> v2: se agregan las tablas del dominio de Menus.
            crearTablasMenu(db);
            sembrarMenu(db);
        }
    }

    private void crearTablasMenu(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_MENU + " (" +
                MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MENU_ETIQUETA + " TEXT NOT NULL, " +
                MENU_FECHA_INICIO + " TEXT)");

        // dia y franja se guardan con name() del enum (validez garantizada en Java); sin CHECK redundante.
        db.execSQL("CREATE TABLE " + T_MI + " (" +
                MI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MI_MENU_ID + " INTEGER NOT NULL, " +
                MI_COMIDA_ID + " INTEGER NOT NULL, " +
                MI_DIA + " TEXT NOT NULL, " +
                MI_FRANJA + " TEXT NOT NULL, " +
                MI_FACTOR + " REAL NOT NULL DEFAULT 1 CHECK (" + MI_FACTOR + " > 0 AND " + MI_FACTOR + " <= 4), " +
                "FOREIGN KEY (" + MI_MENU_ID + ") REFERENCES " + T_MENU + "(" + MENU_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + MI_COMIDA_ID + ") REFERENCES " + T_COM + "(" + COM_ID + ") ON DELETE RESTRICT)");
    }

    // Siembra el menu unico (una fila, sin asignaciones; el usuario lo arma en la UI).
    private void sembrarMenu(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(MENU_ID, MENU_UNICO_ID);
        cv.put(MENU_ETIQUETA, "Menú semanal");
        cv.putNull(MENU_FECHA_INICIO);
        db.insertOrThrow(T_MENU, null, cv);
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