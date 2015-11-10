package com.zjrc.isale.client.ui.widgets.calendar;

import com.zjrc.isale.client.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：日历单元格布局
 */
public class CalendarGridView extends ViewGroup {
	private final Paint dividerPaint = new Paint();
	private int oldWidthMeasureSize;
	private int oldNumRows;
	private boolean isDrawLine = false;// 单元分割线

	public CalendarGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		dividerPaint
				.setColor(getResources().getColor(R.color.calendar_divider));
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (getChildCount() == 0) {
			((CalendarRowView) child).setIsHeaderRow(true);
		}
		super.addView(child, index, params);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		final ViewGroup row = (ViewGroup) getChildAt(1);
		int top = row.getTop();
		int bottom = getBottom();
		final int left = row.getChildAt(0).getLeft() + getLeft();
		if (isDrawLine) {
			canvas.drawLine(left, top, left, bottom, dividerPaint);
			for (int c = 0; c < 7; c++) {
				int x = left + row.getChildAt(c).getRight();
				canvas.drawLine(x, top, x, bottom, dividerPaint);
			}
		}

	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		final boolean retVal = super.drawChild(canvas, child, drawingTime);
		if (isDrawLine) {
			final int bottom = child.getBottom() - 1;
			canvas.drawLine(child.getLeft(), bottom, child.getRight(), bottom,
					dividerPaint);
		}
		return retVal;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
		if (oldWidthMeasureSize == widthMeasureSize) {
			setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
			return;
		}
		long start = System.currentTimeMillis();
		oldWidthMeasureSize = widthMeasureSize;
		int cellSize = widthMeasureSize / 7;
		widthMeasureSize = cellSize * 7;
		int totalHeight = 0;
		final int rowWidthSpec = makeMeasureSpec(widthMeasureSize, EXACTLY);
		final int rowHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			if (child.getVisibility() == View.VISIBLE) {
				if (c == 0) { // title
					measureChild(child, rowWidthSpec,
							makeMeasureSpec(cellSize, AT_MOST));
				} else {
					measureChild(child, rowWidthSpec, rowHeightSpec);
				}
				totalHeight += child.getMeasuredHeight();
			}
		}
		final int measuredWidth = widthMeasureSize + 2;

		setMeasuredDimension(measuredWidth, totalHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		top = 0;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			final int rowHeight = child.getMeasuredHeight();
			child.layout(left, top, right, top + rowHeight);
			top += rowHeight;
		}
	}

	public void setNumRows(int numRows) {
		if (oldNumRows != numRows) {
			oldWidthMeasureSize = 0;
		}
		oldNumRows = numRows;
	}

	public int getNumRows() {
		return oldNumRows;
	}

}
