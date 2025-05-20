package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MeuTreino extends AppCompatActivity {

    private TextView caixaDeTexto;
    private Button editarBt, treinoA, treinoB, treinoC, excluir, adicionarBt, concluirBt;
    private ImageView setaImage, personImage;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MeusTreinosPrefs";
    private static final String KEY_TREINO_A = "treino_a";
    private static final String KEY_TREINO_B = "treino_b";
    private static final String KEY_TREINO_C = "treino_c";
    private static final int REQUEST_CODE_ADICIONAR_TREINO = 1;
    private static final int REQUEST_CODE_EDITAR_EXERCICIO = 2;
    private String currentTreino = KEY_TREINO_A;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_treino);

        initViews();
        initSharedPreferences();
        setupListeners();
        configurarTextWatcher();
        carregarTreino(currentTreino);
    }

    private void initViews() {
        caixaDeTexto = findViewById(R.id.caixa_de_texto);
        editarBt = findViewById(R.id.editar_bt);
        treinoA = findViewById(R.id.treinoA);
        treinoB = findViewById(R.id.treinoB);
        treinoC = findViewById(R.id.treinoC);
        excluir = findViewById(R.id.excluir);
        adicionarBt = findViewById(R.id.adicionar_bt);
        concluirBt = findViewById(R.id.concluir_bt);
        setaImage = findViewById(R.id.seta_image);
        personImage = findViewById(R.id.person_image);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void setupListeners() {
        treinoA.setOnClickListener(v -> {
            currentTreino = KEY_TREINO_A;
            carregarTreino(currentTreino);
        });

        treinoB.setOnClickListener(v -> {
            currentTreino = KEY_TREINO_B;
            carregarTreino(currentTreino);
        });

        treinoC.setOnClickListener(v -> {
            currentTreino = KEY_TREINO_C;
            carregarTreino(currentTreino);
        });

        adicionarBt.setOnClickListener(v -> {
            String treinoSalvo = sharedPreferences.getString(currentTreino, "");
            int count = treinoSalvo.isEmpty() ? 0 : treinoSalvo.split("\n").length - 1;

            if (count >= 8) {
                Toast.makeText(this, "Este treino já tem o máximo de 8 exercícios", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MeuTreino.this, AdicionarTreino.class);
                intent.putExtra("TREINO_SELECIONADO", currentTreino);
                startActivityForResult(intent, REQUEST_CODE_ADICIONAR_TREINO);
            }
        });

        editarBt.setOnClickListener(v -> {
            if (!caixaDeTexto.getText().toString().isEmpty()) {
                mostrarDialogoEdicao();
            }
        });

        excluir.setOnClickListener(v -> {
            if (!caixaDeTexto.getText().toString().isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmar exclusão")
                        .setMessage("Tem certeza que deseja excluir este treino?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            sharedPreferences.edit().remove(currentTreino).apply();
                            caixaDeTexto.setText("");
                            atualizarEstadoBotaoEditar();
                            Toast.makeText(this, "Treino removido", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        setaImage.setOnClickListener(v -> navegarPara(FormLogin.class));
        personImage.setOnClickListener(v -> navegarPara(Perfil.class));
        concluirBt.setOnClickListener(v -> navegarPara(Concluido.class));
    }

    private void configurarTextWatcher() {
        caixaDeTexto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarEstadoBotaoEditar();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void carregarTreino(String treinoKey) {
        String treinoSalvo = sharedPreferences.getString(treinoKey, "");
        caixaDeTexto.setText(treinoSalvo);
        atualizarEstadoBotaoEditar();
    }

    private void atualizarEstadoBotaoEditar() {
        boolean temConteudo = !caixaDeTexto.getText().toString().trim().isEmpty();
        editarBt.setEnabled(temConteudo);
        editarBt.setAlpha(temConteudo ? 1f : 0.5f);
    }

    private void navegarPara(Class<?> destino) {
        startActivity(new Intent(this, destino));
        finish();
    }

    private void mostrarDialogoEdicao() {
        String treinoCompleto = caixaDeTexto.getText().toString();
        String[] partes = treinoCompleto.split("\n");

        if (partes.length <= 1) {
            Toast.makeText(this, "Nenhum exercício para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nomeTreino = partes[0].replace("Treino: ", "");
        String[] exercicios = Arrays.copyOfRange(partes, 1, partes.length);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar exercício de " + nomeTreino);

        String[] opcoes = new String[exercicios.length];
        for (int i = 0; i < exercicios.length; i++) {
            opcoes[i] = "Exercício " + (i + 1) + ": " + exercicios[i];
        }

        builder.setItems(opcoes, (dialog, which) -> {
            abrirTelaEdicaoExercicio(currentTreino, exercicios[which], which + 1);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void abrirTelaEdicaoExercicio(String treinoKey, String exercicio, int indice) {
        try {
            String[] partes = exercicio.split(" – ");
            String nomeExercicio = partes[0].replace("• ", "").trim();
            String[] seriesReps = partes[1].split("x");
            String series = seriesReps[0];
            String repeticoes = seriesReps[1];

            Intent intent = new Intent(this, EditarExercicio.class);
            intent.putExtra("TREINO_KEY", treinoKey);
            intent.putExtra("EXERCICIO_INDEX", indice);
            intent.putExtra("NOME_EXERCICIO", nomeExercicio);
            intent.putExtra("SERIES", series);
            intent.putExtra("REPETICOES", repeticoes);
            startActivityForResult(intent, REQUEST_CODE_EDITAR_EXERCICIO);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao editar exercício: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADICIONAR_TREINO && resultCode == RESULT_OK && data != null) {
            String treinoKey = data.getStringExtra("TREINO_SELECIONADO");
            String novoTreino = data.getStringExtra("NOVO_TREINO");

            sharedPreferences.edit().putString(treinoKey, novoTreino).apply();
            caixaDeTexto.setText(novoTreino);
            atualizarEstadoBotaoEditar();
        } else if (requestCode == REQUEST_CODE_EDITAR_EXERCICIO && resultCode == RESULT_OK && data != null) {
            String treinoKey = data.getStringExtra("TREINO_KEY");
            int exercicioIndex = data.getIntExtra("EXERCICIO_INDEX", 0);
            String exercicioEditado = data.getStringExtra("EXERCICIO_EDITADO");

            String treinoAtual = sharedPreferences.getString(treinoKey, "");
            String[] partes = treinoAtual.split("\n");

            if (partes.length > 1 && exercicioIndex >= 1 && exercicioIndex < partes.length) {
                partes[exercicioIndex] = exercicioEditado;
                String treinoEditado = TextUtils.join("\n", partes);

                sharedPreferences.edit().putString(treinoKey, treinoEditado).apply();
                caixaDeTexto.setText(treinoEditado);
            } else {
                Toast.makeText(this, "Índice do exercício inválido", Toast.LENGTH_SHORT).show();
            }
        }
    }
}