package org.wangguangjie.crime.controler.datefragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import org.wangguangjie.headline.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangguangjie on 2017/10/18.
 */

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME="org.wangguangjie.timepicker";
    private static final String ARG_TIME="time";
    private TimePicker mTimePicker;
    private Date mDate;

    public static TimePickerFragment newInstance(Date date){
        Bundle bundle=new Bundle();
        bundle.putSerializable(ARG_TIME,date);
        TimePickerFragment timePickerFragment=new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        mDate=(Date)getArguments().getSerializable(ARG_TIME);
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.crime_time_picker,null);
        mTimePicker=(TimePicker)view.findViewById(R.id.crime_time_picker);
        mTimePicker.setIs24HourView(true);
        return new AlertDialog.Builder(getActivity())
                .setView(mTimePicker)
                .setPositiveButton(R.string.crime_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDate=setHour(mTimePicker.getHour(),mDate);
                        mDate=setMinute(mTimePicker.getMinute(),mDate);
                        sendResult(getTargetRequestCode(),mDate);
                    }
                })
                .create();
    }
    private Date setHour(int hours,Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,hours);
        return calendar.getTime();
    }
    private Date setMinute(int minutes,Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE,minutes);
        return calendar.getTime();
    }
    public void sendResult(int requestCode,Date date){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(requestCode, Activity.RESULT_OK,intent);
    }
}
