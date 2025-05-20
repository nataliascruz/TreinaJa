package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Perfil extends NavigationActivity {
    private EditText caixaNome, idade, peso, altura, objetivo;
    private Button salvarBt, editarBt;
    private static final String PREFS_NAME = "DadosUsuarioPrefs";
    private boolean editando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        inicializarViews();
        configurarListeners();
        carregarDados();
        atualizarEstadoEdicao();
    }

    private void inicializarViews() {
        setupBackButton(findViewById(R.id.seta_image_perfil));
        caixaNome = findViewById(R.id.caixa_nome);
        idade = findViewById(R.id.idade);
        peso = findViewById(R.id.peso);
        altura = findViewById(R.id.altura);
        objetivo = findViewById(R.id.objetivo);
        salvarBt = findViewById(R.id.salvar_dados_pessoais_bt);
        editarBt = findViewById(R.id.editar_dados_pessoais_bt);
    }

    private void configurarListeners() {
        editarBt.setOnClickListener(v -> {
            editando = true;
            atualizarEstadoEdicao();
        });

        salvarBt.setOnClickListener(v -> {
            if (!editando) {
                Toast.makeText(this, "Clique em Editar Dados primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validarCampos()) {
                salvarDados();
                editando = false;
                atualizarEstadoEdicao();
                Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarEstadoEdicao() {
        boolean habilitado = editando;
        caixaNome.setEnabled(habilitado);
        idade.setEnabled(habilitado);
        peso.setEnabled(habilitado);
        altura.setEnabled(habilitado);
        objetivo.setEnabled(habilitado);
        editarBt.setEnabled(!habilitado);
    }

    private void carregarDados() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        caixaNome.setText(prefs.getString("nome", ""));
        idade.setText(prefs.getString("idade", ""));
        peso.setText(prefs.getString("peso", ""));
        altura.setText(prefs.getString("altura", ""));
        objetivo.setText(prefs.getString("objetivo", ""));
    }

    private boolean validarCampos() {
        return validarCampo(caixaNome, "Informe seu nome") &&
                validarCampo(idade, "Informe sua idade") &&
                validarCampo(peso, "Informe seu peso") &&
                validarCampo(altura, "Informe sua altura") &&
                validarCampo(objetivo, "Informe seu objetivo");
    }

    private boolean validarCampo(EditText campo, String mensagemErro) {
        if (campo.getText().toString().trim().isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void salvarDados() {
        try {
            int idadeValor = Integer.parseInt(idade.getText().toString());
            double pesoValor = Double.parseDouble(peso.getText().toString());
            double alturaValor = Double.parseDouble(altura.getText().toString());

            if (idadeValor <= 0 || idadeValor > 100 || pesoValor <= 0 || pesoValor > 300 || alturaValor <= 0 || alturaValor > 3) {
                throw new NumberFormatException();
            }

            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("nome", caixaNome.getText().toString().trim());
            editor.putString("idade", String.valueOf(idadeValor));
            editor.putString("peso", String.valueOf(pesoValor));
            editor.putString("altura", String.valueOf(alturaValor));
            editor.putString("objetivo", objetivo.getText().toString().trim());
            editor.apply();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique os valores numéricos", Toast.LENGTH_LONG).show();
            throw e;
        }
    }
}