package corer.me.rainview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


/**
 * Created by corer.zhang on 16/7/11.
 */
public class RainItem implements IRainItem {


    int mItemHeight;
    int mItemWidth;
    Bitmap mBitmap;
    Paint mPaint;

    float mStartX;
    float mStartY;
    float mEndY;
    float mLength;
    int mDelay;
    int mFactory;

    int mTimePass;

    int mMinAlpha = (int) (255 * 0.5);

    float mProgress;

    float mCurrentY;

    public RainItem(Context context, int resId, int startX, int startY, int endY, int delay, int factory) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        options.inSampleSize = calculateInSampleSize(options, 100, 100); // 计算inSampleSize
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resId,options);
        mItemHeight = mBitmap.getHeight();
        mItemWidth = mBitmap.getWidth();
        mStartX = startX;
        mStartY = startY - mItemHeight;
        mEndY = endY + mItemHeight;

        mLength = Math.abs(mEndY - mStartY);

        mDelay = delay;
        mFactory = Math.max(factory, 20);

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


        //如果时间还没到延迟的时长，就忽略
        //不准确的╮(╯▽╰)╭
        mTimePass = mTimePass + 30;
        if (mDelay > mTimePass) {
            return;
        }

        //已经绘制完
        if (mCurrentY>=mEndY){
            mProgress=1;
            return;
        }

        mCurrentY = mCurrentY + mFactory;

        mProgress = Math.min(mCurrentY >= 0 ? (mCurrentY) / mLength : 0, 1);
        mPaint.setAlpha((int) (Math.max(255 * (mProgress < 0.5 ? mProgress : (1 - mProgress)), mMinAlpha)));
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
        mProgress=0;
        mTimePass=0;
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
