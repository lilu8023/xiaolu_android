package com.must.base.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
/**
 * Description:
 *
 * @author lilu
 * @date 2019/10/8
 * I Am The King of This World.
 */
public class DragView extends AppCompatImageButton {

    private int parentHeight;
    private int parentWidth;

    public DragView(Context context) {
        super(context);
    }
    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private int lastX;
    private int lastY;
    private boolean isDrag;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX=rawX;
                lastY=rawY;
                ViewGroup parent;
                if(getParent()!= null){
                    parent= (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(parentHeight <= 0||parentWidth == 0){
                    isDrag = false;
                    break;
                }else {
                    isDrag = true;
                }
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些华为手机无法触发点击事件
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                if(distance == 0){
                    isDrag = false;
                    break;
                }
                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x<0?0:x>parentWidth-getWidth()?parentWidth-getWidth():x;
                y = getY()<0?0:getY()+getHeight()>parentHeight?parentHeight-getHeight():y;
//                setX(x);

//                if(getY() < 0){
//                    y = 0;
//                }else{
//                    if(getY() + getHeight() > parentHeight - UiUtils.dip2px(getContext(),49)){
//                        y = parentHeight - UiUtils.dip2px(getContext(),49);
//                    }
//                }
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                if(!isNotDrag()){
                    //恢复按压效果
                    setPressed(false);
                    //Log.i("getX="+getX()+"；screenWidthHalf="+screenWidthHalf);
                   /* animate().setInterpolator(new DecelerateInterpolator())
                            .setDuration(500)
                            .start();*/
//                    if(rawX>=parentWidth/2){
//                        //靠右吸附
//                        animate().setInterpolator(new DecelerateInterpolator())
//                                .setDuration(500)
//                                .xBy(parentWidth-getWidth()-getX())
//                                .start();
//                    }else {
//                        //靠左吸附
//                        ObjectAnimator oa=ObjectAnimator.ofFloat(this,"x",getX(),0);
//                        oa.setInterpolator(new DecelerateInterpolator());
//                        oa.setDuration(500);
//                        oa.start();
//                    }
                }
                break;
        }
        //如果是拖拽则消s耗事件，否则正常传递即可。
        return !isNotDrag() || super.onTouchEvent(event);
//        return event.getAction() != MotionEvent.ACTION_UP && (isDrag || super.onTouchEvent(event));
    }

    private boolean isNotDrag(){
        return !isDrag&&(getX()==0 ||(getX()==parentWidth-getWidth()));
    }
}
