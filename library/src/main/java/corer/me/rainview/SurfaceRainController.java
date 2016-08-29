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

    private static final float ITEM_COUNT = 100;
    private static final int MAX_DELAY=5000;
    private static final int MAX_INCREMENTY=30;
    List<IRainItem> mRainItems = new ArrayList<>();
    int[] mDrawablesResId;
    float mProgress;


    public SurfaceRainController(int[] drawables) {
        mDrawablesResId = drawables;
    }

    @Override
    public void handleLoadItem(ViewGroup parent) {
         mProgress = 0;
        if (mRainItems!=null&&mRainItems.size()>0){
            return;
        }
        double a = parent.getMeasuredWidth(), b = parent.getMeasuredHeight(), c = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)));
        double p = (a + b + c) / 2;
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        double h = 2 * s / c;

        int x1 = (int) (h * a / b);//运行Y的轨迹等于h时的对应x位置
        int x2 = (int) Math.sqrt((Math.pow(b, 2) - Math.pow(h, 2)));  //运行Y的轨迹等于2h时的对应x位置

        int width = (int) c;
        Random random = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {
            //根据startX计算startY和endY，
            // 因为不同的的startX，item的Y轴绘制长度不一致，
            // 这样可以实现更精确的绘制来降低性能的消耗
            int startX = random.nextInt(width);
            int startY;

            if (startX < x1) {
                startY = (int) (h - ((b / a) * startX));
            } else {
                startY = (int) ((startX - x1) * h / (c - x1));
            }

            int incrementY; //c边以下的y轴补偿量
            if (startX < x2) {
                incrementY = (int) ((a / b) * startX);
            } else {
                incrementY = (int) ((b / a) * (c - startX));
            }
            int endY = (int) (incrementY + h);

            int delay = random.nextInt(MAX_DELAY); //item延迟出现的时长
            int speed = MAX_INCREMENTY; //item每次的y轴偏移量
            int resId = mDrawablesResId[random.nextInt(mDrawablesResId.length)];
            mRainItems.add(new SurfaceRainItem(parent.getContext(), resId, startX, startY, endY, speed, delay));
        }
    }

    @Override
    public boolean handleOnDraw(Canvas canvas, int width, int height) {
        //绘制canvas背景颜色
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.parseColor("#bb000000"));
        //一些直角三角形的计算
        double a = width, b = height, c = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)));
        double p = (a + b + c) / 2;
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        double h = 2 * s / c;  //直角三角形的高
        double angleB = Math.toDegrees(Math.atan(b / a)); //夹角a

        //旋转canvas，让x轴对齐三角形的斜边
        canvas.save();
        canvas.rotate((float) angleB);
        canvas.translate(0, (float) -h);
        //item绘制
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
    }

    @Override
    public void clear() {
        if (mRainItems != null && mRainItems.size() > 0) {
            for (IRainItem mRainItem : mRainItems) {
                mRainItem.clear();
            }
            mRainItems.clear();
        }
    }
}
