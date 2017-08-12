package com.example.nagasudhir.debtonator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nagasudhir on 8/3/2017.
 */

public class AppDB extends SQLiteOpenHelper {
    /**
     * Database name
     */
    private static String DBNAME = "debtonator_2";

    /**
     * Version number of the database
     */
    private static int VERSION = 1;

    /**
     * An instance variable for SQLiteDatabase
     */
    private SQLiteDatabase mDB;

    /**
     * Constructor
     */
    public AppDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    /*
    Returns the Writable Database instance variable
     */
    public SQLiteDatabase getWritableDB() {
        return mDB;
    }

    /**
     * This is a callback method, invoked when the method
     * getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("BEGIN TRANSACTION");
        // People table creation sql
        /*
        String sql = "DROP TABLE IF EXISTS people_details;";
        db.execSQL(sql);
        sql = "CREATE TABLE people_details (" +
                "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "`username` TEXT NOT NULL UNIQUE," +
                "`phone_number` TEXT UNIQUE," +
                "`email_id` TEXT," +
                "`metadata` TEXT," +
                "`created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "`updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(sql);
        sql = "CREATE TRIGGER people_details_updated_at_trigger AFTER \n" +
                "  UPDATE \n" +
                "  ON people_details BEGIN \n" +
                "  UPDATE people_details \n" +
                "  SET    updated_at = DATETIME('now') \n" +
                "  WHERE  id = NEW.id; \n" +
                "END";
        db.execSQL(sql);
        PersonModel.insertSeeds(db);
        // People table creation sql

        // Transaction Sets creation SQL
        sql = "DROP TABLE IF EXISTS transaction_sets";
        db.execSQL(sql);
        sql = "CREATE TABLE transaction_sets (" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                " `name_string` TEXT NOT NULL UNIQUE, " +
                " `metadata` TEXT, " +
                " `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                " `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP " +
                ")";
        db.execSQL(sql);
        sql = "CREATE TRIGGER transaction_sets_updated_at_trigger AFTER \n" +
                " UPDATE \n" +
                " ON transaction_sets BEGIN \n" +
                " UPDATE transaction_sets \n" +
                " SET    updated_at = DATETIME('now') \n" +
                " WHERE  id = NEW.id; \n" +
                " END";
        db.execSQL(sql);
        TransactionSetModel.insertSeeds(db);

        // Transactions Details creation SQL
        sql = "DROP TABLE IF EXISTS transaction_sets";
        db.execSQL(sql);
        sql = "CREATE TABLE \"transactions_details( " +
                "`id`                  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "`transaction_sets_id` INTEGER NOT NULL, " +
                "`description` TEXT, " +
                "`metadata` TEXT, " +
                "`transaction_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "`created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "`updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(`transaction_sets_id`) REFERENCES `transaction_sets`(`id`) ON" +
                "DELETE CASCADE " +
                "ON " +
                "UPDATE CASCADE " +
                ");";
        */
        String sql = "DROP TABLE IF EXISTS people_details;\n" +
                "DROP TABLE IF EXISTS transaction_contributions;\n" +
                "DROP TABLE IF EXISTS transaction_tags;\n" +
                "DROP TABLE IF EXISTS transactions_details;\n" +
                "DROP TABLE IF EXISTS transaction_sets;\n" +
                "CREATE TABLE \"people_details\" \n" +
                "             ( \n" +
                "                          `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "                          `username` TEXT NOT NULL UNIQUE, \n" +
                "                          `phone_number` TEXT UNIQUE, \n" +
                "                          `email_id` TEXT, \n" +
                "                          `metadata` TEXT, \n" +
                "                          `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                "                          `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP \n" +
                "             );\n" +
                "CREATE TABLE \"transactions_details\" \n" +
                "             ( \n" +
                "                          `id`                  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "                          `transaction_sets_id` INTEGER NOT NULL, \n" +
                "                          `description` TEXT, \n" +
                "                          `metadata` TEXT, \n" +
                "                          `transaction_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                "                          `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                "                          `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                "                          FOREIGN KEY(`transaction_sets_id`) REFERENCES `transaction_sets`(`id`) ON\n" +
                "             DELETE CASCADE \n" +
                "             ON \n" +
                "             UPDATE CASCADE \n" +
                "             );\n" +
                "CREATE TABLE \"transaction_sets\" \n" +
                "                   ( \n" +
                "                                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "                                `name_string` TEXT NOT NULL UNIQUE, \n" +
                "                                `metadata` TEXT, \n" +
                "                                `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP, \n" +
                "                                `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP \n" +
                "                   );\n" +
                "CREATE TABLE \"transaction_tags\" \n" +
                "                   ( \n" +
                "                                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "                                `name_string` TEXT NOT NULL, \n" +
                "                                `transactions_details_id` INTEGER NOT NULL, \n" +
                "                                `updated_at`              INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "                                FOREIGN KEY(`transactions_details_id`) REFERENCES `transactions_details`(`id`) ON\n" +
                "                   UPDATE CASCADE \n" +
                "                   ON \n" +
                "                   DELETE CASCADE \n" +
                "                   );\n" +
                "CREATE TABLE \"transaction_contributions\" \n" +
                "                         ( \n" +
                "                                      `id`                      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "                                      `transactions_details_id` INTEGER NOT NULL, \n" +
                "                                      `people_details_id`       INTEGER NOT NULL, \n" +
                "                                      `contribution`            REAL NOT NULL DEFAULT 0, \n" +
                "                                      `is_consumer`             INTEGER NOT NULL DEFAULT 0, \n" +
                "                                      FOREIGN KEY(`transactions_details_id`) REFERENCES `transactions_details`(`id`) ON\n" +
                "                         UPDATE CASCADE \n" +
                "                         ON \n" +
                "                         DELETE CASCADE, \n" +
                "                                FOREIGN KEY(`people_details_id`) REFERENCES `people_details`(`id`)\n" +
                "                         ON \n" +
                "                         UPDATE CASCADE \n" +
                "                         ON \n" +
                "                         DELETE CASCADE \n" +
                "                         );\n" +
                "CREATE INDEX `transactions_details_transaction_sets_id_index` \n" +
                "                  ON `transactions_details` \n" +
                "                               ( \n" +
                "                                            `transaction_sets_id` \n" +
                "                               );\n" +
                "CREATE UNIQUE INDEX `trasaction_contributions_people_transaction_details_unique` \n" +
                "                  ON `transaction_contributions` \n" +
                "                                      ( \n" +
                "                                                          `transactions_details_id` , \n" +
                "                                                          `people_details_id` \n" +
                "                                      );\n" +
                "CREATE TRIGGER people_details_updated_at_trigger AFTER \n" +
                "                    UPDATE \n" +
                "                    ON people_details BEGIN \n" +
                "                    UPDATE people_details \n" +
                "                    SET    updated_at = DATETIME('now') \n" +
                "                    WHERE  id = NEW.id; \n" +
                "                   \n" +
                "                  END;\n" +
                "CREATE TRIGGER transaction_sets_updated_at_trigger AFTER \n" +
                "                    UPDATE \n" +
                "                    ON transaction_sets BEGIN \n" +
                "                    UPDATE transaction_sets \n" +
                "                    SET    updated_at = DATETIME('now') \n" +
                "                    WHERE  id = NEW.id; \n" +
                "                   \n" +
                "                  END;\n" +
                "CREATE TRIGGER transaction_tags_updated_at_trigger AFTER \n" +
                "                    UPDATE \n" +
                "                    ON transaction_tags BEGIN \n" +
                "                    UPDATE transaction_tags \n" +
                "                    SET    updated_at = DATETIME('now') \n" +
                "                    WHERE  id = NEW.id; \n" +
                "                   \n" +
                "                  END;\n";
        // Inserting seeds into the tables
        sql += "INSERT INTO `transaction_contributions` VALUES (1,1,3,125.0,0);\n" +
                "INSERT INTO `transaction_contributions` VALUES (2,1,1,450.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (3,1,4,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (4,1,5,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (5,2,2,1010.0,0);\n" +
                "INSERT INTO `transaction_contributions` VALUES (6,2,4,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (7,3,2,5000.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (8,3,3,1200.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (12,3,4,10000.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (13,3,1,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (14,3,5,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (15,6,1,182.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (16,6,3,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (17,6,4,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (18,6,5,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (19,7,4,69.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (20,8,1,20.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (21,8,2,150.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (22,9,1,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (23,9,2,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (24,9,3,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (25,9,4,0.0,1);\n" +
                "INSERT INTO `transaction_contributions` VALUES (26,9,5,326.0,1);\n" +
                "INSERT INTO `transactions_details` VALUES (1,1,'electricity bill','elec bill','2017-06-28 17:14:59','2017-06-27 18:16:04','2017-06-27 18:16:04');\n" +
                "INSERT INTO `transactions_details` VALUES (2,1,'debt','sudhir to naveen','2017-06-28 17:14:59','2017-06-28 16:46:31','2017-06-28 16:46:31');\n" +
                "INSERT INTO `transactions_details` VALUES (3,1,'trip','trip to mars','2017-06-28 17:14:59','2017-06-28 16:46:49','2017-06-28 16:46:49');\n" +
                "INSERT INTO `transactions_details` VALUES (6,3,'rice','rice for roommates','2017-07-01 15:15:09','2017-07-01 15:15:09','2017-07-01 15:15:09');\n" +
                "INSERT INTO `transactions_details` VALUES (7,3,'pasta','just for fun','2017-08-08 15:06:23','2017-08-08 15:06:23','2017-08-08 15:06:23');\n" +
                "INSERT INTO `transactions_details` VALUES (8,3,'biriyani','party by kishore','2017-08-08 15:06:56','2017-08-08 15:06:56','2017-08-08 15:06:56');\n" +
                "INSERT INTO `transactions_details` VALUES (9,3,'groceries',NULL,'2017-08-08 15:07:26','2017-08-08 15:07:26','2017-08-08 15:07:26');\n" +
                "INSERT INTO `transaction_tags` VALUES (1,'mandatory',1,'2017-08-08 15:28:26');\n" +
                "INSERT INTO `transaction_tags` VALUES (2,'room',1,'2017-08-08 15:28:44');\n" +
                "INSERT INTO `transaction_tags` VALUES (3,'debt',2,'2017-08-08 15:29:02');\n" +
                "INSERT INTO `transaction_tags` VALUES (4,'luxury',3,'2017-08-08 15:29:17');\n" +
                "INSERT INTO `transaction_tags` VALUES (5,'partying',3,'2017-08-08 15:29:41');\n" +
                "INSERT INTO `transaction_tags` VALUES (6,'mandatory',6,'2017-08-08 15:31:24');\n" +
                "INSERT INTO `transaction_tags` VALUES (7,'groceries',6,'2017-08-08 15:31:40');\n" +
                "INSERT INTO `transaction_tags` VALUES (8,'room',6,'2017-08-08 15:31:48');\n" +
                "INSERT INTO `transaction_tags` VALUES (9,'personal',7,'2017-08-08 15:32:06');\n" +
                "INSERT INTO `transaction_tags` VALUES (10,'party',8,'2017-08-08 15:32:19');\n" +
                "INSERT INTO `transaction_tags` VALUES (11,'groceries',9,'2017-08-08 15:32:39');\n" +
                "INSERT INTO `transaction_tags` VALUES (12,'room',9,'2017-08-08 15:32:44');\n" +
                "INSERT INTO `transaction_sets` VALUES (1,'Aug2017','nothing','2017-06-27 18:13:35','2017-08-08 14:55:50');\n" +
                "INSERT INTO `transaction_sets` VALUES (3,'Sep2017','new_metadata','2017-07-01 16:17:12','2017-08-08 14:56:03');\n" +
                "INSERT INTO `people_details` VALUES (1,'SUDHIR','9819679462','nagasudhirpulla@gmail.com','nothing','2017-06-27 18:12:12','2017-07-02 07:55:49');\n" +
                "INSERT INTO `people_details` VALUES (2,'kishore','9888545242','kishore@gmail.com','na','2017-06-27 18:12:59','2017-06-27 18:13:24');\n" +
                "INSERT INTO `people_details` VALUES (3,'prashanth','1234567890','prashanth.eeenitw@gmail.com','nothing','2017-06-28 16:42:22','2017-06-28 16:43:36');\n" +
                "INSERT INTO `people_details` VALUES (4,'aditya','3451842451','adityamahesh810@gmail.com','stupid fellow','2017-06-28 16:43:41','2017-06-28 16:44:55');\n" +
                "INSERT INTO `people_details` VALUES (5,'naveen','2645542155','kotinaveen@gmail.com','','2017-06-28 16:44:39','2017-06-28 16:45:50');\n";
        String[] parts = sql.split(";\\n");
        for (int i = 0; i < parts.length; i++) {
            db.execSQL(parts[i]);
        }
        db.execSQL("COMMIT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
