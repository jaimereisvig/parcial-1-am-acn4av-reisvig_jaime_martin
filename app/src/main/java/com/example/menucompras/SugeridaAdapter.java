package com.example.menucompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.menucompras.modelo.ComidaSugerida;

import java.util.List;

public class SugeridaAdapter extends RecyclerView.Adapter<SugeridaAdapter.SugeridaViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ComidaSugerida comida);
    }

    private List<ComidaSugerida> comidas;
    private OnItemClickListener listener;

    public SugeridaAdapter(List<ComidaSugerida> comidas) {
        this.comidas = comidas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ComidaSugerida> nuevos) {
        this.comidas = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SugeridaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sugerida, parent, false);
        return new SugeridaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SugeridaViewHolder holder, int position) {
        final ComidaSugerida c = comidas.get(position);
        holder.tvNombre.setText(c.getNombre());
        holder.tvResumen.setText(holder.itemView.getContext()
                .getString(R.string.sugerida_resumen, c.getCategoria(), c.getIngredientes().size()));

        String url = c.getImagenUrl();
        if (url == null || url.trim().isEmpty()) {
            holder.ivImagen.setImageResource(R.drawable.ic_sin_imagen);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.bg_chip_neutral)
                    .error(R.drawable.ic_imagen_rota)
                    .into(holder.ivImagen);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (listener != null) listener.onItemClick(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comidas.size();
    }

    static class SugeridaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre;
        TextView tvResumen;

        SugeridaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_imagen_sugerida);
            tvNombre = itemView.findViewById(R.id.tv_nombre_sugerida);
            tvResumen = itemView.findViewById(R.id.tv_resumen_sugerida);
        }
    }
}