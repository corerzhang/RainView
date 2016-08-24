package corer.me.rainview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;


/**
 * Created by corer.zhang on 16/8/23.
 */
public class SurfaceRainView extends SurfaceView implements IRainView ,SurfaceHolder.Callback {

    private static final String TAG=RainView.class.getSimpleName();
    private static  final boolean DEBUG=true;

    RainCallback mCallback;
    IRainController mRainController;

    HandlerThread mHandlerThread;
    Handler mHandler;


    boolean canvasAvailable;

    boolean raining;

    SurfaceHolder mSurfaceHolder;

    float mTime;
    public SurfaceRainView(Context context) {
        this(context, null);
    }

    public SurfaceRainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceRainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(true);
        setZOrderOnTop(true);
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
         mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        mHandlerThread =new HandlerThread("render");
        mHandlerThread.start();


        mHandler=new Handler(mHandlerThread.getLooper()){
            @Override
            public void dispatchMessage(Message msg) {
                if (msg.what!=0){
                    return;
                }
                if (!canvasAvailable){
                    return;
                }
                boolean isOver = false;
                Canvas canvas=mSurfaceHolder.lockCanvas();
                if (canvas!=null){
                    isOver= mRainController.handleOnDraw(canvas,getMeasuredWidth(),getMeasuredHeight());
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }

                if (isOver){
                    mHandler.removeMessages(0);
                    stopRain();
                }else {
                    mHandler.sendEmptyMessageDelayed(0,10);
                }

                if (DEBUG){
                    long time= SystemClock.elapsedRealtime();
                    Log.i(TAG,"SurfaceRainView FPS="+(1000/(time-mTime)));
                    mTime=time;
                }

            }
        };

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
        mTime=SystemClock.elapsedRealtime();
        if (raining) {
            return;
        }

        raining = true;

        //父view只添加一次RainView
        if (getParent() == null) {
            ((ViewGroup) activity.getWindow().getDecorView()).addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (mRainController != null) {
            mRainController.handleLoadItem(((ViewGroup) getParent()));
        }

        callbackStart();
        setVisibility(VISIBLE);

    }

    @Override
    public void stopRain() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mRainController != null) {
                    mRainController.reset();
                }
                callbackEnd();
                setVisibility(INVISIBLE);
                raining = false;
            }
        });


    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        canvasAvailable = true;
        mHandler.sendEmptyMessageDelayed(0,10);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        canvasAvailable = false;
        stopRain();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh == 0 || oldw == 0) {
            return;
        }

        if (raining) {
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
        canvasAvailable = false;
        mHandlerThread.quit();
        clear();   //被移除出当前window后，清空缓存
    }

    protected void clear() {
        if (mRainController != null) {
            mRainController.clear();
        }
    }

    protected void callbackStart() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onRainStart();
                }
            }
        });
    }

    protected void callbackProgress(final float progress) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onRainProgress(progress);
                }
            }
        });
    }

    protected void callbackEnd() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onRainEnd();
                }
            }
        });
    }


}
