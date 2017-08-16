package com.example.nagasudhir.debtonator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Nagasudhir on 8/12/2017.
 */

public class TransactionContributionProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.example.nagasudhir.debtonator.transaction_contribution";

    /**
     * A uri to do operations on transaction_contributions table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transaction_contributions");

    /**
     * Constants to identify the requested operation
     */
    private static final int TRANSACTION_CONTRIBUTIONS = 1;
    private static final int TRANSACTION_CONTRIBUTION = 2;
    private static final int TRANSACTION_CONTRIBUTION_BY_TRANSACTION_DETAIL = 3;
    private static final int TRANSACTION_CONTRIBUTION_UPSERT = 4;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_contributions", TRANSACTION_CONTRIBUTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_contributions/#", TRANSACTION_CONTRIBUTION);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_contributions/by_transaction_detail/#", TRANSACTION_CONTRIBUTION_BY_TRANSACTION_DETAIL);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_contributions/upsert", TRANSACTION_CONTRIBUTION_UPSERT);
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
        if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTIONS) {
            return TransactionContributionModel.getAllTransactionsContributions(mAppDB);
        } else if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION) {
            return TransactionContributionModel.getTransactionContributionById(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION_BY_TRANSACTION_DETAIL) {
            return TransactionContributionModel.getTransactionContributionByTransactionDetailId(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION_UPSERT) {
            return TransactionContributionModel.upsertTransactionContribution(mAppDB, selectionArgs[0], selectionArgs[1], selectionArgs[2], selectionArgs[3]);
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION_BY_TRANSACTION_DETAIL) {
            return (int) TransactionContributionModel.deleteTransactionContribution(mAppDB, "transactions_details_id=?", selectionArgs);
        } else if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION) {
            return (int) TransactionContributionModel.deleteTransactionContribution(mAppDB, "id=?", selectionArgs);
        } else if (uriMatcher.match(uri) == TRANSACTION_CONTRIBUTION_UPSERT) {
            return (int) TransactionContributionModel.deleteTransactionContribution(mAppDB, "transactions_details_id=? AND  people_details_id=?", selectionArgs);
        }
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = TransactionContributionModel.insertTransactionContribution(mAppDB, values);
        return Uri.parse(Person.CONTENT_URI + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return (int) TransactionContributionModel.updateTransactionContribution(mAppDB, values, selection, selectionArgs);
    }
}
