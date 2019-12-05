package com.must.imageloader;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/9/19
 * I Am The King of This World.
 */
public interface ILoaderStrategy<T extends LoaderOptions> {

    /**
     * 加载图片
     * @param loaderOptions 图片条件
     */
    void displayImage(T loaderOptions);

    /**
     * 清理内存缓存
     */
    void clearMemoryCache();

    /**
     * 清理磁盘缓存
     */
    void clearDiskCache();
}
