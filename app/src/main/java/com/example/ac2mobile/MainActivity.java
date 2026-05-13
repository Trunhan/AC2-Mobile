package com.example.ac2mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TreinoAdapter.OnTreinoListener {

    private Spinner spinnerFiltro;
    private RecyclerView recyclerTreinos;
    private TextView txtTempoTotal;
    private FloatingActionButton fabAdicionar;

    private FirebaseFirestore db;
    private TreinoAdapter adapter;
    private List<Treino> listaTreinos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa Firebase e Lista
        db = FirebaseFirestore.getInstance();
        listaTreinos = new ArrayList<>();

        // Vincula os componentes da tela
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        recyclerTreinos = findViewById(R.id.recyclerTreinos);
        txtTempoTotal = findViewById(R.id.txtTempoTotal);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        // Configura a lista (RecyclerView)
        recyclerTreinos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreinoAdapter(listaTreinos, this);
        recyclerTreinos.setAdapter(adapter);

        // Ação: Botão Adicionar
        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // Ação: Filtro ao escolher uma opção no Spinner
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregarTreinos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Sempre que a tela principal reaparecer, atualiza a lista
    @Override
    protected void onStart() {
        super.onStart();
        carregarTreinos();
    }

    // Busca os dados no Firebase
    private void carregarTreinos() {
        String filtro = spinnerFiltro.getSelectedItem().toString();
        Query query = db.collection("treinos");

        // Aplica o filtro se o usuário selecionou uma atividade específica
        if (spinnerFiltro.getSelectedItemPosition() > 0) {
            query = query.whereEqualTo("tipoAtividade", filtro);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaTreinos.clear();
                int tempoTotal = 0; // Para o Extra de somar tempo

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Treino treino = document.toObject(Treino.class);
                    listaTreinos.add(treino);
                    tempoTotal += treino.getDuracao(); // Soma as durações
                }

                adapter.notifyDataSetChanged(); // Avisa a lista para se redesenhar
                txtTempoTotal.setText("Tempo total: " + tempoTotal + " min"); // Atualiza texto
            } else {
                Toast.makeText(this, "Erro ao carregar os treinos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTreinoClick(Treino treino) {
        Intent intent = new Intent(this, CadastroActivity.class);
        intent.putExtra("treino", treino); // Envia o objeto clicado para a tela de cadastro
        startActivity(intent);
    }

    // CLIQUE LONGO: Excluir
    @Override
    public void onTreinoLongClick(Treino treino) {
        // EXTRA: Caixa de confirmação antes de excluir
        new AlertDialog.Builder(this)
                .setTitle("Excluir Treino")
                .setMessage("Deseja realmente excluir o treino de " + treino.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> deletarNoFirebase(treino.getId()))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deletarNoFirebase(String idTreino) {
        db.collection("treinos").document(idTreino)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Treino excluído com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarTreinos();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao excluir.", Toast.LENGTH_SHORT).show());
    }
}