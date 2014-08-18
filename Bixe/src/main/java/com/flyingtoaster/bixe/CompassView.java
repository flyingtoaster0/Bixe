package com.flyingtoaster.bixe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by tim on 2014-08-02.
 */
public class CompassView extends View {

    private int mArrowOffset = 24;
    private int mCompassRadius = 40;
    private int mArrowWidth = 6;
    private int mArrowHeight = 20;
    private int mFontSize;
    private int mBorderThickness = 4;
    private boolean mHasReceivedUpdate = false;


    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private Paint mDirectionPaint;
    private Paint shadowPaint;
    private Paint mBackgroundStroke;
    private Paint mForegroundStroke;
    private Paint mDirectionStroke;
    private int mBackgroundColor = Color.BLACK; //R.color.se_dark_gray;
    private int mForegroundColor = android.R.color.white;
    private int mPinColor = Color.RED;

    private int mMaxwidth = 400;

    private float mPinAngle = 0;
    private float mCompassAngle = 0;

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initResources();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources();
    }

    public CompassView(Context context) {
        super(context);
        initResources();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int h = getHeight();
        int w = getWidth();

        int cx = w/2;
        int cy = h/2;

        //float r = Math.min(h, w) / 2 - convertDpToPx(mCompassRadius);
        float r = convertDpToPx(mCompassRadius);
        //r = Math.min(r, convertDpToPx(3)/2);


        canvas.drawCircle(cx, cy, r + mBorderThickness, shadowPaint);
        canvas.drawCircle(cx, cy, r, mBackgroundStroke);


        //canvas.drawLine(cx, cy - 40, cx, cy - 120, mForegroundStroke);

        //canvas.rotate(-mCompassAngle, cx, cy);
        drawPercentPie(canvas, cx, cy, r);

        if(mHasReceivedUpdate) {
            drawPin(canvas, cx, cy, r, mPinAngle);
        }

        mHasReceivedUpdate = true;

    }

    private void initResources() {
        //mBackgroundPaint = getPaint(mBackgroundColor);

        mFontSize = (int)convertDpToPx(mCompassRadius)/3;
        mBorderThickness = (int)convertDpToPx(mBorderThickness);

        Paint bgPaint = new Paint();
        //int whiteColor = getContext().getResources().getColor(res);
        bgPaint.setColor(Color.WHITE);
        bgPaint.setAntiAlias(true);

        Paint pinPaint = new Paint();
        pinPaint.setColor(Color.RED);
        pinPaint.setAntiAlias(true);

        Paint dirPaint = new Paint();
        dirPaint.setColor(Color.WHITE);
        dirPaint.setTextSize(20);
        dirPaint.setAntiAlias(true);

        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStrokeWidth(10);
        shadowPaint.setStrokeMiter(10000);
        shadowPaint.setTextSize(mFontSize);

        mBackgroundPaint = bgPaint;

        mBackgroundStroke = new Paint(mBackgroundPaint);
        mBackgroundStroke.setStrokeWidth(convertDpToPx(mCompassRadius));
        mBackgroundStroke.setStrokeJoin(Join.ROUND);
        mBackgroundStroke.setStrokeMiter(10000);
        mBackgroundStroke.setStyle(Style.FILL);


        mForegroundPaint = pinPaint;

        mForegroundStroke = new Paint(mForegroundPaint);
        mForegroundStroke.setStrokeWidth(convertDpToPx(mCompassRadius));
        mForegroundStroke.setStrokeJoin(Join.ROUND);
        mForegroundStroke.setStrokeMiter(10000);
        mForegroundStroke.setStrokeWidth(10);
        mForegroundStroke.setStyle(Style.STROKE);


        mDirectionPaint = dirPaint;

        mDirectionStroke = new Paint(mDirectionPaint);
        mDirectionStroke.setStrokeWidth(convertDpToPx(mCompassRadius));
        mDirectionStroke.setStrokeJoin(Join.ROUND);
        mDirectionStroke.setStrokeMiter(10000);
        mDirectionStroke.setStrokeWidth(10);
        mDirectionStroke.setStyle(Style.STROKE);

        setMinimumHeight((int) convertDpToPx(mMaxwidth));
        setMinimumWidth((int) convertDpToPx(mMaxwidth));


        mArrowWidth = (int)convertDpToPx(mArrowWidth);
        mArrowHeight = (int)convertDpToPx(mArrowHeight);
        mArrowOffset = (int)convertDpToPx(mArrowOffset);
    }

    private void drawPin(Canvas canvas, float cx, float cy, float r, float degrees) {
        //canvas.drawPath(path, mForegroundStroke);

        canvas.rotate(degrees, cx, cy);
        float arrowCenterY = cy - mArrowOffset;

        Path arrow = new Path();
        arrow.moveTo(cx,arrowCenterY);
        arrow.lineTo(cx+mArrowWidth,arrowCenterY+mArrowHeight);
        arrow.lineTo(cx,arrowCenterY+mArrowHeight/4*3);
        arrow.lineTo(cx-mArrowWidth,arrowCenterY+mArrowHeight);
        arrow.close();

        canvas.drawPath(arrow, shadowPaint);
        //canvas.drawLine(cx, cy, cx, cy - 120, shadowPaint);
        canvas.rotate(-degrees, cx, cy);
    }

    private void drawPercentPie(Canvas canvas, float cx, float cy, float r) {
        float startAngle = 270.0f;

        // draw circle background

        //canvas.drawText("http://android-er.blogspot.com/", 0, 0, shadowPaint);


        RectF oval = new RectF(cx - r, cy - r, cx + r, cy + r);
        Path bp = new Path();
        bp.addArc(oval, startAngle, 360.0f);


        //canvas.drawLine(cx, cy - 40, cx, cy - 120, mForegroundStroke);

        //canvas.rotate(-mCompassAngle, cx, cy);


        canvas.drawText("N", cx - shadowPaint.measureText("N") / 2, cy - (int)(r/(1.5)), shadowPaint);
        //canvas.drawLine(cx, cy - 20, cx, cy - 60, shadowPaint);
        canvas.rotate(90, cx, cy);
        canvas.drawText("E", cx - shadowPaint.measureText("E") / 2, cy - (int)(r/(1.5)), shadowPaint);
        //canvas.drawLine(cx, cy - 20, cx, cy - 60, shadowPaint);
        canvas.rotate(90, cx, cy);
        canvas.drawText("S", cx - shadowPaint.measureText("S") / 2, cy - (int)(r/(1.5)), shadowPaint);
        //canvas.drawLine(cx, cy - 20, cx, cy - 60, shadowPaint);
        canvas.rotate(90, cx, cy);
        canvas.drawText("W", cx - shadowPaint.measureText("W") / 2, cy - (int)(r/(1.5)), shadowPaint);
        //canvas.drawLine(cx, cy - 20, cx, cy - 60, shadowPaint);
        canvas.rotate(90, cx, cy);

        //canvas.drawPath(bp, mBackgroundStroke);

        //canvas.rotate(mCompassAngle, cx, cy);

        // calculate the percentage arc
        //Path p = new Path();
        //float sweepAngle = (float) (360.0f * mPercent);
        //p.addArc(oval, startAngle, sweepAngle);
        //canvas.drawPath(p, mPercentStroke);
    }

    private float convertDpToPx(int dp) {
        return (float) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics()));
    }

    private Paint getPaint(int res) {
        Paint paint = new Paint();
        //int whiteColor = getContext().getResources().getColor(res);
        //paint.setColor(whiteColor);
        //paint.setAntiAlias(true);
        return paint;
    }

    public void setPinAngle(float degrees) {
        mPinAngle = degrees;
    }

    public void setCompassAngle(float degrees) {
        mCompassAngle = degrees;
    }

}
