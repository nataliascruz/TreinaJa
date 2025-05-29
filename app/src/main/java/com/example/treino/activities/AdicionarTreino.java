package com.example.treino.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;

public class AdicionarTreino extends NavigationActivity {
    private static final int MAX_EXERCICIOS = 8;
    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;
    private String treinoSelecionado;
    private DataBaseHelper dbHelper;
    private final long userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);
        dbHelper = new DataBaseHelper(this);
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

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("treino",
                new String[]{"nome"},
                "tipo = ? AND usuario_id = ?",
                new String[]{treinoSelecionado, String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            editNomeTreino.setText(cursor.getString(0));
            editNomeTreino.setEnabled(false);
        }
        cursor.close();
    }

    private void configurarListeners() {
        setupBackButton(findViewById(R.id.seta_voltar_adicionar_treino));
        findViewById(R.id.person_image_adicionar_treino).setOnClickListener(v -> navigateTo(Perfil.class));
        findViewById(R.id.salvar_bt).setOnClickListener(v -> salvarTreino());
    }

    private void salvarTreino() {
        if (!validarCampos()) return;

        String nomeTreino = editNomeTreino.getText().toString().trim();
        String nomeExercicio = editNomeExercicio.getText().toString().trim();
        int series = Integer.parseInt(editNumSeries.getText().toString());
        int repeticoes = Integer.parseInt(editNumRepeticoes.getText().toString());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM exercicio e JOIN treino t ON e.treino_id = t.id " +
                        "WHERE t.tipo = ? AND t.usuario_id = ?",
                new String[]{treinoSelecionado, String.valueOf(userId)});

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count >= MAX_EXERCICIOS) {
            Toast.makeText(this, "Limite de exercícios atingido", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent result = new Intent();
        result.putExtra("TREINO_SELECIONADO", treinoSelecionado);
        result.putExtra("NOME_TREINO", nomeTreino);
        result.putExtra("NOME_EXERCICIO", nomeExercicio);
        result.putExtra("SERIES", series);
        result.putExtra("REPETICOES", repeticoes);
        setResult(RESULT_OK, result);

        new AlertDialog.Builder(this)
                .setTitle("Exercício adicionado")
                .setMessage("Adicionar mais um exercício?")
                .setPositiveButton("Sim", (d, w) -> limparCampos())
                .setNegativeButton("Não", (d, w) -> finish())
                .show();
    }

    private boolean validarCampos() {
        if (editNomeTreino.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe o nome do treino", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editNomeExercicio.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe o nome do exercício", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editNumSeries.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe o número de séries", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editNumRepeticoes.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe o número de repetições", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int series = Integer.parseInt(editNumSeries.getText().toString());
            int repeticoes = Integer.parseInt(editNumRepeticoes.getText().toString());

            if (series <= 0 || repeticoes <= 0) {
                Toast.makeText(this, "Séries e repetições devem ser maiores que zero", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores inválidos para séries/repetições", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void limparCampos() {
        editNomeExercicio.setText("");
        editNumSeries.setText("");
        editNumRepeticoes.setText("");
        editNomeExercicio.requestFocus();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}