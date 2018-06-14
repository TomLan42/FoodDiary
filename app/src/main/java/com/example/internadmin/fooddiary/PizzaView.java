package com.example.internadmin.fooddiary;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.view.MotionEvent;
import android.view.View;

public class PizzaView extends View {

    private Bitmap mBitmap;
    private Paint mPaint;
    private RectF mOval;
    private float mAngle;
    private Paint mTextPaint;
    private float w2,h2;

    public PizzaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOval = new RectF();
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(48);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(0x000000);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        w2 = getWidth() / 2f;
        h2 = getHeight() / 2f;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PizzaView, 0, 0);
        try {
            mAngle = ta.getDimension(R.styleable.PizzaView_StartingAngle, 90.0f);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Matrix m = new Matrix();
        mBitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.cutpizza, w, h);
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
        canvas.drawText(Integer.toString((int)(mAngle/3.6f+0.5)), getWidth() / 2, getHeight() / 2, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            mAngle = (float) Math.toDegrees(Math.atan2(event.getY() - h2, event.getX() - w2));
            mAngle += 90 + 360;
            mAngle %= 360;
            invalidate();
        }
        return true;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public float getServingSlice(){
        return mAngle/360f;
    }

    public void setServingSlice(float fraction){
        mAngle = (fraction - (float)Math.floor((double) fraction))*360;
        invalidate();
    }
}