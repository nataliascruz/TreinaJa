package com.example.treino.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;
import java.util.regex.Pattern;

public class EditarExercicio extends NavigationActivity {
    private EditText editNomeExercicio, editSeries, editRepeticoes;
    private DataBaseHelper dbHelper;
    private long exercicioId = -1;
    private String nomeOriginal, seriesOriginal, repeticoesOriginal;
    private static final Pattern CHARACTER_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]*$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_exercicio);
        dbHelper = new DataBaseHelper(this);
        inicializarViews();
        carregarDadosExercicio();
        configurarListeners();
        configurarValidacoes();
    }

    private void inicializarViews() {
        editNomeExercicio = findViewById(R.id.edit_nome_exercicio);
        editSeries = findViewById(R.id.edit_series);
        editRepeticoes = findViewById(R.id.edit_repeticoes);
    }

    private void carregarDadosExercicio() {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("EXERCICIO_ID")) {
            Toast.makeText(this, "Erro: Dados do exercício não encontrados.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        exercicioId = intent.getLongExtra("EXERCICIO_ID", -1);
        if (exercicioId == -1) {
            Toast.makeText(this, "Erro: ID do exercício inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                    new String[]{DataBaseHelper.COLUMN_EXERCICIO_NOME, DataBaseHelper.COLUMN_SERIES, DataBaseHelper.COLUMN_REPETICOES},
                    DataBaseHelper.COLUMN_EXERCICIO_ID + " = ?",
                    new String[]{String.valueOf(exercicioId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                nomeOriginal = cursor.getString(0);
                seriesOriginal = String.valueOf(cursor.getInt(1));
                repeticoesOriginal = String.valueOf(cursor.getInt(2));

                editNomeExercicio.setText(nomeOriginal);
                editSeries.setText(seriesOriginal);
                editRepeticoes.setText(repeticoesOriginal);
            } else {
                Toast.makeText(this, "Exercício não encontrado.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao carregar exercício.", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void configurarListeners() {
        setupBackButton(findViewById(R.id.seta_voltar_edit));
        findViewById(R.id.person_image).setOnClickListener(v -> navigateTo(Perfil.class));
        findViewById(R.id.btn_cancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btn_salvar).setOnClickListener(v -> {
            if (validarCampos()) {
                salvarEdicaoNoBanco();
            }
        });
    }

    private void configurarValidacoes() {
        editSeries.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("\\d*")) {
                    editSeries.setText(s.toString().replaceAll("[^\\d]", ""));
                }
            }
        });

        editRepeticoes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("\\d*")) {
                    editRepeticoes.setText(s.toString().replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    private boolean validarCampos() {
        String nomeExercicio = editNomeExercicio.getText().toString().trim();
        String series = editSeries.getText().toString().trim();
        String repeticoes = editRepeticoes.getText().toString().trim();

        if (nomeExercicio.isEmpty()) {
            mostrarErro("Informe o nome do exercício");
            return false;
        }
        if (!CHARACTER_PATTERN.matcher(nomeExercicio).matches()) {
            mostrarErro("Nome do exercício não pode conter caracteres especiais");
            return false;
        }

        if (series.isEmpty()) {
            mostrarErro("Informe o número de séries");
            return false;
        }
        if (repeticoes.isEmpty()) {
            mostrarErro("Informe o número de repetições");
            return false;
        }

        try {
            int numSeries = Integer.parseInt(series);
            int numRepeticoes = Integer.parseInt(repeticoes);
            if (numSeries <= 0 || numRepeticoes <= 0) {
                mostrarErro("Séries e repetições devem ser maiores que zero");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Valores inválidos para séries ou repetições");
            return false;
        }

        if (nomeExercicio.equals(nomeOriginal) &&
                series.equals(seriesOriginal) &&
                repeticoes.equals(repeticoesOriginal)) {
            mostrarErro("Nenhuma alteração foi feita");
            return false;
        }

        return true;
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void salvarEdicaoNoBanco() {
        String novoNome = capitalizarCadaPalavra(editNomeExercicio.getText().toString().trim());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DataBaseHelper.COLUMN_EXERCICIO_NOME, novoNome);
            values.put(DataBaseHelper.COLUMN_SERIES, Integer.parseInt(editSeries.getText().toString()));
            values.put(DataBaseHelper.COLUMN_REPETICOES, Integer.parseInt(editRepeticoes.getText().toString()));

            int rowsAffected = db.update(DataBaseHelper.TABLE_EXERCICIO, values,
                    DataBaseHelper.COLUMN_EXERCICIO_ID + " = ?",
                    new String[]{String.valueOf(exercicioId)});

            db.setTransactionSuccessful();

            if (rowsAffected > 0) {
                Toast.makeText(this, "Exercício atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Falha ao atualizar o exercício.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    private String capitalizarCadaPalavra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        StringBuilder resultado = new StringBuilder();
        String[] palavras = texto.split("\\s+");

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                if (resultado.length() > 0) resultado.append(" ");
                resultado.append(palavra.substring(0, 1).toUpperCase())
                        .append(palavra.substring(1).toLowerCase());
            }
        }

        return resultado.toString();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}