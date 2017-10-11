package org.wangguangjie.crime;

import java.util.UUID;

/**
 * Created by wangguangjie on 2017/10/11.
 */

public class Crime {

    private UUID mId;
    private String mTitle;

    public Crime(){
        mId=UUID.randomUUID();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }
}
