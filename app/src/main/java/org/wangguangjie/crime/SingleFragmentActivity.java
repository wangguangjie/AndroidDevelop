package org.wangguangjie.crime;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.wangguangjie.headline.R;

/**
 * Created by wangguangjie on 2017/10/16.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();


    protected int getLayoutResId(){
        return R.layout.crime_activity_fragment;
    }

    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        setContentView(getLayoutResId());
        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.crime_fragment_container);
        if(fragment==null){
            fragment=createFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.crime_fragment_container,fragment).commit();
    }
}
