package com.example.nagasudhir.debtonator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Nagasudhir on 8/3/2017.
 */

/**
 * A custom Content Provider to do the database operations
 */

public class Person extends ContentProvider {
    public static final String PROVIDER_NAME = "com.example.nagasudhir.debtonator.person";

    /**
     * A uri to do operations on people_details table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/persons");

    /**
     * Constants to identify the requested operation
     */
    private static final int PERSONS = 1;
    private static final int PERSON = 2;
    private static final int PERSONS_BY_TRANSACTION_SET = 3;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "persons", PERSONS);
        uriMatcher.addURI(PROVIDER_NAME, "persons/#", PERSON);
        uriMatcher.addURI(PROVIDER_NAME, "persons/by_transaction_set/#", PERSONS_BY_TRANSACTION_SET);
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

        if (uriMatcher.match(uri) == PERSONS) {
            return PersonModel.getAllPersons(mAppDB);
        } else if (uriMatcher.match(uri) == PERSONS_BY_TRANSACTION_SET) {
            return PersonModel.getTransactionSetPersonsInDetail(mAppDB, uri.getLastPathSegment());
        } else if (uriMatcher.match(uri) == PERSON) {
            return PersonModel.getPersonById(mAppDB, uri.getLastPathSegment());
        } else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return (int) PersonModel.deletePerson(mAppDB, selection, selectionArgs);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = PersonModel.insertPerson(mAppDB, values);
        return Uri.parse(Person.CONTENT_URI + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return (int) PersonModel.updatePerson(mAppDB, values, selection, selectionArgs);
    }
}
