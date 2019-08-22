package com.must.base.widget.statuslayout;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.List;

/**
 * Description:
 *
 * @author lilu
 * @date 2019/5/24
 * This is a simple function, how do I do it.
 */

public interface ProgressLayout {

    void showContent();

    void showContent(List<Integer> idsOfViewsNotToShow);

    void showLoading();

    void showLoading(List<Integer> idsOfViewsNotToHide);

    void showEmpty(int icon, String description);

    void showEmpty(Drawable icon, String description);

    void showEmpty(int icon, String description, View.OnClickListener buttonClickListener);

    void showEmpty(int icon, String description, List<Integer> idsOfViewsNotToHide);

    void showEmpty(Drawable icon, String description, List<Integer> idsOfViewsNotToHide);

    void showError(int icon, String description, String buttonText, View.OnClickListener buttonClickListener);

    void showError(Drawable icon, String description, String buttonText, View.OnClickListener buttonClickListener);

    void showError(int icon, String description, String buttonText, View.OnClickListener buttonClickListener, List<Integer> idsOfViewsNotToHide);

    void showError(Drawable icon, String description, String buttonText, View.OnClickListener buttonClickListener, List<Integer> idsOfViewsNotToHide);

    String getCurrentState();

    boolean isContentCurrentState();

    boolean isLoadingCurrentState();

    boolean isEmptyCurrentState();

    boolean isErrorCurrentState();

}
