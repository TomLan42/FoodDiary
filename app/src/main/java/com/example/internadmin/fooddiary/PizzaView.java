package com.example.internadmin.fooddiary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PizzaView extends View {

    private Bitmap mBitmap;
    private Paint mPaint;
    private RectF mOval;
    private float mAngle = 135;
    private Paint mTextPaint;
    private float w2,h2;

    public PizzaView(Context context, AttributeSet AttributeSet) {
        super(context);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cutpizza);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOval = new RectF();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(48);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(0xffffaa00);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        w2 = getWidth() / 2f;
        h2 = getHeight() / 2f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Matrix m = new Matrix();
        mBitmap = Bitmap.createScaledBitmap(mBitmap, w, h, false);
        RectF src = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        RectF dst = new RectF(0, 0, w, h);
        m.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
        Shader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(m);
        mPaint.setShader(shader);
        m.mapRect(mOval, src);
        w2 = w / 2f;
        h2 = h / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mOval, -90, mAngle, true, mPaint);
        canvas.drawText("click me", getWidth() / 2, getHeight() / 2, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            mAngle = (float) Math.toDegrees(Math.atan2(event.getY() - h2, event.getX() - w2));
            Log.i("PizzaView", "Detected Angle is " + String.valueOf(mAngle));
            mAngle += 90 + 360;
            mAngle %= 360;
            Log.i("PizzaView", "Calculated Angle is " + String.valueOf(mAngle));
            invalidate();
        }
        return true;
    }
}