package com.example.treino.activities;

import android.content.ContentValues;
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

            cursor.close();
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM exercicio e " +
                            "JOIN treino t ON e.treino_id = t.id " +
                            "WHERE t.tipo = ? AND t.usuario_id = ?",
                    new String[]{treinoSelecionado, String.valueOf(userId)});

            if (cursor.moveToFirst() && cursor.getInt(0) >= MAX_EXERCICIOS) {
                Toast.makeText(this, "Limite de 8 exercícios atingido", Toast.LENGTH_LONG).show();
                finish();
            }
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("treino",
                new String[]{"id"},
                "tipo = ? AND usuario_id = ?",
                new String[]{treinoSelecionado, String.valueOf(userId)},
                null, null, null);

        long treinoId;

        if (cursor.moveToFirst()) {
            treinoId = cursor.getLong(0);
        } else {
            // Cria um novo treino
            ContentValues treinoValues = new ContentValues();
            treinoValues.put("nome", nomeTreino);
            treinoValues.put("tipo", treinoSelecionado);
            treinoValues.put("usuario_id", userId);
            treinoId = db.insert("treino", null, treinoValues);
        }
        cursor.close();

        ContentValues exercicioValues = new ContentValues();
        exercicioValues.put("nome", nomeExercicio);
        exercicioValues.put("series", series);
        exercicioValues.put("repeticoes", repeticoes);
        exercicioValues.put("ordem", getProximaOrdem(treinoId));
        exercicioValues.put("treino_id", treinoId);
        db.insert("exercicio", null, exercicioValues);

        cursor = db.rawQuery(
                "SELECT COUNT(*) FROM exercicio WHERE treino_id = ?",
                new String[]{String.valueOf(treinoId)});

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count >= MAX_EXERCICIOS) {
            finalizar(nomeTreino, nomeExercicio, series, repeticoes);
        } else {
            mostrarDialogoContinuar(nomeTreino, nomeExercicio, series, repeticoes);
        }
    }

    private int getProximaOrdem(long treinoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(ordem) FROM exercicio WHERE treino_id = ?",
                new String[]{String.valueOf(treinoId)});

        int ordem = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            ordem = cursor.getInt(0) + 1;
        }
        cursor.close();
        return ordem;
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

    private void mostrarDialogoContinuar(String nomeTreino, String nomeExercicio, int series, int repeticoes) {
        new AlertDialog.Builder(this)
                .setTitle("Exercício adicionado")
                .setMessage("Adicionar mais um exercício?")
                .setPositiveButton("Sim", (d, w) -> limparCampos())
                .setNegativeButton("Não", (d, w) -> finalizar(nomeTreino, nomeExercicio, series, repeticoes))
                .show();
    }

    private void limparCampos() {
        editNomeExercicio.setText("");
        editNumSeries.setText("");
        editNumRepeticoes.setText("");
        editNomeExercicio.requestFocus();
    }

    private void finalizar(String nomeTreino, String nomeExercicio, int series, int repeticoes) {
        Intent result = new Intent();
        result.putExtra("TREINO_SELECIONADO", treinoSelecionado);
        result.putExtra("NOME_TREINO", nomeTreino);
        result.putExtra("NOME_EXERCICIO", nomeExercicio);
        result.putExtra("SERIES", series);
        result.putExtra("REPETICOES", repeticoes);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}