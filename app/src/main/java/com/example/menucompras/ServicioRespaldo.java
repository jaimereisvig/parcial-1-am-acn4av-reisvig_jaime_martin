package com.example.menucompras;

import android.content.Context;

import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.MenuItem;
import com.example.menucompras.modelo.Usuario;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Respaldo del usuario en Firestore: documento respaldos/{uid}.
 * C.2 = respaldar (set). C.3 = restaurar (pendiente).
 */
public class ServicioRespaldo {

    private static final String COL = "respaldos";
    private static final int VERSION = 1;

    public interface Callback {
        void onExito();
        void onError(String mensaje);
    }

    public interface CallbackFecha {
        void onFecha(Date fecha); // null si todavía no hay respaldo
    }

    private ServicioRespaldo() {}

    // ---- Respaldar: sube todo lo local del usuario a su documento ----
    public static void respaldar(Context context, Callback cb) {
        String uid = SesionActual.uid();
        if (uid == null) { cb.onError("No hay sesión activa."); return; }

        // 1) Leer lo local.
        Usuario perfil = RepositorioUsuarios.getInstancia(context).buscarPorUid(uid);
        List<Ingrediente> ingredientes = RepositorioIngredientes.getInstancia(context).getTodos();
        List<Comida> comidas = RepositorioComidas.getInstancia(context).getTodos();
        List<MenuItem> menuItems = RepositorioMenus.getInstancia(context).getItems();

        // 2) Armar el documento (ids originales incluidos, para remapear al restaurar).
        Map<String, Object> doc = new HashMap<>();
        doc.put("version", VERSION);
        doc.put("timestamp", FieldValue.serverTimestamp());

        if (perfil != null) {
            Map<String, Object> p = new HashMap<>();
            p.put("nombreUsuario", perfil.getNombreUsuario());
            p.put("alias", perfil.getAlias());
            p.put("email", perfil.getEmail());
            doc.put("perfil", p);
        }

        List<Map<String, Object>> ings = new ArrayList<>();
        for (Ingrediente i : ingredientes) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", i.getId());
            m.put("nombre", i.getNombre());
            m.put("tipoMedida", i.getTipoMedida().name());
            m.put("presentacionCantidad", i.getPresentacionCantidad());
            m.put("nombrePresentacion", i.getNombrePresentacion());
            m.put("nombreUnidad", i.getNombreUnidad());
            m.put("categoria", i.getCategoria().name());
            ings.add(m);
        }
        doc.put("ingredientes", ings);

        List<Map<String, Object>> coms = new ArrayList<>();
        for (Comida c : comidas) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("nombre", c.getNombre());
            m.put("comensalesBase", c.getComensalesBase());
            m.put("tipo", c.getTipo().name());
            m.put("categoria", c.getCategoria().name());
            m.put("preparacion", c.getPreparacion());
            m.put("imagenUrl", c.getImagenUrl());
            List<Map<String, Object>> lineas = new ArrayList<>();
            for (LineaDeComida l : c.getLineas()) {
                Map<String, Object> lm = new HashMap<>();
                lm.put("ingredienteId", l.getIngredienteId());
                lm.put("aGusto", l.isAGusto());
                lm.put("cantidad", l.isAGusto() ? null : l.getCantidad());
                lineas.add(lm);
            }
            m.put("lineas", lineas);
            coms.add(m);
        }
        doc.put("comidas", coms);

        List<Map<String, Object>> items = new ArrayList<>();
        for (MenuItem it : menuItems) {
            Map<String, Object> m = new HashMap<>();
            m.put("comidaId", it.getComidaId());
            m.put("dia", it.getDia().name());
            m.put("franja", it.getFranja().name());
            m.put("factor", it.getFactor());
            items.add(m);
        }
        doc.put("menuItems", items);

        // 3) Subir. set() reemplaza el documento entero (es un respaldo, no un merge).
        FirebaseFirestore.getInstance()
                .collection(COL).document(uid)
                .set(doc)
                .addOnSuccessListener(unused -> cb.onExito())
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // ---- Fecha del último respaldo (para mostrarla discreta en Home) ----
    public static void ultimaFecha(CallbackFecha cb) {
        String uid = SesionActual.uid();
        if (uid == null) { cb.onFecha(null); return; }
        FirebaseFirestore.getInstance()
                .collection(COL).document(uid)
                .get()
                .addOnSuccessListener(snap -> {
                    Date fecha = null;
                    if (snap.exists()) {
                        try { fecha = snap.getDate("timestamp"); } catch (Exception ignored) {}
                    }
                    cb.onFecha(fecha);
                })
                .addOnFailureListener(e -> cb.onFecha(null));
    }
}