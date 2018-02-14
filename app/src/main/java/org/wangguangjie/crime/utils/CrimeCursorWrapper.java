package org.wangguangjie.crime.utils;

import android.database.Cursor;
import android.database.CursorWrapper;

import org.wangguangjie.crime.Model.Crime;
import org.wangguangjie.crime.utils.CrimeDbScheme;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wangguangjie on 2017/10/23.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString=getString(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.UUID));
        String title=getString(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.DATE));
        int solved=getInt(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.SOLVED));
        String suspect=getString(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.SUSPECT));
        String phoneNumber=getString(getColumnIndex(CrimeDbScheme.CrimeTable.Cols.PHONENUMBER));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(solved==1);
        crime.setSuspect(suspect);
        crime.setPhoneNumber(phoneNumber);
        return crime;
    }
}
