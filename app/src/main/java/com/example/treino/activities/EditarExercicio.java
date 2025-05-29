package com.example.treino.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;

public class EditarExercicio extends NavigationActivity {
    private EditText editNomeExercicio, editSeries, editRepeticoes;
    private DataBaseHelper dbHelper;
    private String treinoKey;
    private int exercicioIndex;
    private long exercicioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_exercicio);
        dbHelper = new DataBaseHelper(this);
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
        if (intent == null) {
            Toast.makeText(this, "Intent inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        treinoKey = intent.getStringExtra("TREINO_KEY");
        exercicioIndex = intent.getIntExtra("EXERCICIO_INDEX", -1);
        String nomeExercicio = intent.getStringExtra("NOME_EXERCICIO");
        String series = intent.getStringExtra("SERIES");
        String repeticoes = intent.getStringExtra("REPETICOES");

        if (nomeExercicio != null && series != null && repeticoes != null) {
            editNomeExercicio.setText(nomeExercicio);
            editSeries.setText(series);
            editRepeticoes.setText(repeticoes);
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            Cursor treinoCursor = db.query("treino",
                    new String[]{"id"},
                    "tipo = ? AND usuario_id = ?",
                    new String[]{treinoKey, String.valueOf(1)},
                    null, null, null);

            if (!treinoCursor.moveToFirst()) {
                Toast.makeText(this, "Treino não encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            long treinoId = treinoCursor.getLong(0);
            treinoCursor.close();

            String query = "SELECT id, nome, series, repeticoes FROM exercicio WHERE treino_id = ? ORDER BY ordem LIMIT 1 OFFSET ?";
            Cursor exercicioCursor = db.rawQuery(query,
                    new String[]{String.valueOf(treinoId), String.valueOf(exercicioIndex - 1)});

            if (!exercicioCursor.moveToFirst()) {
                Toast.makeText(this, "Exercício não encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            exercicioId = exercicioCursor.getLong(0);
            editNomeExercicio.setText(exercicioCursor.getString(1));
            editSeries.setText(String.valueOf(exercicioCursor.getInt(2)));
            editRepeticoes.setText(String.valueOf(exercicioCursor.getInt(3)));
            exercicioCursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_LONG).show();
            Log.e("EditarExercicio", "Erro em carregarDados", e);
            finish();
        } finally {
            db.endTransaction();
        }
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

        try {
            int series = Integer.parseInt(editSeries.getText().toString());
            int repeticoes = Integer.parseInt(editRepeticoes.getText().toString());
            if (series <= 0 || repeticoes <= 0) {
                mostrarErro("Séries e repetições devem ser maiores que zero");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Valores inválidos para séries/repetições");
            return false;
        }
        return true;
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void salvarEdicao() {
        if (!validarCampos()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("nome", editNomeExercicio.getText().toString().trim());
            values.put("series", Integer.parseInt(editSeries.getText().toString()));
            values.put("repeticoes", Integer.parseInt(editRepeticoes.getText().toString()));

            int rowsAffected = db.update("exercicio", values, "id = ?", new String[]{String.valueOf(exercicioId)});

            if (rowsAffected > 0) {
                Intent result = new Intent();
                result.putExtra("TREINO_KEY", treinoKey);
                result.putExtra("EXERCICIO_INDEX", exercicioIndex);
                result.putExtra("EXERCICIO_EDITADO",
                        String.format("• %s – %sx%s",
                                editNomeExercicio.getText().toString().trim(),
                                editSeries.getText().toString().trim(),
                                editRepeticoes.getText().toString().trim()));
                setResult(RESULT_OK, result);
                db.setTransactionSuccessful();
                finish();
            } else {
                Toast.makeText(this, "Nenhum exercício foi atualizado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("EditarExercicio", "Erro em salvarEdicao", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}