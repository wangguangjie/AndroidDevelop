package org.wangguangjie.crime.Model;


import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by wangguangjie on 2017/10/11.
 */

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mPhoneNumber;

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    private String mSuspect;

    public Crime(){
        this(UUID.randomUUID());

    }

    public Crime(UUID id){
        mId=id;
        mDate=new Date(System.currentTimeMillis());
    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public String getFormateDate() {
        return (String)DateFormat.format("EEEE, MM dd, yyyy",mDate);
    }

    public String getFormateTime(){
        return (String) DateFormat.format("HH:mm:ss",mDate);
    }
    public Date getDate(){
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPhotoFileName(){
        return "IMG_"+getId()+".jpg";
    }
}
