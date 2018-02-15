package org.wangguangjie.hit;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wangguangjie.RefreshLinearLayout;
import org.wangguangjie.RefreshableView;
import org.wangguangjie.headline.R;
import org.wangguangjie.sidemenu.interfaces.Screenable;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wangguangjie on 2017/8/31.
 */

public class HitFragment extends Fragment implements Screenable{

    private StoreInformation store_lists;

    private final String HIT1 = "http://www.hitsz.edu.cn/article/id-74.html";
    private final String HIT2 = "http://www.hitsz.edu.cn/article/id-75.html";
    private final String HIT3 = "http://www.hitsz.edu.cn/article/id-77.html";
    private final String HIT4 = "http://www.hitsz.edu.cn/article/id-78.html";
    private final String HIT5 = "http://www.hitsz.edu.cn/article/id-80.html";

    private String url1 = HIT1 + "?maxPageItems=10&keywords=&pager.offset=";
    private String url2 = HIT2 + "?maxPageItems=10&keywords=&pager.offset=";
    private String url3 = HIT3 + "?maxPageItems=10&keywords=&pager.offset=";
    private String url4 = HIT4 + "?maxPageItems=10&keywords=&pager.offset=";
    private String url5 = HIT5 + "?maxPageItems=10&keywords=&pager.offset=";

    private String className1="announcement";
    private String className2="newsletters";
    private String className3="";
    private String className4="";
    private String className5="";
    private String className6="";
    //
    private String url = url1;
    private String page_url;
    private int pages;

    //页码数;初始页码为1;
    private int page_number;

    final private String HIT = "http://www.hitsz.edu.cn";
    //
    private ListView listView;
    //
    private InformationAdapter adapter;
    //
    private boolean first;
    //
    private int select;
    //主线程执行信息显示,如果出现异常情况通知用户;
    SpinnerAdapter spinnerAdapter;
    ActionBar.OnNavigationListener navigationListener;
    ActionBar actionBar;
    Spinner mSpinner;
    private View frgamentView;
    private int position;
    private boolean isRecover;

    private Bitmap mBitmap;
    private View mContainerView;
    private String res;

    private RefreshLinearLayout mLinearLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //更改解析出信息,更新界面;
            if (msg.what == 0x123) {
                showInfo();
                //缓存数据;
                new Thread(){
                    public void run(){
                        store_lists.storeData();
                    }
                }.start();
            }
            //通过actionbar的选择进行解析数据
            else if (msg.what == 0x124) {
                first = true;
                page_number = 0;
                page_url = url + page_number;
                page_number+=10;
                new Thread(new getThread()).start();
            }
            //如果无更多页面不许进行加载更多;
            else if (msg.what == 0x125) {
                Toast.makeText(getActivity(), "无更多信息!", Toast.LENGTH_LONG).show();
               // listView.getMoreComplete();
            }
            //处理异常信息;
            else if (msg.what == 0x111) {
                Toast.makeText(getActivity(), "无法获取信息", Toast.LENGTH_LONG).show();
            }
            //获取信息失败;
            else if (msg.what == 0x222) {
                Toast.makeText(getActivity(), "信息获取失败,请重新尝试!", Toast.LENGTH_LONG).show();
            }
            //无法连接网络;
            else if (msg.what == 0x333) {
                Toast.makeText(getActivity(), "无法连接网络,请重新尝试!", Toast.LENGTH_LONG).show();
            }
        }
    };

    //子线程执行网络信息的获取任务;
    class getThread implements Runnable {

        @Override
        public void run() {
            getMessage();
        }
    }

    class WastTime implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                Message message = new Message();
                message.what = 0x125;
                handler.sendMessage(message);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }


    public void setFrgamentView(View view){
        frgamentView=view;
    }

    @Override
    public void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        Log.d("test","fragment onCreate");
        initValues();
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d("test","fragment onAttach");
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        Log.d("test","fragment onActivityCreated");
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("test","fragment onStart");
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.d("test","fragment onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d("test","fragment onPause");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d("test","fragment onStop");
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d("test","fragment onDestroy");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("test","fragment onDestroy");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.d("test","fragment onDetach");
    }
    public void initValues(){
        first=true;
        page_number=0;
        page_url=url+page_number;
        page_number+=10;
        //isFistSpinner=true;
    }
    public void onItemSelected(int position){
        switch (position)
        {
            case 0:
                url=url1;
                break;
            case 1:
                url=url2;
                break;
            case 2:
                url=url3;
                break;
            case 3:
                url=url4;
                break;
            case 4:
                url=url5;
                break;
            default:
                break;
        }
        //if(!isFirst)
        {
            Message msg = new Message();
            msg.what = 0x124;
            handler.sendMessage(msg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle bundle){
        Log.d("test","fragment onCreateView");
        View rootView=inflater.inflate(R.layout.pulllist,viewGroup,false);
        mLinearLayout=(RefreshLinearLayout) rootView;
        listView=(ListView)rootView.findViewById(R.id.hitfragment_container);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                Intent intent1=new Intent(getActivity(),WebInformation.class);
                Bundle bundle=new Bundle();
                NewItem item=store_lists.getLists().get(position-1);
                bundle.putString("url",item.getUrl());
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });
        //刷新;
        mLinearLayout.setOnRefreshingListener(new RefreshLinearLayout.RefreshingListener() {
            @Override
            public void onRefresh() {
                first=true;
                page_number=0;
                page_url=url+page_number;
                page_number+=10;
                //page_number++;
                //page_url+=page_number;
                //pages=0;
                //lists.clear();
                new Thread(new getThread()).start();
            }
        });
        //加载更多;
        mLinearLayout.setOnGetMoreListener(new RefreshLinearLayout.GetMoreListener() {
            @Override
            public void onGetMore() {
                first=false;
                if(page_number/10<=pages)
                {
                    page_url=url+page_number;
                    page_number+=10;
                    new Thread(new getThread()).start();
                }
                else
                {
                    new Thread(new WastTime()).start();
                }
            }
        });
        Log.d("onCreateView","createView");
        initData();
        // isFistSpinner=true;
        return rootView;
    }

    private void initData(){
        store_lists = new StoreInformation(getActivity().getSharedPreferences("hit1", MODE_PRIVATE));
        store_lists.recoveryData();
        //如果有数据则加载（此种处理，包括上面注释的处理的目的都在于认为内存中加载的数据比网络获取数据更快，可以避免让用户长时间等待而看不到数据）
        if (store_lists.getLists().size() > 0) {
            adapter = new InformationAdapter(getActivity(), store_lists.getLists());
            listView.setAdapter(adapter);
            listView.deferNotifyDataSetChanged();
        }

        //new Thread(new getThread()).start();
    }

    //主线程显示信息;
    private void showInfo()
    {
        adapter = new InformationAdapter(getActivity(), store_lists.getLists());
        listView.setAdapter(adapter);
        //信息获取完毕,技术刷新操作;
        //mLinearLayout.finishRefreshing();
        //listView.getMoreComplete();
        //刷新动画;
        if(first&&frgamentView!=null) {
            View view1 = frgamentView;
            int[] location = {0, 0};
            view1.getLocationOnScreen(location);
            int cx = location[0] + view1.getWidth() / 2;
            int cy = view1.getHeight() / 2;
            int radix = (int) Math.hypot(view1.getWidth() / 2, view1.getHeight() / 2);
            Animator animator = ViewAnimationUtils.createCircularReveal(view1, cx, cy, 0, radix);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(750);
            animator.start();
        }
    }
    //获取网络信息;
    private void getMessage()
    {
        if(isNetWorkAvailable())
        {
            analyHtml();
            Message msg = new Message();
            msg.what = 0x123;
            handler.sendMessage(msg);
        }
        else
        {
            Message message=new Message();
            message.what=0x333;
            handler.sendMessage(message);
        }
    }

    //直接从获取页码的document进行解析;
    //本程序采用这种方法解析html;
    public void  analyHtml()
    {
        Connection connect = Jsoup.connect(page_url);
        //伪装成浏览器对url进行访问,防止无法获取某些网站的document;
        connect.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
        try
        {
            Document doc = connect.get();

            //解析出body 标签下的div标签;
            Elements elements = doc.select("body ul");
            Log.d("mylogcat2","2");
            //上拉刷新或者第一次刷新,清除数据;
            if(first)
            {
                store_lists.clear();
                Elements elements1=doc.select("body div");
                for(Element element:elements1){
                    if(element.className().equals("page_num")){
                        pages=element.getElementsByTag("a").size();
                    }
                }
            }

            //获取相关信息:Jsoup;
            for(Element el1:elements)
            {
                if(el1.className().equals("announcement"))
                {
                    analyze1(el1);
                    break;
                }
                else if(el1.className().equals("newsletters"))
                {
                    analyze2(el1);
                    break;
                }
                else if(el1.className().equals("lecture_n"))
                {
                    analyze3(el1);
                    break;
                }

            }
        }
        catch (Exception ie)
        {
            Log.d("mylogcat0",ie.toString());
            //无法获取document时候提醒用户;
            Message message=new Message();
            message.what=0x222;
            handler.sendMessage(message);
            ie.printStackTrace();
        }
    }
    //通知公告和媒体报道.
    private void analyze1(Element el1){
        String title;
        String url;
        String time;
        String visitCount;
        String top;
        Elements el0=el1.getElementsByTag("li");
        for(Element ell:el0)
        {
            title=ell.getElementsByTag("a").text();
            url=HIT+ell.getElementsByTag("a").attr("href");
            Elements ss=ell.getElementsByTag("span");
            if(ss.size()==3){
                top=ss.get(0).text();
                title=title.substring(0,title.length()-2)+"[置顶]";
                time=ss.get(1).text();
                visitCount=ss.get(2).text();
            }else
            {
                time=ss.get(0).text();
                visitCount=ss.get(1).text();
            }
            //title=ss.get(1).text();
            store_lists.addItem(new NewItem(title,time,visitCount,url));
        }
    }
    //新闻速递和校园动态.
    private void analyze2(Element el1){
        String title;
        String url;
        String time;
        String visitCount;
        String top;
        Elements el0=el1.getElementsByTag("li");
        for(Element ell:el0){
            title=ell.getElementsByTag("a").text();
            url=HIT+ell.getElementsByTag("a").get(0).attr("href");
            Elements ss=ell.getElementsByTag("span");
            time=ss.get(0).text();
            visitCount=ss.get(1).text();
            store_lists.addItem(new NewItem(title,time,visitCount,url));
        }
    }
    //讲座论坛.
    private void analyze3(Element el1){
        String title="";
        String url;
        String time="";
        String visitCount="";
        String top;
        Elements el0=el1.getElementsByTag("li");
        for(Element ell:el0){
            Elements s=ell.getElementsByTag("div");
            title=s.get(0).getElementsByTag("a").text();
            url=HIT+s.get(0).getElementsByTag("a").attr("href");
            //time=s.get(2).getElementsByTag("span").get(0).text();
            Elements s1=s.get(2).getElementsByTag("div");
            time=s1.get(1).text();
            visitCount=s1.get(2).text()+"\n"+s1.get(3).text();
            store_lists.addItem(new NewItem(title,time,visitCount,url));
        }
    }
    //判断是否有网络连接;
    public boolean isNetWorkAvailable()
    {
        Context context=getActivity().getApplicationContext();
        ConnectivityManager connectmanger=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo network=connectmanger.getActiveNetworkInfo();
        if(connectmanger!=null)
        {
            NetworkInfo ninfo=connectmanger.getActiveNetworkInfo();
            return ninfo != null && ninfo.isConnected();
        }
        else{
            return false;
        }
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
    public Bitmap getScreenBimap() {
        return mBitmap;
    }
}
