package org.wangguangjie.crime.controler;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;

import org.wangguangjie.crime.Model.Crime;
import org.wangguangjie.crime.SingleFragmentActivity;
import org.wangguangjie.crime.utils.CrimeLab;
import org.wangguangjie.headline.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguangjie on 2017/10/11.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.CallBacks,CrimeFragment.Callbacks,CrimeFragment.DeleteCallbacks{

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        Log.d("crime","onCreate");
    }

    @Override
    protected  int getLayoutResId(){
        return R.layout.crime_masterdetail;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        setIconEnable(menu,true);
        //getMenuInflater().inflate(R.menu.fragment_crune_list,menu);
        return true;
    }





    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //下面传入参数
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d("crime","onStart");
    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.d("crime","onRestart");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("crime","onStop");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("crime","onResume");
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_crime_container)==null) {
            startActivityForResult(CrimePageActivity.newIntent(this, crime.getId()), CrimeListFragment.CRIME_REQUEST);
        }else{
            CrimeFragment crimeFragment=CrimeFragment.newInstanceCrime(crime.getId());

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_crime_container,crimeFragment).commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment=(CrimeListFragment)getSupportFragmentManager().findFragmentById(R.id.crime_fragment_container);
        crimeListFragment.updateUI();
    }

    @Override
    public void onShowNextCrime(Crime mCrime) {
        List<Crime> list;
        list= CrimeLab.get(this).getCrimes();
        int index=0;
        for(int i=0;i<list.size();i++)
        {
            if(mCrime.getId().toString().equals(list.get(i).getId().toString())){
                index=i;
                break;
            }
        }
        index+=1;
        Crime crime;
        if(index<list.size()) {
            crime = list.get(index);
        }else{
            index-=2;
            if(index>=0){
                crime=list.get(index);
            }else{
                crime=null;
            }
        }
        CrimeLab.get(this).deleteCrime(mCrime);
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.crime_fragment_container);
        crimeListFragment.updateUI();
        if(crime!=null) {
            CrimeFragment crimeFragment = CrimeFragment.newInstanceCrime(crime.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_crime_container, crimeFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.detail_crime_container)).commit();
        }
    }
}
