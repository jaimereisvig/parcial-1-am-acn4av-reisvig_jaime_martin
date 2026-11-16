package com.example.menucompras;

import android.content.Context;

import com.example.menucompras.modelo.Categoria;
import com.example.menucompras.modelo.CategoriaComida;
import com.example.menucompras.modelo.Comida;
import com.example.menucompras.modelo.Dia;
import com.example.menucompras.modelo.Franja;
import com.example.menucompras.modelo.Ingrediente;
import com.example.menucompras.modelo.LineaDeComida;
import com.example.menucompras.modelo.MenuItem;
import com.example.menucompras.modelo.TipoComida;
import com.example.menucompras.modelo.TipoMedida;
import com.example.menucompras.modelo.Usuario;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Respaldo del usuario en Firestore: documento respaldos/{uid}
 * (get + reemplazo con remapeo de ids).
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

    public interface CallbackRestaurar {
        void onExito();
        void onSinRespaldo();
        void onError(String mensaje);
    }

    private ServicioRespaldo() {}

    // ---- Respaldar: sube todo lo local del usuario a su documento ----
    public static void respaldar(Context context, Callback cb) {
        String uid = SesionActual.uid();
        if (uid == null) { cb.onError("No hay sesión activa."); return; }

        Usuario perfil = RepositorioUsuarios.getInstancia(context).buscarPorUid(uid);
        List<Ingrediente> ingredientes = RepositorioIngredientes.getInstancia(context).getTodos();
        List<Comida> comidas = RepositorioComidas.getInstancia(context).getTodos();
        List<MenuItem> menuItems = RepositorioMenus.getInstancia(context).getItems();

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

        FirebaseFirestore.getInstance()
                .collection(COL).document(uid)
                .set(doc)
                .addOnSuccessListener(unused -> cb.onExito())
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // ---- Fecha del último respaldo (para Home) ----
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

    // ---- Restaurar: baja el respaldo y reemplaza lo local (con remapeo de ids) ----
    @SuppressWarnings("unchecked")
    public static void restaurar(Context context, CallbackRestaurar cb) {
        String uid = SesionActual.uid();
        if (uid == null) { cb.onError("No hay sesión activa."); return; }

        FirebaseFirestore.getInstance()
                .collection(COL).document(uid)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) { cb.onSinRespaldo(); return; }
                    try {
                        Usuario perfil = null;
                        Map<String, Object> p = (Map<String, Object>) snap.get("perfil");
                        if (p != null) {
                            perfil = new Usuario(uid, str(p.get("nombreUsuario")),
                                    str(p.get("alias")), str(p.get("email")));
                        }

                        List<Ingrediente> ingredientes = new ArrayList<>();
                        List<Map<String, Object>> ings = (List<Map<String, Object>>) snap.get("ingredientes");
                        if (ings != null) for (Map<String, Object> m : ings) {
                            ingredientes.add(new Ingrediente(
                                    intt(m.get("id")),
                                    str(m.get("nombre")),
                                    TipoMedida.valueOf(str(m.get("tipoMedida"))),
                                    dbl(m.get("presentacionCantidad")),
                                    str(m.get("nombrePresentacion")),
                                    str(m.get("nombreUnidad")),
                                    Categoria.valueOf(str(m.get("categoria")))));
                        }

                        List<Comida> comidas = new ArrayList<>();
                        List<Map<String, Object>> coms = (List<Map<String, Object>>) snap.get("comidas");
                        if (coms != null) for (Map<String, Object> m : coms) {
                            List<LineaDeComida> lineas = new ArrayList<>();
                            List<Map<String, Object>> ls = (List<Map<String, Object>>) m.get("lineas");
                            if (ls != null) for (Map<String, Object> lm : ls) {
                                boolean aGusto = Boolean.TRUE.equals(lm.get("aGusto"));
                                double cant = aGusto ? 0 : dbl(lm.get("cantidad"));
                                lineas.add(new LineaDeComida(intt(lm.get("ingredienteId")), cant, aGusto));
                            }
                            comidas.add(new Comida(
                                    intt(m.get("id")),
                                    str(m.get("nombre")),
                                    intt(m.get("comensalesBase")),
                                    TipoComida.valueOf(str(m.get("tipo"))),
                                    CategoriaComida.valueOf(str(m.get("categoria"))),
                                    str(m.get("preparacion")),
                                    str(m.get("imagenUrl")),
                                    lineas));
                        }

                        List<MenuItem> menuItems = new ArrayList<>();
                        List<Map<String, Object>> mis = (List<Map<String, Object>>) snap.get("menuItems");
                        if (mis != null) for (Map<String, Object> m : mis) {
                            menuItems.add(new MenuItem(
                                    0,
                                    intt(m.get("comidaId")),
                                    Dia.valueOf(str(m.get("dia"))),
                                    Franja.valueOf(str(m.get("franja"))),
                                    dbl(m.get("factor"))));
                        }

                        MenuComprasDBHelper.getInstancia(context)
                                .restaurarParaUsuario(uid, perfil, ingredientes, comidas, menuItems);
                        cb.onExito();
                    } catch (Exception e) {
                        cb.onError(e.getMessage());
                    }
                })
                .addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    // ---- Helpers de parseo (Firestore devuelve Long/Double/Boolean) ----
    private static String str(Object o) { return o == null ? null : o.toString(); }
    private static int intt(Object o) { return o == null ? 0 : ((Number) o).intValue(); }
    private static double dbl(Object o) { return o == null ? 0 : ((Number) o).doubleValue(); }
}