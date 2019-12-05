package com.must.base.widget.text;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import com.must.base.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * 有条件限制的一款editText
 * @author lilu
 * @date 2019/9/27
 * I Am The King of This World.
 */
public class RestrictEditText extends AppCompatEditText {

    private int maxLength;

    public RestrictEditText(Context context) {
        super(context);
    }

    public RestrictEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RestrictText);
        maxLength = array.getInteger(R.styleable.RestrictText_length,0);

        array.recycle();
        setInputFilter();
    }

    public RestrictEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setInputFilter(){

        //表情限制
        InputFilter emojiFilter= new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                }
                return null;
            }
        };

        if(maxLength > 0){
            //长度限制
            InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(maxLength);
            //表情长度一起限制
            setFilters(new InputFilter[]{emojiFilter,lengthFilter});
        }else{
            //只限制表情
            setFilters(new InputFilter[]{emojiFilter});
        }
    }

    public int getMaxLength(){
        return maxLength;
    }
}
