package org.wangguangjie.sidemenu.interfaces;

import android.graphics.Bitmap;

/**
 * Created by wangguangjie on 2017/8/31.
 * 定义屏幕获取接口;
 * 在动画开始之前，首先对屏幕进行设置，避免动画从白色背景开始绘制;
 */

public interface Screenable {
    void takeScreen();
    Bitmap getScreenBitmap();
}
