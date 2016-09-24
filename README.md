# RainView
>一个飘雨动画的控件, 有两种实现RainView和SurfaceRainView, 它们都实现了IRainView接口



![image](https://github.com/corerzhang/RainView/raw/master/screenshots/demo.gif)

```java
   IRainView mRainView=new RainView(MainActivity.this);
   mRainView.setRainController(new RainController(new int[]{R.mipmap.snowflake,R.mipmap.ic_launcher}));
   mRainView.setRainCallback(new RainView.RainCallbackAdapter(){
         @Override
         public void onRainStart() {
            Toast.makeText(MainActivity.this,"RainView start",Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onRainEnd() {
            Toast.makeText(MainActivity.this,"RainView over",Toast.LENGTH_SHORT).show();
         }
      });
    mRainView.startRain(MainActivity.this);
```
```java
   IRainView mSurfaceRainView = new SurfaceRainView(MainActivity.this);
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
   mSurfaceRainView.startRain(MainActivity.this);    
```                         




