package org.wangguangjie;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.wangguangjie.headline.R;

/**
 * Created by wangguangjie on 2018/2/14.
 */

public class RefreshLinearLayout extends LinearLayout implements View.OnTouchListener{


    /**
     * refresh 四种状态
     */
    private int REFRESH_COMPLETED=0;
    private int PULL_TO_REFRESH=1;
    private int RELEASE_TO_REFRESH=2;
    private int REFRESHING=3;
    private int PUSH_GET_MORE=4;

    //当前状态;
    private int mCurrentState=REFRESH_COMPLETED;
    //上次状态，用于避免重复操作;
    private int mLastState;
    //刷新头
    private LinearLayout mHeader;
    //进度条
    private ProgressBar mProgressBar;
    //
    private ImageView mImageView;
    //状态描述
    private TextView mDescriptionTextView;
    //上次刷新时间
    private TextView mUpdateTimeTextView;
    //rooter
    private LinearLayout mRooter;
    //
    private TextView mRootTextView;

    //在被认为滚动操作之前的最大触摸距离;
    private int mTouchSlop;

    //
    private SharedPreferences mSharedPreferences;
    //
    private ListView mListView;
    //判断是或否已经加载
    private boolean hasLoaded;
    //按下纵方向位置
    private float mYDown;
    //
    private int mRatio=-10;
    //
    private MarginLayoutParams mHeaderMarginLayoutParams;
    private MarginLayoutParams mRooterMarginLayoutParams;
    //刷新监听器;
    private RefreshingListener mRefreshingListener;

    //
    final static private String LAST_UPDATE_TIME="last_update_time";


    public RefreshLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化值;
        mHeader=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_head,null);
        mProgressBar=(ProgressBar)mHeader.findViewById(R.id.progressbar);
        mImageView=(ImageView)mHeader.findViewById(R.id.arrow);
        mDescriptionTextView=(TextView)mHeader.findViewById(R.id.description);
        mUpdateTimeTextView=(TextView)mHeader.findViewById(R.id.update_time);
        //
        mRooter=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.refresh_root,null);
        mRootTextView=(TextView)mRooter.findViewById(R.id.get_more);

        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        hasLoaded=false;
        //是指为垂直布局;
        setOrientation(VERTICAL);
        //添加header
        addView(mHeader,0);
        addView(mRooter,2);

    }

    /**
     * LinearLayout 回调函数
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed,int l,int t, int r, int b){
        super.onLayout(changed,l,t,r,b);
        //避免重复加载;
        if(changed&&!hasLoaded){
            //设置header隐藏;
//            mHeaderMarginLayoutParams=(MarginLayoutParams)mHeader.getLayoutParams();
//            mHeaderMarginLayoutParams.topMargin=-mHeader.getHeight();
//            mHeader.setLayoutParams(mHeaderMarginLayoutParams);

           // mListView = (ListView) this.getChildAt(1);
            //
//            mRooterMarginLayoutParams=(MarginLayoutParams)mRooter.getLayoutParams();
//            mRooterMarginLayoutParams.topMargin=mListView.getHeight()+mRooter.getHeight();
//            mRooter.setLayoutParams(mRooterMarginLayoutParams);

            if(mListView!=null)
             //  mListView.setOnTouchListener(this);
            hasLoaded=true;
        }
    }

    private void setHeaderDescription(){
        String description="";
        if(mCurrentState==PULL_TO_REFRESH){
            description=getResources().getString(R.string.pull_to_refresh);
        }else if(mCurrentState==RELEASE_TO_REFRESH){
            description=getResources().getString(R.string.release_to_refresh);
        }else{
            description=getResources().getString(R.string.refreshing);
        }
        mDescriptionTextView.setText(description);
    }

    private void setHeaderUpdateTime(){
        long currentTime=System.currentTimeMillis();
        long lastTime=mSharedPreferences.getLong(LAST_UPDATE_TIME,-1);
        String description="";
        Long time=(currentTime-lastTime)/1000;
        if(lastTime==-1){
            description=getResources().getString(R.string.not_updated_yet);
        }else if(time<60){
            description=getResources().getString(R.string.updated_just_now);
        }else if(time<60*60){
            description=getResources().getString(R.string.updated_at,time/60+"分钟");
        }else if(time<60*60*24){
            description=getResources().getString(R.string.updated_at,time/60/60+"小时");
        }else if(time<60*60*24*30){
            description=getResources().getString(R.string.updated_at,time/60/60/24+"天");
        }else if(time<60*60*24*30*12){
            description=getResources().getString(R.string.updated_at,time/60/60/24/30+"天");
        }else{
            description=getResources().getString(R.string.updated_at,time/60/60/24/30/12+"年");
        }
        mUpdateTimeTextView.setText(description);
    }

    //
    private void setAnimation(){
        float centerX=mImageView.getWidth()/2;
        float centerY=mImageView.getHeight()/2;
        float fromDegree=0;
        float toDegree=0;
        if(mCurrentState!=mLastState) {
            if (mCurrentState == PULL_TO_REFRESH) {
                mImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                fromDegree = 180;
                toDegree = 360;
                RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, centerX, centerY);
                animation.setDuration(100);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            } else if (mCurrentState == RELEASE_TO_REFRESH) {
                mImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                fromDegree = 0;
                toDegree = 180;
                RotateAnimation animation = new RotateAnimation(fromDegree, toDegree, centerX, centerY);
                animation.setDuration(100);
                animation.setFillAfter(true);
                mImageView.startAnimation(animation);
            } else {
                mImageView.clearAnimation();
                mImageView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    //ListView触摸事件;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(canRefresh()||canGetMore())
        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mYDown = event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    float currentY = event.getRawY();
                    float distance = currentY - mYDown;
                    //下拉;
                    if(distance>=0){
                        //距离小于阈值时候不进行上拉操作;
                        if(distance<mTouchSlop)
                            return false;
                        //控制下拉操作的范围;
                        if(distance<=600){
                            //根据用户移动的距离计算下拉距离;
                            mHeaderMarginLayoutParams.topMargin = (int) distance / 2 - mHeader.getHeight();
                            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
                            mLastState=mCurrentState;
                            //根据此时header上边缘的距离判断此时状态;
                            if (mHeaderMarginLayoutParams.topMargin < 0) {
                                mCurrentState = PULL_TO_REFRESH;
                            } else {
                                mCurrentState = RELEASE_TO_REFRESH;
                            }
                            //根据状态调整动画
                            setAnimation();
                            //根据状态设置描述信息
                            setHeaderDescription();
                    }else{
                            //上拉
                            distance=mYDown-currentY;
                            if(distance<mTouchSlop)
                                return false;
                            if(distance<=600){

                            }else{
                                return false;
                            }
                        }


                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (mCurrentState == PULL_TO_REFRESH) {
                        new HideHeaderTask().execute();
                    } else if (mCurrentState == RELEASE_TO_REFRESH) {
                        mLastState=mCurrentState;
                        mCurrentState=REFRESHING;
                        new RefreshingTask().execute();
                    } else {
                        return false;
                    }
                    break;
                }
                default:break;
            }
            //设置上次更新时间;
            setHeaderUpdateTime();
            //根据状态调整动画
            setAnimation();
            //根据状态设置描述信息
            setHeaderDescription();
            return true;
        }
       else{
            return false;
        }
    }
    //判断是否可以进行下拉操作;
    private boolean canRefresh(){
        View firstChild = mListView.getChildAt(0);
        //只有当前一次刷新操作完毕才进行下次下拉操作
        if(mCurrentState!=REFRESHING) {
            //如果ListView不为空,则只有当其第一项在最顶端且此时顶端距离父组件为0才允许进行下拉操作;
            if (firstChild != null) {
                int firstVisiblePos = mListView.getFirstVisiblePosition();
                if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
            //如果ListView为空时不允许下拉操作;
            else {
                return false;
            }
        }else {
            return false;
        }
    }

    private boolean canGetMore(){
        View lastChild=mListView.getChildAt(mListView.getCount()-1);
        if(mCurrentState==REFRESH_COMPLETED||mCurrentState==PUSH_GET_MORE){
            if(lastChild!=null){
                int lastVisiblePos=mListView.getLastVisiblePosition();
                if(lastVisiblePos==mListView.getCount()-1&&lastChild.getBottom()==0){
                    return true;
                }
                else{
                    return false;
                }
            }
            else return false;
        }
        else{
            return false;
        }
    }

    private class HideHeaderTask extends AsyncTask<Void,Integer,Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            int marginY=mHeaderMarginLayoutParams.topMargin;
            while(true){
                marginY+=mRatio;
                if(marginY<=(-mHeader.getHeight())){
                    marginY=-mHeader.getHeight();
                    publishProgress(marginY);
                    break;
                }
                publishProgress(marginY);
                try{
                    Thread.sleep(20);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin){
            mHeaderMarginLayoutParams.topMargin=topMargin[0];
            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
        }
        @Override
        protected void onPostExecute(Integer res){
            refreshCompleted();
        }
    }

    private class RefreshingTask extends  AsyncTask<Void,Integer,Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            if(true){
                int marginY=mHeaderMarginLayoutParams.topMargin;
                while(true){
                    marginY+=mRatio;
                    if(marginY<=0){
                        marginY=0;
                        publishProgress(marginY);
                        break;
                    }
                    publishProgress(marginY);
                    try{
                        Thread.sleep(20);
                    }catch (InterruptedException ie){
                        ie.printStackTrace();
                    }
                }
                if(mRefreshingListener!=null)
                    mRefreshingListener.onRefresh();
                try{
                    Thread.sleep(3000);
                    //refreshCompleted();
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin){
            mHeaderMarginLayoutParams.topMargin=topMargin[0];
            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer res){
            mSharedPreferences.edit().putLong(LAST_UPDATE_TIME,System.currentTimeMillis()).commit();
            refreshCompleted();
            setAnimation();
        }
    }

    //刷新完毕;
    private void refreshCompleted(){
        mCurrentState=REFRESH_COMPLETED;
        mHeaderMarginLayoutParams.topMargin=-mHeader.getHeight();
        mHeader.setLayoutParams(mHeaderMarginLayoutParams);
    }

    public void setOnRefreshingListener(RefreshingListener refreshingListener){
        mRefreshingListener=refreshingListener;
    }
    interface RefreshingListener{
        void onRefresh();
    }

}
