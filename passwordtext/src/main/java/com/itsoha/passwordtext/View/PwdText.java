package com.itsoha.passwordtext.View;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.itsoha.passwordtext.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/24.
 */

public class PwdText extends View {

    private InputMethodManager input;
    /**
     * 保存当前输入的密码
     */
    private ArrayList<Integer> result;
    /**
     * 密码长度
     */
    private int pwdLength;

    /**
     * 明文密码颜色
     */
    private int pwdColor;

    /**
     * 密码框颜色
     */
    private int borderColor;
    /**
     * 输入时密码框颜色
     */
    private int inputBorderColor;
    /**
     * 密码框阴影颜色
     */
    private int borderShadowColor;
    /**
     * 边框图片
     */
    private int borderImg;
    /**
     * 输入时边框图片
     */
    private int inputBorderImg;
    /**
     * 是否使用图片绘制边框
     */
    private boolean isBorderImg;
    /**
     * 是否按返回键时绘制明文密码
     */
    private boolean isShowTextPwd;
    /**
     * 是否绘制在输入时密码框阴影的颜色
     */
    private boolean isShowBorderShadow;
    /**
     * 是否值绘制明文密码
     */
    private boolean clearTextPwd;
    /**
     * 是否只绘制圆点密码
     */
    private boolean drawPoint;
    /**
     * 是否输入密码时不更改密码框的颜色
     */
    private boolean isInputChangeColor;
    /**
     * 延迟绘制圆点的时间
     */
    private int delayTime;
    /**
     * 密码文字的大小
     */
    private float pwdTextSize;
    /**
     * 边框的圆角大小
     */
    private float borderRaduis;
    /**
     * 边框宽度
     */
    private int borderWidth;
    /**
     * 边框之间的间距
     */
    private int borderSpacing;
    /**
     * 边框圆角矩形
     */
    private RectF borderRectF;
    /**
     * 密码圆点画笔
     */
    private Paint pwdPointPaint;
    /**
     * 明文密码画笔
     */
    private Paint pwdTextPaint;
    /**
     * 边框画笔
     */
    private Paint borderPaint;
    /**
     * 输入时边框画笔
     */
    private Paint inputBorderPaint;
    /**
     * 边框之间的间距宽度
     */
    private int spacingWidth;
    /**
     * 整个View的高度
     */
    private int height;
    /**
     * 保存按下返回键时输入的密码总数
     */
    private int saveResult;
    /**
     * 输入完成时监听
     */
    private InputCallBack inputCallBack;
    private static boolean invalidated = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    invalidated = true;
                    invalidate();
                    break;
            }
        }
    };


    public PwdText(Context context) {
        super(context);
    }

    public PwdText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PwdText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public interface InputCallBack {
        void onInputFinish(String password);
    }

    public void setInputCallBack(InputCallBack inputCallBack) {
        this.inputCallBack = inputCallBack;
    }

    private void initView(Context context, AttributeSet attrs) {
        this.setOnKeyListener(new NumKeyListener());
        //可触焦
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        result = new ArrayList<>();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PwdText);
        if (array != null) {
            pwdLength = array.getInt(R.styleable.PwdText_PwdLength, 6);
            pwdColor = array.getColor(R.styleable.PwdText_pwdColor, Color.parseColor("#3779e3"));
            borderColor = array.getColor(R.styleable.PwdText_borderColor, Color.parseColor("#999999"));
            inputBorderColor = array.getColor(R.styleable.PwdText_inputBorder, Color.parseColor("#3779e3"));
            borderShadowColor = array.getColor(R.styleable.PwdText_borderShadow, Color.parseColor("#3577e2"));
            borderImg = array.getResourceId(R.styleable.PwdText_borderImg, R.drawable.pic_dlzc_srk1);
            inputBorderImg = array.getResourceId(R.styleable.PwdText_inputBorderImg, R.drawable.pic_dlzc_srk);
            isBorderImg = array.getBoolean(R.styleable.PwdText_isDrawBorderImg, false);
            isShowTextPwd = array.getBoolean(R.styleable.PwdText_isShowTextPwd, false);
            isShowBorderShadow = array.getBoolean(R.styleable.PwdText_isShowBorderShadow, false);
            clearTextPwd = array.getBoolean(R.styleable.PwdText_clearTextPwd, false);
            drawPoint = array.getBoolean(R.styleable.PwdText_drawPwdPoint, false);
            isInputChangeColor = array.getBoolean(R.styleable.PwdText_isInputChangeColor, false);
            delayTime = array.getInt(R.styleable.PwdText_delayTime, 1000);
            pwdTextSize = array.getDimension(R.styleable.PwdText_pwd_textsize,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
            borderRaduis = array.getDimension(R.styleable.PwdText_borderRadius,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        } else {
            pwdLength = 6;
            pwdColor = Color.parseColor("#3779e3");
            borderColor = Color.parseColor("#999999");
            inputBorderColor = Color.parseColor("#3779e3");
            borderShadowColor = Color.parseColor("#3577e2");
            borderImg = R.drawable.pic_dlzc_srk1;
            inputBorderImg = R.drawable.pic_dlzc_srk;
            delayTime = 1000;
            clearTextPwd = false;
            drawPoint = false;
            isBorderImg = false;
            isShowTextPwd = false;
            isShowBorderShadow = false;
            isInputChangeColor = false;

            //明文密码字体大小，初始化18sp
            pwdTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
            //边框圆角程度初始化8dp
            borderRaduis = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        }
        //边框宽度初始化为40dp
        borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        //边框之间的间距为10dp
        borderSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        borderRectF = new RectF();
        //初始化画笔
        initPoint();

    }

    /**
     * 初始化画笔
     */
    private void initPoint() {
        //密码圆点画笔初始化
        pwdPointPaint = new Paint();
        //抗锯齿
        pwdPointPaint.setAntiAlias(true);
        //设置笔画的宽度
        pwdPointPaint.setStrokeWidth(3);
        pwdPointPaint.setStyle(Paint.Style.FILL);
        pwdPointPaint.setColor(pwdColor);

        //明文密码画笔初始化
        pwdTextPaint = new Paint();
        pwdTextPaint.setAntiAlias(true);
        //粗体
        pwdTextPaint.setFakeBoldText(true);
        pwdTextPaint.setColor(pwdColor);

        //边框画笔初始化
        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);
        //这种风格通常描边
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3);

        //输入时边框画笔初始化
        inputBorderPaint = new Paint();
        inputBorderPaint.setAntiAlias(true);
        inputBorderPaint.setStyle(Paint.Style.STROKE);
        inputBorderPaint.setColor(borderColor);
        inputBorderPaint.setStrokeWidth(3);

        //是否绘制边框阴影
        if (isShowBorderShadow) {
            inputBorderPaint.setShadowLayer(6, 0, 0, borderShadowColor);
            //表示视图有一个软件层。软件层由位图支持，即使硬件加速被启用，也会使视图呈现在Android的软件渲染管道中。
            setLayerType(LAYER_TYPE_SOFTWARE, inputBorderPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpec = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //等于Wrap_content
        if (widthSpec == MeasureSpec.AT_MOST) {
            //高度已知，宽度未知的情况下
            if (heightSpec != MeasureSpec.AT_MOST) {
                spacingWidth = heightSize / 4;
                //计算控件的宽度
                widthSize = (heightSize * pwdLength) + (spacingWidth * (pwdLength - 1));
                borderWidth = heightSize;
            } else {
                //宽高都未知时
                widthSize = (borderWidth * pwdLength) + (spacingWidth * (pwdLength - 1));
                heightSize = (int) (borderWidth + ((borderPaint.getStrokeWidth()) * 2));
            }
        } else {
            //宽度已知但高度未知时
            if (heightSpec == MeasureSpec.AT_MOST) {
                borderWidth = (widthSize * 4) / (5 * pwdLength);
                spacingWidth = borderWidth / 4;
                heightSize = (int) (borderWidth + ((borderPaint.getStrokeWidth()) * 2));
            }
        }
        height = heightSize;
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //密码圆点为边框宽度的六分之一
        int point = borderWidth / 6;

        //如果明文密码字体大小为默认大小，则取边框的八分之一，否则用自定义大小
        if (pwdTextSize == (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics())) {
            pwdTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, borderWidth / 8, getResources().getDisplayMetrics());
        }

        pwdTextPaint.setTextSize(pwdTextSize);

        //绘制密码边框
        drawBorder(canvas, height);
        //是否输入时不更改密码框颜色
        if (isInputChangeColor) {
            //是否绘制明文密码
            if (clearTextPwd) {
                for (int i = 0; i < result.size(); i++) {
                    String num = String.valueOf(result.get(i));
                    drawText(canvas, num, i);
                }
            } else if (drawPoint) {
                for (int i = 0; i < result.size(); i++) {
                    float circleX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2) + (6 * spacingWidth));
                    float circleY = height / 2;
                    canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
                }
            } else {
                if (invalidated) {
                    drawDelayCircle(canvas, height, point);
                    return;
                }
                for (int i = 0; i < result.size(); i++) {
                    //明文密码
                    String num = result.get(i) + "";
                    //圆点坐标
                    float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
                    float circleY = height / 2;
                    //密码框坐标
                    drawText(canvas, num, i);

				/*
                * 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
                    if (i + 1 == result.size()) {
                        handler.sendEmptyMessageDelayed(1, delayTime);
                    }
                    //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                    //即按下back键时，不绘制明文密码
                    if (!isShowTextPwd) {
                        if (saveResult > result.size()) {
                            canvas.drawCircle((float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 + (0.6 * spacingWidth))), circleY, point, pwdPointPaint);
                        }
                    }
                    //当输入第二个密码时，才开始从第一个位置绘制圆点
                    if (i >= 1) {
                        canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
                    }
                }
            }
        } else {
            if (clearTextPwd) {
                for (int i = 0; i < result.size(); i++) {
                    String num = result.get(i) + "";
                    drawText(canvas, num, i);
                    //计算密码边框坐标
                    int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
                    int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));

                    drawBitmapOrBorder(canvas, left, right, height);
                }
            } else if (drawPoint) {
                for (int i = 0; i < result.size(); i++) {
                    float circleX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
                    float circleY = height / 2;
                    int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
                    int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
                    drawBitmapOrBorder(canvas, left, right, height);
                    canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
                }
            } else {
                if (invalidated) {
                    drawDelayCircle(canvas, height, point);
                    return;
                }
                for (int i = 0; i < result.size(); i++) {
                    //明文密码
                    String num = result.get(i) + "";
                    //圆点坐标
                    float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
                    float circleY = height / 2;
                    //密码框坐标
                    int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
                    int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));

                    drawBitmapOrBorder(canvas, left, right, height);

                    drawText(canvas, num, i);

				/*
				* 当输入位置 = 输入长度时
				* 即判断当前绘制位置是否等于当前正在输入密码的位置
				* 若是则延迟delayTime时间后绘制为圆点
				* */
                    if (i + 1 == result.size()) {
                        handler.sendEmptyMessageDelayed(1, delayTime);
                    }
                    //若按下back键保存的密码 > 输入的密码长度，则只绘制圆点
                    //即按下back键时，不绘制明文密码
                    if (!isShowTextPwd) {
                        if (saveResult > result.size()) {
                            canvas.drawCircle((float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 + (0.6 * spacingWidth))), circleY, point, pwdPointPaint);
                        }
                    }
                    //当输入第二个密码时，才开始从第一个位置绘制圆点
                    if (i >= 1) {
                        canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
                    }
                }

            }
        }


    }

    /**
     * 延迟多长时间后，将当前输入的明文密码绘制为圆点
     *
     * @param canvas 画布
     * @param height 高度
     * @param point  点
     */
    private void drawDelayCircle(Canvas canvas, int height, int point) {
        invalidated = false;
        if (isInputChangeColor) {
            for (int i = 0; i < result.size(); i++) {
                float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
                float circleY = height / 2;
                canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
            }
            canvas.drawCircle((float) ((float) (((result.size() - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2)) + (0.6 * spacingWidth)),
                    height / 2, point, pwdPointPaint);
        } else {
            for (int i = 0; i < result.size(); i++) {
                float circleX = (float) (((i - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2) + (0.6 * spacingWidth));
                float circleY = height / 2;
                int left = (int) (i * (borderWidth + spacingWidth) + (0.5 * spacingWidth));
                int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
                canvas.drawCircle(circleX, circleY, point, pwdPointPaint);
                drawBitmapOrBorder(canvas, left, right, height);
            }
            canvas.drawCircle((float) ((float) (((result.size() - 1) * (borderWidth + spacingWidth)) + (borderWidth / 2)) + (0.6 * spacingWidth)),
                    height / 2, point, pwdPointPaint);
        }
        handler.removeMessages(1);
    }

    /**
     * 清除密码
     */
    public void clearPsw() {
        result.clear();
        invalidate();
    }

    /**
     * 获取密码
     */
    public String getPsw() {
        StringBuffer sb = new StringBuffer();
        for (int i : result) {
            sb.append(i);
        }
        return sb.toString();
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBord() {
        input.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击弹出键盘
            requestFocus();
            input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//只允许输入数字
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new NumInputConnection(this, false);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            input.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    /**
     * 是否使用图片绘制密码框
     */
    private void drawBitmapOrBorder(Canvas canvas, int left, int right, int height) {
        if (isBorderImg) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), inputBorderImg);
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(left, (int) (0 + ((borderPaint.getStrokeWidth()))), right, (int) (height - (borderPaint.getStrokeWidth())));
            canvas.drawBitmap(bitmap, src, dst, inputBorderPaint);
            bitmap.recycle();
        } else {
            borderRectF.set(left, 0 + (borderPaint.getStrokeWidth()), right, height - (borderPaint.getStrokeWidth()));
            canvas.drawRoundRect(borderRectF, borderRaduis, borderRaduis, inputBorderPaint);
        }
    }

    /**
     * 绘制明文密码
     *
     * @param canvas 画布
     * @param num    密码
     * @param i      索引
     */
    private void drawText(Canvas canvas, String num, int i) {
        Rect mTextBound = new Rect();
        pwdTextPaint.getTextBounds(num, 0, num.length(), mTextBound);
        //绘制文本
        Paint.FontMetrics fontMetrics = pwdTextPaint.getFontMetrics();
        float textX = (float) ((i * (borderWidth + spacingWidth)) + (borderWidth / 2 - mTextBound.width() / 2) + (0.5 + spacingWidth));
        float textY = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        if (saveResult != 0 || saveResult < result.size()) {
            canvas.drawText(num, textX, textY, pwdTextPaint);
        }
    }

    /**
     * 绘制初始密码框时判断是否用图片绘制密码框
     *
     * @param canvas 画布
     * @param height 高度
     */
    private void drawBorder(Canvas canvas, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), borderImg);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < pwdLength; i++) {
            int left = (int) ((i * (borderWidth + spacingWidth)) + (0.5 * spacingWidth));
            int right = (int) (((i + 1) * borderWidth) + (i * spacingWidth) + (0.7 * spacingWidth));
            if (isBorderImg) {
                Rect dst = new Rect(left, (int) (0 + (borderPaint.getStrokeWidth())), right, (int) (height - (borderPaint.getStrokeWidth())));
                canvas.drawBitmap(bitmap, src, dst, borderPaint);
            } else {
                borderRectF.set(left, 0 + (borderPaint.getStrokeWidth()), right, height - (borderPaint.getStrokeWidth()));
                canvas.drawRoundRect(borderRectF, borderRaduis, borderRaduis, borderPaint);
            }
        }
        bitmap.recycle();
    }


    private class NumInputConnection extends BaseInputConnection {

        public NumInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            //这里是接收文本的输入法，我们只允许输入数字，则不做任何处理
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            //屏蔽返回键，发送自己的删除事件
            if (beforeLength == 1 && afterLength == 0) {
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    /**
     * 输入监听
     */
    private class NumKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.isShiftPressed()) {//处理*#等键
                    return false;
                }
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//只处理数字
                    if (result.size() < pwdLength) {
                        result.add(keyCode - 7);
                        invalidate();
                        FinishInput();
                    }
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!result.isEmpty()) {//不为空时，删除最后一个数字
                        saveResult = result.size();
                        result.remove(result.size() - 1);
                        invalidate();
                    }
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    FinishInput();
                    return true;
                }
            }
            return false;
        }

        /**
         * 输入完成后调用的方法
         */
        void FinishInput() {
            if (result.size() == pwdLength && inputCallBack != null) {//输入已完成
                StringBuffer sb = new StringBuffer();
                for (int i : result) {
                    sb.append(i);
                }
                InputMethodManager imm = (InputMethodManager) PwdText.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(PwdText.this.getWindowToken(), 0); //输入完成后隐藏键盘
                inputCallBack.onInputFinish(sb.toString());
            }
        }
    }

}
