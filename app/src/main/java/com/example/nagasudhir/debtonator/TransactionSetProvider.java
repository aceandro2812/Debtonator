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

public class TransactionSetProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.example.nagasudhir.debtonator.transaction_set";

    /**
     * A uri to do operations on transactions_details table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transaction_sets");

    /**
     * Constants to identify the requested operation
     */
    private static final int TRANSACTION_SETS = 1;
    private static final int TRANSACTION_SET = 2;
    private static final int TRANSACTION_SET_BY_NAME = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_sets", TRANSACTION_SETS);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_sets/#", TRANSACTION_SET);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_sets/name/#", TRANSACTION_SET_BY_NAME);
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
        if (uriMatcher.match(uri) == TRANSACTION_SETS) {
            return TransactionSetModel.getAllTransactionSets(mAppDB);
        } else if (uriMatcher.match(uri) == TRANSACTION_SET) {
            return TransactionSetModel.getTransactionSetById(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == TRANSACTION_SET_BY_NAME) {
            return TransactionSetModel.getTransactionSetByName(mAppDB, uri.getLastPathSegment());
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == TRANSACTION_SET_BY_NAME) {
            return (int) TransactionSetModel.deleteTransactionSet(mAppDB, "name_string=?", selectionArgs);
        } else if (uriMatcher.match(uri) == TRANSACTION_SET) {
            return (int) TransactionSetModel.deleteTransactionSet(mAppDB, "id=?", selectionArgs);
        }
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = TransactionSetModel.insertTransactionSet(mAppDB, values);
        return Uri.parse(Person.CONTENT_URI + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return (int) TransactionSetModel.updateTransactionSet(mAppDB, values, selection, selectionArgs);
    }
}
