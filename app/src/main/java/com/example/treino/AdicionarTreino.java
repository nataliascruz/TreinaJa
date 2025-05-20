package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdicionarTreino extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino); // Certifique-se de que esse é o layout correto

        // Encontrar a ImageView da seta de voltar
        ImageView setaVoltar = findViewById(R.id.seta_voltar);

        // Definir ação da seta para voltar à tela de MeuPerfil
        setaVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(AdicionarTreino.this, MeuPerfil.class);
            startActivity(intent);
            finish(); // Fecha a tela atual
        });

        // Encontrar o botão que leva para "Meu Treino"
        Button meuTreinoButton = findViewById(R.id.bt_meu_perfil_bt);

        // Configurar o comportamento de clique no botão
        meuTreinoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdicionarTreino.this, MeuPerfil.class);
            startActivity(intent);
        });
    }
}
