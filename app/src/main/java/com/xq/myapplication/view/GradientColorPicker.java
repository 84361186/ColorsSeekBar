package com.xq.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SumT on 2016/12/14.
 */

public class GradientColorPicker extends View {

    private Paint textPaint;//画文字的画笔
    private Paint frontPaint;//画图片的画笔
    private Paint pointerPaint;

    private int screenW;//图宽

    private String text;//绘制的文本
    private float xText;//文本x位置
    private float yText;//文本y位置
    private float mTextWidth;//文本宽度
    String s1 = "1000$";
    String s2 = "10000$";

    //private Bitmap mPoint;//指示器位图
    private float xPoint;//指示器x位置
    private float yPoint;//指示器y位置
    //private int mPointWidth;//指示器图宽
    //private int mPointHeight;//指示器图高

    private Bitmap mDrawable;
    private int mDrawableHeight;
    private int[] colors = new int[]{0XFFE8132B, 0xFFF79C0C, 0XFFB0D263, 0XFF0C76BD};
    private GradientDrawable gd;

    private boolean isDraging = false;//是否在拖拽
    private boolean isFirst = true;

    private int mColorBarPosition;
    private OnColorChangeListener mOnColorChangeLister;
    private List<Integer> mColors = new ArrayList<>();
    private int c0, c1, mRed, mGreen, mBlue;

    private int firstColor;
    private float mRealW;
    private float mRCircle;

    public GradientColorPicker(Context context) {
        this(context, null);

    }

    public GradientColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        //文本画笔设置
        textPaint = new Paint();
        textPaint.setAntiAlias(true);//抗锯齿
        textPaint.setSubpixelText(true);//有助于文本在LCD屏幕上的显示效果
        textPaint.setTextSize(sp2px(getContext(), 10.0f));
        textPaint.setColor(Color.BLACK);
        //图片画笔设置
        frontPaint = new Paint();
        frontPaint.setAntiAlias(true);
        frontPaint.setFilterBitmap(true);//对位图进行滤波处理
        //pointerPaint设置
        pointerPaint = new Paint();
        pointerPaint.setAntiAlias(true);//抗锯齿
        pointerPaint.setColor(Color.WHITE);

       /* mPoint = BitmapFactory.decodeResource(context.getResources(), R.drawable.colorpoint);
        mPointWidth = mPoint.getWidth();
        mPointHeight = mPoint.getHeight();*/

    }

    /**
     * 返回文字的宽度
     *
     * @return
     */
    private float getFontWeight(String text) {
        return textPaint.measureText(text);
    }

    /**
     * 返回文字的高度
     *
     * @return
     */
    private float getFontHeight() {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * drawbale转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenW = w;
        mRealW = screenW - getFontWeight(s1) - getFontWeight(s2);
        super.onSizeChanged(w, h, oldw, oldh);

        for (int i = 0; i <= 100; i++) {
            mColors.add(pickColor(i));
        }

        xPoint = (float) getColorBarPosition(firstColor) / 100 * mRealW;
        yText = 28.0f;
        yPoint = yText + 5;

        text = (int) ((float) (getColorBarPosition(firstColor)) / 100 * 9000) + 1000 + "K";
        mTextWidth = getFontWeight(text);
        xText = xPoint + 5 / 2 - mTextWidth / 2;//文字位于指示器中间


        gd = new GradientDrawable(GradientDrawable.Orientation
                .LEFT_RIGHT, colors);
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setSize(screenW - (int) getFontWeight(s1) - (int) getFontWeight(s2), 35);
        gd.setCornerRadius(50.0f);

        mDrawable = drawableToBitmap(gd);//drawable转bitmap才能获得高
        mDrawableHeight = mDrawable.getHeight();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*-------------画文字----------------*/
        canvas.drawText(text, xText + getFontWeight(s1), yText, textPaint);
        //渲染文本，参数一是String类型的文本，参数二x轴，参数三y轴，参数四是Paint对象。
        /*---------------end----------------*/
        /*---------------画指示器----------------*/
        //canvas.drawBitmap(bitmap, x, y, paint);将图画到指定坐标
        //canvas.drawBitmap(mPoint, xPoint - (mPointWidth - 5) / 2 + getFontWeight(s1), yPoint,
        // frontPaint);
        if (isFirst) {
            frontPaint.setColor(firstColor);
        } else {
            frontPaint.setColor(getColor());
        }
        frontPaint.setStyle(Paint.Style.FILL);
        mRCircle = 15.0f;
        float xCircle = xPoint + getFontWeight(s1) + 5 / 2;
        float yCircle = yPoint + mRCircle;
        canvas.drawCircle(xCircle, yCircle, mRCircle, frontPaint);//
        // 绘制圆，参数一是中心点的x轴，参数二是中心点的y轴，参数三是半径，参数四是paint对象；
        Path path = new Path();
        path.moveTo(xCircle - 6, yCircle + mRCircle - 2);// 此点为多边形的起点
        path.lineTo(xCircle + 6, yCircle + mRCircle - 2);
        path.lineTo(xCircle, yCircle + mRCircle + 8);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, frontPaint);
        /*---------------end----------------*/

        canvas.drawText(s1, 0, yPoint + mRCircle * 2 + 8 + 19, textPaint);
        /*---------------画背景颜色--start--------------*/
        canvas.translate(getFontWeight(s1), yPoint + mRCircle * 2 + 8);
        gd.draw(canvas);
        canvas.drawText(s2, mDrawable.getWidth(), 19, textPaint);
        /*---------------end----------------*/
        /*---------------画指示器的竖线----------------*/
        //canvas.saveLayerAlpha(0, yPoint + mPointHeight, screenW,yPoint + mPointHeight + 35,0x88 );
        canvas.drawRect(xPoint, 0, xPoint + 5, 35, pointerPaint);// 长方形
        /*---------------end----------------*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        this.getParent().requestDisallowInterceptTouchEvent(true);
        float tmpX;
        float tmpY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isFirst = false;
                tmpX = screenW - x - 5;
                tmpY = yPoint + mRCircle * 2 + 8 + mDrawableHeight - y;
                if (tmpX < getFontWeight(s1) || tmpY < 0 || x < getFontWeight(s1) || y < 0) {
                    isDraging = false;
                    return true;
                }
                isDraging = true;
                move(x);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                tmpX = screenW - x - 5;
                tmpY = yPoint + mRCircle * 2 + 8 + mDrawableHeight - y;
                if (tmpX < getFontWeight(s1) || tmpY < 0 || x < getFontWeight(s1) || y < 0) {
                    isDraging = false;
                    return true;
                }
                if (!isDraging) {
                    return true;
                }
                move(x);
                break;
            case MotionEvent.ACTION_UP:
                isDraging = false;
                /*---------------取颜色----------------*/
                if (mOnColorChangeLister != null) {
                    mOnColorChangeLister.onColorChangeListener(mColorBarPosition, getColor());
                }
                break;
        }
        return true;
    }

    private void move(float x) {
        x = x - getFontWeight(s1);
        int textValue = (int) (x * 9000 / mRealW + 1000);
        if (textValue < 1000) {
            textValue = 1000;
        }
        if (textValue > 10000) {
            textValue = 10000;
        }
        text = textValue + "$";
        float value = x / mRealW * 100;
        mColorBarPosition = (int) value;
        if (mColorBarPosition < 0) mColorBarPosition = 0;
        if (mColorBarPosition > mRealW) mColorBarPosition = 100;
        xText = x + 5 / 2 - mTextWidth / 2;
        xPoint = x;
        invalidate();
    }

    public int getColor() {
        if (mColorBarPosition >= mColors.size()) {
            int color = pickColor(mColorBarPosition);
            return Color.argb(1, Color.red(color), Color.green(color), Color.blue(color));
        }
        int color = mColors.get(mColorBarPosition);
        return color;
    }

    private int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }

    private int pickColor(int value) {
        return pickColor((float) value / 100 * mRealW);

    }

    private int pickColor(float position) {
        float unit = position / mRealW;
        if (unit <= 0.0)
            return colors[0];

        if (unit >= 1)
            return colors[colors.length - 1];

        float colorPosition = unit * (colors.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        c0 = colors[i];
        c1 = colors[i + 1];
        mRed = mix(Color.red(c0), Color.red(c1), colorPosition);
        mGreen = mix(Color.green(c0), Color.green(c1), colorPosition);
        mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition);
        return Color.rgb(mRed, mGreen, mBlue);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int getColorBarPosition(int color) {
        for (int i = 0; i < mColors.size(); i++) {
            if (color == mColors.get(i)) {
                return i;
            }
        }
        return 0;
    }

    public void setColor(int color) {
        firstColor = color;
    }

    public interface OnColorChangeListener {
        /**
         * @param colorBarPosition between 0-maxValue
         * @param color            return the color contains alpha value whether showAlphaBar is
         *                         true or without alpha value
         */
        void onColorChangeListener(int colorBarPosition, int color);
    }

    /**
     * @param onColorChangeListener
     */
    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeLister = onColorChangeListener;
    }
}
