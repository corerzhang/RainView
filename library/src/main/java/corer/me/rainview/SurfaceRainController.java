package corer.me.rainview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by corer.zhang on 16/8/23.
 */
public class SurfaceRainController implements IRainController {

    private static final float ITEM_COUNT = 200;
    private static final int MAX_DELAY = 5000;
    private static final int MAX_INCREMENT = 20;
    List<IRainItem> mRainItems = new ArrayList<>();
    int[] mDrawablesResId;
    float mProgress;
    boolean isAllOver;


    public SurfaceRainController(int[] drawables) {
        mDrawablesResId = drawables;
    }

    @Override
    public void handleLoadItem(ViewGroup parent) {
        mProgress = 0;
        if (mRainItems != null && mRainItems.size() > 0) {
            return;
        }
        int width = parent.getMeasuredWidth(), height = parent.getMeasuredHeight();

        Random random = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {

            int startX = random.nextInt(width);
            int startY = 0;

            int delay = random.nextInt(MAX_DELAY); //item延迟出现的时长
            int speed = random.nextInt(10) + MAX_INCREMENT; //item每次的y轴偏移量
            int resId = mDrawablesResId[random.nextInt(mDrawablesResId.length)];
            mRainItems.add(new SurfaceRainItem(parent.getContext(), resId, startX, startY, height, speed, delay));
        }
    }

    @Override
    public boolean handleOnDraw(Canvas canvas, int width, int height) {
        //绘制canvas背景颜色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        //item绘制
        canvas.save();
        if (mRainItems != null && mRainItems.size() > 0) {
            for (IRainItem mRainItem : mRainItems) {
                mRainItem.draw(canvas);
            }
        }
        canvas.restore();
        //判断是否终止item的绘制
        boolean isAllOver = true;
        float allProgress = 0;
        if (mRainItems != null && mRainItems.size() > 0) {
            for (IRainItem mRainItem : mRainItems) {
                allProgress = mRainItem.progress() + allProgress;
                boolean isItemOut = mRainItem.isOut();
                if (!isItemOut) {
                    isAllOver = false;
                }
            }
        }
        mProgress = allProgress / ITEM_COUNT;
        this.isAllOver = isAllOver;
        return isAllOver;

    }

    @Override
    public boolean isOver() {
        return isAllOver;
    }

    @Override
    public float progress() {
        return mProgress;
    }

    @Override
    public void reset() {
        if (mRainItems != null && mRainItems.size() > 0) {
            for (IRainItem mRainItem : mRainItems) {
                mRainItem.reset();
            }
        }
        isAllOver = false;
    }

    @Override
    public void clear() {
        if (mRainItems != null && mRainItems.size() > 0) {
            for (IRainItem mRainItem : mRainItems) {
                mRainItem.clear();
            }
            mRainItems.clear();
        }
        isAllOver = false;
    }
}
