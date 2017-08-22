package com.example.nagasudhir.debtonator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Nagasudhir on 8/22/2017.
 */

public class TransactionTagProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.example.nagasudhir.debtonator.transaction_tag";

    /**
     * A uri to do operations on transactions_details table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transaction_tags");

    /**
     * Constants to identify the requested operation
     */
    private static final int TRANSACTION_TAGS = 1;
    private static final int TRANSACTION_TAG = 2;
    private static final int TRANSACTION_TAG_BY_TRANSACTION_DETAIL = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_tags", TRANSACTION_TAGS);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_tags/#", TRANSACTION_TAG);
        uriMatcher.addURI(PROVIDER_NAME, "transaction_tags/transaction_detail/#", TRANSACTION_TAG_BY_TRANSACTION_DETAIL);
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
        if (uriMatcher.match(uri) == TRANSACTION_TAGS) {
            return TransactionTagModel.getAllDistinctTransactionTags(mAppDB);
        } else if (uriMatcher.match(uri) == TRANSACTION_TAG) {
            return TransactionTagModel.getTransactionTagById(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == TRANSACTION_TAG_BY_TRANSACTION_DETAIL) {
            return TransactionTagModel.getTransactionTagsByTransactionDetailId(mAppDB, uri.getLastPathSegment());
        } else {
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = TransactionTagModel.insertTransactionTag(mAppDB, values);
        return Uri.parse(TransactionTagProvider.CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return (int) TransactionTagModel.updateTransactionTag(mAppDB, values, selection, selectionArgs);
    }
}
