package corer.me.demo.rainview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import corer.me.rainview.IRainView;
import corer.me.rainview.RainController;
import corer.me.rainview.RainView;
import corer.me.rainview.SurfaceRainController;
import corer.me.rainview.SurfaceRainView;

public class MainActivity extends AppCompatActivity {

    IRainView mRainView,mSurfaceRainView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.rainView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRainView == null) {
                    mRainView = new RainView(MainActivity.this);
                    mRainView.setRainController(new RainController(new int[]{R.mipmap.snowflake, R.mipmap.ic_launcher}));
                    mRainView.setRainCallback(new IRainView.RainCallbackAdapter() {
                        @Override
                        public void onRainStart() {
                            Toast.makeText(MainActivity.this, "RainView start", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRainEnd() {
                            Toast.makeText(MainActivity.this, "RainView over", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mRainView.startRain(MainActivity.this);
            }
        });

        findViewById(R.id.surfaceRainView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSurfaceRainView == null) {
                    mSurfaceRainView = new SurfaceRainView(MainActivity.this);
                    mSurfaceRainView.setRainController(new SurfaceRainController(new int[]{R.mipmap.snowflake, R.mipmap.ic_launcher}));
                    mSurfaceRainView.setRainCallback(new IRainView.RainCallbackAdapter() {
                        @Override
                        public void onRainStart() {
                            Toast.makeText(MainActivity.this, "SurfaceRainView start", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRainEnd() {
                            Toast.makeText(MainActivity.this, "SurfaceRainView over", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mSurfaceRainView.startRain(MainActivity.this);
            }
        });

    }



}
