package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by Nagasudhir on 8/4/2017.
 */

public class TransactionSetModel {
    /**
     * Fields of the table transaction_sets
     */
    public static final String KEY_ROW_ID = "id";
    public static final String KEY_NAME_STRING = "name_string";
    public static final String KEY_METADATA = "metadata";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    /**
     * A constant, stores the the table name
     */
    private static final String TABLE_NAME = "transaction_sets";

    /**
     * Inserts seeds into the table
     */
    public static void insertSeeds(SQLiteDatabase db) {
        // Create some rows
        TransactionSetPojo[] transactionSets = new TransactionSetPojo[2];
        transactionSets[0] = new TransactionSetPojo("1", "First Set", "first set metadata");
        transactionSets[1] = new TransactionSetPojo("2", "Second Set", "second set metadata");
        for (int i = 0; i < transactionSets.length; i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ROW_ID, transactionSets[i].getId());
            initialValues.put(KEY_NAME_STRING, transactionSets[i].getNameString());
            initialValues.put(KEY_METADATA, transactionSets[i].getMetadata());
            db.insert(TABLE_NAME, null, initialValues);
        }
    }

    /**
     * Returns all the transaction_sets in the table
     */
    public static Cursor getAllTransactionSets(SQLiteDatabase db) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_sets;", null);
    }

    /**
     * Returns a transaction_set in the table by its id
     */
    public static Cursor getTransactionSetById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_sets WHERE id = ?;", new String[]{idString});
    }

    /**
     * Returns a transaction_set in the table by its name
     */
    public static Cursor getTransactionSetByName(SQLiteDatabase db, String nameString) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_sets WHERE name_string = ?;", new String[]{nameString});
    }

    /**
     * Creates a the transaction in the table
     */
    public static long insertTransactionSet(SQLiteDatabase db, ContentValues values) {
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates a transaction in the table
     */
    public static long updateTransactionSet(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Deletes a transaction in the table
     */
    public static long deleteTransactionSet(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
