package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MeuPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil); // Layout de MeuPerfil

        // Seta para voltar à tela de login
        ImageView setaImage = findViewById(R.id.seta_image);
        setaImage.setOnClickListener(v -> {
            // Intent para voltar à tela de login
            Intent intent = new Intent(MeuPerfil.this, FormLogin.class);
            startActivity(intent);
            finish(); // Finaliza para evitar retorno ao perfil com botão de voltar
        });

        // icone de usuario
        ImageView personImage = findViewById(R.id.person_image);
        personImage.setOnClickListener(v ->{
            //Volta a login
            Intent intent = new Intent(MeuPerfil.this, Perfil.class);
            startActivity(intent);
            finish();
        } );

        // Botão para adicionar treino (laranja)
        Button adicionarButton = findViewById(R.id.adicionar_bt);
        adicionarButton.setOnClickListener(v -> {
            // Intent para ir para a tela de Adicionar Treino
            Intent intent = new Intent(MeuPerfil.this, AdicionarTreino.class);
            startActivity(intent);
        });

        // Botão para concluir (opcional, se existir no layout)
        Button concluirButton = findViewById(R.id.concluit_bt);
        concluirButton.setOnClickListener(v -> {
            // Intent para ir para a tela de Concluído
            Intent intent = new Intent(MeuPerfil.this, Concluido.class);
            startActivity(intent);
        });
    }
}
