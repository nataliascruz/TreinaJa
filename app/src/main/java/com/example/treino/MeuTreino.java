package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import java.util.Arrays;

public class MeuTreino extends NavigationActivity {
    private TextView caixaDeTexto;
    private Button concluirBt;
    private SharedPreferences sharedPreferences;
    private String currentTreino = "treino_a";
    private ActivityResultLauncher<Intent> adicionarLauncher;
    private ActivityResultLauncher<Intent> editarLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_treino);

        inicializarComponentes();
        registrarLaunchers();
        configurarListeners();
        carregarTreino(currentTreino);
    }

    private void inicializarComponentes() {
        caixaDeTexto = findViewById(R.id.caixa_de_texto);
        concluirBt = findViewById(R.id.concluir_bt);
        sharedPreferences = getSharedPreferences("MeusTreinosPrefs", MODE_PRIVATE);
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
        findViewById(R.id.treinoA).setOnClickListener(v -> switchTreino("treino_a"));
        findViewById(R.id.treinoB).setOnClickListener(v -> switchTreino("treino_b"));
        findViewById(R.id.treinoC).setOnClickListener(v -> switchTreino("treino_c"));

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

    private void carregarTreino(String treino) {
        String conteudo = sharedPreferences.getString(treino, "");
        caixaDeTexto.setText(conteudo);
        atualizarUI(!conteudo.isEmpty());
    }

    private void adicionarExercicio() {
        String treino = sharedPreferences.getString(currentTreino, "");
        if (treino.split("\n").length - 1 >= 8) {
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
        sharedPreferences.edit().remove(currentTreino).apply();
        caixaDeTexto.setText("");
        atualizarUI(false);
        Toast.makeText(this, "Treino removido", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoEdicao() {
        String[] partes = caixaDeTexto.getText().toString().split("\n");
        if (partes.length <= 1) {
            Toast.makeText(this, "Nenhum exercício para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Editar exercício")
                .setItems(gerarOpcoes(partes), (d, w) ->
                        abrirEdicao(currentTreino, partes[w + 1], w + 1))
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
        String[] partes = caixaDeTexto.getText().toString().split("\n");
        StringBuilder novoTreino = new StringBuilder(partes[0]);

        for (int i = 1; i < partes.length; i++) {
            if (i != index) novoTreino.append("\n").append(partes[i]);
        }

        sharedPreferences.edit().putString(currentTreino, novoTreino.toString()).apply();
        caixaDeTexto.setText(novoTreino.toString());
        atualizarUI(!novoTreino.toString().isEmpty());
        Toast.makeText(this, "Exercício removido", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Erro ao editar", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAdicionarResult(Intent data) {
        String treino = data.getStringExtra("TREINO_SELECIONADO");
        String conteudo = data.getStringExtra("NOVO_TREINO");
        sharedPreferences.edit().putString(treino, conteudo).apply();
        caixaDeTexto.setText(conteudo);
        atualizarUI(true);
    }

    private void handleEditarResult(Intent data) {
        String treinoKey = data.getStringExtra("TREINO_KEY");
        int index = data.getIntExtra("EXERCICIO_INDEX", 0);
        String exercicio = data.getStringExtra("EXERCICIO_EDITADO");

        String[] partes = sharedPreferences.getString(treinoKey, "").split("\n");
        if (partes.length > 1 && index >= 1 && index < partes.length) {
            partes[index] = exercicio;
            String novoConteudo = TextUtils.join("\n", partes);
            sharedPreferences.edit().putString(treinoKey, novoConteudo).apply();
            caixaDeTexto.setText(novoConteudo);
            atualizarUI(true);
        } else {
            Toast.makeText(this, "Índice inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarUI(boolean habilitado) {
        boolean temConteudo = !caixaDeTexto.getText().toString().trim().isEmpty();

        caixaDeTexto.setEnabled(habilitado);
        float alpha = habilitado ? 1f : 0.5f;

        findViewById(R.id.editar_bt).setEnabled(habilitado);
        findViewById(R.id.editar_bt).setAlpha(alpha);
        findViewById(R.id.excluir).setEnabled(habilitado);
        findViewById(R.id.excluir).setAlpha(alpha);
        findViewById(R.id.remover_exercicio).setEnabled(habilitado);
        findViewById(R.id.remover_exercicio).setAlpha(alpha);

        concluirBt.setEnabled(habilitado && temConteudo);
        concluirBt.setAlpha(habilitado && temConteudo ? 1f : 0.5f);
    }
}