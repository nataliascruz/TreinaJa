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
    private long exercicioId = -1;

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
        if (intent == null || !intent.hasExtra("EXERCICIO_ID")) {
            Toast.makeText(this, "Dados do exercício não encontrados", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        exercicioId = intent.getLongExtra("EXERCICIO_ID", -1);
        if (exercicioId == -1) {
            Toast.makeText(this, "ID do exercício inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query("exercicio",
                    new String[]{"nome", "series", "repeticoes"},
                    "id = ?",
                    new String[]{String.valueOf(exercicioId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                editNomeExercicio.setText(cursor.getString(0));
                editSeries.setText(String.valueOf(cursor.getInt(1)));
                editRepeticoes.setText(String.valueOf(cursor.getInt(2)));
            } else {
                Toast.makeText(this, "Exercício não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar exercício", Toast.LENGTH_SHORT).show();
            Log.e("EditarExercicio", "Erro ao carregar dados", e);
            finish();
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
        if (exercicioId == -1) {
            Toast.makeText(this, "ID do exercício inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("nome", editNomeExercicio.getText().toString().trim());
            values.put("series", Integer.parseInt(editSeries.getText().toString()));
            values.put("repeticoes", Integer.parseInt(editRepeticoes.getText().toString()));

            int rowsAffected = db.update("exercicio", values, "id = ?", new String[]{String.valueOf(exercicioId)});
            db.setTransactionSuccessful();

            if (rowsAffected > 0) {
                Toast.makeText(this, "Exercício atualizado com sucesso", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Falha ao atualizar exercício", Toast.LENGTH_SHORT).show();
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