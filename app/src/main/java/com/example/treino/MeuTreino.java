package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MeuTreino extends AppCompatActivity {

    private EditText caixaDeTexto;
    private Button editarButton, treinoA, treinoB, treinoC, excluir, adicionarButton, concluirButton;

    private ImageView setaImage, personImage;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "MeusTreinosPrefs";
    private static final String KEY_TREINO_A = "treino_a";
    private static final String KEY_TREINO_B = "treino_b";
    private static final String KEY_TREINO_C = "treino_c";
    private static final int REQUEST_CODE_ADICIONAR_TREINO = 1;
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
    private void initViews () {
        caixaDeTexto = findViewById(R.id.caixa_de_texto);
        editarButton = findViewById(R.id.editar_bt);
        treinoA = findViewById(R.id.treinoA);
        treinoB = findViewById(R.id.treinoB);
        treinoC = findViewById(R.id.treinoC);
        excluir = findViewById(R.id.excluir);
        adicionarButton = findViewById(R.id.adicionar_bt);
        concluirButton = findViewById(R.id.concluir_bt);
        setaImage = findViewById(R.id.seta_image);
        personImage = findViewById(R.id.person_image);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void setupListeners() {
        treinoA.setOnClickListener(v ->{
            currentTreino = KEY_TREINO_A;
            carregarTreino(KEY_TREINO_A);
        });

        treinoB.setOnClickListener(v ->{
            currentTreino = KEY_TREINO_B;
            carregarTreino(KEY_TREINO_B);
        });

        treinoC.setOnClickListener(v ->{
            currentTreino = KEY_TREINO_C;
            carregarTreino(KEY_TREINO_C);
        });

        adicionarButton.setOnClickListener(v -> {
            Intent intent = new Intent(MeuTreino.this, AdicionarTreino.class);
            intent.putExtra("TREINO_SELECIONADO", currentTreino);
            startActivityForResult(intent, REQUEST_CODE_ADICIONAR_TREINO);
        });

        editarButton.setOnClickListener(v -> {
            if (!caixaDeTexto.getText().toString().isEmpty()) {
                Toast.makeText(this, "Modo edição ativado", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MeuTreino.this, AdicionarTreino.class);
                intent.putExtra("TREINO_SELECIONADO", currentTreino);
                intent.putExtra("MODO_EDICAO", true);
                startActivityForResult(intent, REQUEST_CODE_ADICIONAR_TREINO);
            }
        });

        excluir.setOnClickListener(v -> {
            if (!caixaDeTexto.getText().toString().isEmpty()) {
                sharedPreferences.edit().remove(currentTreino).apply();
                caixaDeTexto.setText("");
                atualizarEstadoBotaoEditar();
                Toast.makeText(this, "Treino removido", Toast.LENGTH_SHORT).show();
            }
        });

        setaImage.setOnClickListener(v -> navegarPara(FormLogin.class));
        personImage.setOnClickListener(v -> navegarPara(Perfil.class));
        concluirButton.setOnClickListener(v -> navegarPara(Concluido.class));
    }

    private void configurarTextWatcher() {
        caixaDeTexto.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarEstadoBotaoEditar();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void carregarTreino(String treinoKey) {
        String treinoSalvo = sharedPreferences.getString(treinoKey, "");
        caixaDeTexto.setText(treinoSalvo);
        atualizarEstadoBotaoEditar();
    }

    private void atualizarEstadoBotaoEditar() {
        boolean temConteudo = !caixaDeTexto.getText().toString().trim().isEmpty();
        editarButton.setEnabled(temConteudo);
        editarButton.setAlpha(temConteudo ? 1f : 0.5f);
    }

    private void navegarPara(Class<?> destination) {
        startActivity(new Intent(this, destination));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADICIONAR_TREINO && resultCode == RESULT_OK && data != null) {
            String treinoKey = data.getStringExtra("TREINO_SELECIONADO");
            String novoTreino = data.getStringExtra("NOVO_TREINO");

            sharedPreferences.edit().putString(treinoKey, novoTreino).apply();
            caixaDeTexto.setText(novoTreino);
        }
    }

}