package com.example.nagasudhir.debtonator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nagasudhir on 8/3/2017.
 */

public class PersonModel {
    /**
     * Field 1 of the table people_details, which is the primary key
     */
    public static final String KEY_ROW_ID = "id";

    /**
     * Field 2 of the table people_details, stores the customer code
     */
    public static final String KEY_USERNAME = "username";

    /**
     * Field 3 of the table people_details, stores the customer name
     */
    public static final String KEY_PHONE_NUMBER = "phone_number";

    /**
     * Field 4 of the table people_details, stores the phone number of the customer
     */
    public static final String KEY_EMAIL_ID = "email_id";

    /**
     * Field 5 of the table people_details, stores the phone number of the customer
     */
    public static final String KEY_METADATA = "metadata";

    /**
     * Field 6 of the table people_details, stores the phone number of the customer
     */
    public static final String KEY_CREATED_AT = "created_at";

    /**
     * Field 7 of the table people_details, stores the phone number of the customer
     */
    public static final String KEY_UPDATED_AT = "updated_at";

    /**
     * A constant, stores the the table name
     */
    private static final String TABLE_NAME = "people_details";

    /**
     * Inserts seeds into the tables
     */
    public static void insertSeeds(SQLiteDatabase db) {
        // Create some rows
        PersonPojo[] persons = new PersonPojo[5];
        persons[0] = new PersonPojo("SUDHIR", "9819679462", "nagasudhirpulla@gmail.com", "nothing");
        persons[1] = new PersonPojo("kishore", "9888545242", "kishore@gmail.com", "na");
        persons[2] = new PersonPojo("prashanth", "1234567890", "prashanth.eeenitw@gmail.com", "nothing");
        persons[3] = new PersonPojo("aditya", "3451842451", "adityamahesh810@gmail.com", "stupid fellow");
        persons[4] = new PersonPojo("naveen", "2645542155", "kotinaveen@gmail.com", "");
        for (int i = 0; i < persons.length; i++) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_USERNAME, persons[i].getUsername());
            initialValues.put(KEY_PHONE_NUMBER, persons[i].getPhone());
            initialValues.put(KEY_EMAIL_ID, persons[i].getEmail());
            initialValues.put(KEY_METADATA, persons[i].getMetadata());
            db.insert(TABLE_NAME, null, initialValues);
        }
        /*sql = "INSERT INTO `people_details` VALUES (1,'SUDHIR','9819679462','nagasudhirpulla@gmail.com','nothing',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
        db.execSQL(sql);
        sql = "INSERT INTO `people_details` VALUES (2,'kishore','9888545242','kishore@gmail.com','na',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
        db.execSQL(sql);
        sql = "INSERT INTO `people_details` VALUES (3,'prashanth','1234567890','prashanth.eeenitw@gmail.com','nothing',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
        db.execSQL(sql);
        sql = "INSERT INTO `people_details` VALUES (4,'aditya','3451842451','adityamahesh810@gmail.com','stupid fellow',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
        db.execSQL(sql);
        sql = "INSERT INTO `people_details` VALUES (5,'naveen','2645542155','kotinaveen@gmail.com','',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);";
        db.execSQL(sql);*/
    }

    /**
     * Returns all the persons in the table
     */
    public static Cursor getAllPersons(SQLiteDatabase db) {
        return db.rawQuery("SELECT id AS _id, * FROM people_details;", null);
    }

    /**
     * Returns a person in the table
     */
    public static Cursor getPersonById(SQLiteDatabase db, String idString) {
        return db.rawQuery("SELECT id AS _id, * FROM people_details WHERE id = ?;", new String[]{idString});
    }

    /**
     * Creates a the person in the table
     */
    public static long insertPerson(SQLiteDatabase db, ContentValues values) {
        return db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Updates a person in the table
     */
    public static long updatePerson(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.updateWithOnConflict(TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Deletes a person in the table
     */
    public static long deletePerson(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(TABLE_NAME, selection, selectionArgs);
    }
}
