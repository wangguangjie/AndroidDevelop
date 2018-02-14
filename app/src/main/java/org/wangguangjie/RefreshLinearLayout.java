package org.wangguangjie;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.wangguangjie.headline.R;

/**
 * Created by wangguangjie on 2018/2/14.
 */

public class RefreshLinearLayout extends LinearLayout {


    /**
     * refresh 四种状态
     */
    private int REFRESH_COMPLETED=0;
    private int PULL_TO_REFRESH=1;
    private int RELEASE_TO_REFRESH=2;
    private int REFRESHING=3;

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

    public RefreshLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHeader=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_head,null);
        mProgressBar=(ProgressBar)mHeader.findViewById(R.id.progressbar);
        mImageView=(ImageView)mHeader.findViewById(R.id.arrow);
        mDescriptionTextView=(TextView)mHeader.findViewById(R.id.description);
        mUpdateTimeTextView=(TextView)mHeader.findViewById(R.id.update_time);
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        setOrientation(VERTICAL);

        addView(mHeader,0);
    }

    public void
}
