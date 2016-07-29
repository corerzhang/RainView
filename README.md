# RainView
一个飘雨动画的控件

![image](https://github.com/corerzhang/RainView/raw/master/screenshots/demo.gif)

```java
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
    mRainView.startRain(MainActivity.this);
```




