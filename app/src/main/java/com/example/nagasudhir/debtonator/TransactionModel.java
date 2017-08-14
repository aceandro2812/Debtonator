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
    public static final String VARIABLE_TRANSACTION_SUM = "tran_sum";
    public static final String VARIABLE_TRANSACTION_PEOPLE = "tran_people_names";
    public static final String VARIABLE_TRANSACTION_PEOPLE_IDS = "tran_people_ids";
    public static final String VARIABLE_NEXT_TRAN_ID = "next_id";
    public static final String VARIABLE_PREV_TRAN_ID = "prev_id";
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
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details", null);
    }

    /**
     * Returns a transaction in the table by its id
     */
    public static Cursor getTransactionById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details WHERE id = ?", new String[]{idString});
    }

    /**
     * Returns next and prev transaction ids
     */
    public static Cursor getNextPrevTransactionsById(SQLiteDatabase db, String idString, String transactionSetId) {
        return db.rawQuery("SELECT (SELECT transactions_details.id \n" +
                "        FROM   transactions_details \n" +
                "        WHERE  id < ? AND transaction_sets_id = ? \n" +
                "        ORDER  BY id DESC \n" +
                "        LIMIT  1) AS prev_id, \n" +
                "       (SELECT transactions_details.id \n" +
                "        FROM   transactions_details \n" +
                "        WHERE  id > ? AND transaction_sets_id = ? \n" +
                "        ORDER  BY id \n" +
                "        LIMIT  1) AS next_id;", new String[]{idString, transactionSetId, idString, transactionSetId});
    }

    /**
     * Returns transactions in the table by its transaction_sets_id
     */
    public static Cursor getTransactionByTransactionSetId(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM transactions_details WHERE transaction_sets_id = ?", new String[]{idString});
    }

    /**
     * Returns transactions in the table by its transaction_sets_id
     */
    public static Cursor getTransactionByTransactionSetIdInDetail(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT transactions_details.id AS _id, \n" +
                "       transactions_details.description, \n" +
                "       transactions_details.metadata, \n" +
                "       transactions_details.transaction_time, \n" +
                "       transactions_details.updated_at, \n" +
                "       tran_aggregate_info.tran_sum, \n" +
                "       tran_aggregate_info.tran_people_names, \n" +
                "       tran_aggregate_info.tran_people_ids \n" +
                "FROM   transactions_details \n" +
                "       LEFT OUTER JOIN (SELECT sorted_trans.transactions_details_id, \n" +
                "                               Sum(sorted_trans.contribution) \n" +
                "                               AS \n" +
                "                                                           tran_sum, \n" +
                "Group_concat(sorted_trans.people_details_id, ', ') AS \n" +
                "                            tran_people_ids, \n" +
                "Group_concat(people_details.username, ', ')        AS \n" +
                "                            tran_people_names \n" +
                "FROM   (SELECT * \n" +
                " FROM   transaction_contributions \n" +
                " ORDER  BY transaction_contributions.contribution DESC, \n" +
                "           transaction_contributions.is_consumer DESC) AS \n" +
                "sorted_trans \n" +
                "LEFT OUTER JOIN people_details \n" +
                "             ON people_details.id = \n" +
                "                sorted_trans.people_details_id \n" +
                "GROUP  BY sorted_trans.transactions_details_id) AS \n" +
                "                    tran_aggregate_info \n" +
                "ON tran_aggregate_info.transactions_details_id = \n" +
                "transactions_details.id \n" +
                "WHERE  transactions_details.transaction_sets_id = ? \n" +
                "GROUP  BY transactions_details.id", new String[]{idString});
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
