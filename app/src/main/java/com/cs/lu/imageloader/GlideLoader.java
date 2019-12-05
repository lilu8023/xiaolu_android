package com.cs.lu.imageloader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.must.imageloader.ILoaderStrategy;
import com.must.imageloader.LoaderOptions;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/12/5
 * I Am The King of This World.
 */
public class GlideLoader implements ILoaderStrategy {


    @Override
    public void displayImage(LoaderOptions loaderOptions) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(loaderOptions.placeholderResId);
        requestOptions.error(loaderOptions.errorResId);
        requestOptions.fallback(loaderOptions.errorResId);
        Glide.with(loaderOptions.targetView.getContext())
                .load(loaderOptions.url)
                .apply(requestOptions)
                .into(loaderOptions.targetView);
    }

    @Override
    public void clearMemoryCache() {

    }

    @Override
    public void clearDiskCache() {

    }
}
