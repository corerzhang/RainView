package corer.me.demo.rainview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import corer.me.rainview.RainController;
import corer.me.rainview.RainView;

public class MainActivity extends AppCompatActivity {

    RainView mRainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRainView==null){
                    mRainView=new RainView(MainActivity.this);
                    mRainView.setRainController(new RainController(new int[]{R.mipmap.snowflake,R.mipmap.ic_launcher}));
                    mRainView.setRainCallback(new RainView.RainCallbackAdapter(){
                        @Override
                        public void onRainStart() {
                            Toast.makeText(MainActivity.this,"Rain start",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRainEnd() {
                            Toast.makeText(MainActivity.this,"Rain over",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mRainView.startRain(MainActivity.this);
            }
        });
    }



}
