package com.example.treino.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TreinaJa.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USUARIO = "usuario";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOME = "nome";
    public static final String COLUMN_IDADE = "idade";
    public static final String COLUMN_PESO = "peso";
    public static final String COLUMN_ALTURA = "altura";
    public static final String COLUMN_OBJETIVO = "objetivo";

    public static final String TABLE_TREINO = "treino";
    public static final String COLUMN_TREINO_ID = "id";
    public static final String COLUMN_TREINO_NOME = "nome";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_USUARIO_ID = "usuario_id";

    public static final String TABLE_EXERCICIO = "exercicio";
    public static final String COLUMN_EXERCICIO_ID = "id";
    public static final String COLUMN_EXERCICIO_NOME = "nome";
    public static final String COLUMN_SERIES = "series";
    public static final String COLUMN_REPETICOES = "repeticoes";
    public static final String COLUMN_ORDEM = "ordem";
    public static final String COLUMN_TREINO_ID_FK = "treino_id";

    private static final String CREATE_TABLE_USUARIO =
            "CREATE TABLE " + TABLE_USUARIO + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOME + " TEXT NOT NULL, " +
                    COLUMN_IDADE + " INTEGER CHECK (" + COLUMN_IDADE + " > 0 AND " + COLUMN_IDADE + " <= 100), " +
                    COLUMN_PESO + " REAL CHECK (" + COLUMN_PESO + " > 0 AND " + COLUMN_PESO + " <= 300), " +
                    COLUMN_ALTURA + " REAL CHECK (" + COLUMN_ALTURA + " > 0 AND " + COLUMN_ALTURA + " <= 3), " +
                    COLUMN_OBJETIVO + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_TREINO =
            "CREATE TABLE " + TABLE_TREINO + " (" +
                    COLUMN_TREINO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TREINO_NOME + " TEXT NOT NULL, " +
                    COLUMN_TIPO + " TEXT NOT NULL CHECK (" + COLUMN_TIPO + " IN ('A', 'B', 'C')), " +
                    COLUMN_USUARIO_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

    private static final String CREATE_TABLE_EXERCICIO =
            "CREATE TABLE " + TABLE_EXERCICIO + " (" +
                    COLUMN_EXERCICIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXERCICIO_NOME + " TEXT NOT NULL, " +
                    COLUMN_SERIES + " INTEGER NOT NULL CHECK (" + COLUMN_SERIES + " > 0), " +
                    COLUMN_REPETICOES + " INTEGER NOT NULL CHECK (" + COLUMN_REPETICOES + " > 0), " +
                    COLUMN_ORDEM + " INTEGER NOT NULL, " +
                    COLUMN_TREINO_ID_FK + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_TREINO_ID_FK + ") REFERENCES " + TABLE_TREINO + "(" + COLUMN_TREINO_ID + ") ON DELETE CASCADE);";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
        db.execSQL(CREATE_TABLE_TREINO);
        db.execSQL(CREATE_TABLE_EXERCICIO);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREINO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}