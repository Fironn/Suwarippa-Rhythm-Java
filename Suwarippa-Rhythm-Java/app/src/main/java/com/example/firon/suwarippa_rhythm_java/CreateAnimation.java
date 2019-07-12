package com.example.firon.suwarippa_rhythm_java;

import android.os.Handler;
import android.view.animation.Animation;

public class CreateAnimation extends Animation {

    private PaintCanvas arc;
    private boolean flg = true;

    // 中心座標
    private float centerX;
    private float centerY;

    // アニメーション角度
    private float oldAngle;
    private float newAngle;

    private Runnable runnable;
    private int pos = 450;

    private final Handler handler = new Handler();

    int period = 50;

    CreateAnimation(PaintCanvas arc, int newAngle) {
        this.arc = arc;
    }

    protected void move(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if(flg){
                    pos += 10;
                    arc.setPositon(pos);
                    // 再描画のために無効にする
                    arc.invalidate();

                    if(pos>1700){
                        stopTask();
                        pos = 0;
                    }
                    handler.postDelayed(this, period);
                }
            }
        };

        handler.post(runnable);
    }

    private void stopTask(){
        handler.removeCallbacks(runnable);
        runnable = null;
        flg = false;
    }


}
