package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditarExercicio extends NavigationActivity {
    private EditText editNomeExercicio, editSeries, editRepeticoes;
    private String treinoKey;
    private int exercicioIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_exercicio);

        inicializarViews();
        carregarDados();
        configurarListeners();
    }

    private void inicializarViews() {
        editNomeExercicio = findViewById(R.id.edit_nome_exercicio);
        editSeries = findViewById(R.id.edit_series);
        editRepeticoes = findViewById(R.id.edit_repeticoes);
    }

    private void carregarDados() {
        Intent intent = getIntent();
        treinoKey = intent.getStringExtra("TREINO_KEY");
        exercicioIndex = intent.getIntExtra("EXERCICIO_INDEX", -1);
        editNomeExercicio.setText(intent.getStringExtra("NOME_EXERCICIO"));
        editSeries.setText(intent.getStringExtra("SERIES"));
        editRepeticoes.setText(intent.getStringExtra("REPETICOES"));
    }

    private void configurarListeners() {
        setupBackButton(findViewById(R.id.seta_voltar_edit));
        findViewById(R.id.btn_cancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btn_salvar).setOnClickListener(v -> {
            if (validarCampos()) salvarEdicao();
        });
    }

    private boolean validarCampos() {
        if (editNomeExercicio.getText().toString().trim().isEmpty()) {
            mostrarErro("Informe o nome do exercício");
            return false;
        }
        if (editSeries.getText().toString().trim().isEmpty()) {
            mostrarErro("Informe o número de séries");
            return false;
        }
        if (editRepeticoes.getText().toString().trim().isEmpty()) {
            mostrarErro("Informe o número de repetições");
            return false;
        }
        return true;
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void salvarEdicao() {
        Intent result = new Intent();
        result.putExtra("TREINO_KEY", treinoKey);
        result.putExtra("EXERCICIO_INDEX", exercicioIndex);
        result.putExtra("EXERCICIO_EDITADO", String.format("• %s – %sx%s",
                editNomeExercicio.getText().toString().trim(),
                editSeries.getText().toString().trim(),
                editRepeticoes.getText().toString().trim()));
        setResult(RESULT_OK, result);
        finish();
    }
}