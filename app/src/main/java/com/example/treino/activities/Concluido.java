package com.example.treino.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.treino.R;

public class Concluido extends NavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concluido);

        ImageView personImageConcluido = findViewById(R.id.person_image_tela_Concluido);
        ImageView setaVoltarConcluido = findViewById(R.id.seta_voltar_Concluido);
        Button okButton = findViewById(R.id.ok_concluido);

        personImageConcluido.setOnClickListener(v -> navigateTo(Perfil.class));
        setupBackButton(setaVoltarConcluido);

        okButton.setOnClickListener(v -> {
            Intent intent = new Intent(Concluido.this, FormLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}