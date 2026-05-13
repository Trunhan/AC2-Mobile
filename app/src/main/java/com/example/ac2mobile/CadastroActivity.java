package com.example.ac2mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.ac2mobile.Treino;

import java.util.Calendar;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editData, editDuracao;
    private Spinner spinnerTipo, spinnerIntensidade;
    private CheckBox checkConcluido;
    private Button btnSalvar;

    private FirebaseFirestore db;
    private Treino treinoParaEdicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = FirebaseFirestore.getInstance();
        inicializarComponentes();

        editData.setOnClickListener(v -> mostrarCalendario());

        treinoParaEdicao = (Treino) getIntent().getSerializableExtra("treinoSelecionado");
        if (treinoParaEdicao != null) {
            preencherCampos(treinoParaEdicao);
        }

        btnSalvar.setOnClickListener(v -> validarESalvar());
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNomeTreino);
        editData = findViewById(R.id.editDataTreino);
        editDuracao = findViewById(R.id.editDuracao);
        spinnerTipo = findViewById(R.id.spinnerTipoAtividade);
        spinnerIntensidade = findViewById(R.id.spinnerIntensidade);
        checkConcluido = findViewById(R.id.checkConcluido);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void mostrarCalendario() {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String dataFormatada = String.format("%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year);
                    editData.setText(dataFormatada);
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void validarESalvar() {
        String nome = editNome.getText().toString().trim();
        String tipo = spinnerTipo.getSelectedItem().toString();
        String data = editData.getText().toString().trim();
        String duracaoStr = editDuracao.getText().toString().trim();
        String intensidade = spinnerIntensidade.getSelectedItem().toString();
        boolean concluido = checkConcluido.isChecked();

        if (nome.isEmpty()) {
            editNome.setError("O nome do treino não pode estar vazio");
            return;
        }
        if (spinnerTipo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione o tipo de atividade", Toast.LENGTH_SHORT).show();
            return;
        }
        if (data.isEmpty()) {
            editData.setError("A data não pode estar vazia");
            return;
        }
        if (duracaoStr.isEmpty()) {
            editDuracao.setError("A duração não pode estar vazia");
            return;
        }
        if (spinnerIntensidade.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione a intensidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int duracao = Integer.parseInt(duracaoStr);

        // Criar objeto Treino
        Treino t = (treinoParaEdicao == null) ? new Treino() : treinoParaEdicao;
        t.setNome(nome);
        t.setTipoAtividade(tipo);
        t.setData(data);
        t.setDuracao(duracao);
        t.setIntensidade(intensidade);
        t.setConcluido(concluido);

        salvarNoFirebase(t);
    }

    private void salvarNoFirebase(Treino t) {
        if (t.getId() == null) {
            // Novo cadastro
            db.collection("treinos")
                    .add(t)
                    .addOnSuccessListener(documentReference -> {
                        t.setId(documentReference.getId()); // Pega o ID gerado
                        db.collection("treinos").document(t.getId()).set(t); // Salva o ID dentro do objeto
                        Toast.makeText(this, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show());
        } else {
            // Edição
            db.collection("treinos").document(t.getId())
                    .set(t)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Treino atualizado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private void preencherCampos(Treino t) {
        editNome.setText(t.getNome());
        editData.setText(t.getData());
        editDuracao.setText(String.valueOf(t.getDuracao()));
        checkConcluido.setChecked(t.isConcluido());

        ArrayAdapter<CharSequence> adapterTipo = (ArrayAdapter<CharSequence>) spinnerTipo.getAdapter();
        spinnerTipo.setSelection(adapterTipo.getPosition(t.getTipoAtividade()));

        ArrayAdapter<CharSequence> adapterInt = (ArrayAdapter<CharSequence>) spinnerIntensidade.getAdapter();
        spinnerIntensidade.setSelection(adapterInt.getPosition(t.getIntensidade()));

        btnSalvar.setText("Atualizar Treino");
    }
}
