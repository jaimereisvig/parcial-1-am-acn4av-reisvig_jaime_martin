package com.example.menucompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.DatosEjemplo;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.LineaDeComida;

import java.util.HashMap;

public class MenuComprasDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "menucompras.db";
    public static final int DB_VERSION = 5;

    // Tabla ingrediente
    public static final String T_ING = "ingrediente";
    public static final String ING_ID = "id";
    public static final String ING_USUARIO = "usuario_uid";
    public static final String ING_NOMBRE = "nombre";
    public static final String ING_TIPO_MEDIDA = "tipo_medida";
    public static final String ING_PRES_CANT = "presentacion_cantidad";
    public static final String ING_NOMBRE_PRES = "nombre_presentacion";
    public static final String ING_NOMBRE_UNIDAD = "nombre_unidad";
    public static final String ING_CATEGORIA = "categoria";

    // Tabla comida
    public static final String T_COM = "comida";
    public static final String COM_ID = "id";
    public static final String COM_USUARIO = "usuario_uid";
    public static final String COM_NOMBRE = "nombre";
    public static final String COM_COMENSALES = "comensales_base";
    public static final String COM_TIPO = "tipo";
    public static final String COM_PREPARACION = "preparacion";
    public static final String COM_CATEGORIA = "categoria";
    public static final String COM_IMAGEN = "imagen_url";

    // Tabla comida_ingrediente (linea de comida)
    public static final String T_CI = "comida_ingrediente";
    public static final String CI_ID = "id";
    public static final String CI_COMIDA_ID = "comida_id";
    public static final String CI_INGREDIENTE_ID = "ingrediente_id";
    public static final String CI_CANTIDAD = "cantidad";
    public static final String CI_A_GUSTO = "a_gusto";

    // Tabla menu (una fila por usuario)
    public static final String T_MENU = "menu";
    public static final String MENU_ID = "id";
    public static final String MENU_USUARIO = "usuario_uid";
    public static final String MENU_ETIQUETA = "etiqueta";
    public static final String MENU_FECHA_INICIO = "fecha_inicio";

    // Tabla menu_item
    public static final String T_MI = "menu_item";
    public static final String MI_ID = "id";
    public static final String MI_MENU_ID = "menu_id";
    public static final String MI_COMIDA_ID = "comida_id";
    public static final String MI_DIA = "dia";
    public static final String MI_FRANJA = "franja";
    public static final String MI_FACTOR = "factor";

    // Tabla usuario (perfil local)
    public static final String T_USER = "usuario";
    public static final String USER_UID = "uid";
    public static final String USER_NOMBRE = "nombre_usuario";
    public static final String USER_ALIAS = "alias";
    public static final String USER_EMAIL = "email";

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
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        crearTablasDatos(db);
        crearTablasMenu(db);
        crearTablaUsuario(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            crearTablaUsuario(db);
        }
        if (oldVersion < 4) {
            // v<4: reestructura a datos por-usuario (recrea tablas; descarta datos globales previos).
            db.execSQL("DROP TABLE IF EXISTS " + T_MI);
            db.execSQL("DROP TABLE IF EXISTS " + T_MENU);
            db.execSQL("DROP TABLE IF EXISTS " + T_CI);
            db.execSQL("DROP TABLE IF EXISTS " + T_COM);
            db.execSQL("DROP TABLE IF EXISTS " + T_ING);
            crearTablasDatos(db);
            crearTablasMenu(db);
        } else if (oldVersion < 5) {
            // v4 -> v5: agrega la columna de imagen a comida (aditivo, preserva datos).
            db.execSQL("ALTER TABLE " + T_COM + " ADD COLUMN " + COM_IMAGEN + " TEXT");
        }
    }

    private void crearTablasDatos(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_ING + " (" +
                ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ING_USUARIO + " TEXT NOT NULL, " +
                ING_NOMBRE + " TEXT NOT NULL, " +
                ING_TIPO_MEDIDA + " TEXT NOT NULL, " +
                ING_PRES_CANT + " REAL NOT NULL CHECK (" + ING_PRES_CANT + " > 0), " +
                ING_NOMBRE_PRES + " TEXT, " +
                ING_NOMBRE_UNIDAD + " TEXT, " +
                ING_CATEGORIA + " TEXT, " +
                "UNIQUE (" + ING_USUARIO + ", " + ING_NOMBRE + " COLLATE NOCASE))");

        db.execSQL("CREATE TABLE " + T_COM + " (" +
                COM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COM_USUARIO + " TEXT NOT NULL, " +
                COM_NOMBRE + " TEXT NOT NULL, " +
                COM_COMENSALES + " INTEGER NOT NULL CHECK (" + COM_COMENSALES + " > 0), " +
                COM_TIPO + " TEXT NOT NULL, " +
                COM_PREPARACION + " TEXT, " +
                COM_CATEGORIA + " TEXT NOT NULL DEFAULT 'VARIOS', " +
                COM_IMAGEN + " TEXT, " +
                "UNIQUE (" + COM_USUARIO + ", " + COM_NOMBRE + " COLLATE NOCASE))");

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
    }

    private void crearTablasMenu(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_MENU + " (" +
                MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MENU_USUARIO + " TEXT NOT NULL, " +
                MENU_ETIQUETA + " TEXT NOT NULL, " +
                MENU_FECHA_INICIO + " TEXT)");

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

    private void crearTablaUsuario(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USER + " (" +
                USER_UID + " TEXT PRIMARY KEY, " +
                USER_NOMBRE + " TEXT NOT NULL, " +
                USER_ALIAS + " TEXT NOT NULL, " +
                USER_EMAIL + " TEXT NOT NULL)");
    }

    // Copia el seed de ejemplo para un usuario, remapeando ids.
    public void sembrarParaUsuario(String uid) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            HashMap<Integer, Integer> mapIng = new HashMap<>();
            for (Ingrediente ing : DatosEjemplo.getIngredientes()) {
                ContentValues cv = new ContentValues();
                cv.put(ING_USUARIO, uid);
                cv.put(ING_NOMBRE, ing.getNombre());
                cv.put(ING_TIPO_MEDIDA, ing.getTipoMedida().name());
                cv.put(ING_PRES_CANT, ing.getPresentacionCantidad());
                cv.put(ING_NOMBRE_PRES, ing.getNombrePresentacion());
                cv.put(ING_NOMBRE_UNIDAD, ing.getNombreUnidad());
                cv.put(ING_CATEGORIA, ing.getCategoria().name());
                long nuevoId = db.insertOrThrow(T_ING, null, cv);
                mapIng.put(ing.getId(), (int) nuevoId);
            }

            for (Comida c : DatosEjemplo.getComidas()) {
                ContentValues cv = new ContentValues();
                cv.put(COM_USUARIO, uid);
                cv.put(COM_NOMBRE, c.getNombre());
                cv.put(COM_COMENSALES, c.getComensalesBase());
                cv.put(COM_TIPO, c.getTipo().name());
                cv.put(COM_PREPARACION, c.getPreparacion());
                cv.put(COM_CATEGORIA, c.getCategoria().name());
                cv.put(COM_IMAGEN, c.getImagenUrl());
                long nuevaComidaId = db.insertOrThrow(T_COM, null, cv);

                for (LineaDeComida l : c.getLineas()) {
                    Integer ingNuevo = mapIng.get(l.getIngredienteId());
                    if (ingNuevo == null) continue;
                    ContentValues cvl = new ContentValues();
                    cvl.put(CI_COMIDA_ID, (int) nuevaComidaId);
                    cvl.put(CI_INGREDIENTE_ID, ingNuevo);
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

            ContentValues cvm = new ContentValues();
            cvm.put(MENU_USUARIO, uid);
            cvm.put(MENU_ETIQUETA, "Menú semanal");
            cvm.putNull(MENU_FECHA_INICIO);
            db.insertOrThrow(T_MENU, null, cvm);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}