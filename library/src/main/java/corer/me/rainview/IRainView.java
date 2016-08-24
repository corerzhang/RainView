package corer.me.rainview;

import android.app.Activity;

/**
 * Created by corer.zhang on 16/8/24.
 */
public interface IRainView {


    void startRain(Activity activity);
    void stopRain();
    void setRainCallback(RainCallback callback);
    void setRainController(IRainController controller);

     interface RainCallback {
        void onRainStart();

        void onRainProgress(float progress);

        void onRainEnd();
    }

    class RainCallbackAdapter implements RainCallback {
        @Override
        public void onRainStart() {
        }

        @Override
        public void onRainProgress(float progress) {

        }

        @Override
        public void onRainEnd() {
        }
    }
}
