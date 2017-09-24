package com.example.saleprogressview.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.saleprogressview.R;

/**
 * 仿淘宝淘抢购进度条
 * Created by Administrator on 2017/9/22.
 */

public class SaleProgress extends View {

    //商品总数
    private int totalCount;
    //当前卖出数
    private int currentCount;
    //动画需要的
    private int progressCount;
    //售出比例
    private float scale;
    //边框颜色
    private int sideColor;
    //文字颜色
    private int textColor;
    //边框粗细
    private float sideWidth;
    //边框所在的矩形
    private Paint sidePaint;
    //背景矩形
    private RectF bgRectF;
    private Paint srcPoint;
    private Paint textPoint;
    private String nearOverText;
    private String overText;
    private float textSize;
    private float radius;
    private PorterDuffXfermode mDuffXfermode;
    private float nearOverTextWidth;
    private float overTextWidth;
    private boolean isNeedAnim;
    private Bitmap bgBitmap;
    private int width;
    private int height;
    private Bitmap bgSrc;
    private Bitmap fgSrc;
    private float baseLineY;

    public SaleProgress(Context context) {
        super(context);
    }

    public SaleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaint();
    }


    public SaleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * 初始化样式 属性
     *
     * @param context 上下文
     * @param attrs   属性集
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        //获得样式属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SaleProgressView);
        sideColor = typedArray.getColor(R.styleable.SaleProgressView_sideColor, 0xffff3c32);
        textColor = typedArray.getColor(R.styleable.SaleProgressView_textColor, 0xffff3c32);
        sideWidth = typedArray.getDimension(R.styleable.SaleProgressView_sideWidth, dp2px(2));
        overText = typedArray.getString(R.styleable.SaleProgressView_overText);
        nearOverText = typedArray.getString(R.styleable.SaleProgressView_nearOverText);
        textSize = typedArray.getDimensionPixelSize(R.styleable.SaleProgressView_textSize, sp2px(16));
        isNeedAnim = typedArray.getBoolean(R.styleable.SaleProgressView_isNeedAnim, true);
        //重复利用
        typedArray.recycle();

    }

    private int dp2px(int dpValues) {
        float densityDpi = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValues * densityDpi + 0.5f);
    }

    private int sp2px(float spValues) {
        float density = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValues * density + 0.5f);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //构建Paint时直接加上去锯齿属性
        sidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //只绘制图形轮廓（描边）
        sidePaint.setStyle(Paint.Style.STROKE);
        sidePaint.setStrokeWidth(sideWidth);
        sidePaint.setColor(sideColor);

        srcPoint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //只绘制图形内容
        textPoint.setStyle(Paint.Style.FILL);
        textPoint.setTextSize(textSize);

        //在两者相交的地方绘制源图像，并且绘制的效果会受到目标图像对应地方透明度的影响
        mDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        if (!TextUtils.isEmpty(nearOverText)) {
            nearOverTextWidth = textPoint.measureText(nearOverText);
        }
        if (!TextUtils.isEmpty(overText)) {
            overTextWidth = textPoint.measureText(overText);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isNeedAnim) {
            progressCount = currentCount;
        }

        if (totalCount == 0) {
            scale = 0.0f;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NumberFormat numberInstance = NumberFormat.getNumberInstance();
                numberInstance.setMaximumFractionDigits(2);
                scale = Float.parseFloat(numberInstance.format((float) progressCount / (float) totalCount));
            } else {
                java.text.NumberFormat numberInstance = java.text.NumberFormat.getNumberInstance();
                numberInstance.setMaximumFractionDigits(2);
                scale = Float.parseFloat(numberInstance.format((float) progressCount / (float) totalCount));
            }
        }
        drawSide(canvas);
        drawBg(canvas);
        drawFg(canvas);
        drawText(canvas);


        //演示
        if (progressCount != currentCount) {
            if (progressCount < currentCount) {
                progressCount++;
            } else {
                progressCount--;
            }
            postInvalidate();
        }
    }


    /**
     * 绘制文字信息
     *
     * @param canvas 帆布
     */
    private void drawText(Canvas canvas) {
        //百分比
        String scaleText;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            scaleText = new DecimalFormat("#%").format(scale);
        } else {
            scaleText = new java.text.DecimalFormat("#%").format(scale);
        }
        String saleText = String.format("已抢购%s件", progressCount);

        float scaleTextWidth = textPoint.measureText(scaleText);

        Bitmap textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textPoint.setColor(textColor);

        if (scale < 0.8f) {
            textCanvas.drawText(saleText, dp2px(10), baseLineY, textPoint);
            textCanvas.drawText(scaleText, width - scaleTextWidth - dp2px(10), baseLineY, textPoint);
        } else if (scale < 1.0f) {
            textCanvas.drawText(nearOverText, width / 2 - nearOverTextWidth / 2, baseLineY, textPoint);
            textCanvas.drawText(scaleText, width - scaleTextWidth - dp2px(10), baseLineY, textPoint);
        } else {
            textCanvas.drawText(overText, width / 2 - overTextWidth / 2, baseLineY, textPoint);
        }

        textPoint.setXfermode(mDuffXfermode);
        textPoint.setColor(Color.WHITE);
        textCanvas.drawRoundRect(new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth),
                radius, radius, textPoint);

        canvas.drawBitmap(textBitmap, 0, 0, null);
        textPoint.setXfermode(null);

    }

    /**
     * 绘制进度条
     *
     * @param canvas 帆布
     */
    private void drawFg(Canvas canvas) {
        if (scale == 0.0f) {
            return;
        }
        Bitmap fgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas fgCanvas = new Canvas(fgBitmap);
        if (fgSrc == null) {
            fgSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.fg);
        }
        fgCanvas.drawRoundRect(new RectF(sideWidth, sideWidth, (width - sideWidth) * scale, height - sideWidth), radius, radius, srcPoint);
        srcPoint.setXfermode(mDuffXfermode);
        fgCanvas.drawBitmap(fgSrc, null, bgRectF, srcPoint);
        canvas.drawBitmap(fgBitmap, 0, 0, null);
        srcPoint.setXfermode(null);
    }

    /**
     * 绘制背景
     *
     * @param canvas 帆布
     */
    private void drawBg(Canvas canvas) {
        if (bgBitmap == null) {
            bgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas bgCanvas = new Canvas(bgBitmap);
        if (bgSrc == null) {
            bgSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.bg);
        }
        bgCanvas.drawRoundRect(bgRectF, radius, radius, srcPoint);

        srcPoint.setXfermode(mDuffXfermode);
        bgCanvas.drawBitmap(bgSrc, null, bgRectF, srcPoint);

        canvas.drawBitmap(bgBitmap, 0, 0, null);
        srcPoint.setXfermode(null);
    }

    /**
     * 画边缘
     *
     * @param canvas 帆布
     */
    private void drawSide(Canvas canvas) {
        canvas.drawRoundRect(bgRectF, radius, radius, sidePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取View的宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        //设置圆角半径、
        radius = height / 2.0f;

        //留出一定间隙，避免边框被减掉一部分
        if (bgRectF == null) {
            bgRectF = new RectF(sideWidth, sideWidth, width - sideWidth, height - sideWidth);
        }

        if (baseLineY == 0.0f) {
            Paint.FontMetricsInt fm = textPoint.getFontMetricsInt();
            baseLineY = height / 2 - (fm.descent / 2 + fm.ascent / 2);
        }

    }

    /**
     * 设置总数和当前的数量
     */
    public void setTotalAndCurrentCount(int totalCount, int currentCount) {
        this.totalCount = totalCount;
        if (currentCount > totalCount) {
            currentCount = totalCount;
        }
        this.currentCount = currentCount;
        //可在子线程中重绘
        postInvalidate();
    }

}
