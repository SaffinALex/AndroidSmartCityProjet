package com.example.saffin.androidsmartcity.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Th0ma on 29/05/2020
 */
public class EventDatabase {

    private EventOpenHelper dbHelper;

    private SQLiteDatabase database;

    private final static String TABLE_EVENTS = "table_events"; // name of table

    public static final String COLONNE_JOUR = "JOUR";
    public static final String COLONNE_HEURES = "HEURES";
    public static final String COLONNE_TITRE = "TITRE";
    public static final String COLONNE_DETAILS = "DETAILS";

        /**
         *
         * @param context
         */
        public EventDatabase(Context context){
            dbHelper = new EventOpenHelper(context);
            database = dbHelper.getWritableDatabase();
        }


        public boolean createRecords(String jour, String heures, String titre, String details ){
            ContentValues contentValues = new ContentValues();

            contentValues.put(COLONNE_JOUR,jour);
            contentValues.put(COLONNE_HEURES,heures);
            contentValues.put(COLONNE_TITRE,titre);
            contentValues.put(COLONNE_DETAILS,details);

            long result = database.insert(TABLE_EVENTS, null, contentValues);
            return result != -1;
        }

        public Cursor selectRecords() {
            String[] cols = new String[] {COLONNE_JOUR, COLONNE_HEURES,COLONNE_TITRE,COLONNE_DETAILS};
            Cursor mCursor = database.query(true, TABLE_EVENTS,null,null
                    , null, null, null, null, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor; // iterate to get each value.
        }

        public Cursor selectRecordsOfTheDay(String day){
            String [] whereArguments = new String[] {day};
            String whereClause = COLONNE_JOUR + " = ?";
           // Cursor mCursor = database.query(true, TABLE_EVENTS,null,whereClause, null, null, null, null, null);
            Cursor mCursor = database.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + whereClause,whereArguments);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor; // iterate to get each value.
        }
}
