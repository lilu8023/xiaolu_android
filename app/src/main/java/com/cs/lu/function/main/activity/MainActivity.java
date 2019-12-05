package com.cs.lu.function.main.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;

import com.cs.lu.R;
import com.must.base.activity.BaseActivity;
import com.must.imageloader.ImageLoader;

/**
 * Description:
 * 主界面
 * @author lilu
 * @date 2019/7/30
 * This is a simple function, how do I do it.
 */
public class MainActivity extends BaseActivity {


    private ImageView main_iv;
    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        setTitle("主页");

        main_iv = findViewById(R.id.main_iv);

        ImageLoader.getInstance().load("https://schoolsafetest-1257420679.cos.ap-chengdu.myqcloud.com/COS.folderName/1-picture-1568793683697.jpg")
        .into(main_iv);
    }

}
