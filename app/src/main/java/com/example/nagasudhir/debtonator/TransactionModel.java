package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nagasudhir on 8/4/2017.
 */

public class TransactionModel {
    /**
     * Fields of the table transactions_details
     */
    public static final String KEY_ROW_ID = "id";
    public static final String KEY_TRANSACTION_SET_ID = "transaction_sets_id";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_METADATA = "metadata";
    public static final String KEY_TRANSACTION_TIME = "transaction_time";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    /**
     * A constant, stores the the table name
     */
    private static final String TABLE_NAME = "transactions_details";

    /**
     * Inserts seeds into the tables
     */
    public static void insertSeeds(SQLiteDatabase db) {
        // todo Create some rows

    }

    /**
     * Returns all the transactions in the table
     */
    public static Cursor getAllTransactions(SQLiteDatabase db) {
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details;", null);
    }

    /**
     * Returns a transaction in the table by its id
     */
    public static Cursor getTransactionById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details WHERE id = ?;", new String[]{idString});
    }

    /**
     * Returns transactions in the table by its transaction_sets_id
     */
    public static Cursor getTransactionByTransactionSetId(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details WHERE transaction_sets_id = ?;", new String[]{idString});
    }

    /**
     * Creates a the transaction in the table
     */
    public static long insertTransaction(SQLiteDatabase db, ContentValues values) {
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Updates a transaction in the table
     */
    public static long updateTransaction(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.updateWithOnConflict(TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a transaction in the table
     */
    public static long deleteTransaction(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
