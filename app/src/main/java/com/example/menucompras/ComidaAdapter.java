package com.example.menucompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menucompras.modelo.Comida;

import java.util.List;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Comida comida);
    }

    private List<Comida> comidas;
    private OnItemClickListener listener;

    public ComidaAdapter(List<Comida> comidas) {
        this.comidas = comidas;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Comida> nuevos) {
        this.comidas = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comida, parent, false);
        return new ComidaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ComidaViewHolder holder, int position) {
        final Comida comida = comidas.get(position);
        holder.tvNombre.setText(comida.getNombre());
        holder.tvCategoria.setText(comida.getCategoria().getEtiqueta());
        int nIngr = comida.getLineas().size();
        holder.tvRinde.setText(holder.itemView.getContext()
                .getString(R.string.comida_rinde, comida.getComensalesBase(), nIngr));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(comida);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comidas.size();
    }

    static class ComidaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCategoria;
        TextView tvRinde;

        ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_comida);
            tvCategoria = itemView.findViewById(R.id.tv_categoria_comida);
            tvRinde = itemView.findViewById(R.id.tv_rinde);
        }
    }
}