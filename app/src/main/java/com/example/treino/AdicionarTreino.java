package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdicionarTreino extends AppCompatActivity {

    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;
    private String treinoSelecionado;
    private boolean modoEdicao = false;
    private String treinoAtual = "";
    private int exercicioCount = 0;
    private static final int MAX_EXERCICIOS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        treinoSelecionado = getIntent().getStringExtra("TREINO_SELECIONADO");
        modoEdicao = getIntent().getBooleanExtra("MODO_EDICAO", false);

        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
        ImageView setaVoltar = findViewById(R.id.seta_voltar);
        Button salvarButton = findViewById(R.id.salvar_bt);

        if (modoEdicao) {
            SharedPreferences sharedPreferences = getSharedPreferences("MeusTreinosPrefs", MODE_PRIVATE);
            treinoAtual = sharedPreferences.getString(treinoSelecionado, "");

            if (treinoAtual != null && !treinoAtual.isEmpty()) {
                exercicioCount = treinoAtual.split("\n").length - 1;
                String[] linhas = treinoAtual.split("\n");
                String nomeTreino = linhas[0].replace("Treino: ", "").trim();
                editNomeTreino.setText(nomeTreino);
                editNomeTreino.setEnabled(false);

                if (exercicioCount >= MAX_EXERCICIOS) {
                    Toast.makeText(this, "Este treino já atingiu o limite de 8 exercícios", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        setaVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        salvarButton.setOnClickListener(v -> salvarTreino());
    }

    private void salvarTreino() {
        if (editNomeTreino.getText().toString().isEmpty() ||
                editNomeExercicio.getText().toString().isEmpty() ||
                editNumSeries.getText().toString().isEmpty() ||
                editNumRepeticoes.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String nomeTreino = editNomeTreino.getText().toString();
            String exercicioFormatado = String.format("• %s – %sx%s",
                    editNomeExercicio.getText().toString(),
                    editNumSeries.getText().toString(),
                    editNumRepeticoes.getText().toString());

            if (modoEdicao) {
                if (treinoAtual.isEmpty()) {
                    treinoAtual = "Treino: " + nomeTreino;
                }
                treinoAtual += "\n" + exercicioFormatado;
            } else {
                if (treinoAtual.isEmpty()) {
                    treinoAtual = "Treino: " + nomeTreino + "\n" + exercicioFormatado;
                } else {
                    treinoAtual += "\n" + exercicioFormatado;
                }
            }

            exercicioCount = treinoAtual.split("\n").length - 1;

            if (exercicioCount >= MAX_EXERCICIOS) {
                Toast.makeText(this, "Você atingiu o limite máximo de 8 exercícios", Toast.LENGTH_LONG).show();
                finalizarAdicaoTreino(treinoAtual);
                return;
            }

            if (!modoEdicao) {
                mostrarDialogoContinuar();
            } else {
                finalizarAdicaoTreino(treinoAtual);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar treino: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoContinuar() {
        new AlertDialog.Builder(this)
                .setTitle("Exercício adicionado")
                .setMessage("Deseja adicionar mais um exercício?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    editNomeExercicio.setText("");
                    editNumSeries.setText("");
                    editNumRepeticoes.setText("");
                    editNomeExercicio.requestFocus();
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    finalizarAdicaoTreino(treinoAtual);
                })
                .setCancelable(false)
                .show();
    }

    private void finalizarAdicaoTreino(String treinoCompleto) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("TREINO_SELECIONADO", treinoSelecionado);
        resultIntent.putExtra("NOVO_TREINO", treinoCompleto);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}