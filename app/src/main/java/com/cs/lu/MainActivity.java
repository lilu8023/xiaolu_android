package com.cs.lu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.must.base.activity.BaseActivity;

/**
 * Description:
 * 主界面
 * @author lilu
 * @date 2019/7/30
 * This is a simple function, how do I do it.
 */
public class MainActivity extends BaseActivity {


    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        setTitle("主页");

        showLoading();
    }
}
