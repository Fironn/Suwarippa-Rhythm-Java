package com.example.firon.suwarippa_rhythm_java;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PaintCanvas extends View {

    private Paint paint;
    private RectF rect;
    private Path pathUp;
    private Path pathDown;
    private Path pathLeft;
    private Path pathRight;

    private int yval = 0;
    private Boolean viewflg;
    private int caLeft,caTop,caWidth,caHeight;
    private int press;

    public PaintCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        pathUp = new Path();
        pathDown = new Path();
        pathLeft = new Path();
        pathRight = new Path();
        yval = 450;
    }

    protected void setPress(int press){
        this.press=press;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Resources res = getResources();
        int color_main_1 = res.getColor(R.color.colorMain1);
        int color_last_1 = res.getColor(R.color.colorLast1);
        int color_last_white_1 = res.getColor(R.color.colorLastWhite1);
        int color_last_2 = res.getColor(R.color.colorLast2);
        int color_last_white_2 = res.getColor(R.color.colorLastWhite2);
        int color_last_3 = res.getColor(R.color.colorLast3);
        int color_last_white_3 = res.getColor(R.color.colorLastWhite3);
        int color_last_4 = res.getColor(R.color.colorLast4);
        int color_last_white_4 = res.getColor(R.color.colorLastWhite4);

        canvas.drawColor(color_main_1);

        caLeft=this.getLeft();
        caTop=this.getTop();
        caWidth=this.getWidth();
        caHeight=this.getHeight();

        float tx1 = 0;
        float ty1 = 0;
        float tx2 = tx1+caWidth;
        float ty2 = ty1;
        float tx3 = tx1+caWidth/2;
        float ty3 = ty1+caHeight/2;

        paint.setStrokeWidth(10);
        if(press==1)paint.setColor(color_last_white_1);
        else paint.setColor(color_last_1);
        pathUp.moveTo(tx1, ty1);
        pathUp.lineTo(tx2, ty2);
        pathUp.lineTo(tx3, ty3);
        pathUp.lineTo(tx1, ty1);
        canvas.drawPath(pathUp, paint);


        tx1 = 0;
        ty1 = 0;
        tx2 = tx1;
        ty2 = ty1+caHeight;
        tx3 = tx1+caWidth/2;
        ty3 = ty1+caHeight/2;

        paint.setStrokeWidth(10);
        if(press==2)paint.setColor(color_last_white_2);
        else paint.setColor(color_last_2);
        pathLeft.moveTo(tx1, ty1);
        pathLeft.lineTo(tx2, ty2);
        pathLeft.lineTo(tx3, ty3);
        pathLeft.lineTo(tx1, ty1);
        canvas.drawPath(pathLeft, paint);


        tx1 = caWidth;
        ty1 = 0;
        tx2 = tx1;
        ty2 = ty1+caHeight;
        tx3 = tx1-caWidth/2;
        ty3 = ty1+caHeight/2;

        paint.setStrokeWidth(10);
        if(press==3)paint.setColor(color_last_white_3);
        else paint.setColor(color_last_3);
        pathRight.moveTo(tx1, ty1);
        pathRight.lineTo(tx2, ty2);
        pathRight.lineTo(tx3, ty3);
        pathRight.lineTo(tx1, ty1);
        canvas.drawPath(pathRight, paint);


        tx1 = 0;
        ty1 = caHeight;
        tx2 = tx1+caWidth;
        ty2 = ty1;
        tx3 = tx1+caWidth/2;
        ty3 = ty1-caHeight/2;

        paint.setStrokeWidth(10);
        if(press==4)paint.setColor(color_last_white_4);
        else paint.setColor(color_last_4);
        pathDown.moveTo(tx1, ty1);
        pathDown.lineTo(tx2, ty2);
        pathDown.lineTo(tx3, ty3);
        pathDown.lineTo(tx1, ty1);
        canvas.drawPath(pathDown, paint);


        if (viewflg) {

        }else{
            // 描画クリア
            canvas.drawColor(color_main_1);
        }

    }

    public void showCanvas(boolean flg){
        viewflg = flg;
        // 再描画
        invalidate();
    }

    public void setPositon(int pos) {
        yval = pos;
    }
}
