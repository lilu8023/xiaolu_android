package com.must.base.widget.text;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.must.base.R;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/9/27
 * I Am The King of This World.
 */
public class LLEditText extends RelativeLayout {

    //显示型输入框
    private RestrictEditText ll_edit;
    //长度提示文本
    private TextView ll_text;

    //一般在直接New一个View的时候调用。
    public LLEditText(Context context) {
        super(context);
    }

    //一般在layout文件中使用的时候会调用，关于它的所有属性(包括自定义属性)都会包含在attrs中传递进来。
    public LLEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.ll_edit_layout,this,true);
        ll_edit = findViewById(R.id.ll_edit);
        ll_text = findViewById(R.id.ll_text);
        ll_text.setText("0/"+ll_edit.getMaxLength());

        //2019年9月27日17:07:22
        RelativeLayout.LayoutParams params = (LayoutParams) ll_text.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE,R.id.ll_edit);
        ll_text.setLayoutParams(params);

        ll_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ll_text.setText(s.length() + "/" + ll_edit.getMaxLength());
            }
        });
    }

    public LLEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
