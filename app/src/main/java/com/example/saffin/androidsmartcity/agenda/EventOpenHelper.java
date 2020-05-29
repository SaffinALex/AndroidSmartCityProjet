package com.example.saffin.androidsmartcity.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Th0ma on 28/05/2020
 */
public class EventOpenHelper extends SQLiteOpenHelper {

    private final static int BASE_VERSION = 1;
    private final static String TABLE_EVENTS = "table_events";
    private final static String BASE_NOM = "events.db";

    public static final String COLONNE_JOUR = "JOUR";
    public static final String COLONNE_HEURES = "HEURES";
    public static final String COLONNE_TITRE = "TITRE";
    public static final String COLONNE_DETAILS = "DETAILS";

    private static final String REQUETE_CREATION_DB = "CREATE TABLE " + TABLE_EVENTS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLONNE_JOUR + " TEXT not null, "
            + COLONNE_HEURES + " TEXT not null, "
            + COLONNE_TITRE + " TEXT not null, "
            + COLONNE_DETAILS + " TEXT not null)";

    public EventOpenHelper(Context context){
        super(context, BASE_NOM, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(REQUETE_CREATION_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_EVENTS);

        onCreate(sqLiteDatabase);
    }

    public SQLiteDatabase open(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db;
    }

    public boolean addData(String jour, String heures, String titre, String details ){
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLONNE_JOUR,jour);
        contentValues.put(COLONNE_HEURES,heures);
        contentValues.put(COLONNE_TITRE,titre);
        contentValues.put(COLONNE_DETAILS,details);


        long result = db.insert(TABLE_EVENTS, null, contentValues);
        return result != -1;
    }

    public Cursor showData() {
        SQLiteDatabase db =  this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
    }
}
