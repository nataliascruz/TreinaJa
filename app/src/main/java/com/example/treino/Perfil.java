package com.example.treino;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Seta para voltar à tela
        ImageView setaImage = findViewById(R.id.seta_image);
        setaImage.setOnClickListener(v -> {
            // Intent para voltar à tela de login
            Intent intent = new Intent(Perfil.this, MeuPerfil.class); // Alteração aqui
            startActivity(intent);
            finish(); // Finaliza para evitar retorno ao perfil com botão de voltar
        });
    }
}
