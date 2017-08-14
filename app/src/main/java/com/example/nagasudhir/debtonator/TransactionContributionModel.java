package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nagasudhir on 8/12/2017.
 */

public class TransactionContributionModel {
    /**
     * Fields of the table transaction_contributions
     */
    public static final String KEY_ROW_ID = "id";
    public static final String KEY_TRANSACTION_DETAILS_ID = "transactions_details_id";
    public static final String KEY_PERSON_ID = "people_details_id";
    public static final String KEY_CONTRIBUTION = "contribution";
    public static final String KEY_IS_CONSUMER = "is_consumer";
    /**
     * A constant, stores the the table name
     */
    private static final String TABLE_NAME = "transaction_contributions";

    /**
     * Inserts seeds into the tables
     */
    public static void insertSeeds(SQLiteDatabase db) {
        // todo Create some rows

    }
    /**
     * Returns all the transaction contributions in the table
     */
    public static Cursor getAllTransactionsContributions(SQLiteDatabase db) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_contributions", null);
    }

    /**
     * Returns a transaction_contribution in the table by its id
     */
    public static Cursor getTransactionContributionById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transaction_contributions WHERE id = ?", new String[]{idString});
    }

    /**
     * Returns transaction contributions in the table by its transactions_details_id
     */
    public static Cursor getTransactionContributionByTransactionDetailId(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT people_details.id AS _id, \n" +
                "       people_details.username, \n" +
                "       tran_contributions.* \n" +
                "FROM   people_details \n" +
                "       LEFT OUTER JOIN (SELECT * \n" +
                "                        FROM   transaction_contributions \n" +
                "                        WHERE  transaction_contributions.transactions_details_id \n" +
                "                               = ?) AS \n" +
                "                                             tran_contributions \n" +
                "                    ON tran_contributions.people_details_id = people_details.id \n" +
                "ORDER  BY tran_contributions.contribution DESC, \n" +
                "          tran_contributions.is_consumer DESC, \n" +
                "          people_details.username COLLATE NOCASE ASC", new String[]{idString});
    }
    
    /**
     * Creates a the transaction contribution in the table
     */
    public static long insertTransactionContribution(SQLiteDatabase db, ContentValues values) {
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Updates a transaction in the table
     */
    public static long updateTransactionContribution(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.updateWithOnConflict(TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a transaction in the table
     */
    public static long deleteTransactionContribution(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
