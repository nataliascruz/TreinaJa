package com.example.treino.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;
import java.util.ArrayList;
import java.util.List;

public class MeuTreino extends NavigationActivity {
    private TextView caixaDeTexto;
    private Button concluirBt, editarBt, excluirBt, removerExercicioBt;
    private DataBaseHelper dbHelper;
    private String currentTreinoTipo = "";
    private ActivityResultLauncher<Intent> adicionarLauncher;
    private ActivityResultLauncher<Intent> editarLauncher;
    private long currentTreinoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_treino);
        dbHelper = new DataBaseHelper(this);
        inicializarComponentes();
        registrarLaunchers();
        configurarListeners();
        carregarTreinoAtual();
    }

    private void inicializarComponentes() {
        caixaDeTexto = findViewById(R.id.caixa_de_texto);
        concluirBt = findViewById(R.id.concluir_bt);
        editarBt = findViewById(R.id.editar_bt);
        excluirBt = findViewById(R.id.excluir);
        removerExercicioBt = findViewById(R.id.remover_exercicio);
    }

    private void registrarLaunchers() {
        adicionarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarTreinoAtual();
                    }
                });

        editarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        carregarTreinoAtual();
                    }
                });
    }

    private void configurarListeners() {
        findViewById(R.id.treinoA).setOnClickListener(v -> switchTreino("A"));
        findViewById(R.id.treinoB).setOnClickListener(v -> switchTreino("B"));
        findViewById(R.id.treinoC).setOnClickListener(v -> switchTreino("C"));
        findViewById(R.id.adicionar_bt).setOnClickListener(v -> adicionarExercicio());
        editarBt.setOnClickListener(v -> mostrarDialogoEdicao());
        excluirBt.setOnClickListener(v -> confirmarExclusaoTreino());
        concluirBt.setOnClickListener(v -> navigateTo(Concluido.class));
        removerExercicioBt.setOnClickListener(v -> mostrarDialogoRemocaoExercicio());
        setupBackButton(findViewById(R.id.seta_image_meu_treino));
        findViewById(R.id.person_image_meu_treino).setOnClickListener(v -> navigateTo(Perfil.class));
    }

    private void switchTreino(String tipoTreino) {
        currentTreinoTipo = tipoTreino;
        carregarTreinoAtual();
    }

    private void carregarTreinoAtual() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder conteudo = new StringBuilder();
        currentTreinoId = -1;
        String nomeTreino = "";

        Cursor treinoCursor = db.query(DataBaseHelper.TABLE_TREINO,
                new String[]{DataBaseHelper.COLUMN_TREINO_ID, DataBaseHelper.COLUMN_TREINO_NOME},
                DataBaseHelper.COLUMN_TIPO + " = ?",
                new String[]{currentTreinoTipo},
                null, null, null);

        if (treinoCursor.moveToFirst()) {
            currentTreinoId = treinoCursor.getLong(0);
            nomeTreino = treinoCursor.getString(1);
            conteudo.append("Treino: ").append(nomeTreino).append(" (").append(currentTreinoTipo).append(")\n\n");

            Cursor exerciciosCursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                    new String[]{DataBaseHelper.COLUMN_EXERCICIO_ID, DataBaseHelper.COLUMN_EXERCICIO_NOME, DataBaseHelper.COLUMN_SERIES, DataBaseHelper.COLUMN_REPETICOES},
                    DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?",
                    new String[]{String.valueOf(currentTreinoId)},
                    null, null, DataBaseHelper.COLUMN_ORDEM + " ASC");

            if (exerciciosCursor.moveToFirst()) {
                int index = 1;
                do {
                    conteudo.append(index++).append(". ")
                            .append(exerciciosCursor.getString(1))
                            .append(" - ")
                            .append(exerciciosCursor.getInt(2))
                            .append("x")
                            .append(exerciciosCursor.getInt(3))
                            .append("\n");
                } while (exerciciosCursor.moveToNext());
            } else {
                conteudo.append("Nenhum exercício adicionado ainda.\n");
            }
            exerciciosCursor.close();
        } else {
            conteudo.append(currentTreinoTipo).append(" ainda não iniciado.\nAdicione exercícios para começar.");
        }
        treinoCursor.close();

        caixaDeTexto.setText(conteudo.toString());
        atualizarEstadoBotoes(currentTreinoId != -1);
    }

    private void adicionarExercicio() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;
        if (currentTreinoId != -1) {
            Cursor cursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                    new String[]{"COUNT(*)"},
                    DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?",
                    new String[]{String.valueOf(currentTreinoId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        if (count >= 8) {
            Toast.makeText(this, "Limite de 8 exercícios atingido para este treino.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, AdicionarTreino.class);
            intent.putExtra("TREINO_SELECIONADO", currentTreinoTipo);
            adicionarLauncher.launch(intent);
        }
    }

    private void confirmarExclusaoTreino() {
        if (currentTreinoId == -1) {
            Toast.makeText(this, "Nenhum treino selecionado para excluir.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir todo o " + currentTreinoTipo + " e seus exercícios?")
                .setPositiveButton("Sim", (dialog, which) -> excluirTreinoCompleto())
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluirTreinoCompleto() {
        if (currentTreinoId == -1) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(DataBaseHelper.TABLE_EXERCICIO, DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?", new String[]{String.valueOf(currentTreinoId)});
            int deletedRows = db.delete(DataBaseHelper.TABLE_TREINO, DataBaseHelper.COLUMN_TREINO_ID + " = ?", new String[]{String.valueOf(currentTreinoId)});
            db.setTransactionSuccessful();

            if (deletedRows > 0) {
                Toast.makeText(this, currentTreinoTipo + " removido com sucesso.", Toast.LENGTH_SHORT).show();
                carregarTreinoAtual();
            } else {
                Toast.makeText(this, "Falha ao remover o treino.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao excluir treino: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }
    }

    private void mostrarDialogoEdicao() {
        if (currentTreinoId == -1) {
            Toast.makeText(this, "Crie ou selecione um treino antes de editar exercícios.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor exercicioCursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                new String[]{DataBaseHelper.COLUMN_EXERCICIO_ID, DataBaseHelper.COLUMN_EXERCICIO_NOME, DataBaseHelper.COLUMN_SERIES, DataBaseHelper.COLUMN_REPETICOES},
                DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?",
                new String[]{String.valueOf(currentTreinoId)},
                null, null, DataBaseHelper.COLUMN_ORDEM + " ASC");

        if (exercicioCursor.getCount() == 0) {
            Toast.makeText(this, "Nenhum exercício neste treino para editar.", Toast.LENGTH_SHORT).show();
            exercicioCursor.close();
            return;
        }

        final List<Long> idsExercicios = new ArrayList<>();
        final List<String> nomesExercicios = new ArrayList<>();
        int index = 1;
        while (exercicioCursor.moveToNext()) {
            idsExercicios.add(exercicioCursor.getLong(0));
            nomesExercicios.add(index++ + ". " + exercicioCursor.getString(1) +
                    " (" + exercicioCursor.getInt(2) + "x" + exercicioCursor.getInt(3) + ")");
        }
        exercicioCursor.close();

        new AlertDialog.Builder(this)
                .setTitle("Editar Exercício")
                .setItems(nomesExercicios.toArray(new String[0]), (dialog, which) -> {
                    abrirTelaEdicao(idsExercicios.get(which));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoRemocaoExercicio() {
        if (currentTreinoId == -1) {
            Toast.makeText(this, "Crie ou selecione um treino antes de remover exercícios.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor exercicioCursor = db.query(DataBaseHelper.TABLE_EXERCICIO,
                new String[]{DataBaseHelper.COLUMN_EXERCICIO_ID, DataBaseHelper.COLUMN_EXERCICIO_NOME, DataBaseHelper.COLUMN_SERIES, DataBaseHelper.COLUMN_REPETICOES},
                DataBaseHelper.COLUMN_TREINO_ID_FK + " = ?",
                new String[]{String.valueOf(currentTreinoId)},
                null, null, DataBaseHelper.COLUMN_ORDEM + " ASC");

        if (exercicioCursor.getCount() == 0) {
            Toast.makeText(this, "Nenhum exercício neste treino para remover.", Toast.LENGTH_SHORT).show();
            exercicioCursor.close();
            return;
        }

        final List<Long> idsExercicios = new ArrayList<>();
        final List<String> nomesExercicios = new ArrayList<>();
        int index = 1;
        while (exercicioCursor.moveToNext()) {
            idsExercicios.add(exercicioCursor.getLong(0));
            nomesExercicios.add(index++ + ". " + exercicioCursor.getString(1) +
                    " (" + exercicioCursor.getInt(2) + "x" + exercicioCursor.getInt(3) + ")");
        }
        exercicioCursor.close();

        new AlertDialog.Builder(this)
                .setTitle("Remover Exercício")
                .setItems(nomesExercicios.toArray(new String[0]), (dialog, which) -> {
                    confirmarRemocaoExercicio(idsExercicios.get(which), nomesExercicios.get(which));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarRemocaoExercicio(long exercicioId, String nomeExercicio) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Remoção")
                .setMessage("Tem certeza que deseja remover o exercício: \n" + nomeExercicio + "?")
                .setPositiveButton("Sim", (dialog, which) -> removerExercicioDoBanco(exercicioId))
                .setNegativeButton("Não", null)
                .show();
    }

    private void removerExercicioDoBanco(long exercicioId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            int deletedRows = db.delete(DataBaseHelper.TABLE_EXERCICIO, DataBaseHelper.COLUMN_EXERCICIO_ID + " = ?", new String[]{String.valueOf(exercicioId)});
            if (deletedRows > 0) {
                Toast.makeText(this, "Exercício removido com sucesso.", Toast.LENGTH_SHORT).show();
                carregarTreinoAtual();
            } else {
                Toast.makeText(this, "Falha ao remover o exercício.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao remover: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void abrirTelaEdicao(long exercicioId) {
        Intent intent = new Intent(this, EditarExercicio.class);
        intent.putExtra("EXERCICIO_ID", exercicioId);
        editarLauncher.launch(intent);
    }

    private void atualizarEstadoBotoes(boolean treinoExiste) {
        float alphaHabilitado = 1f;
        float alphaDesabilitado = 0.5f;

        editarBt.setEnabled(treinoExiste);
        editarBt.setAlpha(treinoExiste ? alphaHabilitado : alphaDesabilitado);

        excluirBt.setEnabled(treinoExiste);
        excluirBt.setAlpha(treinoExiste ? alphaHabilitado : alphaDesabilitado);

        removerExercicioBt.setEnabled(treinoExiste);
        removerExercicioBt.setAlpha(treinoExiste ? alphaHabilitado : alphaDesabilitado);

        boolean temExercicios = treinoExiste && !caixaDeTexto.getText().toString().contains("Nenhum exercício");
        concluirBt.setEnabled(temExercicios);
        concluirBt.setAlpha(temExercicios ? alphaHabilitado : alphaDesabilitado);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}