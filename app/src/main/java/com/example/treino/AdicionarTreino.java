package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdicionarTreino extends AppCompatActivity {

    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;
    private String treinoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        treinoSelecionado = getIntent().getStringExtra("TREINO_SELECIONADO");

        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
        ImageView setaVoltar = findViewById(R.id.seta_voltar);
        Button salvarButton = findViewById(R.id.salvar_bt);

        setaVoltar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        salvarButton.setOnClickListener(v -> salvarTreino());
    }

    private void salvarTreino (){
        if (editNomeTreino.getText().toString().isEmpty() ||
                editNomeExercicio.getText().toString().isEmpty() ||
                editNumSeries.getText().toString().isEmpty() ||
                editNumRepeticoes.getText().toString().isEmpty()) {

            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String treinoFormatado = formatarTreino(
                    editNomeTreino.getText().toString(),
                    editNomeExercicio.getText().toString(),
                    editNumSeries.getText().toString(),
                    editNumRepeticoes.getText().toString()
            );

            Intent resultIntent = new Intent();
            resultIntent.putExtra("TREINO_SELECIONADO", treinoSelecionado);
            resultIntent.putExtra("TREINO_DADOS", treinoFormatado);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar treino", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatarTreino(String nome, String exercicio, String series, String repeticoes) {
        return String.format(
                "Treino: %s\nExercício: %s\nSéries: %s\nRepetições: %s",
                nome, exercicio, series, repeticoes
        );
    }
}
