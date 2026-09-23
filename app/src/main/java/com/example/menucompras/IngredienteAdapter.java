package com.example.menucompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.Ingrediente;

import java.util.List;

public class IngredienteAdapter extends RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Ingrediente ingrediente);
    }

    private List<Ingrediente> ingredientes;
    private OnItemClickListener listener;

    public IngredienteAdapter(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Ingrediente> nuevos) {
        this.ingredientes = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingrediente, parent, false);
        return new IngredienteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredienteViewHolder holder, int position) {
        final Ingrediente ing = ingredientes.get(position);

        holder.tvNombre.setText(ing.getNombre());
        holder.tvGondola.setText(ing.getCategoria().getEtiqueta());

        if (ing.getEnUso() > 0) {
            holder.tvEnUso.setText("en uso · " + ing.getEnUso());
            holder.tvEnUso.setVisibility(View.VISIBLE);
        } else {
            holder.tvEnUso.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(ing);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientes.size();
    }

    static class IngredienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvGondola;
        TextView tvEnUso;

        IngredienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvGondola = itemView.findViewById(R.id.tv_gondola);
            tvEnUso = itemView.findViewById(R.id.tv_en_uso);
        }
    }
}