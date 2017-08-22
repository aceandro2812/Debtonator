package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nagasudhir on 8/22/2017.
 */

public class TransactionTagModel {
    /**
     * Fields of the table transactions_details
     */
    public static final String KEY_ROW_ID = "id";
    public static final String KEY_TRANSACTION_DETAILS_ID = "transactions_details_id";
    public static final String KEY_NAME = "name_string";
    public static final String KEY_UPDATED_AT = "updated_at";
    /**
     * A constant, stores the the table name
     */
    private static final String TABLE_NAME = "transaction_tags";

    /**
     * Inserts seeds into the tables
     */
    public static void insertSeeds(SQLiteDatabase db) {
        // todo Create some rows
    }

    /**
     * Returns all the transaction tags in the table
     */
    public static Cursor getAllTransactionTags(SQLiteDatabase db) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_tags", null);
    }

    /**
     * Returns all the transaction tags in the table
     */
    public static Cursor getAllDistinctTransactionTags(SQLiteDatabase db) {
        return db.rawQuery("SELECT DISTINCT name_string, id AS _id FROM transaction_tags GROUP BY name_string ORDER BY name_string", null);
    }

    /**
     * Returns a transaction tag in the table by its id
     */
    public static Cursor getTransactionTagById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_tags WHERE id = ?", new String[]{idString});
    }

    /**
     * Returns transaction tags in the table by its transactions_details_id
     */
    public static Cursor getTransactionTagsByTransactionDetailId(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_tags WHERE transactions_details_id = ?", new String[]{idString});
    }

    /**
     * Creates a the transaction tag in the table
     */
    public static long insertTransactionTag(SQLiteDatabase db, ContentValues values) {
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Updates a transaction tag in the table
     */
    public static long updateTransactionTag(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.updateWithOnConflict(TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a transaction tag in the table
     */
    public static long deleteTransactionTag(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
