package com.example.treino.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;

public class Perfil extends NavigationActivity {
    private EditText caixaNome, idade, peso, altura, objetivo;
    private Button salvarBt, editarBt;
    private boolean editando = false;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = new DataBaseHelper(this);
        inicializarViews();
        configurarListeners();
        carregarDados();
        atualizarEstadoEdicao();
    }

    private void inicializarViews() {
        setupBackButton(findViewById(R.id.seta_image_perfil));
        caixaNome = findViewById(R.id.caixa_nome);
        idade = findViewById(R.id.idade);
        peso = findViewById(R.id.peso);
        altura = findViewById(R.id.altura);
        objetivo = findViewById(R.id.objetivo);
        salvarBt = findViewById(R.id.salvar_dados_pessoais_bt);
        editarBt = findViewById(R.id.editar_dados_pessoais_bt);
    }

    private void configurarListeners() {
        editarBt.setOnClickListener(v -> {
            editando = true;
            atualizarEstadoEdicao();
        });

        salvarBt.setOnClickListener(v -> {
            if (!editando) {
                Toast.makeText(this, "Clique em Editar Dados primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validarCampos()) {
                salvarDados();
                editando = false;
                atualizarEstadoEdicao();
                Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarEstadoEdicao() {
        boolean habilitado = editando;
        caixaNome.setEnabled(habilitado);
        idade.setEnabled(habilitado);
        peso.setEnabled(habilitado);
        altura.setEnabled(habilitado);
        objetivo.setEnabled(habilitado);
        editarBt.setEnabled(!habilitado);
    }

    private void carregarDados() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "usuario",
                new String[]{"nome", "idade", "peso", "altura", "objetivo"},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            caixaNome.setText(cursor.getString(0));
            idade.setText(String.valueOf(cursor.getInt(1)));
            peso.setText(String.valueOf(cursor.getDouble(2)));
            altura.setText(String.valueOf(cursor.getDouble(3)));
            objetivo.setText(cursor.getString(4));
        }
        cursor.close();
    }

    private boolean validarCampos() {
        return validarCampo(caixaNome, "Informe seu nome") &&
                validarCampo(idade, "Informe sua idade") &&
                validarCampo(peso, "Informe seu peso") &&
                validarCampo(altura, "Informe sua altura") &&
                validarCampo(objetivo, "Informe seu objetivo");
    }

    private boolean validarCampo(EditText campo, String mensagemErro) {
        if (campo.getText().toString().trim().isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            Toast.makeText(this, mensagemErro, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void salvarDados() {
        try {
            int idadeValor = Integer.parseInt(idade.getText().toString());
            double pesoValor = Double.parseDouble(peso.getText().toString());
            double alturaValor = Double.parseDouble(altura.getText().toString());

            if (idadeValor <= 0 || idadeValor > 100 || pesoValor <= 0 || pesoValor > 300 || alturaValor <= 0 || alturaValor > 3) {
                throw new NumberFormatException();
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nome", caixaNome.getText().toString().trim());
            values.put("idade", idadeValor);
            values.put("peso", pesoValor);
            values.put("altura", alturaValor);
            values.put("objetivo", objetivo.getText().toString().trim());

            // Verifica se já existe um usuário
            Cursor cursor = db.query("usuario", null, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                // Atualiza o usuário existente
                db.update("usuario", values, null, null);
            } else {
                // Insere um novo usuário
                db.insert("usuario", null, values);
            }
            cursor.close();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique os valores numéricos", Toast.LENGTH_LONG).show();
            throw e;
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}