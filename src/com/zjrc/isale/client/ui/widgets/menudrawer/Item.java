package com.zjrc.isale.client.ui.widgets.menudrawer;

public class Item {

    public String mTitle;
    public int mIconRes;
    public int mNum=-1;
    public boolean selected;
    public Item(String title, int iconRes,int mNum,boolean selected) {
        mTitle = title;
        mIconRes = iconRes;
        this.mNum = mNum;
        this.selected =selected;
    }
}
