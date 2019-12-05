package com.must.base.widget.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.must.base.R;

/**
 * Description:
 * 用于首页导航的一种控件
 * 采用viewpager+fragment+recyclerview
 * @author lilu
 * @date 2019/8/22
 * This is a simple function, how do I do it.
 */

public class NavigationLayout extends RelativeLayout{

    public NavigationLayout(Context context) {
        super(context);
    }

    public NavigationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View navigationView = LayoutInflater.from(context).inflate(R.layout.navigation_layout,null);
    }
}
