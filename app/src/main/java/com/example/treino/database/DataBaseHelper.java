package com.example.treino.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataBaseHelper";
    private static final String DATABASE_NAME = "TreinaJa.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USUARIO =
            "CREATE TABLE usuario (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "idade INTEGER CHECK (idade > 0 AND idade <= 100), " +
                    "peso REAL CHECK (peso > 0 AND peso <= 300), " +
                    "altura REAL CHECK (altura > 0 AND altura <= 3), " +
                    "objetivo TEXT NOT NULL);";

    private static final String CREATE_TABLE_TREINO =
            "CREATE TABLE treino (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "tipo TEXT NOT NULL CHECK (tipo IN ('A', 'B', 'C')), " +
                    "usuario_id INTEGER, " +
                    "FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE);";

    private static final String CREATE_TABLE_EXERCICIO =
            "CREATE TABLE exercicio (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "series INTEGER NOT NULL CHECK (series > 0), " +
                    "repeticoes INTEGER NOT NULL CHECK (repeticoes > 0), " +
                    "ordem INTEGER NOT NULL, " +
                    "treino_id INTEGER, " +
                    "FOREIGN KEY (treino_id) REFERENCES treino(id) ON DELETE CASCADE);";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DataBaseHelper inicializado");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Criando banco de dados...");

            db.execSQL(CREATE_TABLE_USUARIO);
            db.execSQL(CREATE_TABLE_TREINO);
            db.execSQL(CREATE_TABLE_EXERCICIO);

            db.execSQL("PRAGMA foreign_keys = ON");

            Log.d(TAG, "Banco de dados criado com sucesso");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao criar banco de dados", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.w(TAG, "Atualizando banco de dados da versão " + oldVersion + " para " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS exercicio");
            db.execSQL("DROP TABLE IF EXISTS treino");
            db.execSQL("DROP TABLE IF EXISTS usuario");

            Log.d(TAG, "Tabelas antigas removidas");
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao atualizar banco de dados", e);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        Log.d(TAG, "Configuração de chaves estrangeiras ativada");
    }
}