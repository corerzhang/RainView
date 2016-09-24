package corer.me.rainview;

import android.graphics.Canvas;
import android.view.ViewGroup;

/**
 * Created by corer.zhang on 16/7/13.
 */
public interface IRainController {

    void handleLoadItem(ViewGroup parent);

    boolean handleOnDraw(Canvas canvas, int width, int height);

    void clear();

    void reset();

    float progress();

    boolean isOver();

}
