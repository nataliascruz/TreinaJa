package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdicionarTreino extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        ImageView setaVoltar = findViewById(R.id.seta_voltar);
        ImageView personImage = findViewById(R.id.person_image);
        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
        Button salvarButton = findViewById(R.id.salvar_bt);
        Button meuPerfilButton = findViewById(R.id.bt_meu_perfil_bt);

        // Configura listeners
        setaVoltar.setOnClickListener(v -> voltarParaPerfil());

        personImage.setOnClickListener(v -> {
            Intent intent = new Intent(AdicionarTreino.this, Perfil.class);
            startActivity(intent);
        });

        salvarButton.setOnClickListener(v -> salvarTreino());

        meuPerfilButton.setOnClickListener(v -> voltarParaPerfil());
    }

    private void salvarTreino() {
        String nomeTreino = editNomeTreino.getText().toString().trim();
        String nomeExercicio = editNomeExercicio.getText().toString().trim();
        String numSeries = editNumSeries.getText().toString().trim();
        String numRepeticoes = editNumRepeticoes.getText().toString().trim();

        if (nomeTreino.isEmpty() || nomeExercicio.isEmpty() ||
                numSeries.isEmpty() || numRepeticoes.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int series = Integer.parseInt(numSeries);
            int repeticoes = Integer.parseInt(numRepeticoes);

            if (series <= 0 || repeticoes <= 0) {
                Toast.makeText(this, "Valores devem ser maiores que zero!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("TREINO_ATIVO", true);
            editor.putString("NOME_TREINO", nomeTreino);
            editor.putString("EXERCICIO", nomeExercicio);
            editor.putInt("SERIES", series);
            editor.putInt("REPETICOES", repeticoes);
            editor.apply();

            Toast.makeText(this, "Treino salvo com sucesso!", Toast.LENGTH_SHORT).show();
            limparCampos();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Digite valores numéricos válidos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void voltarParaPerfil() {
        startActivity(new Intent(this, MeuPerfil.class));
        finish();
    }

    private void limparCampos() {
        editNomeTreino.setText("");
        editNomeExercicio.setText("");
        editNumSeries.setText("");
        editNumRepeticoes.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Garante que os campos estejam limpos ao retornar para esta tela
        limparCampos();
    }
}