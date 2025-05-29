package com.example.treino.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;

public class MeuTreino extends NavigationActivity {
    private TextView caixaDeTexto;
    private Button concluirBt;
    private DataBaseHelper dbHelper;
    private String currentTreino = "A";
    private ActivityResultLauncher<Intent> adicionarLauncher;
    private ActivityResultLauncher<Intent> editarLauncher;
    private final long userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_treino);
        dbHelper = new DataBaseHelper(this);
        inicializarComponentes();
        registrarLaunchers();
        configurarListeners();
        carregarTreino(currentTreino);
    }

    private void inicializarComponentes() {
        caixaDeTexto = findViewById(R.id.caixa_de_texto);
        concluirBt = findViewById(R.id.concluir_bt);
    }

    private void registrarLaunchers() {
        adicionarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleAdicionarResult(result.getData());
                    }
                });

        editarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleEditarResult(result.getData());
                    }
                });
    }

    private void configurarListeners() {
        findViewById(R.id.treinoA).setOnClickListener(v -> switchTreino("A"));
        findViewById(R.id.treinoB).setOnClickListener(v -> switchTreino("B"));
        findViewById(R.id.treinoC).setOnClickListener(v -> switchTreino("C"));
        findViewById(R.id.adicionar_bt).setOnClickListener(v -> adicionarExercicio());
        findViewById(R.id.editar_bt).setOnClickListener(v -> mostrarDialogoEdicao());
        findViewById(R.id.excluir).setOnClickListener(v -> confirmarExclusao());
        concluirBt.setOnClickListener(v -> navigateTo(Concluido.class));
        findViewById(R.id.remover_exercicio).setOnClickListener(v -> mostrarDialogoRemocao());
        setupBackButton(findViewById(R.id.seta_image_meu_treino));
        findViewById(R.id.person_image_meu_treino).setOnClickListener(v -> navigateTo(Perfil.class));
    }

    private void switchTreino(String treino) {
        currentTreino = treino;
        carregarTreino(treino);
    }

    private void carregarTreino(String tipoTreino) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder conteudo = new StringBuilder();

        Cursor treinoCursor = db.query("treino",
                new String[]{"id"},
                "tipo = ? AND usuario_id = ?",
                new String[]{tipoTreino, String.valueOf(userId)},
                null, null, null);

        if (treinoCursor.moveToFirst()) {
            long treinoId = treinoCursor.getLong(0);
            Cursor exerciciosCursor = db.query("exercicio",
                    new String[]{"nome", "series", "repeticoes"},
                    "treino_id = ?",
                    new String[]{String.valueOf(treinoId)},
                    null, null, "ordem");

            if (exerciciosCursor.moveToFirst()) {
                conteudo.append("Treino: ").append(tipoTreino).append("\n");
                do {
                    conteudo.append("• ")
                            .append(exerciciosCursor.getString(0))
                            .append(" – ")
                            .append(exerciciosCursor.getInt(1))
                            .append("x")
                            .append(exerciciosCursor.getInt(2))
                            .append("\n");
                } while (exerciciosCursor.moveToNext());
            }
            exerciciosCursor.close();
        }
        treinoCursor.close();
        caixaDeTexto.setText(conteudo.toString());
        atualizarUI(!conteudo.toString().isEmpty());
    }

    private void adicionarExercicio() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM exercicio e JOIN treino t ON e.treino_id = t.id WHERE t.tipo = ? AND t.usuario_id = ?",
                new String[]{currentTreino, String.valueOf(userId)});

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count >= 8) {
            Toast.makeText(this, "Limite de 8 exercícios atingido", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, AdicionarTreino.class);
            intent.putExtra("TREINO_SELECIONADO", currentTreino);
            adicionarLauncher.launch(intent);
        }
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage("Excluir este treino?")
                .setPositiveButton("Sim", (d, w) -> excluirTreino())
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluirTreino() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("treino",
                new String[]{"id"},
                "tipo = ? AND usuario_id = ?",
                new String[]{currentTreino, String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            long treinoId = cursor.getLong(0);
            db.delete("exercicio", "treino_id = ?", new String[]{String.valueOf(treinoId)});
            db.delete("treino", "id = ?", new String[]{String.valueOf(treinoId)});
            caixaDeTexto.setText("");
            atualizarUI(false);
            Toast.makeText(this, "Treino removido", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void mostrarDialogoEdicao() {
        String[] partes = caixaDeTexto.getText().toString().split("\n");
        if (partes.length <= 1) {
            Toast.makeText(this, "Nenhum exercício para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Editar exercício")
                .setItems(gerarOpcoes(partes), (d, w) -> abrirEdicao(currentTreino, partes[w + 1], w + 1))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoRemocao() {
        String[] partes = caixaDeTexto.getText().toString().split("\n");
        if (partes.length <= 1) {
            Toast.makeText(this, "Nenhum exercício para remover", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Remover exercício")
                .setItems(gerarOpcoes(partes), (d, w) -> removerExercicio(w + 1))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String[] gerarOpcoes(String[] partes) {
        String[] opcoes = new String[partes.length - 1];
        for (int i = 1; i < partes.length; i++) {
            opcoes[i - 1] = "Exercício " + i + ": " + partes[i];
        }
        return opcoes;
    }

    private void removerExercicio(int index) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor treinoCursor = db.query("treino",
                new String[]{"id"},
                "tipo = ? AND usuario_id = ?",
                new String[]{currentTreino, String.valueOf(userId)},
                null, null, null);

        if (treinoCursor.moveToFirst()) {
            long treinoId = treinoCursor.getLong(0);
            Cursor exercicioCursor = db.query("exercicio",
                    new String[]{"id"},
                    "treino_id = ?",
                    new String[]{String.valueOf(treinoId)},
                    null, null, "ordem", (index - 1) + ",1");

            if (exercicioCursor.moveToFirst()) {
                long exercicioId = exercicioCursor.getLong(0);
                db.delete("exercicio", "id = ?", new String[]{String.valueOf(exercicioId)});
                carregarTreino(currentTreino);
                Toast.makeText(this, "Exercício removido", Toast.LENGTH_SHORT).show();
            }
            exercicioCursor.close();
        }
        treinoCursor.close();
    }

    private void abrirEdicao(String treinoKey, String exercicio, int index) {
        try {
            String[] partes = exercicio.split(" – ");
            String nome = partes[0].replace("• ", "").trim();
            String[] seriesReps = partes[1].split("x");

            Intent intent = new Intent(this, EditarExercicio.class);
            intent.putExtra("TREINO_KEY", treinoKey);
            intent.putExtra("EXERCICIO_INDEX", index);
            intent.putExtra("NOME_EXERCICIO", nome);
            intent.putExtra("SERIES", seriesReps[0]);
            intent.putExtra("REPETICOES", seriesReps[1]);
            editarLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao editar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("MeuTreino", "Erro em abrirEdicao", e);
        }
    }

    private void handleAdicionarResult(Intent data) {
        String tipoTreino = data.getStringExtra("TREINO_SELECIONADO");
        String nomeTreino = data.getStringExtra("NOME_TREINO");
        String nomeExercicio = data.getStringExtra("NOME_EXERCICIO");
        int series = data.getIntExtra("SERIES", 0);
        int repeticoes = data.getIntExtra("REPETICOES", 0);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor cursor = db.query("treino",
                    new String[]{"id"},
                    "tipo = ? AND usuario_id = ?",
                    new String[]{tipoTreino, String.valueOf(userId)},
                    null, null, null);

            long treinoId;
            if (cursor.moveToFirst()) {
                treinoId = cursor.getLong(0);
            } else {
                ContentValues treinoValues = new ContentValues();
                treinoValues.put("nome", nomeTreino);
                treinoValues.put("tipo", tipoTreino);
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
            db.setTransactionSuccessful();
            carregarTreino(tipoTreino);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao adicionar exercício", Toast.LENGTH_LONG).show();
            Log.e("MeuTreino", "Erro em handleAdicionarResult", e);
        } finally {
            db.endTransaction();
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

    private void handleEditarResult(Intent data) {
        if (data == null) return;

        String treinoKey = data.getStringExtra("TREINO_KEY");
        int index = data.getIntExtra("EXERCICIO_INDEX", -1);
        String exercicioEditado = data.getStringExtra("EXERCICIO_EDITADO");

        if (treinoKey == null || exercicioEditado == null || index == -1) {
            Toast.makeText(this, "Dados de edição inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            Cursor treinoCursor = db.query("treino",
                    new String[]{"id"},
                    "tipo = ? AND usuario_id = ?",
                    new String[]{treinoKey, String.valueOf(userId)},
                    null, null, null);

            if (!treinoCursor.moveToFirst()) {
                Toast.makeText(this, "Treino não encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            long treinoId = treinoCursor.getLong(0);
            treinoCursor.close();

            String query = "SELECT id FROM exercicio WHERE treino_id = ? ORDER BY ordem LIMIT 1 OFFSET ?";
            Cursor exercicioCursor = db.rawQuery(query,
                    new String[]{String.valueOf(treinoId), String.valueOf(index - 1)});

            if (!exercicioCursor.moveToFirst()) {
                Toast.makeText(this, "Exercício não encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            long exercicioId = exercicioCursor.getLong(0);
            exercicioCursor.close();

            String[] partes = exercicioEditado.split(" – ");
            String nome = partes[0].replace("• ", "").trim();
            String[] seriesReps = partes[1].split("x");

            ContentValues values = new ContentValues();
            values.put("nome", nome);
            values.put("series", Integer.parseInt(seriesReps[0].trim()));
            values.put("repeticoes", Integer.parseInt(seriesReps[1].trim()));

            db.update("exercicio", values, "id = ?", new String[]{String.valueOf(exercicioId)});
            db.setTransactionSuccessful();
            carregarTreino(treinoKey);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao salvar edição: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("MeuTreino", "Erro em handleEditarResult", e);
        } finally {
            db.endTransaction();
        }
    }

    private void atualizarUI(boolean habilitado) {
        boolean temConteudo = !caixaDeTexto.getText().toString().trim().isEmpty();
        float alpha = habilitado ? 1f : 0.5f;

        caixaDeTexto.setEnabled(habilitado);
        findViewById(R.id.editar_bt).setEnabled(habilitado);
        findViewById(R.id.editar_bt).setAlpha(alpha);
        findViewById(R.id.excluir).setEnabled(habilitado);
        findViewById(R.id.excluir).setAlpha(alpha);
        findViewById(R.id.remover_exercicio).setEnabled(habilitado);
        findViewById(R.id.remover_exercicio).setAlpha(alpha);
        concluirBt.setEnabled(habilitado && temConteudo);
        concluirBt.setAlpha(habilitado && temConteudo ? 1f : 0.5f);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}