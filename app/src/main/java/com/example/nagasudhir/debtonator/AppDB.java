package com.example.nagasudhir.debtonator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nagasudhir on 8/3/2017.
 */

public class AppDB extends SQLiteOpenHelper {
    /**
     * Database name
     */
    private static String DBNAME = "debtonator_2";

    /**
     * Version number of the database
     */
    private static int VERSION = 1;

    /**
     * An instance variable for SQLiteDatabase
     */
    private SQLiteDatabase mDB;

    /**
     * Constructor
     */
    public AppDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    /*
    Returns the Writable Database instance variable
     */
    public SQLiteDatabase getWritableDB() {
        return mDB;
    }

    /**
     * This is a callback method, invoked when the method
     * getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("BEGIN TRANSACTION");
        String sql = "DROP TABLE IF EXISTS people_details;";
        db.execSQL(sql);
        sql = "CREATE TABLE people_details (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "`username` TEXT NOT NULL UNIQUE," +
                "`phone_number` TEXT UNIQUE," +
                "`email_id` TEXT," +
                "`metadata` TEXT," +
                "`created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "`updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(sql);
        sql = "CREATE TRIGGER people_details_updated_at_trigger AFTER \n" +
                "  UPDATE \n" +
                "  ON people_details BEGIN \n" +
                "  UPDATE people_details \n" +
                "  SET    updated_at = DATETIME('now') \n" +
                "  WHERE  id = NEW.id; \n" +
                "END;";
        db.execSQL(sql);
        PersonModel.insertSeeds(db);
        db.execSQL("COMMIT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
