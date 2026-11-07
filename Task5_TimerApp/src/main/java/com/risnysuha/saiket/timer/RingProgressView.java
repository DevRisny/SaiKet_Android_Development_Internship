package com.risnysuha.saiket.timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

/** A circular progress ring that fills once per minute, drawn with the brand blue to cyan gradient. */
public class RingProgressView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();
    private final float stroke;
    private float progress;

    public RingProgressView(Context context) {
        this(context, null);
    }

    public RingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float density = getResources().getDisplayMetrics().density;
        stroke = 14 * density;

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(stroke);
        trackPaint.setColor(ContextCompat.getColor(context, R.color.brand_blue_soft));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(stroke);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        dotPaint.setColor(Color.WHITE);
        dotPaint.setShadowLayer(4 * density, 0, density, 0x553157FF);
        setLayerType(LAYER_TYPE_SOFTWARE, null); // needed for the dot's soft shadow

        tickPaint.setColor(ContextCompat.getColor(context, R.color.outline));
        tickPaint.setStrokeWidth(2 * density);
        tickPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /** 0 to 1. */
    public void setProgress(float value) {
        progress = Math.max(0f, Math.min(1f, value));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            size = MeasureSpec.getSize(widthMeasureSpec);
        }
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float inset = stroke / 2f + 8 * getResources().getDisplayMetrics().density;
        arcBounds.set(inset, inset, w - inset, h - inset);
        SweepGradient gradient = new SweepGradient(w / 2f, h / 2f,
                new int[]{ContextCompat.getColor(getContext(), R.color.brand_blue),
                        ContextCompat.getColor(getContext(), R.color.brand_cyan),
                        ContextCompat.getColor(getContext(), R.color.brand_blue)},
                new float[]{0f, 0.5f, 1f});
        Matrix rotate = new Matrix();
        rotate.setRotate(-90f, w / 2f, h / 2f);
        gradient.setLocalMatrix(rotate);
        progressPaint.setShader(gradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = arcBounds.centerX();
        float cy = arcBounds.centerY();
        float radius = arcBounds.width() / 2f;

        // 60 small ticks, one per second, just inside the ring.
        float inner = radius - stroke / 2f - 10 * getResources().getDisplayMetrics().density;
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6 - 90);
            float len = i % 5 == 0 ? inner * 0.07f : inner * 0.035f;
            canvas.drawLine(
                    cx + (float) Math.cos(angle) * inner, cy + (float) Math.sin(angle) * inner,
                    cx + (float) Math.cos(angle) * (inner - len), cy + (float) Math.sin(angle) * (inner - len),
                    tickPaint);
        }

        canvas.drawArc(arcBounds, 0, 360, false, trackPaint);
        if (progress > 0f) {
            canvas.drawArc(arcBounds, -90, 360 * progress, false, progressPaint);
        }
        double end = Math.toRadians(360 * progress - 90);
        canvas.drawCircle(cx + (float) Math.cos(end) * radius, cy + (float) Math.sin(end) * radius,
                stroke * 0.36f, dotPaint);
    }
}
