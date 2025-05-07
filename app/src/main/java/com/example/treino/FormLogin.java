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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ativa a funcionalidade de barra de status e navegação
        setContentView(R.layout.activity_form_login); // Carrega o layout da tela de login

        // Aplica os insets para corrigir o padding e garantir que os conteúdos não sejam sobrepostos pelas barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Encontrando o botão de login
        Button btLogin = findViewById(R.id.bt_meu_treino);

        // Definindo o que acontece ao clicar no botão
        btLogin.setOnClickListener(v -> {
            // Inicia a nova Activity (Tela MeuPerfil)
            Intent intent = new Intent(FormLogin.this, MeuPerfil.class); // Alterar para a sua classe de perfil
            startActivity(intent); // Lança a nova Activity
        });

        Button btPerfil = findViewById(R.id.bt_meu_perfil);

        btPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(FormLogin.this, Perfil.class);
            startActivity(intent);
        });
    }
}
