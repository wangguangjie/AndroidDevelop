package org.wangguangjie.headline;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import org.wangguangjie.sidemenu.interfaces.Screenable;

/**
 * Created by wangguangjie on 2017/8/31.
 */

public class MainFragment extends Fragment implements Screenable{

    private Bitmap mBitmap;
    private View mContainerView;
    private String res;

    @Override
    public void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        //res=this.getArguments().getString(MainActivity.RESOURCE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle bundle){
        View rootView=inflater.inflate(R.layout.mainfragment,viewGroup,false);

//        final TextView textView=(TextView)rootView.findViewById(R.id.fragment_content);
//        //textView.setBackgroundResource(res);
//        textView.setText(res);
//        textView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction())
//                {
//                    case MotionEvent.ACTION_UP:
//                    {
//                        int cx=(int)motionEvent.getX();
//                        int cy=(int)motionEvent.getY();
//                        int radix=(int)Math.hypot(cx,cy);
//                        Animator animator= ViewAnimationUtils.createCircularReveal(textView,cx,cy,0,radix);
//                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//                        animator.setDuration(750);
//                        animator.start();
//                    }
//                    default:break;
//                }
//                return true;
//            }
//        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle saveInstanceState){
        super.onViewCreated(view,saveInstanceState);
        mContainerView=view.findViewById(R.id.hitfragment_container);
    }

    @Override
    public void takeScreen() {

        new Thread(){
            @Override
            public void run(){
                Bitmap bp=Bitmap.createBitmap(mContainerView.getWidth(),mContainerView.getHeight(),Bitmap.Config.ARGB_8888);
                Canvas canvas=new Canvas(bp);
                mContainerView.draw(canvas);
                mBitmap=bp;
            }
        }.start();
    }

    @Override
    public Bitmap getScreenBitmap() {
        return mBitmap;
    }
}
