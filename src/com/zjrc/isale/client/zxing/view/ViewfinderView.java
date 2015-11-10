/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjrc.isale.client.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 25L;
    private static final int OPAQUE = 0xFF;

    private static final int SPEED_DISTANCE = 8;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private boolean isFirst;
    private int slideTop;

    private Bitmap qrLineBitmap;//扫描线
    private int qrHeight;//扫描线的高
    private Rect qrSrc;
    private Rect qrDst;

    private Bitmap bgBitmap;//扫描底
    private Rect bgSrc;
    private Rect bgDst;

    private Bitmap borderBitmap;//扫描框
    private Rect borderSrc;
    private Rect borderDst;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();

        qrLineBitmap = BitmapFactory.decodeResource(resources, R.drawable.v2_barcode_line);
        qrHeight = qrLineBitmap.getHeight();
        qrSrc = new Rect(0, 0, qrLineBitmap.getWidth(), qrHeight);
        qrDst = new Rect();

        bgBitmap = BitmapFactory.decodeResource(resources, R.drawable.v2_barcode_bg);
        bgSrc = new Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());

        borderBitmap = BitmapFactory.decodeResource(resources, R.drawable.v2_barcode_border);
        borderSrc = new Rect(0, 0, borderBitmap.getWidth(), borderBitmap.getHeight());

        maskColor = resources.getColor(R.color.v2_barcode_bg);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<ResultPoint>(5);
    }

    @Override
    public void onDraw(Canvas canvas) {
        CameraManager cm = CameraManager.get();
        if (cm != null) {
            Rect frame = cm.getFramingRect();
            if (frame != null) {
                //初始化扫描线滑动的最上边
                if (!isFirst) {
                    isFirst = true;
                    slideTop = frame.top;
                }

                int width = canvas.getWidth();
                int height = canvas.getHeight();

                // Draw the exterior (i.e. outside the framing rect) darkened
                paint.setColor(resultBitmap != null ? resultColor : maskColor);
                canvas.drawRect(0, 0, width, frame.top, paint);
                canvas.drawRect(0, frame.top, frame.left, frame.bottom, paint);
                canvas.drawRect(frame.right, frame.top, width, frame.bottom, paint);
                canvas.drawRect(0, frame.bottom, width, height, paint);

                if (resultBitmap != null) {
                    // Draw the opaque result bitmap over the scanning rectangle
                    paint.setAlpha(OPAQUE);
                    canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
                } else {
                    //绘制扫描线,每次刷新界面，扫描线往下移动SPEED_DISTANCE
                    slideTop += SPEED_DISTANCE;
                    if (slideTop >= frame.bottom) {
                        slideTop = frame.top;
                    }
                    qrDst.set(frame.left, slideTop, frame.right, slideTop + qrHeight);
                    canvas.drawBitmap(qrLineBitmap, qrSrc, qrDst, null);
                    //绘制扫描底
                    if (bgDst == null) {
                        bgDst = new Rect(frame.left, frame.top, frame.right, frame.bottom);
                    }
                    canvas.drawBitmap(bgBitmap, bgSrc, bgDst, null);
                    //绘制扫描框
                    if (borderDst == null) {
                        borderDst = new Rect(frame.left, frame.top, frame.right, frame.bottom);
                    }
                    canvas.drawBitmap(borderBitmap, borderSrc, borderDst, null);

                    Collection<ResultPoint> currentPossible = possibleResultPoints;
                    Collection<ResultPoint> currentLast = lastPossibleResultPoints;

                    if (currentPossible.isEmpty()) {
                        lastPossibleResultPoints = null;
                    } else {
                        possibleResultPoints.clear();
                        lastPossibleResultPoints = currentPossible;
                        paint.setAlpha(OPAQUE);
                        paint.setColor(resultPointColor);
                        for (ResultPoint point : currentPossible) {
                            canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                        }
                    }
                    if (currentLast != null) {
                        paint.setAlpha(OPAQUE / 2);
                        paint.setColor(resultPointColor);
                        for (ResultPoint point : currentLast) {
                            canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                        }
                    }

                    // Request another update at the animation interval, but only repaint the laser line,
                    // not the entire viewfinder mask.
                    postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
                }
            }
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
