package org.wangguangjie.crime.controler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.wangguangjie.crime.Model.Crime;
import org.wangguangjie.crime.utils.CrimeLab;
import org.wangguangjie.headline.R;

import java.util.List;

/**
 * Created by wangguangjie on 2017/10/16.
 */


/**
 *
 * 问题1：
 *    滑动或者刷新列表之后，CheckBox状态错误;
 *    原因：
 *      是因为错误设置了列表CheckBox的状态改变之后的监听，
 *    RecyclerView由于频繁的绑定数据（调用onBindHolder），触发CheckBox监听更改的是之前绑定的数据。
 *    解决方法：
 *      是去掉CheckBox的状态监听（这个功能其实可以不需要），更好的方法是在更改CheckBox的状态之前设置监听器更改的是此时将要绑定的数据，
 *    所以监听触发的更改都是目前绑定的数据.
 */

public class CrimeListFragment extends Fragment{

    private CrimeLab mCrimeLab;
    private RecyclerView mRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    private boolean mSubtitleVisible;

    private CallBacks mCallBack;

    //记录点击的选项，以便界面只更新此选项.
    public static int sClickPosition;
    public static final int CRIME_REQUEST=1;

    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        Log.d("crime","fragment onCrate");
        setHasOptionsMenu(true);
        //获取保存的子标题状态;
       if(onSaveInstanceState!=null){
           mSubtitleVisible=onSaveInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
       }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle bundle){
        Log.d("crime","fragment onCreateView");
        View view=inflater.inflate(R.layout.crime_crimelistfragment,viewGroup,false);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mSubtitleVisible=bundle.getBoolean(SAVED_SUBTITLE_VISIBLE);
        updateUI();
        return view;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallBack=(CallBacks)activity;
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mCallBack=null;
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
        Log.d("crime","Fragment onResume");
    }
    @Override

    public void onPause(){
        super.onPause();
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("crime","Fragment onStop");
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("crime","fragment onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //保存子标题状态;
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
    public CrimeAdapter getCrimeAdapter(){
        return mCrimeAdapter;
    }
    public void updateUI(){
        mCrimeLab=CrimeLab.get(getActivity());
        List<Crime> crimes=mCrimeLab.getCrimes();
        //设置模型数据,并且设置RecyclerView的选项点击事件监听器;
//        mCrimeAdapter = new CrimeAdapter(crimes);
//        mRecyclerView.setAdapter(mCrimeAdapter);
        if(mCrimeAdapter==null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mCrimeAdapter);
        }
        else {
            mCrimeAdapter.setCrimes(mCrimeLab.getCrimes());
            mCrimeAdapter.notifyDataSetChanged();
           // mCrimeAdapter.notifyItemChanged(sClickPosition);//只更改数据改变的选项（优化）;
        }
       updateSubtitle();
    }

    public class CrimeHolder extends RecyclerView.ViewHolder{

        public TextView mTitleTextView;
        public TextView mDateTextView;
        public CheckBox mCheckBox;
        private Crime mCrime;


        public CrimeHolder(final View itemView) {
            super(itemView);
            mTitleTextView=(TextView)itemView.findViewById(R.id.crime_item_title);
            mDateTextView=(TextView)itemView.findViewById(R.id.crime_item_date);
            mCheckBox=(CheckBox)itemView.findViewById(R.id.crime_item_checkbox);
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                    CrimeLab.get(getActivity()).updateCrime(mCrime);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CrimeHolder.this.onClick(itemView);
                }
            });
        }
        public void bindCrime(final Crime crime,final int position){
            //首先更新此时CheckBox监听器更改的Crime，否则因为下面的更改导致监听器更改的是上次的Crime.
            mCrime=crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getFormateDate()+" "+crime.getFormateTime());
            //要在更新Crime之后调用，否则出错.
            mCheckBox.setChecked(crime.isSolved());
            Log.d("test1","setChecked");
            mCrime=crime;
        }
        public void onClick(View view){
            mCallBack.onCrimeSelected(mCrime);
        }

    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> list){
            mCrimes=list;
        }
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("recycleview","onCreateViewHolder");
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.crime_recyclerview_item,parent,false);
            CrimeHolder crimeHolder=new CrimeHolder(view);
            return crimeHolder;
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d("recycleview","onBindViewHolder");
            holder.bindCrime(mCrimes.get(mCrimes.size()-1-position),position);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> list){
            mCrimes=list;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==CRIME_REQUEST&&resultCode==Activity.RESULT_OK){
            Log.d("crime","onActivityResult");
            Toast.makeText(getActivity(),"test",Toast.LENGTH_LONG).show();
        }else{

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crune_list,menu);
        MenuItem item=menu.findItem(R.id.menu_item_show_subtitle);
        //根据子标题状态显示菜单项;
        if(mSubtitleVisible){
            item.setTitle(R.string.hide_subtitle);
        }
        else{
            item.setTitle(R.string.show_subtitle);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallBack.onCrimeSelected(crime);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //根据子标题状态跟新子标题;
    private void updateSubtitle(){
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        //String subtitle1=getString(R.string.subtitle_format,crimeLab.getCrimes().size());
        String subtitle=getResources().getQuantityString(R.plurals.subtitle_plurals,crimeLab.getCrimes().size(),crimeLab.getCrimes().size());
        if(!mSubtitleVisible)
            subtitle=null;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    public interface CallBacks{
        void onCrimeSelected(Crime crime);
    }

}
