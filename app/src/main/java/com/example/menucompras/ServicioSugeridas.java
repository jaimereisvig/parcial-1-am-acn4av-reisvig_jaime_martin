package com.example.menucompras;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.menucompras.modelo.ComidaSugerida;
import com.example.menucompras.modelo.IngredienteSugerido;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServicioSugeridas {

    private static final String URL =
            "https://raw.githubusercontent.com/jaimereisvig/parcial-2-am-acn4av-reisvig_jaime_martin/refs/heads/master/comidas_sugeridas.json";

    public interface Callback {
        void onExito(List<ComidaSugerida> comidas);
        void onError(String mensaje);
    }

    private ServicioSugeridas() {}

    public static void descargar(Context context, Callback cb) {
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        cb.onExito(parsear(response));
                    } catch (Exception e) {
                        cb.onError(e.getMessage());
                    }
                },
                error -> cb.onError(error.getMessage()));
        queue.add(req);
    }

    private static List<ComidaSugerida> parsear(JSONObject root) throws Exception {
        List<ComidaSugerida> comidas = new ArrayList<>();
        JSONArray arr = root.getJSONArray("comidas");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject c = arr.getJSONObject(i);
            List<IngredienteSugerido> ings = new ArrayList<>();
            JSONArray ingArr = c.getJSONArray("ingredientes");
            for (int j = 0; j < ingArr.length(); j++) {
                JSONObject ig = ingArr.getJSONObject(j);
                boolean aGusto = ig.optBoolean("aGusto", false);
                double cantidad = (aGusto || ig.isNull("cantidad")) ? 0 : ig.optDouble("cantidad", 0);
                ings.add(new IngredienteSugerido(
                        ig.optString("nombre"),
                        ig.optString("tipoMedida"),
                        ig.optDouble("presentacionCantidad", 0),
                        ig.optString("nombrePresentacion"),
                        ig.optString("nombreUnidad"),
                        ig.optString("categoria"),
                        cantidad,
                        aGusto));
            }
            comidas.add(new ComidaSugerida(
                    c.optString("nombre"),
                    c.optInt("comensales", 1),
                    c.optString("tipo"),
                    c.optString("categoria"),
                    c.optString("preparacion"),
                    c.optString("imagenUrl"),
                    ings));
        }
        return comidas;
    }
}