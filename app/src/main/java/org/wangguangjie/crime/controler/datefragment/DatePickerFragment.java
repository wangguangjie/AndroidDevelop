package org.wangguangjie.crime.controler.datefragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.wangguangjie.headline.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by wangguangjie on 2017/10/17.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE="date";


    public static final String EXTRA_DATE="org.wangguangjie.date";
    private DatePicker mDatePicker;

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        Log.d("crime1","DatePickerFragment onCreateDialog");
        Date date=(Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.crime_data_dialog,null);
        mDatePicker=(DatePicker) view.findViewById(R.id.crime_date_picker);
        mDatePicker.init(year,month,day,null);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.crime_datepicker)
                .setPositiveButton(R.string.crime_button_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                int year=mDatePicker.getYear();
                                int month=mDatePicker.getMonth();
                                int day=mDatePicker.getDayOfMonth();
                                Date date1=new GregorianCalendar(year,month,day).getTime();
                                sendResult(getTargetRequestCode(),date1);
                            }
                        }).create();

    }

    public void sendResult(int requestCode,Date date){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(requestCode, Activity.RESULT_OK,intent);
    }
    public static DatePickerFragment newInstance(Date date){
        Bundle bundle=new Bundle();
        bundle.putSerializable(ARG_DATE,date);
        DatePickerFragment datePickerFragment=new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }
    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        Log.d("crime1","DatePickerFragment onCreate");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("crime1","DatePickerFragment onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("crime1","DatePickerFragment onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("crime1","DatePickerFragment onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("crime1","DatePickerFragment onStop");
    }
}
