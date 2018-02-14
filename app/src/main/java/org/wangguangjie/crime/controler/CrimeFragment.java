package org.wangguangjie.crime.controler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.wangguangjie.crime.Model.Crime;
import org.wangguangjie.crime.utils.CrimeLab;
import org.wangguangjie.crime.controler.datefragment.DatePickerFragment;
import org.wangguangjie.crime.controler.datefragment.TimePickerFragment;
import org.wangguangjie.crime.utils.PictureUtils;
import org.wangguangjie.headline.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by wangguangjie on 2017/10/11.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME="crime_id";

    public static final int DATEPICKER_REQUESTCODE=1;
    public static final int TIMEPICKER_REQUESTCODE=2;
    public static final int PICTURE_REQUESTCODE=3;

    public static final int REQUEST_CONTACT=3;

    private Crime mCrime;
    private UUID mId;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mButtonDelete;
    private Button mButtonSend;
    private Button mButtonSuspect;
    private Button mButtonCall;
    private ImageView mImageView;
    private ImageButton mImageButton;

    private Callbacks mCallbacks;
    private DeleteCallbacks mDeleteCallbacks;

    private File mPhotoFile;

    public static CrimeFragment newInstanceCrime(UUID id){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME,id);
        CrimeFragment crimeFragment=new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }
    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        Log.d("crime1","CrimeFragment onCreate");
        //mItemPosition=getActivity().getIntent().getIntExtra(POSITION,0);
        mId=(UUID)getArguments().getSerializable(ARG_CRIME);
        Log.d("mytest111", mId.toString());
        mCrime= CrimeLab.get(getActivity()).getCrime(mId);
        try{
            mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("crime1","CrimeFragment onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("crime1","CrimeFragment onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        Log.d("crime1","CrimeFragment onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("crime1","CrimeFragment onStop");
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.d("crime1","onAttach");
        mCallbacks=(Callbacks)activity;
        mDeleteCallbacks=(DeleteCallbacks)activity;
    }
    public void onDetach(){
        super.onDetach();
        Log.d("crime1","onDetach");
        mCallbacks=null;
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        //viewGroup使根元素的布局参数（layout_x）生效，但此组件不添加到vieGroup中;
        View view=inflater.inflate(R.layout.crime_crimefragment,viewGroup,false);
        final LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.crime_root);
        mImageView=(ImageView)view.findViewById(R.id.crime_camera);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhotoFile!=null&&mPhotoFile.exists()) {
                    FragmentManager fragmentManager = getFragmentManager();
                    CrimePhotoDialogFragment fragment = CrimePhotoDialogFragment.creatNewInstance(mPhotoFile);
                    fragment.show(fragmentManager, "PhotoFragment");
                    //mCallbacks.onCrimeUpdated(mCrime);
                }
            }
        });
        ViewTreeObserver treeObserver=mImageView.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //updatePhotoView();
            }
        });
        treeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
               // updatePhotoView();
                return true;
            }
        });
        mImageButton=(ImageButton)view.findViewById(R.id.crime_camera_button);
        PackageManager packageManager=getActivity().getPackageManager();
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final boolean canTakePicture=mPhotoFile!=null&&packageManager.resolveActivity(captureImage,PackageManager.MATCH_DEFAULT_ONLY)!=null;
        mImageButton.setEnabled(canTakePicture);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.fromFile(mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(captureImage,PICTURE_REQUESTCODE);
            }
        });
        //点击界面隐藏键盘;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
        mTitleField=(EditText)view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton=(Button)view.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getFormateDate());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                DatePickerFragment fragment=DatePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this,DATEPICKER_REQUESTCODE);
                fragment.show(manager,"date");
        }
        });
        mTimeButton=(Button)view.findViewById(R.id.crime_time);
        mTimeButton.setText(mCrime.getFormateTime());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                TimePickerFragment fragment=TimePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this,TIMEPICKER_REQUESTCODE);
                fragment.show(manager,"date");
            }
        });
        mSolvedCheckBox=(CheckBox)view.findViewById(R.id.crime_checkbox);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime(mCrime);
            }
        });
        mButtonSuspect=(Button)view.findViewById(R.id.crime_button_select);
        final Intent intentPick=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mButtonSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intentPick,REQUEST_CONTACT);
            }
        });
        if(mCrime.getSuspect()!=null){
            mButtonSuspect.setText(mCrime.getSuspect()+"("+mCrime.getPhoneNumber()+")");
        }
        //进行包检查，如果有对应的应用才进行查找联系人;
        if(packageManager.resolveActivity(intentPick,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mButtonSuspect.setEnabled(true);
        }
        mButtonSend=(Button)view.findViewById(R.id.crime_button_send);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("smsto:"+mCrime.getPhoneNumber());
                Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
//                intent.setType("text/plain");
                intent.putExtra("sms_body",getCrimeReport());
//                intent.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT,R.string.crime_report_subject);
//                intent.putExtra(Intent.EXTRA_PHONE_NUMBER,mCrime.getPhoneNumber());
                //intent=Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);

            }
        });
        mButtonCall=(Button)view.findViewById(R.id.crime_button_call);
        mButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1=Uri.parse("tel:"+mCrime.getPhoneNumber());
                Intent intent1=new Intent(Intent.ACTION_DIAL,uri1);
                startActivity(intent1);
            }
        });

        mButtonDelete=(Button)view.findViewById(R.id.crime_button_delete);
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.alert_delete)
                        .setPositiveButton(R.string.crime_button_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mDeleteCallbacks.onShowNextCrime(mCrime);

                            }
                        })
                        .setNegativeButton(R.string.crime_button_negative,null)
                        .setMessage("确定删除此条Crime？").create().show();

                //startActivity(intent);
                //setResult();
            }
        });
        updatePhotoView();
        return view;
    }
    private void setResult(){
        getActivity().setResult(Activity.RESULT_OK);
    }

   private void updateCrime(Crime crime){
        CrimeLab.get(getActivity()).updateCrime(crime);
        mCallbacks.onCrimeUpdated(crime);
   }
    public String getCrimeReport(){
        String stringSolved;
        if(mCrime.isSolved()){
            stringSolved=getString(R.string.crime_report_solved);
        }else
        {
            stringSolved=getString(R.string.crime_report_unsolved);
        }
        String stringDate;
        stringDate= DateFormat.format("EEEE,MM dd",mCrime.getDate()).toString();
        String stringSuspect;
        if(mCrime.getSuspect()==null){
            stringSuspect=getString(R.string.crime_report_no_suspect);
        }
        else{
            stringSuspect=getString(R.string.crime_report_suspect,mCrime.getSuspect());
        }
        String stringReport=getString(R.string.crime_report,mCrime.getTitle(),stringDate,stringSolved,stringSuspect);
        return stringReport;
    }

    private void updatePhotoView(){
        Point point=new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(point);
        Log.d("SizeTest","Windows:"+point.x+" "+point.y);
        if(mPhotoFile==null||!mPhotoFile.exists()){
            mImageView.setImageDrawable(null);
        }else{
            Bitmap bitmap= PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());

//            Matrix matrix=new Matrix();
//            matrix.setRotate(90);
//            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);

            int width=bitmap.getWidth();
            int height=bitmap.getHeight();
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)mImageView.getLayoutParams();
            params.width=dip2px(getActivity(),200);
            params.height=dip2px(getActivity(),200*height/width);
            mImageView.setLayoutParams(params);

            mImageView.setImageBitmap(bitmap);
        }
    }
    private int dip2px(Context context,float dipValue)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(resultCode!=Activity.RESULT_OK)
        {
            return;
        }else if(requestCode==DATEPICKER_REQUESTCODE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mDateButton.setText(mCrime.getFormateDate());
            updateCrime(mCrime);
        }else if(requestCode==TIMEPICKER_REQUESTCODE){
            Date date=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            mTimeButton.setText(mCrime.getFormateTime());
            updateCrime(mCrime);
        }
        else if(requestCode==REQUEST_CONTACT&&data!=null){
            Uri uri=data.getData();
            String[] fields=new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER};
            //查询联系人名字，联系人是否有号码，联系人的id;
            Cursor cursor=getActivity().getContentResolver().query(uri,fields,null,null,null);
            Cursor cursor1=null;
            try{
                if(cursor.getCount()==0){
                    return ;
                }
                cursor.moveToFirst();
                String contactSuspect=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String id=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                int hasNumber=cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if(hasNumber>0){
                    //通过联系人的id查询联系人的手机号码;
                     cursor1=getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    ,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,null,null);
                    if(cursor1.getCount()==0){
                        mCrime.setSuspect(contactSuspect);
                        return;
                    }
                    cursor1.moveToFirst();
                    String phoneNumber=cursor1.getString(cursor1.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    mCrime.setSuspect(contactSuspect);
                    mCrime.setPhoneNumber(phoneNumber);
                    mButtonSuspect.setText(mCrime.getSuspect()+"("+mCrime.getPhoneNumber()+")");
                    updateCrime(mCrime);
                }

            }
            finally {
                cursor.close();
                if(cursor1!=null)
                  cursor1.close();
            }
        }
        else if(requestCode==PICTURE_REQUESTCODE){
            updatePhotoView();
            updateCrime(mCrime);
        }
    }
    public interface  DeleteCallbacks{
        void onShowNextCrime(Crime crime);
    }
     public interface Callbacks{
        void onCrimeUpdated(Crime crime);
     }
}
