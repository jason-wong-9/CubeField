package com.jasonkcwong.projectionmaze;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.jasonkcwong.projectionmaze.graphics.RenderQueueItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Owner on 2/28/2016.
 */
public class GameView extends View {

    private Paint mPaint;
    private Path mPath;
    private List<RenderQueueItem> mRenderQueue = new ArrayList<>();

    public void setRenderQueue(List<RenderQueueItem> renderQueue) {
        mRenderQueue = renderQueue;
        invalidate();
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();

        mPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(255, 0, 0, 0);

        for (RenderQueueItem item : mRenderQueue) {
            int[] x = item.polygon.xpoints;
            int[] y = item.polygon.ypoints;
            mPath.reset();
            mPath.moveTo(x[0], y[0]);
            mPath.lineTo(x[1], y[1]);
            mPath.lineTo(x[2], y[2]);
            mPath.lineTo(x[3], y[3]);
            mPath.lineTo(x[0], y[0]);
            mPath.close();

            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPath, mPaint);

            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath, mPaint);
        }
    }

}
