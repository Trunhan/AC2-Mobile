package com.example.ac2mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ac2mobile.R;
import com.example.ac2mobile.Treino;
import java.util.List;

public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.MyViewHolder> {

    private List<Treino> listaTreinos;
    private OnTreinoListener listener;

    public interface OnTreinoListener {
        void onTreinoClick(Treino treino);
        void onTreinoLongClick(Treino treino);
    }

    public TreinoAdapter(List<Treino> lista, OnTreinoListener listener) {
        this.listaTreinos = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treino, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Treino treino = listaTreinos.get(position);

        holder.nome.setText(treino.getNome());
        holder.detalhes.setText(treino.getTipoAtividade() + " - " + treino.getData());
        holder.infoExtra.setText("Duração: " + treino.getDuracao() + " min | " + treino.getIntensidade());

        // Clique Curto (Edição)
        holder.itemView.setOnClickListener(v -> listener.onTreinoClick(treino));

        // Clique Longo (Exclusão)
        holder.itemView.setOnLongClickListener(v -> {
            listener.onTreinoLongClick(treino);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaTreinos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome, detalhes, infoExtra;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.txtNomeItem);
            detalhes = itemView.findViewById(R.id.txtDetalhesItem);
            infoExtra = itemView.findViewById(R.id.txtInfoExtraItem);
        }
    }
}
