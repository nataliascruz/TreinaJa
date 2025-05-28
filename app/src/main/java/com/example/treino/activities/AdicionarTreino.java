package com.example.treino.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;

import com.example.treino.R;

public class AdicionarTreino extends NavigationActivity {
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
        carregarDados();
        configurarListeners();
    }

    private void inicializarViews() {
        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
    }

    private void carregarDados() {
        treinoSelecionado = getIntent().getStringExtra("TREINO_SELECIONADO");
        modoEdicao = getIntent().getBooleanExtra("MODO_EDICAO", false);
        if (modoEdicao) carregarTreinoExistente();
    }

    private void carregarTreinoExistente() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        treinoAtual = prefs.getString(treinoSelecionado, "");
        if (!treinoAtual.isEmpty()) {
            exercicioCount = treinoAtual.split("\n").length - 1;
            editNomeTreino.setText(treinoAtual.split("\n")[0].replace(TREINO_PREFIX, "").trim());
            editNomeTreino.setEnabled(false);
            if (exercicioCount >= MAX_EXERCICIOS) {
                Toast.makeText(this, "Limite de 8 exercícios atingido", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void configurarListeners() {
        setupBackButton(findViewById(R.id.seta_voltar_adicionar_treino));
        findViewById(R.id.person_image_adicionar_treino).setOnClickListener(v -> navigateTo(Perfil.class));
        findViewById(R.id.salvar_bt).setOnClickListener(v -> salvarTreino());
    }

    private void salvarTreino() {
        if (!validarCampos()) return;

        String nomeTreino = editNomeTreino.getText().toString();
        String exercicio = formatarExercicio();
        treinoAtual = treinoAtual.isEmpty()
                ? TREINO_PREFIX + nomeTreino + "\n" + exercicio
                : treinoAtual + "\n" + exercicio;

        exercicioCount = treinoAtual.split("\n").length - 1;
        if (exercicioCount >= MAX_EXERCICIOS) {
            Toast.makeText(this, "Limite máximo de 8 exercícios", Toast.LENGTH_LONG).show();
            finalizar(treinoAtual);
        } else if (modoEdicao) {
            finalizar(treinoAtual);
        } else {
            mostrarDialogoContinuar();
        }
    }

    private boolean validarCampos() {
        if (editNomeTreino.getText().toString().isEmpty() ||
                editNomeExercicio.getText().toString().isEmpty() ||
                editNumSeries.getText().toString().isEmpty() ||
                editNumRepeticoes.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
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

    private void mostrarDialogoContinuar() {
        new AlertDialog.Builder(this)
                .setTitle("Exercício adicionado")
                .setMessage("Adicionar mais um exercício?")
                .setPositiveButton("Sim", (d, w) -> limparCampos())
                .setNegativeButton("Não", (d, w) -> finalizar(treinoAtual))
                .show();
    }

    private void limparCampos() {
        editNomeExercicio.setText("");
        editNumSeries.setText("");
        editNumRepeticoes.setText("");
    }

    private void finalizar(String treino) {
        Intent result = new Intent();
        result.putExtra("TREINO_SELECIONADO", treinoSelecionado);
        result.putExtra("NOVO_TREINO", treino);
        setResult(RESULT_OK, result);
        finish();
    }
}