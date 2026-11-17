package com.salvi.TaskManagER.Support;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "task_manager_db";
    private static final int VERSION = 2;

    public static final String TABLE_REUNION = "reunion";
    public static final String TABLE_FUNCTIONAL = "funcional_requirement";
    public static final String TABLE_NON_FUNCTIONAL = "non_funcional_requirement";

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
        db.execSQL("CREATE TABLE reunion (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, " +
            "start_date TEXT NOT NULL, end_date TEXT NOT NULL, " +
            "create_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        createRequirementTable(db, TABLE_FUNCTIONAL);
        createRequirementTable(db, TABLE_NON_FUNCTIONAL);
    }

    private void createRequirementTable(SQLiteDatabase db, String table) {
        db.execSQL("CREATE TABLE " + table + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, reunion_id INTEGER NOT NULL, " +
            "description TEXT NOT NULL, create_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "importance_level INTEGER NOT NULL CHECK (importance_level BETWEEN 1 AND 4), " +
            "difficulty INTEGER NOT NULL CHECK (difficulty BETWEEN 1 AND 4), " +
            "time_required REAL NOT NULL CHECK (time_required >= 0), " +
            "latitude TEXT, longitude TEXT, photo_one_path TEXT, photo_two_path TEXT, " +
            "FOREIGN KEY (reunion_id) REFERENCES reunion(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDatabase(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDatabase(db);
    }

    private void resetDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NON_FUNCTIONAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUNCTIONAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REUNION);
        onCreate(db);
    }

    public long insertReunion(String name, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        return getWritableDatabase().insert(TABLE_REUNION, null, values);
    }

    public Cursor getAllReunions() {
        return getReadableDatabase().query(TABLE_REUNION,
            new String[]{"id", "name", "start_date", "end_date", "create_at"},
            null, null, null, null, "create_at DESC");
    }

    public Cursor getReunionById(int id) {
        return getReadableDatabase().query(TABLE_REUNION,
            new String[]{"id", "name", "start_date", "end_date", "create_at"},
            "id = ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public int updateReunion(int id, String name, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        return getWritableDatabase().update(TABLE_REUNION, values, "id = ?",
            new String[]{String.valueOf(id)});
    }

    public int deleteReunionById(int id) {
        return getWritableDatabase().delete(TABLE_REUNION, "id = ?",
            new String[]{String.valueOf(id)});
    }

    public long insertFunctionalRequirement(int reunionId, String description,
            int importance, int difficulty, double timeRequired, String latitude,
            String longitude, String photoOnePath, String photoTwoPath) {
        return insertRequirement(TABLE_FUNCTIONAL, reunionId, description,
            importance, difficulty, timeRequired, latitude, longitude, photoOnePath, photoTwoPath);
    }

    public Cursor getFunctionalRequirements(int reunionId) {
        return getRequirements(TABLE_FUNCTIONAL, reunionId);
    }

    public int updateFunctionalRequirement(int id, String description, int importance,
            int difficulty, double timeRequired, String photoOnePath, String photoTwoPath) {
        return updateRequirement(TABLE_FUNCTIONAL, id, description, importance,
            difficulty, timeRequired, photoOnePath, photoTwoPath);
    }

    public int deleteFunctionalRequirementById(int id) {
        return deleteRequirement(TABLE_FUNCTIONAL, id);
    }

    public long insertNonFunctionalRequirement(int reunionId, String description,
            int importance, int difficulty, double timeRequired, String latitude,
            String longitude, String photoOnePath, String photoTwoPath) {
        return insertRequirement(TABLE_NON_FUNCTIONAL, reunionId, description,
            importance, difficulty, timeRequired, latitude, longitude, photoOnePath, photoTwoPath);
    }

    public Cursor getNonFunctionalRequirements(int reunionId) {
        return getRequirements(TABLE_NON_FUNCTIONAL, reunionId);
    }

    public int updateNonFunctionalRequirement(int id, String description, int importance,
            int difficulty, double timeRequired, String photoOnePath, String photoTwoPath) {
        return updateRequirement(TABLE_NON_FUNCTIONAL, id, description, importance,
            difficulty, timeRequired, photoOnePath, photoTwoPath);
    }

    public int deleteNonFunctionalRequirementById(int id) {
        return deleteRequirement(TABLE_NON_FUNCTIONAL, id);
    }

    private long insertRequirement(String table, int reunionId, String description,
            int importance, int difficulty, double timeRequired, String latitude,
            String longitude, String photoOnePath, String photoTwoPath) {
        ContentValues values = new ContentValues();
        values.put("reunion_id", reunionId);
        values.put("description", description.trim());
        values.put("importance_level", importance);
        values.put("difficulty", difficulty);
        values.put("time_required", timeRequired);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("photo_one_path", photoOnePath);
        values.put("photo_two_path", photoTwoPath);
        return getWritableDatabase().insert(table, null, values);
    }

    private Cursor getRequirements(String table, int reunionId) {
        return getReadableDatabase().query(table,
            new String[]{"id", "reunion_id", "description", "create_at",
                "importance_level", "difficulty", "time_required", "latitude",
                "longitude", "photo_one_path", "photo_two_path"},
            "reunion_id = ?", new String[]{String.valueOf(reunionId)},
            null, null, "id DESC");
    }

    private int updateRequirement(String table, int id, String description,
            int importance, int difficulty, double timeRequired,
            String photoOnePath, String photoTwoPath) {
        ContentValues values = new ContentValues();
        values.put("description", description.trim());
        values.put("importance_level", importance);
        values.put("difficulty", difficulty);
        values.put("time_required", timeRequired);
        values.put("photo_one_path", photoOnePath);
        values.put("photo_two_path", photoTwoPath);
        return getWritableDatabase().update(table, values, "id = ?",
            new String[]{String.valueOf(id)});
    }

    private int deleteRequirement(String table, int id) {
        return getWritableDatabase().delete(table, "id = ?",
            new String[]{String.valueOf(id)});
    }
}
