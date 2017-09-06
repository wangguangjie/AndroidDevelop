package org.wangguangjie.headline;

import android.animation.Animator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.wangguangjie.hit.HitFragment;
import org.wangguangjie.sidemenu.model.SideItem;
import org.wangguangjie.sidemenu.Listener.SideMenuActionBarDrawerToggle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;
    private LinearLayout content_frame;
    private LinearLayout content_overlay;
    private LinearLayout drawer;
    private Spinner mSpinner;

    private Toolbar mToolbar;
    private String mTitle;
    private int mIcon;
    private int mLog;
    HitFragment mHitFragment;
    private boolean isNull;

    private List<SideItem> items=new ArrayList<>();

    final static String CANCEL="Cancel";
    final static String HIT="Hit";
    final static String MUSIC="Music";
    final static String RESOURCE="reource";
    final static String MOVIE="Movie";
    final static String ALBUM="Album";
    final static String POSITION="position";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test","onCreate");
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null)
            isNull=true;
        //初始化值;
        getValues();
        //初始化视图;
        initView();
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("test","onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("test","onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("test","onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("test","onStop");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("test","onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle b){
        //mHitFragment=new HitFragment();
        Log.d("test","onSaveInstanceState");
        //mHitFragment.setSpinner(mSpinner);
        //mHitFragment.setFrgamentView(content_frame);
        //getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,mHitFragment).commit();
    }
    private void getValues(){
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        content_frame=(LinearLayout)findViewById(R.id.content_frame);
        content_overlay=(LinearLayout)findViewById(R.id.content_overlay);
        drawer=(LinearLayout)findViewById(R.id.drawer);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mSpinner=(Spinner)findViewById(R.id.spinner);
    }
    //初始化视图;
    private void initView(){
        //初始化工具条;
        initActionBar();
        //初始化Fragment;
        initMainFragment();
        //初始化侧边菜单;
        initSideMenu();
    }

    private void initActionBar(){
        //toolbar初始化设置;
        mTitle="title1";
        mIcon=R.drawable.log1;
        mLog=R.mipmap.icon_hit;
        setActionBar();

    }

    private void initMainFragment(){
        mHitFragment = new HitFragment();
        mHitFragment.setSpinner(mSpinner);
        mHitFragment.setFrgamentView(content_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mHitFragment).commit();
    }


    private void initSideMenu(){
        //设置菜单项;
        setItems();
        //设置侧边菜单点击动作处理;
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDrawerLayout.closeDrawers();
            }
        });
        //drawerlayout与actionbar绑定，同时也作为drawer的监听器，当点击左上角图标时显示sidemenu(侧边菜单监听器);
        SideMenuActionBarDrawerToggle sideMenuActionBarDrawerToggle=new SideMenuActionBarDrawerToggle(this,
                mDrawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close,drawer,items,mHitFragment){
            @Override
            public void onSwitch(View view, int position) {
                MainActivity.this.onSwitch(view,position);
            }
        };
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(sideMenuActionBarDrawerToggle);
        //使actionbar左上角的图标状态同步;
        sideMenuActionBarDrawerToggle.syncState();
        new Button(this).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    //设置侧边菜单选项;
    private void setItems()
    {
        items.clear();
        SideItem item0=new SideItem(R.drawable.icon_cancel,CANCEL);
        items.add(item0);
        SideItem item1=new SideItem(R.mipmap.icon_hit,HIT);
        items.add(item1);
        SideItem item2=new SideItem(R.drawable.icon_music,MUSIC);
        items.add(item2);
        for(int i=0;i<20;i++)
            items.add(item2);
        SideItem item3=new SideItem(R.drawable.icon_movie,MOVIE);
        items.add(item3);
        SideItem item4=new SideItem(R.drawable.icon_album,ALBUM);
        items.add(item4);

    }
    private void setActionBar(){
        mToolbar.setTitle("HIT官网");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        mToolbar.setLogo(mLog);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //创建菜单回调;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //点击侧边菜单选项时处理的事件;
    public void onSwitch(View view,int position) {
        switch (position){
            case 0:
            {
                //关闭drawer，不改变fragment;
                //mDrawerLayout.closeDrawers();
            }
            break;
            case 1:
            {

                View view1=findViewById(R.id.content_frame);
                int[] location={0,0};
                view1.getLocationOnScreen(location);
                int cx=location[0]+view1.getWidth()/2;
                int cy=view1.getHeight()/2;
                int radix=(int)Math.hypot(view1.getWidth()/2,view1.getHeight()/2);
                Animator animator= ViewAnimationUtils.createCircularReveal(view1,cx,cy,0,radix);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(750);
                animator.start();
                if(mHitFragment==null)
                {
                    mHitFragment=new HitFragment();
                    mHitFragment.setSpinner(mSpinner);
                    mHitFragment.setFrgamentView(content_frame);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,mHitFragment).commit();
            }
            break;
            case 2:
            {

            }
            break;
            default:
                break;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            mHitFragment=new HitFragment();
            mHitFragment.setSpinner(mSpinner);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,mHitFragment).commit();
        }else{
            mHitFragment=new HitFragment();
            mHitFragment.setSpinner(mSpinner);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,mHitFragment).commit();
        }
    }
    public void setFragmentReource(){

    }
}
