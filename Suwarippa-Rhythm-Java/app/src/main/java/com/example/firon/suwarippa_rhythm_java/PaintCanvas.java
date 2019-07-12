package com.example.firon.suwarippa_rhythm_java;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    public PaintCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        pathUp = new Path();
        pathDown = new Path();
        pathLeft = new Path();
        pathRight = new Path();
        yval = 450;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.argb(125, 0, 0, 255));

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
        paint.setColor(Color.YELLOW);
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
        paint.setColor(Color.BLUE);
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
        paint.setColor(Color.RED);
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
        paint.setColor(Color.GREEN);
        pathDown.moveTo(tx1, ty1);
        pathDown.lineTo(tx2, ty2);
        pathDown.lineTo(tx3, ty3);
        pathDown.lineTo(tx1, ty1);
        canvas.drawPath(pathDown, paint);


        if (viewflg) {

            paint.setColor(Color.GRAY);
            canvas.drawPath(pathDown, paint);

        }else{
            // 描画クリア
//            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            paint.setColor(Color.BLUE);
            canvas.drawPath(pathDown, paint);
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
