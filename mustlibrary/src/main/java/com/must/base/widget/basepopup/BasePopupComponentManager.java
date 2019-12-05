package com.must.base.widget.basepopup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.must.base.utils.PopupUtils;
import com.must.base.utils.log.PopupLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 大灯泡 on 2019/5/13
 * <p>
 * Description：支持管理器
 */
public final class BasePopupComponentManager {

    private static Application mApplicationContext;

    BasePopupComponentProxy proxy;
    private WeakReference<Activity> mTopActivity;
    private int account = 0;


    class BasePopupComponentProxy implements BasePopupComponent {
        private List<BasePopupComponent> IMPL;
        private static final String IMPL_X = "razerdp.basepopup.BasePopupComponentX";


        BasePopupComponentProxy(Context context) {
            IMPL = new ArrayList<>();
            try {
                if (isClassExist(IMPL_X)) {
                    IMPL.add((BasePopupComponent) Class.forName(IMPL_X).newInstance());
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            PopupLog.i(IMPL);
        }

        @Override
        public View findDecorView(BasePopupWindow basePopupWindow, Activity activity) {
            if (PopupUtils.isListEmpty(IMPL)) return null;
            for (BasePopupComponent basePopupComponent : IMPL) {
                View result = basePopupComponent.findDecorView(basePopupWindow, activity);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }

        @Override
        public BasePopupWindow attachLifeCycle(BasePopupWindow basePopupWindow, Object owner) {
            if (PopupUtils.isListEmpty(IMPL)) return null;
            for (BasePopupComponent basePopupComponent : IMPL) {
                if (basePopupWindow.lifeCycleObserver != null) return basePopupWindow;
                basePopupComponent.attachLifeCycle(basePopupWindow, owner);
            }
            return basePopupWindow;
        }

        @Override
        public BasePopupWindow removeLifeCycle(BasePopupWindow basePopupWindow, Object owner) {
            if (PopupUtils.isListEmpty(IMPL)) return null;
            for (BasePopupComponent basePopupComponent : IMPL) {
                if (basePopupWindow.lifeCycleObserver == null) return basePopupWindow;
                basePopupComponent.removeLifeCycle(basePopupWindow, owner);
            }
            return basePopupWindow;
        }
    }


    private static class SingleTonHolder {
        private static BasePopupComponentManager INSTANCE = new BasePopupComponentManager();
    }


    private BasePopupComponentManager() {

    }

    void init(Context context) {
        if (proxy != null) return;
        if (context instanceof Application) {
            mApplicationContext = (Application) context;
            regLifeCallback();
        } else {
            mApplicationContext = (Application) context.getApplicationContext();
            regLifeCallback();
        }
        proxy = new BasePopupComponentProxy(context);
    }

    public Activity getTopActivity() {
        return mTopActivity == null ? null : mTopActivity.get();
    }

    public boolean isAppOnBackground() {
        PopupLog.i("isAppOnBackground", account <= 0);
        return account <= 0;
    }

    private void regLifeCallback() {
        mApplicationContext.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                account++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mTopActivity = new WeakReference<>(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                account--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static BasePopupComponentManager getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    public static Application getApplication() {
        return mApplicationContext;
    }

    private boolean isClassExist(String name) {
        try {
            Class.forName(name);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
