package org.wangguangjie.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.wangguangjie.headline.R;

/**
 * Created by wangguangjie on 2018/2/14.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle onSaveIntanceState){
        super.onCreate(onSaveIntanceState);
        setContentView(R.layout.test_main_layout);
        ListView ls=(ListView)findViewById(R.id.ls);
        ls.setAdapter(new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1,android.R.id.text1,new String[]{"1","2","3","1","2","3"
                ,"1","2","3","1","2","3","1","2","3","1","2","3","1","2","3","1","2","3"}));
    }
}
