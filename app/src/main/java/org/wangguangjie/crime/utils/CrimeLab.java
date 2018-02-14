package org.wangguangjie.crime.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import org.wangguangjie.crime.Model.Crime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by wangguangjie on 2017/10/16.
 */

public class CrimeLab {
    //private List<Crime> mCrimes;
    private static CrimeLab sCrimeLab;
    private SQLiteDatabase mSQLiteDatabase;


    private Context mContext;

    private CrimeLab(Context context){
//        mCrimes=new ArrayList<>();
//        for(int i=0;i<100;i++){
//            Crime crime=new Crime();
//            crime.setTitle("Crime #"+i);
//            crime.setSolved(i%2==0);
//            mCrimes.add(crime);
//        }
        mSQLiteDatabase=new CrimeDataBaseHelper(context.getApplicationContext()).getWritableDatabase();
        mContext=context;
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab==null)
        {
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }
    public List<Crime> getCrimes(){
        List<Crime> list=new ArrayList<>();
        CrimeCursorWrapper cursor=new CrimeCursorWrapper(queryCrimes(null,null));
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                list.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return list;
    }
//    public Crime getCrime(UUID uuid){
//        for(int i=0;i<mCrimes.size();i++){
//            if(mCrimes.get(i).getId().equals(uuid))
//                return mCrimes.get(i);
//        }
//        return null;
//    }
    public void addCrime(Crime crime){
        //mCrimes.add(crime);
        mSQLiteDatabase.insert(CrimeDbScheme.CrimeTable.NAME,null,getContentValues(crime));
    }
    private ContentValues getContentValues(Crime crime){
        ContentValues contentValues=new ContentValues();
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.UUID,crime.getId().toString());
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.TITLE,crime.getTitle());
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.DATE,crime.getDate().getTime());
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.SOLVED,crime.isSolved()?1:0);
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.SUSPECT,crime.getSuspect());
        contentValues.put(CrimeDbScheme.CrimeTable.Cols.PHONENUMBER,crime.getPhoneNumber());
        return contentValues;
    }

    public void deleteCrime(Crime crime){
        mSQLiteDatabase.delete(CrimeDbScheme.CrimeTable.NAME,CrimeDbScheme.CrimeTable.Cols.UUID+"=?",
                new String[]{crime.getId().toString()});
    }
    public void updateCrime(Crime crime){
        //String uuidString=crime.getId().toString();
        ContentValues contentValues=getContentValues(crime);
        mSQLiteDatabase.update(CrimeDbScheme.CrimeTable.NAME,contentValues,CrimeDbScheme.CrimeTable.Cols.UUID+"=?",
                new String[]{crime.getId().toString()});
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper crimeCursorWrapper=queryCrimes(CrimeDbScheme.CrimeTable.Cols.UUID+"=?",new String[]{id.toString()});
        if(crimeCursorWrapper.getCount()==0){
            return null;
        }
        try {
            crimeCursorWrapper.moveToFirst();
            return crimeCursorWrapper.getCrime();
        }
        finally {
            crimeCursorWrapper.close();
        }
    }
    public CrimeCursorWrapper queryCrimes(String whereClause, String[]  whereArgs){
        Cursor cursor=mSQLiteDatabase.query(CrimeDbScheme.CrimeTable.NAME,
                null,whereClause,whereArgs,null,null,null);
        return new CrimeCursorWrapper(cursor);
    }
    public File getPhotoFile(Crime crime){
        File externalFilesDir=mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir==null){
            return null;
        }
        return new File(externalFilesDir,crime.getPhotoFileName());
    }


}
