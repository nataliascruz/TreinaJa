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
    private static final int MAX_EXERCICIOS = 8;
    private static final String PREFS_NAME = "MeusTreinosPrefs";
    private static final String TREINO_PREFIX = "Treino: ";
    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;
    private String treinoSelecionado;
    private boolean modoEdicao = false;
    private String treinoAtual = "";
    private int exercicioCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        inicializarViews();
        carregarDadosIniciais();
        configurarListeners();
    }

    private void inicializarViews() {
        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
    }

    private void carregarDadosIniciais() {
        treinoSelecionado = getIntent().getStringExtra("TREINO_SELECIONADO");
        modoEdicao = getIntent().getBooleanExtra("MODO_EDICAO", false);

        if (modoEdicao) {
            carregarTreinoExistente();
        }
    }

    private void carregarTreinoExistente() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        treinoAtual = sharedPreferences.getString(treinoSelecionado, "");

        if (!treinoAtual.isEmpty()) {
            exercicioCount = treinoAtual.split("\n").length - 1;
            String[] linhas = treinoAtual.split("\n");
            String nomeTreino = linhas[0].replace(TREINO_PREFIX, "").trim();

            editNomeTreino.setText(nomeTreino);
            editNomeTreino.setEnabled(false);

            verificarLimiteExercicios();
        }
    }

    private void verificarLimiteExercicios() {
        if (exercicioCount >= MAX_EXERCICIOS) {
            Toast.makeText(this, "Este treino já atingiu o limite de 8 exercícios", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarListeners() {
        ImageView setaVoltar = findViewById(R.id.seta_voltar);
        Button salvarButton = findViewById(R.id.salvar_bt);

        setaVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        salvarButton.setOnClickListener(v -> salvarTreino());
    }

    private void salvarTreino() {
        if (!validarCampos()) return;

        try {
            String nomeTreino = editNomeTreino.getText().toString();
            String exercicioFormatado = formatarExercicio();

            atualizarTreinoAtual(nomeTreino, exercicioFormatado);
            exercicioCount = treinoAtual.split("\n").length - 1;

            if (exercicioCount >= MAX_EXERCICIOS) {
                Toast.makeText(this, "Você atingiu o limite máximo de 8 exercícios", Toast.LENGTH_LONG).show();
                finalizarAdicaoTreino(treinoAtual);
                return;
            }

            decidirProximoPasso();
        } catch (Exception e) {
            mostrarErro("Erro ao salvar treino: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (editNomeTreino.getText().toString().isEmpty() ||
                editNomeExercicio.getText().toString().isEmpty() ||
                editNumSeries.getText().toString().isEmpty() ||
                editNumRepeticoes.getText().toString().isEmpty()) {

            mostrarErro("Preencha todos os campos!");
            return false;
        }
        return true;
    }

    private String formatarExercicio() {
        return String.format("• %s – %sx%s",
                editNomeExercicio.getText().toString(),
                editNumSeries.getText().toString(),
                editNumRepeticoes.getText().toString());
    }

    private void atualizarTreinoAtual(String nomeTreino, String exercicioFormatado) {
        if (modoEdicao) {
            if (treinoAtual.isEmpty()) {
                treinoAtual = TREINO_PREFIX + nomeTreino;
            }
            treinoAtual += "\n" + exercicioFormatado;
        } else {
            treinoAtual = treinoAtual.isEmpty()
                    ? TREINO_PREFIX + nomeTreino + "\n" + exercicioFormatado
                    : treinoAtual + "\n" + exercicioFormatado;
        }
    }

    private void decidirProximoPasso() {
        if (!modoEdicao) {
            mostrarDialogoContinuar();
        } else {
            finalizarAdicaoTreino(treinoAtual);
        }
    }

    private void mostrarDialogoContinuar() {
        new AlertDialog.Builder(this)
                .setTitle("Exercício adicionado")
                .setMessage("Deseja adicionar mais um exercício?")
                .setPositiveButton("Sim", (dialog, which) -> limparCamposExercicio())
                .setNegativeButton("Não", (dialog, which) -> finalizarAdicaoTreino(treinoAtual))
                .setCancelable(false)
                .show();
    }

    private void limparCamposExercicio() {
        editNomeExercicio.setText("");
        editNumSeries.setText("");
        editNumRepeticoes.setText("");
        editNomeExercicio.requestFocus();
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void finalizarAdicaoTreino(String treinoCompleto) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("TREINO_SELECIONADO", treinoSelecionado);
        resultIntent.putExtra("NOVO_TREINO", treinoCompleto);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}