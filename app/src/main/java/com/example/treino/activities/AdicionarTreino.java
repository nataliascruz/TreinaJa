package com.example.treino.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;
import java.util.regex.Pattern;

public class AdicionarTreino extends NavigationActivity {
    private static final int MAX_EXERCICIOS = 8;
    private EditText editNomeTreino, editNomeExercicio, editNumSeries, editNumRepeticoes;
    private TextView textTipoTreino;
    private String treinoSelecionado;
    private DataBaseHelper dbHelper;
    private static final Pattern CHARACTER_PATTERN = Pattern.compile("^[a-zA-Z0-9 ]*$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);
        dbHelper = new DataBaseHelper(this);
        inicializarViews();
        carregarDadosIniciais();
        configurarListeners();
        configurarValidacoes();
    }

    private void inicializarViews() {
        editNomeTreino = findViewById(R.id.caixa_nome_treino);
        editNomeExercicio = findViewById(R.id.caixa_nome_exercicio);
        editNumSeries = findViewById(R.id.caixa_num_serie);
        editNumRepeticoes = findViewById(R.id.caixa_num_repeticoes);
        textTipoTreino = findViewById(R.id.text_tipo_treino);
    }

    private void carregarDadosIniciais() {
        treinoSelecionado = getIntent().getStringExtra("TREINO_SELECIONADO");
        if (treinoSelecionado == null) {
            Toast.makeText(this, "Erro: Tipo de treino não especificado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        textTipoTreino.setText(treinoSelecionado);
        editNomeTreino.setText("");
        editNomeTreino.setEnabled(true);
    }

    private void configurarListeners() {
        setupBackButton(findViewById(R.id.seta_voltar_adicionar_treino));
        findViewById(R.id.person_image_adicionar_treino).setOnClickListener(v -> navigateTo(Perfil.class));
        findViewById(R.id.salvar_bt).setOnClickListener(v -> salvarExercicioNoBanco());
    }

    private void configurarValidacoes() {
        editNumSeries.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("\\d*")) {
                    editNumSeries.setText(s.toString().replaceAll("[^\\d]", ""));
                }
            }
        });

        editNumRepeticoes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("\\d*")) {
                    editNumRepeticoes.setText(s.toString().replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    private void salvarExercicioNoBanco() {
        if (!validarCampos()) return;

        String nomeTreino = capitalizarPrimeiraLetra(editNomeTreino.getText().toString().trim());
        String nomeExercicio = capitalizarPrimeiraLetra(editNomeExercicio.getText().toString().trim());
        int series = Integer.parseInt(editNumSeries.getText().toString());
        int repeticoes = Integer.parseInt(editNumRepeticoes.getText().toString());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long treinoId = getOrCreateTreinoId(db, nomeTreino);

        if (treinoId == -1) {
            Toast.makeText(this, "Erro ao obter ou criar o treino.", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = getContagemExercicios(db, treinoId);
        if (count >= MAX_EXERCICIOS) {
            Toast.makeText(this, "Limite de " + MAX_EXERCICIOS + " exercícios atingido.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
            return;
        }

        db.beginTransaction();
        try {
            ContentValues exercicioValues = new ContentValues();
            exercicioValues.put(DataBaseHelper.COLUMN_EXERCICIO_NOME, nomeExercicio);
            exercicioValues.put(DataBaseHelper.COLUMN_SERIES, series);
            exercicioValues.put(DataBaseHelper.COLUMN_REPETICOES, repeticoes);
            exercicioValues.put(DataBaseHelper.COLUMN_ORDEM, count);
            exercicioValues.put(DataBaseHelper.COLUMN_TREINO_ID_FK, treinoId);

            long newRowId = db.insert(DataBaseHelper.TABLE_EXERCICIO, null, exercicioValues);
            db.setTransactionSuccessful();

            if (newRowId != -1) {
                setResult(RESULT_OK);
                if (count + 1 >= MAX_EXERCICIOS) {
                    finish();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Exercício Adicionado")
                            .setMessage("Exercício \"" + nomeExercicio + "\" adicionado com sucesso. Deseja adicionar outro?")
                            .setPositiveButton("Sim", (dialog, which) -> limparCamposExercicio())
                            .setNegativeButton("Não", (dialog, which) -> finish())
                            .show();
                }
            } else {
                Toast.makeText(this, "Erro ao salvar o exercício.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    private String capitalizarPrimeiraLetra(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    private long getOrCreateTreinoId(SQLiteDatabase db, String nomeTreino) {
        long treinoId = -1;
        try {
            Cursor cursor = db.query(DataBaseHelper.TABLE_TREINO,
                    new String[]{DataBaseHelper.COLUMN_TREINO_ID},
                    DataBaseHelper.COLUMN_TIPO + " = ?",
                    new String[]{treinoSelecionado},
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    treinoId = cursor.getLong(0);
                    ContentValues treinoValues = new ContentValues();
                    treinoValues.put(DataBaseHelper.COLUMN_TREINO_NOME, nomeTreino);
                    db.update(DataBaseHelper.TABLE_TREINO, treinoValues,
                            DataBaseHelper.COLUMN_TREINO_ID + " = ?",
                            new String[]{String.valueOf(treinoId)});
                } else {
                    ContentValues treinoValues = new ContentValues();
                    treinoValues.put(DataBaseHelper.COLUMN_TREINO_NOME, nomeTreino);
                    treinoValues.put(DataBaseHelper.COLUMN_TIPO, treinoSelecionado);
                    treinoId = db.insert(DataBaseHelper.TABLE_TREINO, null, treinoValues);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro no banco de dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return treinoId;
    }

    private int getContagemExercicios(SQLiteDatabase db, long treinoId) {
        Cursor cursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                new String[]{"COUNT(*)"},
                DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?",
                new String[]{String.valueOf(treinoId)},
                null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private boolean validarCampos() {
        String nomeTreino = editNomeTreino.getText().toString().trim();
        String nomeExercicio = editNomeExercicio.getText().toString().trim();
        String series = editNumSeries.getText().toString().trim();
        String repeticoes = editNumRepeticoes.getText().toString().trim();

        if (nomeTreino.isEmpty()) {
            mostrarErro("Informe o nome do treino");
            return false;
        }
        if (!CHARACTER_PATTERN.matcher(nomeTreino).matches()) {
            mostrarErro("Nome do treino não pode conter caracteres especiais");
            return false;
        }

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
        return true;
    }

    private void mostrarErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void limparCamposExercicio() {
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