
package com.salvi.TaskManagER.Support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "task_manager_db";
    private static final int VERSION = 1;

    public static final String TABLE_REUNION = "reunion";
    public static final String TABLE_FUNCTIONAL = "funcional_requirement";
    public static final String TABLE_NON_FUNCTIONAL =
            "non_funcional_requirement";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Reuniões
        db.execSQL(
                "CREATE TABLE reunion (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "create_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                        ")"
        );

        // Requisitos funcionais
        db.execSQL(
                "CREATE TABLE funcional_requirement (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "reunion_id INTEGER NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "create_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "importance_level INTEGER NOT NULL " +
                        "CHECK (importance_level BETWEEN 1 AND 4), " +
                        "difficulty INTEGER NOT NULL " +
                        "CHECK (difficulty BETWEEN 1 AND 4), " +
                        "time_required REAL NOT NULL CHECK (time_required >= 0), " +
                        "FOREIGN KEY (reunion_id) REFERENCES reunion(id) " +
                        "ON DELETE CASCADE" +
                        ")"
        );

        // Requisitos não funcionais
        db.execSQL(
                "CREATE TABLE non_funcional_requirement (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "reunion_id INTEGER NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "create_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                        "importance_level INTEGER NOT NULL " +
                        "CHECK (importance_level BETWEEN 1 AND 4), " +
                        "difficulty INTEGER NOT NULL " +
                        "CHECK (difficulty BETWEEN 1 AND 4), " +
                        "time_required REAL NOT NULL CHECK (time_required >= 0), " +
                        "FOREIGN KEY (reunion_id) REFERENCES reunion(id) " +
                        "ON DELETE CASCADE" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        // Adicione aqui migrações quando a estrutura do banco mudar.
        // Não exclua as tabelas existentes para evitar perda de dados.
    }

    // =========================================
    // REUNIÕES
    // =========================================

    public long insertReunion(String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());

        return db.insert(TABLE_REUNION, null, values);
    }

    public Cursor getAllReunions() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_REUNION,
                new String[]{"id", "name", "create_at"},
                null,
                null,
                null,
                null,
                "create_at DESC"
        );
    }

    public Cursor getReunionById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_REUNION,
                new String[]{"id", "name", "create_at"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public int updateReunion(int id, String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());

        return db.update(
                TABLE_REUNION,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteReunionById(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_REUNION,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // =========================================
    // REQUISITOS FUNCIONAIS
    // =========================================

    public long insertFunctionalRequirement(
            int reunionId,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        return insertRequirement(
                TABLE_FUNCTIONAL,
                reunionId,
                description,
                importanceLevel,
                difficulty,
                timeRequired
        );
    }

    public Cursor getFunctionalRequirements(int reunionId) {
        return getRequirements(TABLE_FUNCTIONAL, reunionId);
    }

    public Cursor getFunctionalRequirementById(int id) {
        return getRequirementById(TABLE_FUNCTIONAL, id);
    }

    public int updateFunctionalRequirement(
            int id,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        return updateRequirement(
                TABLE_FUNCTIONAL,
                id,
                description,
                importanceLevel,
                difficulty,
                timeRequired
        );
    }

    public int deleteFunctionalRequirementById(int id) {
        return deleteRequirement(TABLE_FUNCTIONAL, id);
    }

    // =========================================
    // REQUISITOS NÃO FUNCIONAIS
    // =========================================

    public long insertNonFunctionalRequirement(
            int reunionId,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        return insertRequirement(
                TABLE_NON_FUNCTIONAL,
                reunionId,
                description,
                importanceLevel,
                difficulty,
                timeRequired
        );
    }

    public Cursor getNonFunctionalRequirements(int reunionId) {
        return getRequirements(TABLE_NON_FUNCTIONAL, reunionId);
    }

    public Cursor getNonFunctionalRequirementById(int id) {
        return getRequirementById(TABLE_NON_FUNCTIONAL, id);
    }

    public int updateNonFunctionalRequirement(
            int id,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        return updateRequirement(
                TABLE_NON_FUNCTIONAL,
                id,
                description,
                importanceLevel,
                difficulty,
                timeRequired
        );
    }

    public int deleteNonFunctionalRequirementById(int id) {
        return deleteRequirement(TABLE_NON_FUNCTIONAL, id);
    }

    // =========================================
    // MÉTODOS INTERNOS COMPARTILHADOS
    // =========================================

    private long insertRequirement(
            String table,
            int reunionId,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("reunion_id", reunionId);
        values.put("description", description.trim());
        values.put("importance_level", importanceLevel);
        values.put("difficulty", difficulty);
        values.put("time_required", timeRequired);

        return db.insert(table, null, values);
    }

    private Cursor getRequirements(String table, int reunionId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                table,
                new String[]{
                        "id",
                        "reunion_id",
                        "description",
                        "create_at",
                        "importance_level",
                        "difficulty",
                        "time_required"
                },
                "reunion_id = ?",
                new String[]{String.valueOf(reunionId)},
                null,
                null,
                "id DESC"
        );
    }

    private Cursor getRequirementById(String table, int id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                table,
                new String[]{
                        "id",
                        "reunion_id",
                        "description",
                        "create_at",
                        "importance_level",
                        "difficulty",
                        "time_required"
                },
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    private int updateRequirement(
            String table,
            int id,
            String description,
            int importanceLevel,
            int difficulty,
            double timeRequired
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("description", description.trim());
        values.put("importance_level", importanceLevel);
        values.put("difficulty", difficulty);
        values.put("time_required", timeRequired);

        return db.update(
                table,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private int deleteRequirement(String table, int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                table,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }
}
