package org.wangguangjie.sidemenu.model;

import org.wangguangjie.sidemenu.interfaces.Resourceable;

/**
 * Created by wangguangjie on 2017/8/31.
 */

public class SideItem implements Resourceable {

    private int resourceId;
    private String name;

    public SideItem(int id,String name){
        this.resourceId=id;
        this.name=name;
    }
    @Override
    public int getResouceId() {
        return resourceId;
    }

    @Override
    public String getName() {
        return name;
    }
}
