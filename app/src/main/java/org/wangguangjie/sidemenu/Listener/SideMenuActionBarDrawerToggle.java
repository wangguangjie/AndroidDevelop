package org.wangguangjie.sidemenu.Listener;

import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.wangguangjie.headline.R;
import org.wangguangjie.sidemenu.interfaces.Screenable;
import org.wangguangjie.sidemenu.model.SideItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguangjie on 2017/9/2.
 * 扩展ActionBarToggle, 能够监听侧边菜单的切换（onSwitch）,并且实现侧边菜单显示和隐藏的自定义方式（比如缩放动画等）;
 */

public abstract class SideMenuActionBarDrawerToggle extends ActionBarDrawerToggle {

    private List<SideItem> items;
    private LinearLayout mdrawerContainer;
    private Screenable mScreenable;
    private DrawerLayout mDrawerLayout;
    //private SideMenu.SideMenuListener mSideMenuListener;
    private AppCompatActivity mainAcitivity;
    private List<View> menuVies=new ArrayList<>();

    final static int UNIT=150;

    public SideMenuActionBarDrawerToggle(AppCompatActivity appCompatActivity,
                                         DrawerLayout drawerLayout, Toolbar toolbar,int s1,int s2,LinearLayout line,List<SideItem>list,
                                         Screenable screenable){
        super(appCompatActivity,drawerLayout,toolbar,s1,s2);
        this.mainAcitivity=appCompatActivity;
        this.mDrawerLayout=drawerLayout;
        this.mdrawerContainer=line;
        this.items=list;
        this.mScreenable=screenable;
    }

    //滑出sideMenu时调用;
    @Override
    public void onDrawerSlide(View drawerView,float sideOffset){
        super.onDrawerSlide(drawerView,sideOffset);
        //
    }
    //菜单完全关闭时调用;
    @Override
    public void onDrawerClosed(View view){
        super.onDrawerClosed(view);
        //drawer.removeAllViews();
        //drawer.invalidate();
        //是菜单选项隐藏,为下次打开菜单时候的动画做准备;
        this.setViewGone();
    }
    //drawer完全关闭时候调用;
    @Override
    public void onDrawerOpened(View view){
        super.onDrawerOpened(view);
        //首次创建菜单选项;
        if(mdrawerContainer.getChildCount()==0){
            this.createSideMenuItems();
        }
        //菜单选项已经创建,只显示sidemenu;
        else{
            this.showSideMenu();
        }
    }

    /**
     * 切换侧边菜单时调用次方法.
     * @param view 点击的组件.
     * @param  position 点击的侧边菜单的位置，从上到下依次递增（从0开始）.
     *
     */
    abstract public void onSwitch(View view,int position);



    public void createSideMenuItems(){
        //setViewClickable(false);
        menuVies.clear();
        //逐一添加sidemenu选项;
        for(int i=0;i<items.size();i++){
            final View view=mainAcitivity.getLayoutInflater().inflate(R.layout.menu_item,null);
            ((ImageView)view.findViewById(R.id.menu_list_item)).setImageResource(items.get(i).getResouceId());
            mdrawerContainer.addView(view);
            final int position=i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //关闭菜单选项;

                    //用户自定义的处理时间;
                    onSwitch(view,position);
                    hideSideMenu();
                }
            });
            menuVies.add(view);
            view.setVisibility(View.GONE);
            view.setEnabled(false);
            int delay=i*UNIT/8;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //view.setVisibility(View.VISIBLE);
                    //view.setEnabled(true);
                    if(position<items.size()){
                        animateShow(position);
                    }
                    if(position==items.size()-1){
                        //菜单选项加载完毕保存fragment的屏幕信息;
                        mScreenable.takeScreen();
                        setViewClickable(true);
                    }
                }
            },delay);
        }
    }
    //显示选项时的动画;
    private void animateShow(int position){
        final View view=menuVies.get(position);
        view.setVisibility(View.VISIBLE);
        //TranslateAnimation animation= new TranslateAnimation(-100,0,0,0);
        ScaleAnimation animation = new ScaleAnimation(
                0, 1.0f, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(4*UNIT);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }
    private void setViewClickable(boolean isClickable){
        //左上角图标设置不可点击;
        mainAcitivity.getSupportActionBar().setHomeButtonEnabled(false);
        //分别设置菜单选项;
        for(int i=0;i<items.size();i++){
            menuVies.get(i).setEnabled(isClickable);
        }
    }

    public void setViewGone(){
        for(int i=0;i<menuVies.size();i++){
            menuVies.get(i).setVisibility(View.GONE);
        }
    }
    public void showSideMenu(){
        setViewClickable(false);
        for(int i=0;i<menuVies.size();i++) {
            final int position=i;
            int delay = i * UNIT /8;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //view.setVisibility(View.VISIBLE);
                    //view.setEnabled(true);
                    if (position < items.size()) {
                        animateShow(position);
                    }
                    if (position == items.size() - 1) {
                        //菜单选项加载完毕保存fragment的屏幕信息;
                        mScreenable.takeScreen();
                        setViewClickable(true);
                    }
                }
            }, delay);
        }
    }
    public void hideSideMenu(){
        setViewClickable(false);
        for(int i=0;i<menuVies.size();i++){
            final int position=i;
            int delay=i*UNIT/8;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateHide(position);
                }
            },delay);
        }
    }

    //隐藏选项时的动画;
    private void animateHide(final int position){
        final View view=menuVies.get(position);
        //view.setVisibility(View.VISIBLE);
        //TranslateAnimation animation= new TranslateAnimation(-100,0,0,0);
        ScaleAnimation animation = new ScaleAnimation(
                1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(4*UNIT);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.GONE);
                if(position==menuVies.size()-1){
                    mDrawerLayout.closeDrawers();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
        //view.setVisibility(View.GONE);
    }
}
