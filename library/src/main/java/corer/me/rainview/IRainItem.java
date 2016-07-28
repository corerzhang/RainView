package corer.me.rainview;

import android.graphics.Canvas;

/**
 * Created by corer.zhang on 16/7/11.
 */
public interface IRainItem {


    void draw(Canvas canvas);

    void reset();

    boolean isOut();

    float progress();

    void clear();

}
