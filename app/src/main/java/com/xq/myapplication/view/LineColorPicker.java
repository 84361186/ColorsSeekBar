package com.xq.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xq.myapplication.R;


/**
 * Created by SumT on 2016/12/13.
 */

public class LineColorPicker extends View {
    String[] pallete = new String[]{"#9B0C2A", "#9D1C36", "#A3243E",
            "#BD415B", "#C5B908", "#E1D51A", "#A3243E", "#E1D51A", "#C5B908", "#435D6D",
            "#335569", "#3C6C8F", "#1C407A", "#305493", "#3E63A1", "#637997"};
    int[] colors = new int[pallete.length];

    private Paint textPaint;//画文字的画笔
    private Paint frontPaint;//画指示器的画笔
    private Paint paint;//画背景的画笔

    private int screenW;//图宽

    private String text;//绘制的文本
    private float xText;//文本x位置
    private float yText;//文本y位置
    private float mTextHeight;//文本高度
    private float mTextWidth;//文本高度

    private Bitmap mPoint;//指示器位图
    private float xPoint;//指示器x位置
    private float yPoint;//指示器y位置
    private int mPointWidth;//指示器图宽
    private int mPointHeight;//指示器图高

    private Rect rect = new Rect();//颜色选择背景矩形区域
    private int cellSize;//色块单元格

    private boolean isDraging = false;//是否在拖拽

    public LineColorPicker(Context context) {
        super(context, null);

    }

    public LineColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        //文本画笔设置
        textPaint = new Paint();
        textPaint.setAntiAlias(true);//抗锯齿
        textPaint.setSubpixelText(true);//有助于文本在LCD屏幕上的显示效果
        textPaint.setTextSize(24.0f);
        textPaint.setColor(Color.BLACK);
        //指示器画笔设置
        frontPaint = new Paint();
        frontPaint.setAntiAlias(true);
        frontPaint.setFilterBitmap(true);//对位图进行滤波处理
        //颜色画笔设置
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);//填充
        paint.setAntiAlias(true);

        cellSize = recalcCellSize();
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.parseColor(pallete[i]);
        }
        mPoint = BitmapFactory.decodeResource(context.getResources(), R.drawable.point);
        mPointWidth = mPoint.getWidth();
        mPointHeight = mPoint.getHeight();

        text = 0 + "%";
        mTextHeight = getFontHeight();
        mTextWidth = getFontWeight();
        xText = 0 + Math.abs((mPointWidth-mTextWidth)/2);//文字位于指示器中间
        yText = mTextHeight;

        xPoint = 0;
        yPoint = yText + 5;
    }
    /**
     * 返回文字的宽度
     * @return
     */
    private float getFontWeight() {
        return textPaint.measureText(text);
    }

    /**
     * 返回文字的高度
     * @return
     */
    private float getFontHeight() {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    /**
     * 根据图宽计算单元格
     *
     * @return 单元格大小
     */
    private int recalcCellSize() {
        cellSize = Math.round(screenW / (colors.length * 1f));
        return cellSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenW = w;

        recalcCellSize();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*-------------画文字----------------*/
        canvas.drawText(text, xText, yText, textPaint);
        //渲染文本，参数一是String类型的文本，参数二x轴，参数三y轴，参数四是Paint对象。
        /*---------------end----------------*/
        /*---------------画指示器----------------*/
        //canvas.drawBitmap(bitmap, x, y, paint);将图画到指定坐标
        canvas.drawBitmap(mPoint, xPoint, yPoint, frontPaint);
        /*mPointWidth = mPoint.getWidth();
        mPointHeight = mPoint.getHeight();*/
        /*---------------end----------------*/
        /*---------------画背景颜色--start--------------*/
        rect.left = 0;
        rect.top = (int) (yPoint + mPointHeight);
        rect.right = cellSize;
        rect.bottom = rect.top + cellSize;

        //画第一个扇形加矩形
        paint.setColor(colors[0]);
        RectF ovalFirst = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线
        canvas.drawArc(ovalFirst, 90, 180, true, paint);
        rect.left = rect.right / 2;
        rect.right = cellSize * 6 / 7;
        RectF rectFirst = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        canvas.drawRect(rectFirst, paint);

        //画中间的色块
        for (int i = 1; i < colors.length - 1; i++) {

            paint.setColor(colors[i]);

            rect.left = rect.right + 5;
            rect.right += cellSize;

            rect = new Rect(rect.left, rect.top, rect.right, rect.bottom);
            canvas.drawRect(rect, paint);
        }

        //画最后一个扇形加矩形
        paint.setColor(colors[colors.length - 1]);
        rect.left = rect.right + 5;
        rect.right += cellSize / 2;
        RectF rectLast = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        canvas.drawRect(rectLast, paint);

        rect.left = rect.right - cellSize / 2;
        rect.right += cellSize / 2;
        RectF ovalLast = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线
        canvas.drawArc(ovalLast, 270, 180, true, paint);
        /*---------------end----------------*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        this.getParent().requestDisallowInterceptTouchEvent(true);
        double tmpX;
        double tmpY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tmpX = screenW - mPointWidth - x;
                tmpY = yPoint + mPointHeight + cellSize - y;
                if (tmpX < 0 || tmpY < 0 || x < 0 || y < 0) {
                    isDraging = false;
                    return true;
                }
                isDraging = true;
                move(x);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                tmpX = screenW - mPointWidth - x;
                tmpY = yPoint + mPointHeight + cellSize - y;
                if (tmpX < 0 || tmpY < 0 || x < 0 || y < 0) {
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
                break;
        }
        return true;
    }

    private void move(float x) {
        text = (int)(x*100/screenW) + "%";
        xText = x + Math.abs((mPointWidth-mTextWidth)/2);
        xPoint = x;
        invalidate();
    }
}
