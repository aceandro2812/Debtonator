package com.example.nagasudhir.debtonator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Nagasudhir on 8/4/2017.
 */

public class TransactionProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.example.nagasudhir.debtonator.transaction";

    /**
     * A uri to do operations on transactions_details table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transactions");

    /**
     * Constants to identify the requested operation
     */
    private static final int TRANSACTIONS = 1;
    private static final int TRANSACTION = 2;
    private static final int TRANSACTION_BY_SET = 3;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transactions", TRANSACTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "transactions/#", TRANSACTION);
        uriMatcher.addURI(PROVIDER_NAME, "transactions/by_transaction_set/#", TRANSACTION_BY_SET);
    }

    /**
     * This content provider does the database operations by this object
     */
    SQLiteDatabase mAppDB;

    /**
     * A callback method which is invoked when the content provider is starting up
     */
    @Override
    public boolean onCreate() {
        mAppDB = new AppDB(getContext()).getWritableDB();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * A callback method which is by the default content uri
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == TRANSACTIONS) {
            return TransactionModel.getAllTransactions(mAppDB);
        } else if (uriMatcher.match(uri) == TRANSACTION) {
            return TransactionModel.getTransactionById(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == TRANSACTION_BY_SET) {
            return TransactionModel.getTransactionByTransactionSetId(mAppDB, uri.getLastPathSegment());
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == TRANSACTION_BY_SET) {
            return (int) TransactionModel.deleteTransaction(mAppDB, "id=?", selectionArgs);
        } else if (uriMatcher.match(uri) == TRANSACTION) {
            return (int) TransactionModel.deleteTransaction(mAppDB, "id=?", selectionArgs);
        }
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = TransactionModel.insertTransaction(mAppDB, values);
        return Uri.parse(Person.CONTENT_URI + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return (int) TransactionModel.updateTransaction(mAppDB, values, selection, selectionArgs);
    }
}
