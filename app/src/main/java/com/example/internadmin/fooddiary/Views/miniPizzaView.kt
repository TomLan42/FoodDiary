package com.example.internadmin.fooddiary.Views

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.internadmin.fooddiary.R

class miniPizzaView : View {

    private var angle = 90f
    private lateinit var mBitmap: Bitmap
    private var mPaint: Paint
    private var mOval: RectF
    private var w2: Float = 0f
    private var h2:Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mOval = RectF()
        w2 = width / 2f
        h2 = height / 2f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val m = Matrix()
        mBitmap = decodeSampledBitmapFromResource(resources, R.drawable.cutpizza_small, w, h)
        val src = RectF(0f, 0f, mBitmap.width.toFloat(), mBitmap.height.toFloat())
        val dst = RectF(0f, 0f, w.toFloat(), h.toFloat())
        m.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)
        val shader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(m)
        mPaint.shader = shader
        m.mapRect(mOval, src)
        w2 = w / 2f
        h2 = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawArc(mOval, -90f, angle.toFloat(), true, mPaint)
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromResource(res: Resources, resId: Int,
                                                reqWidth: Int, reqHeight: Int): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun setServingSlice(fraction: Float){
        angle = (fraction - Math.floor(fraction.toDouble()).toFloat()) * 360
        invalidate()
    }

}