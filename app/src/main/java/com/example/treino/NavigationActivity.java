package com.example.treino;

import android.content.Intent;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {
    protected static final String EXTRA_ORIGEM = "origem_activity";

    protected void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.putExtra(EXTRA_ORIGEM, this.getClass().getName());
        startActivity(intent);
    }

    protected void setupBackButton(ImageView backButton) {
        backButton.setOnClickListener(v -> {
            String origem = getIntent().getStringExtra(EXTRA_ORIGEM);
            try {
                if (origem != null) {
                    Class<?> origemClass = Class.forName(origem);
                    Intent intent = new Intent(this, origemClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    finish();
                }
            } catch (ClassNotFoundException e) {
                finish();
            }
        });
    }
}