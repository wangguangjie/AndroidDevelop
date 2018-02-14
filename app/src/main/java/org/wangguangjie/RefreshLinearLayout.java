package org.wangguangjie;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    //当前状态;
    private int mCurrentState=REFRESH_COMPLETED;
    //上次状态，用于避免重复操作;
    private int lastState;
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
    //刷新监听器;
    private RefreshingListener mRefreshingListener;
    //
    private boolean canToPull;

    public RefreshLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化值;
        mHeader=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_head,null);
        mProgressBar=(ProgressBar)mHeader.findViewById(R.id.progressbar);
        mImageView=(ImageView)mHeader.findViewById(R.id.arrow);
        mDescriptionTextView=(TextView)mHeader.findViewById(R.id.description);
        mUpdateTimeTextView=(TextView)mHeader.findViewById(R.id.update_time);
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        hasLoaded=false;
        //是指为垂直布局;
        setOrientation(VERTICAL);
        //添加header
        addView(mHeader,0);

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
            mHeaderMarginLayoutParams=(MarginLayoutParams)mHeader.getLayoutParams();
            mHeaderMarginLayoutParams.topMargin=-mHeader.getHeight();
            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
            mListView = (ListView) this.getChildAt(1);
            if(mListView!=null)
               mListView.setOnTouchListener(this);
            hasLoaded=true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        canRefresh(event);
        {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    mYDown = event.getRawY();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    float currentY = event.getRawY();
                    float distance = currentY - mYDown;
                    if(distance<=0&&mHeaderMarginLayoutParams.topMargin<=(-mHeader.getHeight()))
                        return false;
                    if(distance<mTouchSlop)
                        return false;
                    if(distance<=600) {
                        if (mCurrentState == REFRESHING) return false;
                        else {
                            if (mHeaderMarginLayoutParams.topMargin < 0) {
                                mCurrentState = PULL_TO_REFRESH;
                            } else {
                                mCurrentState = RELEASE_TO_REFRESH;
                            }
                            mHeaderMarginLayoutParams.topMargin = (int) distance / 2 - mHeader.getHeight();
                            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (mCurrentState == PULL_TO_REFRESH) {
                        new HideHeaderTask().execute();
                    } else if (mCurrentState == RELEASE_TO_REFRESH) {
                        new RefreshingTask().execute();
                    } else {
                        return false;
                    }
                    break;
                }
                default:break;
            }
            return true;
        }
       // return false;
    }
    private void canRefresh(MotionEvent event){
        View firstChild = mListView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = mListView.getFirstVisiblePosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                canToPull=true;
            } else {
              canToPull=false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新
           canToPull=true;
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
            mCurrentState=REFRESH_COMPLETED;
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
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin){
            mHeaderMarginLayoutParams.topMargin=topMargin[0];
            mHeader.setLayoutParams(mHeaderMarginLayoutParams);
            if(topMargin[0]==0){
                refreshCompleted();
            }
        }

        @Override
        protected void onPostExecute(Integer res){
           // refreshCompleted();
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
