package org.wangguangjie.crime.controler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.wangguangjie.crime.Model.Crime;
import org.wangguangjie.crime.utils.CrimeLab;
import org.wangguangjie.headline.R;

import java.util.List;
import java.util.UUID;

/**
 * Created by wangguangjie on 2017/10/17.
 */

public class CrimePageActivity extends AppCompatActivity implements CrimeFragment.Callbacks,CrimeFragment.DeleteCallbacks {

    private final static String EXTRA_CRIME_ID="org.wangguangjie.crime.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private UUID mId;

    public static Intent newIntent(Context context, UUID id){
        Intent intent = new Intent(context, CrimePageActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.crime_page_activity);
        mCrimes= CrimeLab.get(this).getCrimes();
        FragmentManager manager=getSupportFragmentManager();
        mViewPager=(ViewPager)findViewById(R.id.activity_crime_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {

                return CrimeFragment.newInstanceCrime(mCrimes.get(mCrimes.size()-position-1).getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        mId=(UUID)getIntent().getExtras().getSerializable(EXTRA_CRIME_ID);
        for(int i=0;i<mCrimes.size();i++) {
            if(mCrimes.get(i).getId().equals(mId))
            {
                mViewPager.setCurrentItem(mCrimes.size()-1-i);
            }
        }
        Log.d("crime1","onCreate");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("crime1","onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("crime1","onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("crime1","onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("crime1","onStop");
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    @Override
    public void onShowNextCrime(Crime crime) {
        CrimeLab.get(this).deleteCrime(crime);
        Intent intent=new Intent(this,CrimeListActivity.class);
        startActivity(intent);
    }
}
