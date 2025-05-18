package com.example.treino;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MeuPerfil extends AppCompatActivity {

    public Button concluirButton;
    private boolean temTreinoAtivo = false;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        sharedPreferences = getSharedPreferences("APP_PREFS", MODE_PRIVATE);


        Button concluirButton = findViewById(R.id.concluit_bt);
        ImageView setaImage = findViewById(R.id.seta_image);
        ImageView personImage = findViewById(R.id.person_image);
        Button adicionarButton = findViewById(R.id.adicionar_bt);

        verificarTreinoAtivo();
        atualizarEstadoBotaoConcluir();

        setaImage.setOnClickListener(v -> {
            Intent intent = new Intent(MeuPerfil.this, FormLogin.class);
            startActivity(intent);
            finish();
        });

        personImage.setOnClickListener(v ->{
            Intent intent = new Intent(MeuPerfil.this, Perfil.class);
            startActivity(intent);
            finish();
        } );

        adicionarButton.setOnClickListener(v -> {
            Intent intent = new Intent(MeuPerfil.this, AdicionarTreino.class);
            startActivity(intent);
        });
        concluirButton.setOnClickListener(v -> {
            if (temTreinoAtivo) {
                sharedPreferences.edit().putBoolean("TREINO_ATIVO", false).apply();
                Intent intent = new Intent(MeuPerfil.this, Concluido.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nenhum treino ativo para concluir", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarTreinoAtivo();
        atualizarEstadoBotaoConcluir();
    }

    private void verificarTreinoAtivo() {
        temTreinoAtivo = sharedPreferences.getBoolean("TREINO_ATIVO", false);
    }

    private void atualizarEstadoBotaoConcluir() {
        if (temTreinoAtivo) {
            concluirButton.setEnabled(true);
            concluirButton.setAlpha(1f);
        } else {
            concluirButton.setEnabled(false);
            concluirButton.setAlpha(0.5f);
        }
    }
}

        // Botão para concluir (opcional, se existir no layout)
    //Button concluirButton = findViewById(R.id.concluit_bt);
        //concluirButton.setOnClickListener(v -> {
            // Intent para ir para a tela de Concluído
        //Intent intent = new Intent(MeuPerfil.this, Concluido.class);
    // startActivity(intent);
    // });
    //}
//}
