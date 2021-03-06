package org.wangguangjie;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;

import org.wangguangjie.crime.controler.CrimeListActivity;
import org.wangguangjie.headline.R;
import org.wangguangjie.hit.ItemFragment;
import org.wangguangjie.hit.controller.TableFragmentPagerAdapter;
import org.wangguangjie.sidemenu.model.SideItem;
import org.wangguangjie.sidemenu.Listener.SideMenuActionBarDrawerToggle;
import org.wangguangjie.test.TestActivity;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;

/**
 * 问题1.由于三星手机的原因（别的机型测试没有问题），在每次程序进入后台之后（如按HOME键），系统结束程序进程（回收程序的内存），导致重新进入程序之后，
 * 程序重新启动（onCreate开始执行），系统之前保存了Fragment对象，所以在onCreate中对Fragment进行恢复，此时程序奔溃，奔溃的原因有如下两点：
 * 1）在fragment中使用了Activity的View，在恢复Fragment过程中此View为null，导致程序奔溃(空指针异常），在此的分析主要有两种可能：第一，可能是
 *    在恢复Fragment的过程中，View其实也是可以恢复的，只是View的恢复比Fragment晚一些（这是我一开始的猜测），所以导致空指针异常。第二，可能是onCreate
 *    本身并不会恢复View组件，所以无论如何都会导致空指针异常，经过验证后面的猜测是正确的，也很容易理解，程序保存的只是少部分数据（如果连View都保存了，
 *    岂不是什么数据都会保存吗）.
 * 2）在恢复的Fragment中由于更新了界面，与此同时，在Activity中替换了Fragment（replace），也就是说程序销毁了恢复的Fragment对象，导致程序异常.
 * 解决方案：
 * 1)重写Activity 的onSaveInstance方法，不让程序主动保存Fragment对象(也有其他的解决方案，但是此种是最简单有效的).
 * 2)重新对程序的逻辑进行分析，在Fragment尽量不使用Activity的View（也就是在什么地方定义的View尽量在此地方设置监听），由于Spinner在程序初始化的时候，
 *   会执行选择，所以Fragment在初始化的时候不需要再开启子线程来获取和更新数据.
 */
public class MainActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;


    private LinearLayout drawer;
   // private Spinner mSpinner;

    SideMenuActionBarDrawerToggle sideMenuActionBarDrawerToggle;

    private Toolbar mToolbar;


    private List<SideItem> items=new ArrayList<>();

    final static String CANCEL="Cancel";
    final static String HIT="Hit";
    final static String ABOUT="About";
    final static String MUSIC="Music";
    final static String MOVIE="Movie";
    final static String ALBUM="Album";

    public static String PREFERENCES="PERFERENCESNAME";


    private String[] urls=new String[]{
            "http://www.hitsz.edu.cn/article/id-74.html",
            "http://www.hitsz.edu.cn/article/id-75.html",
            "http://www.hitsz.edu.cn/article/id-77.html",
            "http://www.hitsz.edu.cn/article/id-78.html",
            "http://www.hitsz.edu.cn/article/id-80.html"
    };

    private List<ItemFragment> mItemFragments=new ArrayList<>();

    private ViewPager mViewPager;
    private  android.support.design.widget.TabLayout mTableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        drawer=(LinearLayout)findViewById(R.id.drawer);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        //mSpinner=(Spinner)findViewById(R.id.spinner);
        //初始化视图;
        initActionBar();
        initTableLayout();
        initSideMenu();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.d("test","onRestart");
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
    public void onWindowFocusChanged(boolean isChanged){
        Log.d("test","onWindowFocusChanged");
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
        super.onSaveInstanceState(b);
        Log.d("test","onSaveInstanceState");
    }


    private void initActionBar(){
        //toolbar初始化设置;
        mToolbar.setTitle("HIT官网");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        mToolbar.setLogo(R.mipmap.icon_hit);
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initTableLayout(){
        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        for(int i=0;i<urls.length;i++){
            ItemFragment fragment=new ItemFragment();
            Bundle bundle=new Bundle();
            bundle.putString(PREFERENCES,"hit"+i);
            bundle.putString("url",urls[i]);
            fragment.setArguments(bundle);
            mItemFragments.add(fragment);
        }
        mViewPager.setAdapter(new TableFragmentPagerAdapter(getSupportFragmentManager(),
                mItemFragments,getResources().getStringArray(R.array.classifies)));
        mTableLayout=(android.support.design.widget.TabLayout)findViewById(R.id.table_layout);
        mTableLayout.setupWithViewPager(mViewPager);
        //设置选中和不选中的颜色
        mTableLayout.setTabTextColors(getResources().getColor(R.color.dark_blue),getResources().getColor(R.color.dark_red));
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
        //DrawerLayout与actionbar绑定，同时也作为drawer的监听器，当点击左上角图标时显示sidemenu(侧边菜单监听器);
        sideMenuActionBarDrawerToggle=new SideMenuActionBarDrawerToggle(this,
                mDrawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close,drawer,items){
            @Override
            public void onSwitch(View view, int position) {
                MainActivity.this.onSwitch(view,position);
            }
        };
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(sideMenuActionBarDrawerToggle);
        //使actionbar左上角的图标状态同步;
        sideMenuActionBarDrawerToggle.syncState();
    }
    //设置侧边菜单选项;
    private void setItems()
    {
        items.clear();
        SideItem item0=new SideItem(R.drawable.icon_cancel,CANCEL);
        items.add(item0);
        SideItem item1=new SideItem(R.mipmap.icon_hit,HIT);
        items.add(item1);
        SideItem itemAbout=new SideItem(R.mipmap.icon,ABOUT);
        items.add(itemAbout);
        SideItem itemTest=new SideItem(R.mipmap.icon,"Test");
        items.add(itemTest);
        SideItem item2=new SideItem(R.drawable.icon_music,MUSIC);
        items.add(item2);
        for(int i=0;i<10;i++)
            items.add(item2);
        SideItem item3=new SideItem(R.drawable.icon_movie,MOVIE);
        items.add(item3);
        SideItem item4=new SideItem(R.drawable.icon_album,ALBUM);
        items.add(item4);
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

            }
            break;
            case 1:
            {

            }
            break;
            case 2:
            {
                sideMenuActionBarDrawerToggle.closeDrawer();
                Intent intent=new Intent(this, CrimeListActivity.class);
                startActivity(intent);

            }
            break;
            case 3:
            {
                sideMenuActionBarDrawerToggle.closeDrawer();
                Intent intent=new Intent(this, TestActivity.class);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}
