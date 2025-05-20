package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Perfil extends AppCompatActivity {
    private ImageView setaImagePerfil;
    private EditText caixaNome, idade, peso, altura, objetivo;
    private Button salvarDadosPessoaisBt;
    private static final String PREFS_NAME = "DadosUsuarioPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        configurarInsets();
        inicializarComponentes();
        configurarListeners();
        carregarDadosSalvos();
    }

    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void inicializarComponentes() {
        setaImagePerfil = findViewById(R.id.seta_image_perfil);
        caixaNome = findViewById(R.id.caixa_nome);
        idade = findViewById(R.id.idade);
        peso = findViewById(R.id.peso);
        altura = findViewById(R.id.altura);
        objetivo = findViewById(R.id.objetivo);
        salvarDadosPessoaisBt = findViewById(R.id.salvar_dados_pessoais_bt);
    }

    private void configurarListeners() {
        setaImagePerfil.setOnClickListener(v -> abrirTela(FormLogin.class));

        salvarDadosPessoaisBt.setOnClickListener(v -> {
            if (validarCampos()) {
                try {
                    salvarDados();
                    Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                    abrirTela(FormLogin.class);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Erro: Verifique os valores numéricos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void carregarDadosSalvos() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        caixaNome.setText(prefs.getString("nome", ""));
        idade.setText(prefs.getString("idade", ""));
        peso.setText(prefs.getString("peso", ""));
        altura.setText(prefs.getString("altura", ""));
        objetivo.setText(prefs.getString("objetivo", ""));
    }

    private boolean validarCampos() {
        if (campoVazio(caixaNome, "Informe seu nome")) return false;
        if (campoVazio(idade, "Informe sua idade")) return false;
        if (campoVazio(peso, "Informe seu peso")) return false;
        if (campoVazio(altura, "Informe sua altura")) return false;
        if (campoVazio(objetivo, "Informe seu objetivo")) return false;
        return true;
    }

    private boolean campoVazio(EditText campo, String mensagem) {
        if (campo.getText().toString().trim().isEmpty()) {
            campo.setError(mensagem);
            campo.requestFocus();
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void salvarDados() throws NumberFormatException {
        int idadeValor = Integer.parseInt(idade.getText().toString());
        double pesoValor = Double.parseDouble(peso.getText().toString());
        double alturaValor = Double.parseDouble(altura.getText().toString());

        if (idadeValor <= 0 || idadeValor > 100 || pesoValor <= 0 || pesoValor > 300 || alturaValor <= 0 || alturaValor > 3) {
            throw new NumberFormatException("Valores fora do intervalo");
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("nome", caixaNome.getText().toString().trim());
        editor.putString("idade", String.valueOf(idadeValor));
        editor.putString("peso", String.valueOf(pesoValor));
        editor.putString("altura", String.valueOf(alturaValor));
        editor.putString("objetivo", objetivo.getText().toString().trim());
        editor.apply();
    }

    private void abrirTela(Class<?> destino) {
        Intent intent = new Intent(this, destino);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}