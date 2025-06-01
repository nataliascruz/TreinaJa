package com.example.treino.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.treino.R;
import com.example.treino.database.DataBaseHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Perfil extends NavigationActivity {
    private EditText caixaNome, idade, peso, altura, objetivo;
    private Button salvarBt, editarBt, limparBt;
    private boolean editando = false;
    private DataBaseHelper dbHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final String[] OBJETIVOS = {
            "Hipertrofia",
            "Emagrecimento",
            "Forca",
            "Resistencia",
            "Condicionamento Físico",
            "Definicao Muscular"
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        dbHelper.close();
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
        limparBt = findViewById(R.id.limpar_dados_pessoais_bt);

        caixaNome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
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
                formatarCamposAntesDeSalvar();
                salvarDados();
            }
        });

        limparBt.setOnClickListener(v -> mostrarDialogoConfirmacao());
    }

    private void formatarCamposAntesDeSalvar() {
        String nome = formatarNomeProprio(caixaNome.getText().toString().trim());
        caixaNome.setText(nome);

        String pesoText = peso.getText().toString().trim();
        if (!pesoText.isEmpty() && !pesoText.endsWith("kg")) {
            peso.setText(String.format("%skg", pesoText));
        }

        String alturaText = altura.getText().toString().trim();
        if (!alturaText.isEmpty() && !alturaText.endsWith("m")) {
            altura.setText(String.format("%sm", alturaText));
        }

        String objetivoText = objetivo.getText().toString().trim();
        if (!objetivoText.isEmpty()) {
            for (String opcao : OBJETIVOS) {
                if (opcao.equalsIgnoreCase(objetivoText)) {
                    objetivo.setText(opcao);
                    break;
                }
            }
        }
    }

    private String formatarNomeProprio(String texto) {
        if (texto.isEmpty()) return texto;

        String[] palavras = texto.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                if (resultado.length() > 0) resultado.append(" ");
                resultado.append(palavra.substring(0, 1).toUpperCase())
                        .append(palavra.substring(1).toLowerCase());
            }
        }

        return resultado.toString();
    }

    private void mostrarDialogoConfirmacao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar limpeza")
                .setMessage("Tem certeza que deseja limpar todos os dados?")
                .setPositiveButton("Sim", (dialog, which) -> limparDados())
                .setNegativeButton("Não", null)
                .show();
    }

    private void atualizarEstadoEdicao() {
        boolean camposVazios = camposEstaoVazios();
        if (camposVazios) {
            editando = true;
            editarBt.setEnabled(false);
        } else {
            editarBt.setEnabled(true);
        }
        caixaNome.setEnabled(editando);
        idade.setEnabled(editando);
        peso.setEnabled(editando);
        altura.setEnabled(editando);
        objetivo.setEnabled(editando);
        salvarBt.setEnabled(editando);
    }

    private boolean camposEstaoVazios() {
        return caixaNome.getText().toString().trim().isEmpty() &&
                idade.getText().toString().trim().isEmpty() &&
                peso.getText().toString().trim().isEmpty() &&
                altura.getText().toString().trim().isEmpty() &&
                objetivo.getText().toString().trim().isEmpty();
    }

    private void carregarDados() {
        executor.execute(() -> {
            try (SQLiteDatabase db = dbHelper.getReadableDatabase();
                 Cursor cursor = db.query("usuario",
                         new String[]{"nome", "idade", "peso", "altura", "objetivo"},
                         null, null, null, null, null)) {

                final boolean hasData = cursor.moveToFirst();
                final String nome = hasData ? cursor.getString(0) : "";
                final String idadeStr = hasData ? String.valueOf(cursor.getInt(1)) : "";
                final String pesoStr = hasData ? cursor.getDouble(2) + "kg" : "";
                final String alturaStr = hasData ? cursor.getDouble(3) + "m" : "";
                final String objetivoStr = hasData ? cursor.getString(4) : "";

                handler.post(() -> {
                    caixaNome.setText(nome);
                    idade.setText(idadeStr);
                    peso.setText(pesoStr);
                    altura.setText(alturaStr);
                    objetivo.setText(objetivoStr);
                    editando = !hasData;
                    atualizarEstadoEdicao();
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean validarCampos() {
        return validarCampoNome(caixaNome, "Informe seu nome", 30) &&
                validarCampoNumero(idade, "Informe sua idade", 1, 100) &&
                validarCampoPeso(peso, "Informe seu peso") &&
                validarCampoAltura(altura, "Informe sua altura") &&
                validarCampoObjetivo(objetivo, "Selecione um objetivo");
    }

    private boolean validarCampoNome(EditText campo, String mensagemErro, int maxLength) {
        String texto = campo.getText().toString().trim();
        if (texto.isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            return false;
        }

        if (texto.length() > maxLength) {
            campo.setError("Máximo de " + maxLength + " caracteres");
            campo.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarCampoObjetivo(EditText campo, String mensagemErro) {
        String texto = campo.getText().toString().trim();
        if (texto.isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            return false;
        }

        boolean objetivoValido = false;
        for (String opcao : OBJETIVOS) {
            if (opcao.equalsIgnoreCase(texto)) {
                objetivoValido = true;
                break;
            }
        }

        if (!objetivoValido) {
            campo.setError("Selecione um objetivo válido: " + String.join(", ", OBJETIVOS));
            campo.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarCampoNumero(EditText campo, String mensagemErro, int min, int max) {
        String texto = campo.getText().toString().trim();
        if (texto.isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            return false;
        }

        try {
            int valor = Integer.parseInt(texto);
            if (valor < min || valor > max) {
                campo.setError("Valor deve ser entre " + min + " e " + max);
                campo.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            campo.setError("Valor numérico inválido");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarCampoPeso(EditText campo, String mensagemErro) {
        String texto = campo.getText().toString().replace("kg", "").trim();
        if (texto.isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            return false;
        }

        try {
            double valor = Double.parseDouble(texto.replace(',', '.'));
            if (valor <= 0 || valor > 300) {
                campo.setError("Peso deve ser entre 0.1 e 300 kg");
                campo.requestFocus();
                return false;
            }

            if (!texto.matches("^\\d+([.,]\\d{1,2})?$")) {
                campo.setError("Formato inválido. Exemplo: 70.5");
                campo.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            campo.setError("Valor numérico inválido");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarCampoAltura(EditText campo, String mensagemErro) {
        String texto = campo.getText().toString().replace("m", "").trim();
        if (texto.isEmpty()) {
            campo.setError(mensagemErro);
            campo.requestFocus();
            return false;
        }

        try {
            double valor = Double.parseDouble(texto.replace(',', '.'));
            if (valor <= 0 || valor > 3) {
                campo.setError("Altura deve ser entre 0.1 e 3.0 metros");
                campo.requestFocus();
                return false;
            }

            if (!texto.matches("^\\d([.,]\\d{1,2})?$")) {
                campo.setError("Formato inválido. Exemplo: 1.75");
                campo.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            campo.setError("Valor numérico inválido");
            campo.requestFocus();
            return false;
        }
        return true;
    }

    private void salvarDados() {
        String nome = caixaNome.getText().toString().trim();
        String objetivoText = objetivo.getText().toString().trim();
        final String idadeStr = idade.getText().toString().trim();
        final String pesoStr = peso.getText().toString().replace("kg", "").trim().replace(',', '.');
        final String alturaStr = altura.getText().toString().replace("m", "").trim().replace(',', '.');

        executor.execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("nome", nome);
                values.put("idade", idadeStr);
                values.put("peso", pesoStr);
                values.put("altura", alturaStr);
                values.put("objetivo", objetivoText);

                try (Cursor cursor = db.query("usuario", null, null, null, null, null, null)) {
                    boolean existeUsuario = cursor.getCount() > 0;
                    if (existeUsuario) {
                        db.update("usuario", values, null, null);
                    } else {
                        db.insert("usuario", null, values);
                    }
                }

                handler.post(() -> {
                    editando = false;
                    atualizarEstadoEdicao();
                    Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Erro ao salvar dados", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void limparDados() {
        executor.execute(() -> {
            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("usuario", null, null);

                handler.post(() -> {
                    caixaNome.setText("");
                    idade.setText("");
                    peso.setText("");
                    altura.setText("");
                    objetivo.setText("");
                    editando = true;
                    atualizarEstadoEdicao();
                    Toast.makeText(this, "Dados limpos com sucesso", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(this, "Erro ao limpar dados", Toast.LENGTH_SHORT).show());
            }
        });
    }
}