package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FormLogin extends AppCompatActivity {
    private Button btMeuTreino;
    private Button btPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);

        configurarInsets();
        inicializarComponentes();
        configurarListeners();
    }

    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void inicializarComponentes() {
        btMeuTreino = findViewById(R.id.bt_meu_treino);
        btPerfil = findViewById(R.id.bt_meu_perfil);
    }

    private void configurarListeners() {
        btMeuTreino.setOnClickListener(v -> startActivity(new Intent(FormLogin.this, MeuTreino.class)));
        btPerfil.setOnClickListener(v -> startActivity(new Intent(FormLogin.this, Perfil.class)));
    }
}