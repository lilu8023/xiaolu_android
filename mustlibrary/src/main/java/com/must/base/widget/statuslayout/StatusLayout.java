package com.must.base.widget.statuslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.must.base.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description:
 * 多状态布局
 * @author lilu
 * @date 2019/5/24
 * This is a simple function, how do I do it.
 */

public class StatusLayout extends FrameLayout implements ProgressLayout {

    private final String CONTENT = "type_content";
    private final String LOADING = "type_loading";
    private final String EMPTY = "type_empty";
    private final String ERROR = "type_error";

    private LayoutInflater inflater;
    private View view;
    private Drawable defaultBackground;

    private List<View> contentViews = new ArrayList<>();

    private View loadingState;
    private ProgressBar loadingStateProgressBar;

    private View emptyState;
    private ImageView emptyStateImageView;
    private TextView emptyStateContentTextView;
    private TextView status_empty_again_tv;

    private View errorState;
    private ImageView errorStateImageView;
    private TextView errorStateContentTextView;
    private TextView errorStateButton;

    private int loadingStateProgressBarWidth;
    private int loadingStateProgressBarHeight;
    private int loadingStateProgressBarColor;
    private int loadingStateBackgroundColor;

    private int emptyStateImageWidth;
    private int emptyStateImageHeight;
    private int emptyStateTitleTextSize;
    private int emptyStateTitleTextColor;
    private int emptyStateContentTextSize;
    private int emptyStateContentTextColor;
    private int emptyStateBackgroundColor;

    private int errorStateImageWidth;
    private int errorStateImageHeight;
    private int errorStateContentTextSize;
    private int errorStateContentTextColor;
    private int errorStateButtonTextColor;
    private int errorStateButtonBackgroundColor;
    private int errorStateBackgroundColor;

    private String state = CONTENT;

    public StatusLayout(Context context) {
        super(context);
    }

    public StatusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StatusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LayoutStatus);

        //Loading state attrs
        loadingStateProgressBarWidth =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_loadingProgressBarWidth, 108);

        loadingStateProgressBarHeight =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_loadingProgressBarHeight, 108);

        loadingStateProgressBarColor =
                typedArray.getColor(R.styleable.LayoutStatus_loadingProgressBarColor, Color.RED);

        loadingStateBackgroundColor =
                typedArray.getColor(R.styleable.LayoutStatus_loadingBackgroundColor, Color.TRANSPARENT);

        //Empty state attrs
        emptyStateImageWidth =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_emptyImageWidth, 308);

        emptyStateImageHeight =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_emptyImageHeight, 308);

        emptyStateTitleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_emptyTitleTextSize, 15);

        emptyStateTitleTextColor =
                typedArray.getColor(R.styleable.LayoutStatus_emptyTitleTextColor, Color.BLACK);

        emptyStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_emptyContentTextSize, 14);

        emptyStateContentTextColor =
                typedArray.getColor(R.styleable.LayoutStatus_emptyContentTextColor, Color.BLACK);

        emptyStateBackgroundColor =
                typedArray.getColor(R.styleable.LayoutStatus_emptyBackgroundColor, Color.TRANSPARENT);

        //Error state attrs
        errorStateImageWidth =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_errorImageWidth, 308);

        errorStateImageHeight =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_errorImageHeight, 308);

        errorStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.LayoutStatus_errorContentTextSize, 14);

        errorStateContentTextColor =
                typedArray.getColor(R.styleable.LayoutStatus_errorContentTextColor, Color.BLACK);

        errorStateButtonTextColor =
                typedArray.getColor(R.styleable.LayoutStatus_errorButtonTextColor, Color.BLACK);

        errorStateButtonBackgroundColor =
                typedArray.getColor(R.styleable.LayoutStatus_errorButtonBackgroundColor, Color.WHITE);

        errorStateBackgroundColor =
                typedArray.getColor(R.styleable.LayoutStatus_errorBackgroundColor, Color.TRANSPARENT);

        typedArray.recycle();

        defaultBackground = this.getBackground();
    }

    @Override
    public void showContent() {
        switchState(CONTENT, 0, null, null, null, Collections.<Integer>emptyList());
    }

    @Override
    public void showContent(List<Integer> idsOfViewsNotToShow) {
        switchState(CONTENT, 0, null, null, null,idsOfViewsNotToShow);
    }

    @Override
    public void showLoading() {
        switchState(LOADING, 0, null, null, null, Collections.<Integer>emptyList());
    }

    @Override
    public void showLoading(List<Integer> idsOfViewsNotToHide) {
        switchState(LOADING, 0, null, null, null, idsOfViewsNotToHide);
    }

    @Override
    public void showEmpty(int icon,String description) {
        switchState(EMPTY, icon,description, null, null, Collections.<Integer>emptyList());
    }

    @Override
    public void showEmpty(Drawable icon,String description) {
        switchState(EMPTY, icon,  description, null, null, Collections.<Integer>emptyList());
    }

    @Override
    public void showEmpty(int icon, String description, OnClickListener buttonClickListener) {
        switchState(EMPTY, icon,  description, null, buttonClickListener, Collections.<Integer>emptyList());
    }

    @Override
    public void showEmpty(int icon,  String description, List<Integer> idsOfViewsNotToHide) {
        switchState(EMPTY, icon,  description, null, null, idsOfViewsNotToHide);
    }

    @Override
    public void showEmpty(Drawable icon,  String description, List<Integer> idsOfViewsNotToHide) {
        switchState(EMPTY, icon,  description, null, null, idsOfViewsNotToHide);
    }

    @Override
    public void showError(int icon,  String description, String buttonText, OnClickListener buttonClickListener) {
        switchState(ERROR, icon,  description, buttonText, buttonClickListener, Collections.<Integer>emptyList());
    }

    @Override
    public void showError(Drawable icon,  String description, String buttonText, OnClickListener buttonClickListener) {
        switchState(ERROR, icon,  description, buttonText, buttonClickListener, Collections.<Integer>emptyList());
    }

    @Override
    public void showError(int icon,  String description, String buttonText, OnClickListener buttonClickListener, List<Integer> idsOfViewsNotToHide) {
        switchState(ERROR, icon,  description, buttonText, buttonClickListener, idsOfViewsNotToHide);
    }

    @Override
    public void showError(Drawable icon,  String description, String buttonText, OnClickListener buttonClickListener, List<Integer> idsOfViewsNotToHide) {
        switchState(ERROR, icon,  description, buttonText, buttonClickListener, idsOfViewsNotToHide);
    }

    private void switchState(String state, int icon,  String description,
                             String buttonText, OnClickListener buttonClickListener, List<Integer> idsOfViewsNotToHide) {
        this.state = state;

        hideAllStates();

        switch (state) {
            case CONTENT:
                setContentVisibility(true, idsOfViewsNotToHide);
                break;
            case LOADING:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateLoadingView();
                break;
            case EMPTY:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateEmptyView();
                if(icon > 0){
                    emptyStateImageView.setImageResource(icon);
                }else{
                    emptyStateImageView.setImageResource(R.drawable.ic_status_empty);
                }
                if(buttonClickListener != null){
                    status_empty_again_tv.setOnClickListener(buttonClickListener);
                }else{
                    status_empty_again_tv.setVisibility(GONE);
                }
                break;
            case ERROR:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateErrorView();
                if(icon > 0){
                    errorStateImageView.setImageResource(icon);
                }else{
                    errorStateImageView.setImageResource(R.drawable.ic_status_error);
                }
                if(buttonClickListener != null){
                    errorStateButton.setOnClickListener(buttonClickListener);
                }else{
                    errorStateButton.setVisibility(GONE);
                }
                break;
        }
    }

    private void switchState(String state, Drawable icon, String description,
                             String buttonText, OnClickListener buttonClickListener,
                             List<Integer> idsOfViewsNotToHide) {
        this.state = state;

        hideAllStates();

        switch (state) {
            case CONTENT:
                setContentVisibility(true, idsOfViewsNotToHide);
                break;
            case LOADING:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateLoadingView();
                break;
            case EMPTY:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateEmptyView();

                emptyStateImageView.setImageDrawable(icon);
                emptyStateContentTextView.setText(description);
                break;
            case ERROR:
                setContentVisibility(false, idsOfViewsNotToHide);
                inflateErrorView();

                errorStateImageView.setImageDrawable(icon);
//                errorStateTitleTextView.setText(title);
                errorStateContentTextView.setText(description);
                errorStateButton.setText(buttonText);
                errorStateButton.setOnClickListener(buttonClickListener);
                break;
        }
    }

    private void hideAllStates() {
        hideLoadingView();
        hideEmptyView();
        hideErrorView();
        restoreDefaultBackground();
    }

    private void hideLoadingView() {
        if (loadingState != null) {
            loadingState.setVisibility(GONE);
        }
    }

    private void hideEmptyView() {
        if (emptyState != null) {
            emptyState.setVisibility(GONE);
        }
    }

    private void hideErrorView() {
        if (errorState != null) {
            errorState.setVisibility(GONE);
        }
    }

    private void restoreDefaultBackground() {
        this.setBackgroundDrawable(defaultBackground);
    }

    private void setContentVisibility(boolean visible, List<Integer> skipIds) {
        for (View v : contentViews) {
            if (!skipIds.contains(v.getId())) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void inflateLoadingView() {
        if (loadingState == null) {
            view = inflater.inflate(R.layout.status_loading_layout, null);
            loadingState = view.findViewById(R.id.loading_view);
            loadingState.setTag(LOADING);

            loadingStateProgressBar = view.findViewById(R.id.status_loading_pb);
            loadingStateProgressBar.getLayoutParams().width = loadingStateProgressBarWidth;
            loadingStateProgressBar.getLayoutParams().height = loadingStateProgressBarHeight;
            loadingStateProgressBar.getIndeterminateDrawable()
                    .setColorFilter(loadingStateProgressBarColor, PorterDuff.Mode.SRC_IN);
            loadingStateProgressBar.requestLayout();

            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(loadingStateBackgroundColor);
            }

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;

            addView(loadingState, layoutParams);
        } else {
            loadingState.setVisibility(VISIBLE);
        }
    }

    private void inflateEmptyView() {
        if (emptyState == null) {
            view = inflater.inflate(R.layout.status_empty_layout, null);
            emptyState = view.findViewById(R.id.empty_view);
            emptyState.setTag(EMPTY);

            emptyStateImageView = view.findViewById(R.id.status_empty_iv);
            emptyStateContentTextView = view.findViewById(R.id.status_empty_content_tv);

            emptyStateImageView.getLayoutParams().width = emptyStateImageWidth;
            emptyStateImageView.getLayoutParams().height = emptyStateImageHeight;
            emptyStateImageView.requestLayout();

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;

            addView(emptyState, layoutParams);
        } else {
            emptyState.setVisibility(VISIBLE);
        }
    }

    private void inflateErrorView() {
        if (errorState == null) {
            view = inflater.inflate(R.layout.status_error_layout, null);
            errorState = view.findViewById(R.id.error_view);
            errorState.setTag(ERROR);

            errorStateImageView = view.findViewById(R.id.status_error_iv);
            errorStateContentTextView = view.findViewById(R.id.status_error_content_tv);
            errorStateButton = view.findViewById(R.id.status_error_again_tv);

            errorStateImageView.getLayoutParams().width = errorStateImageWidth;
            errorStateImageView.getLayoutParams().height = errorStateImageHeight;
            errorStateImageView.requestLayout();

//            if (errorStateBackgroundColor != Color.TRANSPARENT) {
//                this.setBackgroundColor(errorStateBackgroundColor);
//            }

            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;

            addView(errorState, layoutParams);
        } else {
            errorState.setVisibility(VISIBLE);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child.getTag() == null || (!child.getTag().equals(LOADING) &&
                !child.getTag().equals(EMPTY) && !child.getTag().equals(ERROR))) {

            contentViews.add(child);
        }
    }

    @Override
    public String getCurrentState() {
        return state;
    }

    @Override
    public boolean isContentCurrentState() {
        return state.equals(CONTENT);
    }

    @Override
    public boolean isLoadingCurrentState() {
        return state.equals(LOADING);
    }

    @Override
    public boolean isEmptyCurrentState() {
        return state.equals(EMPTY);
    }

    @Override
    public boolean isErrorCurrentState() {
        return state.equals(ERROR);
    }
}
