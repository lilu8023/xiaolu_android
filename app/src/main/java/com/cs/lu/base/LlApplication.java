package com.cs.lu.base;

import android.app.Application;

import com.cs.lu.imageloader.GlideLoader;
import com.must.imageloader.ImageLoader;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/12/5
 * I Am The King of This World.
 */
public class LlApplication extends Application {

    public static LlApplication mContext;
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        ImageLoader.getInstance().setGlobalImageLoader(new GlideLoader());

    }

    public static LlApplication getmContext() {
        return mContext;
    }
}
