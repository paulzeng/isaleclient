package com.zjrc.isale.client.ui.widgets.calendar;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：日历行布局
 */
public class CalendarRowView extends ViewGroup implements View.OnClickListener {
	private boolean isHeaderRow;
	private CalendarView.CellClickListener listener;
	private int cellSize;

	public CalendarRowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		child.setOnClickListener(this);
		super.addView(child, index, params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		long start = System.currentTimeMillis();
		final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
		cellSize = totalWidth / 7;
		int cellWidthSpec = makeMeasureSpec(cellSize, EXACTLY);
		int cellHeightSpec = isHeaderRow ? makeMeasureSpec(cellSize, AT_MOST)
				: cellWidthSpec;
		int rowHeight = 0;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			child.measure(cellWidthSpec, cellHeightSpec);
			// The row height is the height of the tallest cell.
			if (child.getMeasuredHeight() > rowHeight) {
				rowHeight = child.getMeasuredHeight();
			}
		}
		final int widthWithPadding = totalWidth + getPaddingLeft()
				+ getPaddingRight();
		final int heightWithPadding = rowHeight + getPaddingTop()
				+ getPaddingBottom();
		setMeasuredDimension(widthWithPadding, heightWithPadding);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		long start = System.currentTimeMillis();
		int cellHeight = bottom - top;
		for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
			final View child = getChildAt(c);
			child.layout(c * cellSize, 0, (c + 1) * cellSize, cellHeight);
		}
	}

	public void setIsHeaderRow(boolean isHeaderRow) {
		this.isHeaderRow = isHeaderRow;
	}

	@Override
	public void onClick(View v) {
		// Header rows don't have a click listener
		if (listener != null) {
			listener.handleClick(v, (MonthCellDescriptor) v.getTag());
		}
	}

	public void setCellClickListener(CalendarView.CellClickListener listener) {
		this.listener = listener;
	}
}
