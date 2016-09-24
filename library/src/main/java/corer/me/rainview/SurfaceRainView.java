package corer.me.rainview;

import android.annotation.SuppressLint;
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
public class SurfaceRainView extends SurfaceView implements IRainView, SurfaceHolder.Callback {

    private static final String TAG = RainView.class.getSimpleName();
    private static final boolean DEBUG = true;

    RainCallback mCallback;
    IRainController mRainController;


    boolean canvasAvailable;
    boolean raining;

    private int mWidth, mHeight;
    long mTime;

    SurfaceHolder mSurfaceHolder;


    private static final int MSG_DRAW = 1;
    HandlerThread mHandlerThread;
    Handler mHandler;
    private DrawTask mDrawTask;
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            switch (message.what) {
                case MSG_DRAW:
                    if (!canvasAvailable) {
                        mHandler.removeMessages(MSG_DRAW);
                        return true;
                    }

                    mHandler.removeMessages(MSG_DRAW);
                    mHandler.post(mDrawTask);

                    if (!mRainController.isOver()) {
                        mHandler.sendEmptyMessageDelayed(MSG_DRAW, 10);
                    } else {
                        stopRain();
                    }
                    break;
            }

            return true;
        }
    };


    public SurfaceRainView(Context context) {
        this(context, null);
    }

    public SurfaceRainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceRainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setZOrderOnTop(true);
        setVisibility(INVISIBLE);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mHandlerThread = new HandlerThread("SurfaceRainViewRender");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), mHandlerCallback);

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
        mTime = SystemClock.uptimeMillis();
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

        post(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_DRAW);
            }
        });

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

    private class DrawTask implements Runnable {
        private SurfaceHolder mHolder;

        public DrawTask(SurfaceHolder holder) {
            mHolder = holder;
        }

        @SuppressLint("WrongCall")
        @Override
        public void run() {
            Canvas canvas = null;
            try {
                canvas = mHolder.lockCanvas();
                if (canvas == null) {
                    mHandler.removeMessages(MSG_DRAW);
                    return;
                }
                synchronized (mHolder) {
                    if (!canvasAvailable) {
                        mHandler.removeMessages(MSG_DRAW);
                        stopRain();
                        return;
                    }
                    logFPS();
                    mRainController.handleOnDraw(canvas, mWidth, mHeight);
                    callbackProgress(mRainController.progress());
                }
            } finally {
                if (canvas != null) {
                    try {
                        mHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    private void logFPS(){
        if (DEBUG) {
            long time = SystemClock.uptimeMillis();
            long deltaTime = time - mTime;
            Log.i(TAG, "SurfaceRainView deltaTime=" + deltaTime);
            Log.i(TAG, "SurfaceRainView FPS=" + (1000 / (deltaTime)));
            mTime = time;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        canvasAvailable = true;
        if (mDrawTask == null) {
            mDrawTask = new DrawTask(mSurfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        canvasAvailable = false;
        if (raining) {
            stopRain();
        }

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
