package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    public static final String VARIABLE_FROM_DATE = "min_time";
    public static final String VARIABLE_TO_DATE = "max_time";
    public static final String VARIABLE_WORTH = "tran_set_worth";

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
        return db.rawQuery("SELECT transaction_sets.id as _id, transaction_sets.*, \n" +
                "MAX(transactions_details_summary.transaction_time) AS max_time, \n" +
                "MIN(transactions_details_summary.transaction_time) AS min_time, \n" +
                "SUM(transactions_details_summary.tran_contribution_sum) AS tran_set_worth \n" +
                "FROM transaction_sets \n" +
                "LEFT OUTER JOIN \n" +
                "(SELECT transactions_details.*, SUM(transaction_contributions.contribution) AS tran_contribution_sum \n" +
                "FROM transactions_details \n" +
                "LEFT OUTER JOIN \n" +
                "transaction_contributions ON transaction_contributions.transactions_details_id = transactions_details.id\n" +
                "GROUP BY transactions_details.id \n" +
                " ) AS transactions_details_summary \n" +
                "ON transaction_sets.id = transactions_details_summary.transaction_sets_id \n" +
                "GROUP BY transaction_sets.id;", null);
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
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Updates a transaction in the table
     */
    public static int updateTransactionSet(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.updateWithOnConflict(TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a transaction in the table
     */
    public static int deleteTransactionSet(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }

    /*
    * Export Transaction Set Information to a json object Array
    * */
    public static JSONObject exportTransactionSets(SQLiteDatabase db, String[] transactionSetIds) {
        JSONObject exportJSON = new JSONObject();
        // creating the transaction sets json array
        JSONArray transactionSetsJSONArray = new JSONArray();
        try {
            for (int i = 0; i < transactionSetIds.length; i++) {
                String transactionSetId = transactionSetIds[i];
                Cursor transactionSetCursor = TransactionSetModel.getTransactionSetById(db, transactionSetId);
                if (transactionSetCursor.moveToNext()) {
                    String transactionSetName = transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_NAME_STRING));
                    String transactionSetMetadata = transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_METADATA));
                    String transactionSetCreatedAt = transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_CREATED_AT));
                    String transactionSetUpdatedAt = transactionSetCursor.getString(transactionSetCursor.getColumnIndex(TransactionSetModel.KEY_UPDATED_AT));
                    // creating the transaction set JSON member
                    JSONObject transactionSetJSON = new JSONObject();
                    transactionSetJSON.put(TransactionSetModel.KEY_ROW_ID, transactionSetId);
                    transactionSetJSON.put(TransactionSetModel.KEY_NAME_STRING, transactionSetName);
                    transactionSetJSON.put(TransactionSetModel.KEY_METADATA, transactionSetMetadata);
                    transactionSetJSON.put(TransactionSetModel.KEY_CREATED_AT, transactionSetCreatedAt);
                    transactionSetJSON.put(TransactionSetModel.KEY_UPDATED_AT, transactionSetUpdatedAt);

                    // creating the transaction details JSON Array for the transaction set
                    JSONArray transactionDetailsJSONArray = new JSONArray();

                    // get all the transaction detail ids of the transaction set
                    Cursor transactionDetailsCursor = TransactionModel.getTransactionByTransactionSetId(db, transactionSetId);

                    while (transactionDetailsCursor.moveToNext()) {
                        // creating the transaction details object
                        JSONObject transactionDetailJSON = new JSONObject();

                        String transactionDetailId = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_ROW_ID));
                        String transactionDetailDescription = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_DESCRIPTION));
                        String transactionDetailMetadata = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_METADATA));
                        String transactionDetailTime = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_TRANSACTION_TIME));
                        String transactionDetailUuid = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_UUID));
                        String transactionDetailCreatedAt = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_CREATED_AT));
                        String transactionDetailUpdatedAt = transactionDetailsCursor.getString(transactionDetailsCursor.getColumnIndex(TransactionModel.KEY_UPDATED_AT));
                        transactionDetailJSON.put(TransactionModel.KEY_ROW_ID, transactionDetailId);
                        transactionDetailJSON.put(TransactionModel.KEY_DESCRIPTION, transactionDetailDescription);
                        transactionDetailJSON.put(TransactionModel.KEY_METADATA, transactionDetailMetadata);
                        transactionDetailJSON.put(TransactionModel.KEY_TRANSACTION_TIME, transactionDetailTime);
                        transactionDetailJSON.put(TransactionModel.KEY_UUID, transactionDetailUuid);
                        transactionDetailJSON.put(TransactionModel.KEY_CREATED_AT, transactionDetailCreatedAt);
                        transactionDetailJSON.put(TransactionModel.KEY_UPDATED_AT, transactionDetailUpdatedAt);

                        //create the transaction contributions JSON array for the transaction detail
                        JSONArray transactionContributionsJSONArray = new JSONArray();

                        // get all the transaction contribution rows for the transaction detail Id
                        Cursor transactionContributionsCursor = TransactionContributionModel.getTransactionContributionsByTransactionDetailId(db, transactionDetailId);

                        while (transactionContributionsCursor.moveToNext()) {
                            // creating the transaction contributions object
                            JSONObject transactionContributionJSON = new JSONObject();

                            String transactionContributionId = transactionContributionsCursor.getString(transactionContributionsCursor.getColumnIndex(TransactionContributionModel.KEY_ROW_ID));
                            String transactionContributionPersonId = transactionContributionsCursor.getString(transactionContributionsCursor.getColumnIndex(TransactionContributionModel.KEY_PERSON_ID));
                            String transactionContribution = transactionContributionsCursor.getString(transactionContributionsCursor.getColumnIndex(TransactionContributionModel.KEY_CONTRIBUTION));
                            String transactionContributionIsConsumer = transactionContributionsCursor.getString(transactionContributionsCursor.getColumnIndex(TransactionContributionModel.KEY_IS_CONSUMER));
                            transactionContributionJSON.put(TransactionContributionModel.KEY_ROW_ID, transactionContributionId);
                            transactionContributionJSON.put(TransactionContributionModel.KEY_PERSON_ID, transactionContributionPersonId);
                            transactionContributionJSON.put(TransactionContributionModel.KEY_CONTRIBUTION, transactionContribution);
                            transactionContributionJSON.put(TransactionContributionModel.KEY_IS_CONSUMER, transactionContributionIsConsumer);

                            // pushing single transaction contribution info into the transaction contributions JSON array
                            transactionContributionsJSONArray.put(transactionContributionJSON);
                        }
                        // setting all the transaction contribution details array into the transaction detail JSON
                        transactionDetailJSON.put("contributions", transactionContributionsJSONArray);

                        //create the transaction tags JSON array for the transaction detail
                        JSONArray transactionTagsJSONArray = new JSONArray();

                        // get all the transaction tags rows for the transaction detail Id
                        Cursor transactionTagsCursor = TransactionTagModel.getTransactionTagsByTransactionDetailId(db, transactionDetailId);

                        while (transactionTagsCursor.moveToNext()) {
                            // creating the transaction tag object
                            JSONObject transactionTagJSON = new JSONObject();

                            String transactionTagId = transactionTagsCursor.getString(transactionTagsCursor.getColumnIndex(TransactionTagModel.KEY_ROW_ID));
                            String transactionTag = transactionTagsCursor.getString(transactionTagsCursor.getColumnIndex(TransactionTagModel.KEY_NAME));
                            transactionTagJSON.put(TransactionTagModel.KEY_ROW_ID, transactionTagId);
                            transactionTagJSON.put(TransactionTagModel.KEY_NAME, transactionTag);

                            // pushing single transaction tag info into the transaction tags JSON array
                            transactionTagsJSONArray.put(transactionTagJSON);
                        }
                        // setting all the transaction tags details array into the transaction detail JSON
                        transactionDetailJSON.put("tags", transactionTagsJSONArray);

                        // pushing the single transaction detail information into the array
                        transactionDetailsJSONArray.put(transactionDetailJSON);
                    }

                    // setting all the transaction set details array into the transaction set JSON
                    transactionSetJSON.put("transactions", transactionDetailsJSONArray);

                    //pushing the transaction set json object to the transaction sets json array
                    transactionSetsJSONArray.put(transactionSetJSON);

                }
            }
            // set all the transaction sets to the exports JSON
            exportJSON.put("transaction_sets", transactionSetsJSONArray);

            // creating the transactions people JSON Array for all the transaction sets
            JSONArray peopleJSONArray = new JSONArray();

            // get all the people rows for all the transaction set Ids
            Cursor peopleCursor = PersonModel.getPersonsByTransactionSetsIds(db, transactionSetIds);

            while (peopleCursor.moveToNext()) {
                // creating the person object
                JSONObject personJSON = new JSONObject();
                String personId = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_ROW_ID));
                String personUsername = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_USERNAME));
                String personPhone = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_PHONE_NUMBER));
                String personEmail = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_EMAIL_ID));
                String personMetadata = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_METADATA));
                String personUuid = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_UUID));
                String personCreatedAt = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_CREATED_AT));
                String personUpdatedAt = peopleCursor.getString(peopleCursor.getColumnIndex(PersonModel.KEY_UPDATED_AT));
                personJSON.put(PersonModel.KEY_ROW_ID, personId);
                personJSON.put(PersonModel.KEY_USERNAME, personUsername);
                personJSON.put(PersonModel.KEY_PHONE_NUMBER, personPhone);
                personJSON.put(PersonModel.KEY_EMAIL_ID, personEmail);
                personJSON.put(PersonModel.KEY_METADATA, personMetadata);
                personJSON.put(PersonModel.KEY_UUID, personUuid);
                personJSON.put(PersonModel.KEY_CREATED_AT, personCreatedAt);
                personJSON.put(PersonModel.KEY_UPDATED_AT, personUpdatedAt);

                // pushing single person info into the people JSON array
                peopleJSONArray.put(personJSON);
            }
            // set all the people to the exports JSON
            exportJSON.put("people", peopleJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exportJSON;
    }
}
