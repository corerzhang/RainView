package corer.me.rainview;


import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;


/**
 * Created by corer.zhang on 16/8/23.
 */
public class SurfaceRainItem implements IRainItem {

    FloatEvaluator mEvaluator;

    int mItemHeight;
    int mItemWidth;
    Bitmap mBitmap;
    Paint mPaint;

    float mStartX;
    float mStartY;
    float mEndY;
    int mDelay;

    int mTimePass;

    float mDuring;

    int mMinAlpha = (int) (255 * 0.5);

    float mProgress;

    float mCurrentY;

    float mStartTime;

    public SurfaceRainItem(Context context, int resId, int startX, int startY, int endY,float during, int delay) {

        mEvaluator=new FloatEvaluator();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options, 60, 60); // 计算inSampleSize
        options.inJustDecodeBounds = false;

        mBitmap = BitmapFactory.decodeResource(context.getResources(), resId,options);
        mItemHeight = mBitmap.getHeight();
        mItemWidth = mBitmap.getWidth();
        mStartX = startX;
        mStartY = startY - mItemHeight;
        mEndY = endY + mItemHeight;


        mDuring=during;
        mDelay = delay;

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(mMinAlpha);
    }


    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }

        long currentTime=SystemClock.elapsedRealtime();
        if (mStartTime==0){
            mStartTime=currentTime;
        }
        float deltaTime=currentTime-mStartTime;

        if (deltaTime<=mDelay){
            //还没到延迟时长
           return;
        }

        float fraction=Math.min(((deltaTime-mDelay)/mDuring),1f);

        mProgress = fraction;
        mCurrentY = mEvaluator.evaluate(fraction,mStartY,mEndY);


        //已经绘制完
        if (isOut()){
            return;
        }

        mPaint.setAlpha((int) (Math.max(255 * (mProgress < 0.5 ? mProgress : (1 - mProgress)), mMinAlpha)));
        canvas.drawBitmap(mBitmap, mStartX, mCurrentY, mPaint);

    }

    @Override
    public boolean isOut() {
        return mProgress>=1f;
    }


    @Override
    public float progress() {
        return mProgress;
    }

    @Override
    public void reset() {
        mProgress=0;
        mTimePass=0;
        mStartTime=0;
        mCurrentY=mStartY;
    }

    @Override
    public void clear() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}