package org.wangguangjie.crime.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

/**
 * Created by wangguangjie on 2017/10/26.
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int desWidth, int desHeight){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);

        float srcHeight=options.outHeight;
        float srcWidth=options.outWidth;

        int inSampleSize=1;
        if(srcHeight>desHeight||srcWidth>desWidth){
            if(srcWidth>srcHeight){
                inSampleSize=Math.round(srcHeight/desHeight);
            }else{
                inSampleSize=Math.round(srcWidth/desWidth);
            }
        }
        options=new BitmapFactory.Options();
        options.inSampleSize=inSampleSize;
        return BitmapFactory.decodeFile(path,options);
    }
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point point=new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return getScaledBitmap(path,point.x,point.y);
    }
    public static Bitmap getScaledBitmap(String path, View view){
        return getScaledBitmap(path,view.getMeasuredHeight(),view.getMeasuredWidth());

    }
}
