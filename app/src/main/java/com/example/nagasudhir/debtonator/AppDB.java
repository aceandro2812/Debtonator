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
                "CREATE TABLE \"people_details\" (\n" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " `username` TEXT NOT NULL UNIQUE,\n" +
                " `phone_number` TEXT UNIQUE,\n" +
                " `email_id` TEXT,\n" +
                " `metadata` TEXT,\n" +
                " `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                ");\n" +

                "CREATE TABLE \"transactions_details\" (\n" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " `transaction_sets_id` INTEGER NOT NULL,\n" +
                " `description` TEXT,\n" +
                " `metadata` TEXT,\n" +
                " `transaction_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " FOREIGN KEY(`transaction_sets_id`) REFERENCES `transaction_sets`(`id`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ");\n" +

                "CREATE TABLE \"transaction_contributions\" (\n" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " `transactions_details_id` INTEGER NOT NULL,\n" +
                " `people_details_id` INTEGER NOT NULL,\n" +
                " `contribution` REAL NOT NULL DEFAULT 0,\n" +
                " `is_consumer` INTEGER NOT NULL DEFAULT 0,\n" +
                " FOREIGN KEY(`transactions_details_id`) REFERENCES `transactions_details`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,\n" +
                " FOREIGN KEY(`people_details_id`) REFERENCES `people_details`(`id`) ON UPDATE CASCADE ON DELETE CASCADE\n" +
                ");\n" +

                "CREATE TABLE \"transaction_sets\" (\n" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " `name_string` TEXT NOT NULL UNIQUE,\n" +
                " `metadata` TEXT,\n" +
                " `created_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                ");\n" +

                "CREATE TABLE \"transaction_tags\" (\n" +
                " `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                " `name_string` TEXT NOT NULL,\n" +
                " `transactions_details_id` INTEGER NOT NULL,\n" +
                " `updated_at` INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " FOREIGN KEY(`transactions_details_id`) REFERENCES `transactions_details`(`id`) ON UPDATE CASCADE ON DELETE CASCADE\n" +
                ");\n" +

                "CREATE INDEX `transactions_details_transaction_sets_id_index` ON `transactions_details` (`transaction_sets_id` );\n" +

                "CREATE UNIQUE INDEX `transaction_contributions_people_transaction_details_unique` ON \"transaction_contributions\" (`transactions_details_id` ,`people_details_id` );\n" +

                "CREATE UNIQUE INDEX `transaction_tags_unique_tag_index` ON `transaction_tags` (`name_string` ,`transactions_details_id` );\n" +

                "CREATE TRIGGER people_details_updated_at_trigger AFTER \n" +
                "  UPDATE \n" +
                "  ON people_details BEGIN \n" +
                "  UPDATE people_details \n" +
                "  SET    updated_at = DATETIME('now') \n" +
                "  WHERE  id = NEW.id; \n" +
                "END;\n" +
                "CREATE TRIGGER transaction_sets_updated_at_trigger AFTER \n" +
                "  UPDATE \n" +
                "  ON transaction_sets BEGIN \n" +
                "  UPDATE transaction_sets \n" +
                "  SET    updated_at = DATETIME('now') \n" +
                "  WHERE  id = NEW.id; \n" +
                "END;\n" +
                "CREATE TRIGGER transaction_tags_updated_at_trigger AFTER \n" +
                "  UPDATE \n" +
                "  ON transaction_tags BEGIN \n" +
                "  UPDATE transaction_tags \n" +
                "  SET    updated_at = DATETIME('now') \n" +
                "  WHERE  id = NEW.id; \n" +
                "END;\n";
        // Inserting seeds into the tables
        sql += "INSERT INTO transactions_details VALUES (1,1,'Debt','Sudhir to Prashant','2017-08-13 09:48:11','2017-08-13 09:48:11','2017-08-13 09:48:11');\n" +
                "INSERT INTO transactions_details VALUES (2,1,'Toll Fare','Mumbai to Lonavala','2017-08-13 08:32:00','2017-08-30 07:51:17','2017-08-30 07:51:17');\n" +
                "INSERT INTO transactions_details VALUES (3,1,'Breakfast','At Mumbai Border','2017-08-13 09:00:00','2017-08-30 07:53:37','2017-08-30 07:53:37');\n" +
                "INSERT INTO transactions_details VALUES (4,1,'Toll','Mumbai to Lonavala','2017-08-13 09:30:00','2017-08-30 07:56:05','2017-08-30 07:56:05');\n" +
                "INSERT INTO transactions_details VALUES (5,1,'Diesel',NULL,'2017-08-13 11:00:00','2017-08-30 07:57:59','2017-08-30 07:57:59');\n" +
                "INSERT INTO transactions_details VALUES (6,1,'Parking','At Bhushi Dam','2017-08-13 12:02:00','2017-08-30 07:59:56','2017-08-30 07:59:56');\n" +
                "INSERT INTO transactions_details VALUES (7,1,'Pants','Because of rain','2017-08-13 12:58:00','2017-08-30 08:01:51','2017-08-30 08:01:51');\n" +
                "INSERT INTO transactions_details VALUES (8,1,'Driver DA',NULL,'2017-08-13 13:30:00','2017-08-30 08:03:33','2017-08-30 08:03:33');\n" +
                "INSERT INTO transactions_details VALUES (9,1,'Lunch',NULL,'2017-08-13 13:45:00','2017-08-30 08:06:12','2017-08-30 08:06:12');\n" +
                "INSERT INTO transactions_details VALUES (10,1,'Parking',NULL,'2017-08-13 15:30:00','2017-08-30 08:06:53','2017-08-30 08:06:53');\n" +
                "INSERT INTO transactions_details VALUES (11,1,'Snacks',NULL,'2017-08-13 16:36:00','2017-08-30 08:08:36','2017-08-30 08:08:36');\n" +
                "INSERT INTO transactions_details VALUES (12,1,'Toll','Return from Lonavala to Mumbai','2017-08-13 18:49:00','2017-08-30 08:10:20','2017-08-30 08:10:20');\n" +
                "INSERT INTO transactions_details VALUES (13,1,'Car Rent',NULL,'2017-08-13 20:53:00','2017-08-30 08:11:56','2017-08-30 08:11:56');\n" +
                "INSERT INTO transactions_details VALUES (14,1,'Chicken Biriyani','Maid did not have keys','2017-08-13 21:22:00','2017-08-30 08:14:03','2017-08-30 08:14:03');\n" +
                "INSERT INTO transactions_details VALUES (15,1,'Veg Biriyani',NULL,'2017-08-13 21:33:00','2017-08-30 08:16:15','2017-08-30 08:16:15');\n" +
                "INSERT INTO transaction_tags VALUES (1,'debt',1,'2017-08-30 08:17:38');\n" +
                "INSERT INTO transaction_tags VALUES (2,'travel',2,'2017-08-30 08:17:53');\n" +
                "INSERT INTO transaction_tags VALUES (3,'food',3,'2017-08-30 08:18:30');\n" +
                "INSERT INTO transaction_tags VALUES (4,'travel',4,'2017-08-30 08:18:47');\n" +
                "INSERT INTO transaction_tags VALUES (5,'travel',5,'2017-08-30 08:19:00');\n" +
                "INSERT INTO transaction_tags VALUES (6,'travel',6,'2017-08-30 08:19:09');\n" +
                "INSERT INTO transaction_tags VALUES (7,'clothes',7,'2017-08-30 08:19:27');\n" +
                "INSERT INTO transaction_tags VALUES (8,'travel',8,'2017-08-30 08:19:36');\n" +
                "INSERT INTO transaction_tags VALUES (9,'food',9,'2017-08-30 08:19:45');\n" +
                "INSERT INTO transaction_tags VALUES (10,'travel',10,'2017-08-30 08:19:56');\n" +
                "INSERT INTO transaction_tags VALUES (11,'food',11,'2017-08-30 08:20:06');\n" +
                "INSERT INTO transaction_tags VALUES (12,'travel',12,'2017-08-30 08:20:19');\n" +
                "INSERT INTO transaction_tags VALUES (13,'travel',13,'2017-08-30 08:20:30');\n" +
                "INSERT INTO transaction_tags VALUES (14,'debt',14,'2017-08-30 08:20:49');\n" +
                "INSERT INTO transaction_tags VALUES (15,'food',14,'2017-08-30 08:21:05');\n" +
                "INSERT INTO transaction_tags VALUES (16,'personal',15,'2017-08-30 08:21:16');\n" +
                "INSERT INTO transaction_tags VALUES (17,'food',15,'2017-08-30 08:21:24');\n" +
                "INSERT INTO transaction_sets VALUES (1,'Example set','A trip to Lonavala','2017-08-30 07:47:38','2017-08-30 07:48:00');\n" +
                "INSERT INTO transaction_contributions VALUES (1,1,1,500.0,0);\n" +
                "INSERT INTO transaction_contributions VALUES (2,1,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (3,2,4,35.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (4,2,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (5,2,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (6,2,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (7,3,1,172.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (8,3,2,165.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (9,3,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (10,3,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (11,4,1,138.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (12,4,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (13,4,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (14,4,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (15,5,1,1500.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (16,5,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (17,5,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (18,5,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (19,6,1,100.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (20,6,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (21,6,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (22,6,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (23,7,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (24,7,4,250.0,0);\n" +
                "INSERT INTO transaction_contributions VALUES (25,8,4,300.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (26,8,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (27,8,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (28,8,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (29,9,4,550.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (30,9,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (31,9,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (32,9,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (33,10,4,50.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (34,10,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (35,10,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (36,10,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (37,11,4,150.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (38,11,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (39,11,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (40,11,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (41,12,1,173.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (42,12,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (43,12,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (44,12,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (45,13,2,2000.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (46,13,1,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (47,13,3,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (48,13,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (49,14,1,200.0,0);\n" +
                "INSERT INTO transaction_contributions VALUES (50,14,2,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (51,14,4,0.0,1);\n" +
                "INSERT INTO transaction_contributions VALUES (52,15,1,150.0,1);\n" +
                "INSERT INTO people_details VALUES (1,'Sudhir','1234567890','abcd@xyzc.com','Awesome Person','2017-08-30 07:43:04','2017-08-30 07:46:09');\n" +
                "INSERT INTO people_details VALUES (2,'Prashant','0987654321','xyzc@abcd.com','Good person','2017-08-30 07:43:56','2017-08-30 07:44:45');\n" +
                "INSERT INTO people_details VALUES (3,'Mastan','4561237890','njslx@ksfn.com','Nerd','2017-08-30 07:44:49','2017-08-30 07:45:57');\n" +
                "INSERT INTO people_details VALUES (4,'Venky','8521473695',NULL,NULL,'2017-08-30 07:46:13','2017-08-30 07:47:26');\n";
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
