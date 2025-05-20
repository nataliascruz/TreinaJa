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

        editNomeExercicio = findViewById(R.id.edit_nome_exercicio);
        editSeries = findViewById(R.id.edit_series);
        editRepeticoes = findViewById(R.id.edit_repeticoes);
        Button btnSalvar = findViewById(R.id.btn_salvar);
        Button btnCancelar = findViewById(R.id.btn_cancelar);
        ImageView setaVoltar = findViewById(R.id.seta_voltar);

        treinoKey = getIntent().getStringExtra("TREINO_KEY");
        exercicioIndex = getIntent().getIntExtra("EXERCICIO_INDEX", 0);

        editNomeExercicio.setText(getIntent().getStringExtra("NOME_EXERCICIO"));
        editSeries.setText(getIntent().getStringExtra("SERIES"));
        editRepeticoes.setText(getIntent().getStringExtra("REPETICOES"));

        btnSalvar.setOnClickListener(v -> salvarEdicao());
        btnCancelar.setOnClickListener(v -> finish());
        setaVoltar.setOnClickListener(v -> finish());
    }

    private void salvarEdicao() {
        String nome = editNomeExercicio.getText().toString().trim();
        String series = editSeries.getText().toString().trim();
        String repeticoes = editRepeticoes.getText().toString().trim();

        if (nome.isEmpty() || series.isEmpty() || repeticoes.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String exercicioEditado = String.format("• %s – %sx%s", nome, series, repeticoes);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("TREINO_KEY", treinoKey);
        resultIntent.putExtra("EXERCICIO_INDEX", exercicioIndex);
        resultIntent.putExtra("EXERCICIO_EDITADO", exercicioEditado);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
