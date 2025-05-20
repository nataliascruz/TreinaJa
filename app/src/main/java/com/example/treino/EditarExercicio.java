package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditarExercicio extends AppCompatActivity {

    private EditText editNomeExercicio, editSeries, editRepeticoes;
    private String treinoKey;
    private int exercicioIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_exercicio);

        inicializarViews();
        carregarDadosIntent();
        configurarListeners();
    }

    private void inicializarViews() {
        editNomeExercicio = findViewById(R.id.edit_nome_exercicio);
        editSeries = findViewById(R.id.edit_series);
        editRepeticoes = findViewById(R.id.edit_repeticoes);
        editSeries.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        editRepeticoes.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    }

    private void carregarDadosIntent() {
        Intent intent = getIntent();
        treinoKey = intent.getStringExtra("TREINO_KEY");
        exercicioIndex = intent.getIntExtra("EXERCICIO_INDEX", -1);
        editNomeExercicio.setText(intent.getStringExtra("NOME_EXERCICIO"));
        editSeries.setText(intent.getStringExtra("SERIES"));
        editRepeticoes.setText(intent.getStringExtra("REPETICOES"));
    }

    private void configurarListeners() {
        Button btnSalvar = findViewById(R.id.btn_salvar);
        Button btnCancelar = findViewById(R.id.btn_cancelar);
        ImageView setaVoltar = findViewById(R.id.seta_voltar);

        btnSalvar.setOnClickListener(v -> {
            if (validarCampos()) {
                salvarEdicao();
            }
        });

        btnCancelar.setOnClickListener(v -> finish());
        setaVoltar.setOnClickListener(v -> finish());
    }

    private boolean validarCampos() {
        if (editNomeExercicio.getText().toString().trim().isEmpty()) {
            mostrarErroCampo(editNomeExercicio, "Informe o nome do exercício");
            return false;
        }

        if (editSeries.getText().toString().trim().isEmpty()) {
            mostrarErroCampo(editSeries, "Informe o número de séries");
            return false;
        }

        if (editRepeticoes.getText().toString().trim().isEmpty()) {
            mostrarErroCampo(editRepeticoes, "Informe o número de repetições");
            return false;
        }

        return true;
    }

    private void mostrarErroCampo(EditText campo, String mensagem) {
        campo.setError(mensagem);
        campo.requestFocus();
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void salvarEdicao() {
        try {
            String nome = editNomeExercicio.getText().toString().trim();
            String series = editSeries.getText().toString().trim();
            String repeticoes = editRepeticoes.getText().toString().trim();
            String exercicioEditado = String.format("• %s – %sx%s", nome, series, repeticoes);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("TREINO_KEY", treinoKey);
            resultIntent.putExtra("EXERCICIO_INDEX", exercicioIndex);
            resultIntent.putExtra("EXERCICIO_EDITADO", exercicioEditado);
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}