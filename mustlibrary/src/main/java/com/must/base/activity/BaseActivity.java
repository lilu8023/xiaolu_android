package com.must.base.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.must.base.R;
import com.must.base.widget.statuslayout.StatusLayout;

/**
 * Description:
 * 基础activity,所有的activity都要继承此父类
 * @author lilu
 * @date 2019/7/31
 * This is a simple function, how do I do it.
 */

public abstract class BaseActivity extends AppCompatActivity{

    /**
     * 获取布局文件
     * @return 布局文件
     */
    public abstract @LayoutRes int setLayoutId();

    /**
     * 子类入口
     * @param savedInstanceState 数据缓存 避免子类缓存数据但是却拿不到缓存对象
     */
    public abstract void initView(@Nullable Bundle savedInstanceState);

    //返回
    private ImageView toolbar_back_iv;
    //关闭
    private ImageView toolbar_close_iv;
    //标题
    private TextView toolbar_title_tv;
    //状态布局
    private StatusLayout mContentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置父布局内容
        super.setContentView(R.layout.base_layout);
        //初始化统一得标题栏
        initToolbarView();

        mContentView = findViewById(R.id.base_status);
        //获取子布局内容
        View mSubView = View.inflate(this,setLayoutId(),null);
        //将子布局加入到父布局中  为了统一添加toolbar
        mContentView.addView(mSubView);

        //子类入口
        initView(savedInstanceState);

    }

    /**
     * 初始化统一得标题栏
     */
    private void initToolbarView(){
        //返回图片按钮
        toolbar_back_iv = findViewById(R.id.toolbar_back_iv);
        //关闭图片按钮
        toolbar_close_iv = findViewById(R.id.toolbar_close_iv);
        //标题
        toolbar_title_tv = findViewById(R.id.toolbar_title_tv);

    }

    /**
     * 设置标题栏
     * @param title 标题字
     */
    protected void setTitle(@Nullable String title){
        if(toolbar_title_tv != null){
            toolbar_title_tv.setText(title);
        }
    }

    /**
     * 显示加载中
     */
    protected void showLoading(){
        if(mContentView != null){
            mContentView.showLoading();
        }
    }
}
