package com.must.base.widget.basepopup;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.must.base.utils.InputMethodUtils;
import com.must.base.utils.PopupUiUtils;
import com.must.base.utils.PopupUtils;
import com.must.base.utils.log.PopupLog;


/**
 * Created by 大灯泡 on 2017/12/25.
 * <p>
 * 旨在用来拦截keyevent、以及蒙层
 */
final class PopupDecorViewProxy extends ViewGroup implements InputMethodUtils.OnKeyboardChangeListener {
    private static final String TAG = "PopupDecorViewProxy";
    //模糊层
    private PopupMaskLayout mMaskLayout;
    private BasePopupHelper mHelper;
    private View mTarget;
    private Rect mTouchRect = new Rect();

    private int childLeftMargin;
    private int childTopMargin;
    private int childRightMargin;
    private int childBottomMargin;

    private CheckAndCallAutoAnchorLocate mCheckAndCallAutoAnchorLocate;
    private WindowManagerProxy mWindowManagerProxy;
    private int[] location = new int[2];
    private int originY;
    private Flag mFlag = new Flag();
    private Rect lastKeyboardBounds = new Rect();

    private PopupDecorViewProxy(Context context) {
        this(context, null);
    }

    private PopupDecorViewProxy(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private PopupDecorViewProxy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static PopupDecorViewProxy create(Context context, WindowManagerProxy windowManagerProxy, BasePopupHelper helper) {
        PopupDecorViewProxy result = new PopupDecorViewProxy(context);
        result.init(helper, windowManagerProxy);
        return result;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(BasePopupHelper helper, WindowManagerProxy managerProxy) {
        mWindowManagerProxy = managerProxy;
        mHelper = helper;
        mHelper.mKeyboardStateChangeListener = this;
        setClipChildren(mHelper.isClipChildren());
        mMaskLayout = PopupMaskLayout.create(getContext(), mHelper);
        mFlag.flag = Flag.IDLE;
        if (!mHelper.isOutSideTouchable()) {
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            addViewInLayout(mMaskLayout, -1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mMaskLayout.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return mHelper.isOutSideDismiss();
                        case MotionEvent.ACTION_UP:
                            if (mHelper.isOutSideDismiss()) {
                                int x = (int) event.getX();
                                int y = (int) event.getY();
                                if (mTarget != null) {
                                    View contentView = mTarget.findViewById(mHelper.getContentRootId());
                                    if (contentView == null) {
                                        mTarget.getGlobalVisibleRect(mTouchRect);
                                    } else {
                                        contentView.getGlobalVisibleRect(mTouchRect);
                                    }
                                }
                                if (!mTouchRect.contains(x, y)) {
                                    mHelper.onOutSideTouch();
                                }
                            }
                            break;
                    }
                    return false;
                }
            });
        } else {
            setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            Activity act = PopupUtils.getActivity(getContext());
            if (act == null) return;
            clearDecorMaskLayout(act);
            addMaskToDecor(act.getWindow());
        }
    }

    private void clearDecorMaskLayout(Activity act) {
        if (act == null || act.getWindow() == null) {
            return;
        }
        View decorView = act.getWindow().getDecorView();
        if (decorView instanceof ViewGroup) {
            ViewGroup vg = ((ViewGroup) decorView);
            int childCount = vg.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                View child = vg.getChildAt(i);
                if (child instanceof PopupMaskLayout) {
                    vg.removeViewInLayout(child);
                }
            }
        }
    }

    private void addMaskToDecor(Window window) {
        View decorView = window == null ? null : window.getDecorView();
        if (!(decorView instanceof ViewGroup)) {
            if (mMaskLayout != null) {
                mMaskLayout.onDetachedFromWindow();
                mMaskLayout = null;
            }
            return;
        }
        ((ViewGroup) decorView).addView(mMaskLayout,
                mHelper.maskWidth > 0 ? mHelper.maskWidth : LayoutParams.MATCH_PARENT,
                mHelper.maskHeight > 0 ? mHelper.maskHeight : LayoutParams.MATCH_PARENT);
    }

    public void addPopupDecorView(View target, WindowManager.LayoutParams params) {
        if (target == null) {
            throw new NullPointerException("contentView不能为空");
        }
        if (getChildCount() == 2) {
            removeViewsInLayout(1, 1);
        }
        mTarget = target;
        WindowManager.LayoutParams wp = new WindowManager.LayoutParams();
        wp.copyFrom(params);
        wp.x = 0;
        wp.y = 0;

        if (mHelper.getParaseFromXmlParams() == null) {
            View contentView = findContentView(target);

            if (contentView != null) {
                if (!mHelper.isCustomMeasure()) {
                    if (wp.width <= 0) {
                        wp.width = contentView.getMeasuredWidth() <= 0 ? mHelper.getPopupViewWidth() : contentView.getMeasuredWidth();
                    }
                    if (wp.height <= 0) {
                        wp.height = contentView.getMeasuredHeight() <= 0 ? mHelper.getPopupViewHeight() : contentView.getMeasuredHeight();
                    }
                } else {
                    wp.width = mHelper.getPopupViewWidth();
                    wp.height = mHelper.getPopupViewHeight();
                }
            }
        } else {
            if (target instanceof ViewGroup) {
                View child = ((ViewGroup) target).getChildAt(0);
                if (child != null) {
                    child.setLayoutParams(new FrameLayout.LayoutParams(mHelper.getParaseFromXmlParams()));
                }
            }


            wp.width = mHelper.getPopupViewWidth();
            wp.height = mHelper.getPopupViewHeight();
            childLeftMargin = mHelper.getParaseFromXmlParams().leftMargin;
            childTopMargin = mHelper.getParaseFromXmlParams().topMargin;
            childRightMargin = mHelper.getParaseFromXmlParams().rightMargin;
            childBottomMargin = mHelper.getParaseFromXmlParams().bottomMargin;
        }

        addView(target, wp);
    }

    private View findContentView(View root) {
        if (root == null) return null;
        if (!(root instanceof ViewGroup)) return root;
        ViewGroup rootGroup = (ViewGroup) root;
        if (rootGroup.getChildCount() <= 0) return root;
        String viewSimpleClassName = rootGroup.getClass().getSimpleName();
        View result = null;
        while (!isContentView(viewSimpleClassName)) {
            result = rootGroup.getChildAt(0);
            viewSimpleClassName = result.getClass().getSimpleName();
            if (result instanceof ViewGroup) {
                rootGroup = ((ViewGroup) result);
            } else {
                break;
            }
        }
        return result;
    }

    private boolean isContentView(String contentClassName) {
        return !TextUtils.equals(contentClassName, "PopupDecorView") &&
                !TextUtils.equals(contentClassName, "PopupViewContainer") &&
                !TextUtils.equals(contentClassName, "PopupBackgroundView");

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mFlag.flag &= ~Flag.FLAG_REST_WIDTH_NOT_ENOUGH;
        mFlag.flag &= ~Flag.FLAG_REST_HEIGHT_NOT_ENOUGH;
        PopupLog.i("onMeasure", MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if (!mHelper.isOutSideTouchable()) {
            measureNormal(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureOutSide(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureNormal(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //蒙层给最大值
            if (child == mMaskLayout) {
                measureChild(child, getMaskWidthSpec(), getMaskHeightSpec());
            } else {
                measureWrappedDecorView(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        setMeasuredDimension(getAvaliableWidth(), getAvaliableHeight());
    }

    private void measureOutSide(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = 0;
        int maxHeight = 0;
        int childState = 0;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == mTarget) {
                measureWrappedDecorView(mTarget, widthMeasureSpec, heightMeasureSpec);
            } else {
                if (child == mMaskLayout) {
                    measureChild(child, getMaskWidthSpec(), getMaskHeightSpec());
                } else {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
            }

            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState), resolveSizeAndState(maxHeight, heightMeasureSpec,
                childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    private void measureWrappedDecorView(View mTarget, int widthMeasureSpec, int heightMeasureSpec) {
        if (mTarget == null || mTarget.getVisibility() == GONE) return;
        final LayoutParams lp = mTarget.getLayoutParams();

        widthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width);
        heightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height);


        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int gravity = mHelper.getPopupGravity();
        boolean isAlignAnchorMode = mHelper.getGravityMode() == BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE;


        if (mHelper.getMinWidth() > 0 && heightSize < mHelper.getMinWidth()) {
            widthSize = mHelper.getMinWidth();
        }

        if (mHelper.getMaxWidth() > 0 && widthSize > mHelper.getMaxWidth()) {
            widthSize = mHelper.getMaxWidth();
        }

        if (mHelper.getMinHeight() > 0 && heightSize < mHelper.getMinHeight()) {
            heightSize = mHelper.getMinHeight();
        }

        if (mHelper.getMaxHeight() > 0 && heightSize > mHelper.getMaxHeight()) {
            heightSize = mHelper.getMaxHeight();
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);

        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getMaskWidthSpec() {
        return mHelper.maskWidth > 0 ? MeasureSpec.makeMeasureSpec(mHelper.maskWidth, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(getAvaliableWidth(), MeasureSpec.EXACTLY);
    }

    private int getMaskHeightSpec() {
        return mHelper.maskHeight > 0 ? MeasureSpec.makeMeasureSpec(mHelper.maskHeight, MeasureSpec.EXACTLY) : MeasureSpec.makeMeasureSpec(getAvaliableHeight(), MeasureSpec.EXACTLY);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        PopupLog.i("onLayout", changed, l, t, r, b);
        if (!mHelper.isOutSideTouchable()) {
            layoutNormal(l, t, r, b);
        } else {
            layoutOutSide(l, t, r, b);
        }
    }

    private void layoutOutSide(int l, int t, int r, int b) {
        if ((mFlag.flag & Flag.FLAG_WINDOW_PARAMS_FIT_REQUEST) != 0 && getLayoutParams() instanceof WindowManager.LayoutParams) {
            mWindowManagerProxy.updateViewLayout(this, getLayoutParams());
        }
        final int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.layout(l, t, r, b);
                if (child == mTarget
                        && mMaskLayout != null
                        && mHelper.isAlignBackground()
                        && mHelper.getAlignBackgroundGravity() != Gravity.NO_GRAVITY) {
                    if (getLayoutParams() instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams p = ((WindowManager.LayoutParams) getLayoutParams());
                        l += p.x;
                        t += p.y;
                        r += p.x;
                        b += p.y;
                    }
                    mMaskLayout.handleAlignBackground(mHelper.getAlignBackgroundGravity(), l, t, r, b);
                }
            }
        }
    }

    @SuppressLint("RtlHardcoded")
    private void layoutNormal(int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            int gravity = mHelper.getPopupGravity();

            int childLeft = l;
            int childTop = t;

            int offsetX = mHelper.getOffsetX();
            int offsetY = mHelper.getOffsetY();

            boolean delayLayoutMask = mHelper.isAlignBackground() && mHelper.getAlignBackgroundGravity() != Gravity.NO_GRAVITY;

//            keepClipScreenTop ? childTop : (mHelper.isFullScreen() ? 0 : getStatusBarHeight());

            //最终popup显示的可用区域，因为可能有autolocated或者clipscreen导致反向移位，移位后的区域不可超出该区域边界
            int windowLeft = 0;
            int windowTop = 0;
            int windowRight = getMeasuredWidth();
            int windowBottom = getMeasuredHeight();

            if (child == mMaskLayout) {
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            } else {
                boolean isRelativeToAnchor = mHelper.isShowAsDropDown();
                boolean isAlignAnchorMode = mHelper.getGravityMode() == BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE;
                int anchorCenterX = mHelper.getAnchorX() + (mHelper.getAnchorWidth() >> 1);
                int anchorCenterY = mHelper.getAnchorY() + (mHelper.getAnchorHeight() >> 1);

                //不跟anchorView联系的情况下，gravity意味着在整个view中的方位
                //如果跟anchorView联系，gravity意味着以anchorView为中心的方位
                switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.LEFT:
                        if (isRelativeToAnchor) {
                            childLeft = isAlignAnchorMode ? mHelper.getAnchorX() : mHelper.getAnchorX() - width;
                            // windowLeft = isAlignAnchorMode ? mHelper.getAnchorX() : 0;
                            // windowRight = isAlignAnchorMode ? getMeasuredWidth() : mHelper.getAnchorX();
                        }
                        break;
                    case Gravity.RIGHT:
                        if (isRelativeToAnchor) {
                            childLeft = isAlignAnchorMode ? mHelper.getAnchorX() + mHelper.getAnchorWidth() - width : mHelper.getAnchorX() + mHelper.getAnchorWidth();
                            // windowLeft = isAlignAnchorMode ? 0 : mHelper.getAnchorX() + mHelper.getAnchorWidth();
                            // windowRight = isAlignAnchorMode ? mHelper.getAnchorX() + mHelper.getAnchorWidth() : 0;
                        } else {
                            childLeft = getMeasuredWidth() - width;
                        }
                        break;
                    case Gravity.CENTER_HORIZONTAL:
                        if (isRelativeToAnchor) {
                            childLeft = mHelper.getAnchorX();
                            offsetX += anchorCenterX - (childLeft + (width >> 1));
                            // windowLeft = 0;
                            //windowRight = getMeasuredWidth();
                        } else {
                            childLeft = ((r - l - width) >> 1);
                        }
                        break;
                    default:
                        if (isRelativeToAnchor) {
                            childLeft = mHelper.getAnchorX();
                            //windowLeft = 0;
                            //windowRight = getMeasuredWidth();
                        }
                        break;
                }
                //目前对水平方向不需要限制边界
                windowLeft = 0;
                windowRight = getMeasuredWidth();
                childLeft = childLeft + childLeftMargin - childRightMargin;

                switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                    case Gravity.TOP:
                        if (isRelativeToAnchor) {
                            childTop = isAlignAnchorMode ? mHelper.getAnchorY() : mHelper.getAnchorY() - height;
                            windowTop = isAlignAnchorMode ? mHelper.getAnchorY() : 0;
                            windowBottom = isAlignAnchorMode ? getMeasuredHeight() : mHelper.getAnchorY();
                        }
                        break;
                    case Gravity.BOTTOM:
                        if (isRelativeToAnchor) {
                            childTop = isAlignAnchorMode ? mHelper.getAnchorY() + mHelper.getAnchorHeight() - height : mHelper.getAnchorY() + mHelper.getAnchorHeight();
                            windowTop = isAlignAnchorMode ? 0 : mHelper.getAnchorY() + mHelper.getAnchorHeight();
                            windowBottom = isAlignAnchorMode ? mHelper.getAnchorY() + mHelper.getAnchorHeight() : getMeasuredHeight();
                        } else {
                            childTop = b - t - height;
                        }
                        break;
                    case Gravity.CENTER_VERTICAL:
                        if (isRelativeToAnchor) {
                            childTop = mHelper.getAnchorY() + mHelper.getAnchorHeight();
                            offsetY += anchorCenterY - (childTop + (height >> 1));
                            windowTop = 0;
                            windowBottom = getMeasuredHeight();
                        } else {
                            childTop = ((b - t - height) >> 1);
                        }
                        break;
                    default:
                        if (isRelativeToAnchor) {
                            windowTop = 0;
                            windowBottom = getMeasuredHeight();
                            childTop = mHelper.getAnchorY() + mHelper.getAnchorHeight();
                        }
                        break;
                }
                childTop = childTop + childTopMargin - childBottomMargin;

                if (mHelper.isAutoLocatePopup() && mHelper.isClipToScreen()) {
                    int tBottom = childTop + offsetY + (mHelper.isFullScreen() ? 0 : -PopupUiUtils.getStatusBarHeight());
                    int restHeight;
                    switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                        case Gravity.TOP:
                            restHeight = isAlignAnchorMode ?
                                    getMeasuredHeight() - mHelper.getAnchorY() :
                                    mHelper.getAnchorY();
                            if (height > restHeight) {
                                //需要移位
                                offsetY += isAlignAnchorMode ? 0 : mHelper.getAnchorY() + mHelper.getAnchorHeight() - childTop;
                                //如果自动定位到下方，则可显示的window区域为[anchor底部，屏幕底部]
                                windowTop = mHelper.getAnchorY() + mHelper.getAnchorHeight();
                                postAnchorLocation(false);
                            }
                            break;
                        case Gravity.BOTTOM:
                        default:
                            restHeight = isAlignAnchorMode ?
                                    mHelper.getAnchorY() + mHelper.getAnchorHeight() :
                                    getMeasuredHeight() - (mHelper.getAnchorY() + mHelper.getAnchorHeight());

                            if (height > restHeight) {
                                //需要移位
                                offsetY -= isAlignAnchorMode ? 0 : tBottom - mHelper.getAnchorY();
                                //如果是自动定位到上方，则可显示的window区域为[0,anchor顶部]
                                windowTop = 0;
                                windowBottom = mHelper.getAnchorY();
                                postAnchorLocation(true);
                            }
                            break;
                    }
                } else {
                    //不需要autoLocate的情况下，只需要限定在屏幕内即可
                    windowLeft = 0;
                    windowTop = 0;
                    windowRight = getMeasuredWidth();
                    windowBottom = getMeasuredHeight();
                }

                int left = childLeft + offsetX;
                int top = childTop + offsetY + (mHelper.isFullScreen() ? 0 : -PopupUiUtils.getStatusBarHeight());
                int right = left + width;
                int bottom = top + height;


                boolean isOverFlowScreen = left < 0 || top < 0 || right > getMeasuredWidth() || bottom > getMeasuredHeight();

                if (mHelper.isClipToScreen() && isOverFlowScreen) {
                    int tOffset = 0;

                    if (left < windowLeft) {
                        tOffset = windowLeft - left;
                        if (tOffset <= windowRight - right) {
                            left += tOffset;
                            right = left + width;
                        } else {
                            left = windowLeft;
                        }
                    }

                    if (right > windowRight) {
                        tOffset = right - windowRight;
                        if (tOffset <= left) {
                            //只需要移动left即可
                            left -= tOffset;
                            right = left + width;
                        } else {
                            right = windowRight;
                        }
                    }

                    if (top < windowTop) {
                        tOffset = windowTop - top;
                        if (tOffset <= windowBottom - bottom) {
                            top += tOffset;
                            bottom = top + height;
                        } else {
                            top = windowTop;
                        }
                    }

                    if (bottom > windowBottom) {
                        tOffset = bottom - windowBottom;
                        if (windowTop == 0) {
                            //如果screenTop没有限制，则可以上移，否则只能移到限定的screenTop下
                            top -= tOffset;
                            bottom = top + height;
                        } else {
                            bottom = top + height;
                        }
                    }
                }
                child.layout(left, top, right, bottom);
                if (delayLayoutMask) {
                    mMaskLayout.handleAlignBackground(mHelper.getAlignBackgroundGravity(), left, top, right, bottom);
                }
            }

        }
    }


    public void fitWindowParams(WindowManager.LayoutParams params) {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            mFlag.flag |= Flag.FLAG_WINDOW_PARAMS_FIT_REQUEST;
            return;
        }
        int offsetX = 0;
        int offsetY = 0;
        int gravity = mHelper.getPopupGravity();
        boolean isAlignAnchorMode = mHelper.getGravityMode() == BasePopupWindow.GravityMode.ALIGN_TO_ANCHOR_SIDE;

        // offset需要自行计算，不采用系统方法了
        switch (mHelper.getPopupGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                if (mHelper.isShowAsDropDown()) {
                    offsetX += isAlignAnchorMode ? mHelper.getAnchorX() : mHelper.getAnchorX() - getMeasuredWidth();
                }
                break;
            case Gravity.RIGHT:
                if (mHelper.isShowAsDropDown()) {
                    offsetX += isAlignAnchorMode ? mHelper.getAnchorX() + mHelper.getAnchorWidth() - getMeasuredWidth() : mHelper.getAnchorX() + mHelper.getAnchorWidth();
                } else {
                    offsetX += getAvaliableWidth() - getMeasuredWidth();
                }
                break;
            case Gravity.CENTER_HORIZONTAL:
                offsetX += mHelper.isShowAsDropDown() ? mHelper.getAnchorX() + ((mHelper.getAnchorWidth() - getMeasuredWidth()) >> 1)
                        : (getAvaliableWidth() - getMeasuredWidth()) >> 1;
                break;
            default:
                break;
        }
        offsetX = offsetX + childLeftMargin - childRightMargin;

        switch (mHelper.getPopupGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                if (mHelper.isShowAsDropDown()) {
                    offsetY += isAlignAnchorMode ? mHelper.getAnchorY() : mHelper.getAnchorY() - getMeasuredHeight();
                }
                break;
            case Gravity.BOTTOM:
                if (mHelper.isShowAsDropDown()) {
                    offsetY += isAlignAnchorMode ? mHelper.getAnchorY() + mHelper.getAnchorHeight() - getMeasuredHeight() : mHelper.getAnchorY() + mHelper.getAnchorHeight();
                } else {
                    offsetY += getAvaliableHeight() - getMeasuredHeight();
                }
                break;
            case Gravity.CENTER_VERTICAL:
                offsetY += mHelper.isShowAsDropDown() ? mHelper.getAnchorY() + ((mHelper.getAnchorHeight() - getMeasuredHeight()) >> 1) : (getAvaliableHeight() - getMeasuredHeight()) >> 1;
                break;
            default:
                break;
        }
        offsetY = offsetY + childTopMargin - childBottomMargin;

        PopupLog.i("fitWindowParams  ::  " +
                "{\n\t\tscreenWidth = " + getAvaliableWidth() +
                "\n\t\tscreenHeight = " + getAvaliableHeight() +
                "\n\t\tanchorX = " + mHelper.getAnchorX() +
                "\n\t\tanchorY = " + mHelper.getAnchorY() +
                "\n\t\tviewWidth = " + getMeasuredWidth() +
                "\n\t\tviewHeight = " + getMeasuredHeight() +
                "\n\t\toffsetX = " + offsetX +
                "\n\t\toffsetY = " + offsetY +
                "\n}");

        if (mHelper.isAutoLocatePopup() && mHelper.isClipToScreen()) {
            int tBottom = offsetY + (mHelper.isFullScreen() ? 0 : -PopupUiUtils.getStatusBarHeight());
            int restHeight;
            switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                case Gravity.TOP:
                    restHeight = isAlignAnchorMode ?
                            getAvaliableHeight() - mHelper.getAnchorY() :
                            mHelper.getAnchorY();
                    if (getMeasuredHeight() > restHeight) {
                        //需要移位
                        offsetY += isAlignAnchorMode ? 0 : mHelper.getAnchorY() + mHelper.getMinHeight() - offsetY;
                        postAnchorLocation(false);
                    }
                    break;
                case Gravity.BOTTOM:
                default:
                    restHeight = isAlignAnchorMode ?
                            mHelper.getAnchorY() + mHelper.getAnchorHeight() :
                            getAvaliableHeight() - (mHelper.getAnchorY() + mHelper.getAnchorHeight());

                    if (getMeasuredHeight() > restHeight) {
                        //需要移位
                        offsetY -= isAlignAnchorMode ? 0 : tBottom - mHelper.getAnchorY();
                        postAnchorLocation(true);
                    }
                    break;
            }
        }

        params.x = offsetX + mHelper.getOffsetX();
        params.y = offsetY + mHelper.getOffsetY();
        originY = params.y;
        mFlag.flag &= ~Flag.FLAG_WINDOW_PARAMS_FIT_REQUEST;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mMaskLayout != null) {
            mMaskLayout.handleStart(-2);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mHelper != null) {
            return mHelper.onInterceptTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean intercept = mHelper != null && mHelper.onDispatchKeyEvent(event);
        if (intercept) return true;
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (getKeyDispatcherState() == null) {
                return super.dispatchKeyEvent(event);
            }

            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                final KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                final KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null && state.isTracking(event) && !event.isCanceled()) {
                    if (mHelper != null) {
                        PopupLog.i(TAG, "dispatchKeyEvent: >>> onBackPressed");
                        return mHelper.onBackPressed();
                    }
                }
            }
            return super.dispatchKeyEvent(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHelper != null) {
            if (mHelper.onTouchEvent(event)) {
                return true;
            }
        }
        final int x = (int) event.getX();
        final int y = (int) event.getY();

        if ((event.getAction() == MotionEvent.ACTION_DOWN)
                && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
            if (mHelper != null) {
                PopupLog.i(TAG, "onTouchEvent:[ACTION_DOWN] >>> onOutSideTouch");
                return mHelper.onOutSideTouch();
            }
        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (mHelper != null) {
                PopupLog.i(TAG, "onTouchEvent:[ACTION_OUTSIDE] >>> onOutSideTouch");
                return mHelper.onOutSideTouch();
            }
        }
        return super.onTouchEvent(event);
    }

    int getAvaliableWidth() {
        int result = PopupUiUtils.getScreenWidthCompat();
        if (PopupUiUtils.hasNavigationBar(mHelper.popupWindow.getContext()) &&
                PopupUiUtils.getScreenRotation() == Surface.ROTATION_90) {
            result -= PopupUiUtils.getNavigationBarHeight();
        }
        PopupLog.i(result);
        return result;
    }

    int getAvaliableHeight() {
        int result = PopupUiUtils.getScreenHeightCompat();
        if (PopupUiUtils.hasNavigationBar(mHelper.popupWindow.getContext()) &&
                PopupUiUtils.getScreenRotation() == Surface.ROTATION_0) {
            result -= PopupUiUtils.getNavigationBarHeight();
        }
        PopupLog.i(result);
        return result;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHelper.isOutSideTouchable()) {
            if (mMaskLayout != null && mMaskLayout.getParent() != null) {
                ((ViewGroup) mMaskLayout.getParent()).removeViewInLayout(mMaskLayout);
            }
        }
        mHelper.mKeyboardStateChangeListener = null;
        if (mCheckAndCallAutoAnchorLocate != null) {
            removeCallbacks(mCheckAndCallAutoAnchorLocate);
            mCheckAndCallAutoAnchorLocate = null;
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }


    public void updateLayout() {
        if (mHelper != null) {
            mHelper.onUpdate();
        }
        if (mMaskLayout != null) {
            mMaskLayout.update();
        }
        if (isLayoutRequested()) {
            requestLayout();
        }
    }

    private void removeAnchorLocationChecker() {
        if (mCheckAndCallAutoAnchorLocate != null) {
            removeCallbacks(mCheckAndCallAutoAnchorLocate);
        }
    }

    private void postAnchorLocation(boolean onTop) {
        if (mCheckAndCallAutoAnchorLocate == null) {
            mCheckAndCallAutoAnchorLocate = new CheckAndCallAutoAnchorLocate(onTop);
        } else {
            removeAnchorLocationChecker();
        }
        mCheckAndCallAutoAnchorLocate.onTop = onTop;
        postDelayed(mCheckAndCallAutoAnchorLocate, 32);
    }

    private final class CheckAndCallAutoAnchorLocate implements Runnable {
        private boolean onTop;
        private boolean hasCalled;

        CheckAndCallAutoAnchorLocate(boolean onTop) {
            this.onTop = onTop;
        }

        @Override
        public void run() {
            if (mHelper == null || hasCalled) return;
            if (onTop) {
                mHelper.onAnchorTop();
            } else {
                mHelper.onAnchorBottom();
            }
            hasCalled = true;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View v = this;
        if (getContext() instanceof Activity) {
            v = ((Activity) getContext()).getWindow().getDecorView();
        }
        v.post(new Runnable() {
            @Override
            public void run() {
                updateLayout();
            }
        });
    }

    //-----------------------------------------keyboard-----------------------------------------

    @Override
    public void onKeyboardChange(Rect keyboardBounds, boolean isVisible) {
        int offset = 0;
        boolean forceAdjust = (mHelper.flag & BasePopupFlag.KEYBOARD_FORCE_ADJUST) != 0;
        boolean process = forceAdjust || ((PopupUiUtils.getScreenOrientation() != Configuration.ORIENTATION_LANDSCAPE)
                && (mHelper.getSoftInputMode() == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN ||
                mHelper.getSoftInputMode() == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));

        if (!process) return;
        View anchor = null;

        if ((mHelper.flag & BasePopupFlag.KEYBOARD_ALIGN_TO_VIEW) != 0) {
            if (mHelper.keybaordAlignViewId != 0) {
                anchor = mTarget.findViewById(mHelper.keybaordAlignViewId);
            }
        }

        if ((mHelper.flag & BasePopupFlag.KEYBOARD_ALIGN_TO_ROOT) != 0 || anchor == null) {
            anchor = mTarget;
        }

        boolean animate = (mHelper.flag & BasePopupFlag.KEYBOARD_ANIMATE_ALIGN) != 0;

        anchor.getLocationOnScreen(location);
        int bottom = location[1] + anchor.getHeight();

        if (isVisible && keyboardBounds.height() > 0) {
            offset = keyboardBounds.top - bottom;
            if (bottom <= keyboardBounds.top
                    && (mHelper.flag & BasePopupFlag.KEYBOARD_IGNORE_OVER_KEYBOARD) != 0
                    && lastKeyboardBounds.isEmpty()) {
                offset = 0;
            }

        }

        if (mHelper.isOutSideTouchable()) {
            if ((mFlag.flag & Flag.FLAG_WINDOW_PARAMS_FIT_REQUEST) != 0) return;
            //移动window
            final WindowManager.LayoutParams p = PopupUtils.cast(getLayoutParams(), WindowManager.LayoutParams.class);
            if (p != null) {
                if (animate) {
                    animateTranslateWithOutside(p, isVisible, offset);
                } else {
                    p.y = isVisible ? p.y - offset : originY;
                    mWindowManagerProxy.updateViewLayoutOriginal(this, p);
                }
            }
        } else {
            if (animate) {
                animateTranslate(mTarget, isVisible, offset);
            } else {
                mTarget.setTranslationY(isVisible ? mTarget.getTranslationY() + offset : 0);
            }
        }
        if (isVisible) {
            lastKeyboardBounds.set(keyboardBounds);
        } else {
            lastKeyboardBounds.setEmpty();
        }
    }


    ValueAnimator valueAnimator;

    private void animateTranslateWithOutside(final WindowManager.LayoutParams p, boolean isVisible, int offset) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (isVisible) {
            valueAnimator = ValueAnimator.ofInt(p.y, p.y + offset);
            valueAnimator.setDuration(300);
        } else {
            valueAnimator = ValueAnimator.ofInt(p.y, originY);
            valueAnimator.setDuration(200);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                p.y = (int) animation.getAnimatedValue();
                mWindowManagerProxy.updateViewLayoutOriginal(PopupDecorViewProxy.this, p);
            }
        });
        valueAnimator.start();
    }

    private void animateTranslate(View target, boolean isVisible, int offset) {
        target.animate().cancel();
        if (isVisible) {
            target.animate().translationYBy(offset).setDuration(300).start();
        } else {
            target.animate().translationY(0).setDuration(200).start();
        }
    }

    static class Flag {
        static final int IDLE = 0;
        static final int FLAG_REST_WIDTH_NOT_ENOUGH = 0x00000001;
        static final int FLAG_REST_HEIGHT_NOT_ENOUGH = 0x00000010;
        static final int FLAG_WINDOW_PARAMS_FIT_REQUEST = 0x00000100;

        int flag;

    }
}
