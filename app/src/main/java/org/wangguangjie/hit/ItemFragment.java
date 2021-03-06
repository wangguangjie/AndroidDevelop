package org.wangguangjie.hit;

/**
 * Created by wangguangjie on 2018/3/8.
 */

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wangguangjie.MainActivity;
import org.wangguangjie.RefreshLinearLayout;
import org.wangguangjie.headline.R;
import org.wangguangjie.hit.controller.InformationAdapter;
import org.wangguangjie.hit.controller.WebInformation;
import org.wangguangjie.hit.model.NewItem;
import org.wangguangjie.hit.utils.StoreInformation;

import java.lang.ref.WeakReference;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wangguangjie on 2017/8/31.
 * 开发的一款获取HIT官网文档信息，并且通过jsoup解析文本信息并显示出具官网信息
 * 功能：
 *    1.首页查看官网各个条目的简要信息
 *    2.点击选项可查看具体的消息
 *    3.支持下拉刷新和上拉加载更多
 *    4.本地缓存部分数据，提高用户体验
 * 后期改进：
 *    1.对选项条目进行重新设计，使用TableLayout和ViewPager;
 *    2.添加收藏功能，选项增加收藏条目;
 *    3.使用SQLite对数据进行操作;
 *    4.处理handler内存泄露问题;
 * 开发难点：
 *
 * Bug及修复
 *     1.由于注册RefreshLinearLayout的上拉和下拉监听器时，又开启了新的子线程，导致数据访问异常.
 *     2.在Spinner加载时候就进行回调了依次选项选择监听器，刚打开应用时重复加载数据.
 *
 *
 */

public class ItemFragment extends Fragment{

    private StoreInformation store_lists;

    private final String mString="?maxPageItems=10&keywords=&pager.offset=";
    private String url;
    private int pages;
    private String page_url;

    //页码数;初始页码为1;
    private int page_number;

    final private String HIT = "http://www.hitsz.edu.cn";
    //
    private ListView listView;
    //
    private InformationAdapter adapter;
    //
    private boolean first;

    private Bitmap mBitmap;
    private View mContainerView;

    private RefreshLinearLayout mLinearLayout;

    private String mPreferencesName="default_name";

    private View rootView;

    private Handler handler=new MyHandler(this);
    //handler的匿名内部内拥有外部对象的引用.如果MessageQueue中有此handler发送的message，此message又拥有handler的引用，
    //而handler又拥有外部对象的引用，所以无法垃圾回收外部对象，导致内存泄露.
    //更改的做法是使用静态的handler类,同时使用弱引用应用外部对象，此时垃圾回收机制就可以对外部对象进行垃圾回收；
    //同时当messagequeue中还有次handler发送的消息时，此时handler还会处理处理消息，此时handler应该移除回调在外部对象销毁时;
    private static class MyHandler extends Handler {
        //外层fragment对象的的弱引用;
        private final WeakReference<ItemFragment> mFragmentRe;

        public MyHandler(ItemFragment fragment) {
            mFragmentRe = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            //更改解析出信息,更新界面;
            if (msg.what == 0x123) {
                mFragmentRe.get().updateUI();
            }
            //通过actionbar的选择进行解析数据
            else if (msg.what == 0x124) {
                mFragmentRe.get().first = true;
                mFragmentRe.get().page_number = 0;
                mFragmentRe.get().page_url = mFragmentRe.get().url + mFragmentRe.get().page_number;
                mFragmentRe.get().page_number += 10;
                new Thread(mFragmentRe.get().new getThread()).start();
            }
            //如果无更多页面不许进行加载更多;
            else if (msg.what == 0x125) {
                Toast.makeText(mFragmentRe.get().getActivity(), "无更多信息!", Toast.LENGTH_LONG).show();
                // listView.getMoreComplete();
            } else if (msg.what == 0x126) {
                mFragmentRe.get().adapter = new InformationAdapter(mFragmentRe.get().getContext(),
                        mFragmentRe.get().store_lists.getLists());
                mFragmentRe.get().listView.setAdapter(mFragmentRe.get().adapter);
                mFragmentRe.get().listView.deferNotifyDataSetChanged();
            }
            //处理异常信息;
            else if (msg.what == 0x111) {
                Toast.makeText(mFragmentRe.get().getActivity(), "无法获取信息", Toast.LENGTH_LONG).show();
            }
            //获取信息失败;
            else if (msg.what == 0x222) {
                Toast.makeText(mFragmentRe.get().getActivity(), "信息获取失败,请重新尝试!", Toast.LENGTH_LONG).show();
            }
            //无法连接网络;
            else if (msg.what == 0x333) {
                Toast.makeText(mFragmentRe.get().getActivity(), "无法连接网络,请重新尝试!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //当外部对象销毁时，移除所有handler关联的messagequeue中的所有message，也就是说handler无法再接收到信息（如果说无法再接收到自己所发送的信息
        // 这其实并不恰当，因为message可以是target，所以其他handler也可以发送信息给此handler）;
        //移除此handler所发送的，没有callback的全部message;
        handler.removeCallbacks(null);
    }

    //子线程执行网络信息的获取任务;
    class getThread implements Runnable {
        @Override
        public void run() {
            getMessage();
        }
    }

    //异步加载缓存数据;
    class RecoveryThread implements Runnable {
        @Override
        public void run() {
            store_lists.recoveryData();
            Message msg = new Message();
            msg.getCallback();
            msg.what = 0x126;
            handler.sendMessage(msg);
        }
    }


    @Override
    public void onCreate(Bundle saveInstance){
        super.onCreate(saveInstance);
        Bundle bundle=getArguments();
        mPreferencesName=bundle.getString(MainActivity.PREFERENCES);
        url=bundle.getString("url")+mString;
        //初始化数据;
        first=true;
        page_number=0;
        page_url=url+page_number;
        page_number+=10;
        Log.d("test","fragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,Bundle bundle){
        Log.d("test","fragment onCreateView");
       if(rootView==null){
           rootView = inflater.inflate(R.layout.news_fragment_refresh_linearlayout, viewGroup, false);
           mLinearLayout = (RefreshLinearLayout) rootView.findViewById(R.id.refresh_linear_layout);
           //设置此LinearLayout的SharedPreferences保存数据;
           mLinearLayout.setSharedPreferenceName(mPreferencesName);
           listView = (ListView) rootView.findViewById(R.id.list_view);
           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                   Intent intent1 = new Intent(getActivity(), WebInformation.class);
                   Bundle bundle = new Bundle();
                   NewItem item = store_lists.getLists().get(position);
                   bundle.putString("url", item.getUrl());
                   intent1.putExtras(bundle);
                   startActivity(intent1);
               }
           });
           //注册下拉刷新监听器;
           mLinearLayout.setOnRefreshingListener(new RefreshLinearLayout.RefreshingListener() {
               @Override
               public void onRefresh() {
                   first = true;
                   page_number = 0;
                   page_url = url + page_number;
                   page_number += 10;
                   getMessage();
                   //缓存最新数据;
                   store_lists.storeData();
               }
           });
           //注册上拉加载更多监听器;
           mLinearLayout.setOnGetMoreListener(new RefreshLinearLayout.GetMoreListener() {
               @Override
               public void onGetMore() {
                   first = false;
                   if (page_number / 10 <= pages) {
                       page_url = url + page_number;
                       page_number += 10;
                       getMessage();
                       //缓存最新数据
                       store_lists.storeData();
                   }
               }
           });
           store_lists = new StoreInformation(getActivity().getSharedPreferences(mPreferencesName + "hit", MODE_PRIVATE));
           //开启恢复线程，恢复本地数据;
           new Thread(new RecoveryThread()).start();
       }
        return rootView;
    }
    @Override
    public void onResume(){
        super.onResume();
        //如果首次打开应用，则进行数据获取;
        if(store_lists.getLists().size()==0){
            new Thread(new getThread()).start();
        }
    }

    //主线程显示信息;
    protected void updateUI()
    {
        adapter.notifyDataSetChanged();
    }
    //获取网络信息;
    private void getMessage()
    {
        //判断网络是否可用;
        if(isNetWorkAvailable())
        {
            analyzeHtml();
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
    public void  analyzeHtml()
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
            //下拉更新清除数据;
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
        //mContainerView=view.findViewById(R.id.view_pager);
    }

}
