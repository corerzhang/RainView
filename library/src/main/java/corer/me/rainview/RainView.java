package corer.me.rainview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by corer.zhang on 16/7/11.
 */
public class RainView extends View implements IRainView {


    private static final String TAG=RainView.class.getSimpleName();
    private static  final boolean DEBUG=true;

    IRainView.RainCallback mCallback;
    IRainController mRainController;

    boolean shouldRender;
    boolean raining;
    boolean stopping;


    public RainView(Context context) {
        this(context, null);
    }

    public RainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setVisibility(INVISIBLE);
    }


    @Override
    public void setRainCallback(RainCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void setRainController(IRainController controller) {
        mRainController = controller;
    }

    @Override
    public void startRain(final Activity activity) {

        if (raining) {
            return;
        }

        raining = true;
        shouldRender = true;
        //父view只添加一次RainView
        if (getParent()==null){
            ((ViewGroup) activity.getWindow().getDecorView()).addView(RainView.this);
        }

        setVisibility(VISIBLE);
        setAlpha(1);
        if (mRainController != null) {
            mRainController.handleLoadItem(((ViewGroup) getParent()));
            post(new Runnable() {
                     @Override
                     public void run() {
                         invalidate();
                     }

                 }
            );
        }

        callbackStart();


    }

    public void stopRain() {
        shouldRender = false;

        if (stopping) {
            return;
        }
        stopping = true;
        if (mRainController != null) {
            mRainController.reset();
        }
        callbackEnd();

        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1, 0.5f).setDuration(500);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
                stopping = false;
                raining = false;
            }
        });
        alpha.start();


    }



    long mTime;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!shouldRender) {
            return;
        }

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width == 0 || height == 0) {
            return;
        }

        //判断是否终止item的绘制
        boolean isAllOver = mRainController != null ? mRainController.handleOnDraw(canvas, width, height) : true;

        if (DEBUG){
            long time= SystemClock.elapsedRealtime();
            Log.i(TAG,"RainView FPS="+(1000/(time-mTime)));
            mTime=time;
        }


        float progress = mRainController != null ? mRainController.progress() : 0;
        callbackProgress(progress);
        //判断是否全部的item都已经绘制完成
        if (!isAllOver) {
            invalidate();
        } else {
            shouldRender = false;
            stopRain();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh==0||oldw==0){
            return;
        }

        if (stopping||raining){
            return;
        }

        clear();//size变化了，先清空缓存，等下次下雨的时候就可以重新计算item路径等
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shouldRender = false;
        clear();   //被移除出当前window后，清空缓存
    }

    protected void clear(){
        if (mRainController != null) {
            mRainController.clear();
        }
    }

    protected void callbackStart() {
        if (mCallback != null) {
            mCallback.onRainStart();
        }
    }

    protected void callbackProgress(float progress) {
        if (mCallback != null) {
            mCallback.onRainProgress(progress);
        }
    }

    protected void callbackEnd() {
        if (mCallback != null) {
            mCallback.onRainEnd();
        }
    }


}
