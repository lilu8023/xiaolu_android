package com.must.imageloader;

import android.graphics.Bitmap;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/12/5
 * I Am The King of This World.
 */
public interface BitmapCallBack {

    void onBitmapLoaded(Bitmap bitmap);

    void onBitmapFailed(Exception e);

    public static class EmptyCallback implements BitmapCallBack {


        @Override
        public void onBitmapLoaded(Bitmap bitmap) {

        }

        @Override
        public void onBitmapFailed(Exception e) {

        }
    }
}
