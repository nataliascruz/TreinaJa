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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btMeuTreino = findViewById(R.id.bt_meu_treino);

        btMeuTreino.setOnClickListener(v -> {
            Intent intent = new Intent(FormLogin.this, MeuTreino.class);
            startActivity(intent);
        });

        Button btPerfil = findViewById(R.id.bt_meu_perfil);

        btPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(FormLogin.this, Perfil.class);
            startActivity(intent);
        });
    }
}
