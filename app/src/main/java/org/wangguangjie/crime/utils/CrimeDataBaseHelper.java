package org.wangguangjie.crime.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.wangguangjie.crime.utils.CrimeDbScheme;

/**
 * Created by wangguangjie on 2017/10/23.
 */

public class CrimeDataBaseHelper extends SQLiteOpenHelper {

    private static final int version=1;
    private static final String DATABASE_NAME="crime.db";

    public CrimeDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ CrimeDbScheme.CrimeTable.NAME+"("
        +"_id integer primary key autoincrement, "+
                CrimeDbScheme.CrimeTable.Cols.UUID+", "+
                CrimeDbScheme.CrimeTable.Cols.TITLE+", "+
                CrimeDbScheme.CrimeTable.Cols.DATE+", "+
                CrimeDbScheme.CrimeTable.Cols.SOLVED+", "+
                CrimeDbScheme.CrimeTable.Cols.SUSPECT+","+
                CrimeDbScheme.CrimeTable.Cols.PHONENUMBER+
                ")");
        //db.execSQL("");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}


