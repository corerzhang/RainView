package corer.me.rainview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;


/**
 * Created by corer.zhang on 16/8/23.
 */
public class SurfaceRainItem implements IRainItem {


    int mItemHeight;
    int mItemWidth;
    Bitmap mBitmap;
    Paint mPaint;

    float mStartX;
    float mStartY;
    float mEndY;
    int mDelay;

    int mTimePass;

    float mSpeed;

    float mProgress;

    float mCurrentY;

    float mStartTime;

    float mLength;

    public SurfaceRainItem(Context context, int resId, int startX, int startY, int endY, float speed, int delay) {

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

        mLength = mEndY - mStartY;

        mCurrentY = mStartY;
        mSpeed = speed;
        mDelay = delay;

        mPaint = new Paint();
    }


    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }

        long currentTime = SystemClock.elapsedRealtime();
        if (mStartTime == 0) {
            mStartTime = currentTime;
        }
        float deltaTime = currentTime - mStartTime;

        if (deltaTime <= mDelay) {
            //还没到延迟时长
            return;
        }

        //已经绘制完
        if (mCurrentY >= mEndY) {
            mProgress=1;
            return;
        }

        mCurrentY = mCurrentY + mSpeed;

        mProgress = Math.min(mCurrentY >= 0 ? (mCurrentY) / mLength : 0, 1);
        canvas.drawBitmap(mBitmap, mStartX, mCurrentY, mPaint);

    }

    @Override
    public boolean isOut() {
        return mCurrentY >= mEndY;
    }


    @Override
    public float progress() {
        return mProgress;
    }

    @Override
    public void reset() {
        mProgress = 0;
        mTimePass = 0;
        mStartTime = 0;
        mCurrentY = mStartY;
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
