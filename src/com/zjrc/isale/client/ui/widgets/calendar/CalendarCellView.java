package com.zjrc.isale.client.ui.widgets.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.widgets.calendar.MonthCellDescriptor.RangeState;

/**
 * @项目名称：销售管家客户端 
 * @版本号：V2.00 
 * @创建者: 贺彬 
 * @功能描述：日历日单元格
 */
public class CalendarCellView extends TextView {

	private static final int[] STATE_SELECTABLE = { R.attr.state_selectable };
	private static final int[] STATE_CURRENT_MONTH = { R.attr.state_current_month };
	private static final int[] STATE_TODAY = { R.attr.state_today };
	private static final int[] STATE_RANGE_FIRST = { R.attr.state_range_first };
	private static final int[] STATE_RANGE_MIDDLE = { R.attr.state_range_middle };
	private static final int[] STATE_RANGE_LAST = { R.attr.state_range_last };
	private static final int[] STATE_WEEKEND_TEXT = { R.attr.state_weekend_text };

	private boolean isSelectable = false;
	private boolean isCurrentMonth = false;
	private boolean isToday = false;
	private boolean isCheckIn = false;

	private float dot_width;
	private float dot_height;
	private float dot_margin;

	public boolean isCheckIn() {
		return isCheckIn;
	}

	public void setCheckIn(boolean isCheckIn) {
		this.isCheckIn = isCheckIn;
	}

	public boolean isCheckOut() {
		return isCheckOut;
	}

	public void setCheckOut(boolean isCheckOut) {
		this.isCheckOut = isCheckOut;
	}

	public boolean isVacation() {
		return isVacation;
	}

	public void setVacation(boolean isVacation) {
		this.isVacation = isVacation;
	}

	private boolean isCheckOut = false;
	private boolean isVacation = false;

	private boolean isWeekend = false;

	public boolean isWeekend() {
		return isWeekend;
	}

	public void setWeekend(boolean isWeekend) {
		this.isWeekend = isWeekend;
	}

	private RangeState rangeState = RangeState.NONE;
	private Paint paint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
			| Paint.ANTI_ALIAS_FLAG);

	public CalendarCellView(Context context) {
		super(context);
	}

	public CalendarCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
		refreshDrawableState();
	}

	public void setCurrentMonth(boolean isCurrentMonth) {
		this.isCurrentMonth = isCurrentMonth;
		refreshDrawableState();
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
		refreshDrawableState();
	}

	public void setRangeState(MonthCellDescriptor.RangeState rangeState) {
		this.rangeState = rangeState;
		refreshDrawableState();
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 4);
		if (isSelectable) {
			mergeDrawableStates(drawableState, STATE_SELECTABLE);
		}
		if (isCurrentMonth) {
			setVisibility(View.VISIBLE);
			mergeDrawableStates(drawableState, STATE_CURRENT_MONTH);
		} else {
			setVisibility(View.GONE);
		}

		if (isToday) {
			mergeDrawableStates(drawableState, STATE_TODAY);
		}

		if (isWeekend) {
			mergeDrawableStates(drawableState, STATE_WEEKEND_TEXT);
		}

		if (rangeState == MonthCellDescriptor.RangeState.FIRST) {
			mergeDrawableStates(drawableState, STATE_RANGE_FIRST);
		} else if (rangeState == MonthCellDescriptor.RangeState.MIDDLE) {
			mergeDrawableStates(drawableState, STATE_RANGE_MIDDLE);
		} else if (rangeState == RangeState.LAST) {
			mergeDrawableStates(drawableState, STATE_RANGE_LAST);
		}

		dot_width = getWidth() / 10.0f;
		dot_height = dot_width;
		dot_margin = getWidth() / 15.0f;

		return drawableState;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isToday) {
			paint.setColor(getResources().getColor(
					R.color.calendar_dot_checkout));
			canvas.drawRect(0 + dot_width * 2, getTop() + dot_width * 2,
					getWidth() - dot_width * 2, getBottom() - dot_width * 2,
					paint);
		}
		super.onDraw(canvas);


			if (isCheckIn) {
				paint.setColor(getResources().getColor(
						R.color.calendar_dot_checkin));
				canvas.drawRect((getWidth() / 2) - dot_width*1.5f - dot_margin,
						getBottom() - dot_height * 1.5f, (getWidth() / 2)
								- dot_margin-dot_width/2, getBottom() - dot_height
								* 0.5f, paint);
			}
			if (isCheckOut) {
				paint.setColor(getResources().getColor(
						R.color.calendar_dot_checkout));
				canvas.drawRect((getWidth() / 2) - dot_width / 2, getBottom()
						- dot_height * 1.5f, (getWidth() / 2) + dot_width
						/2, getBottom() - dot_height * 0.5f,
						paint);
			}
        if (isVacation) {
            paint.setColor(getResources().getColor(
                    R.color.calendar_dot_vacation));
            canvas.drawRect((getWidth() / 2) + dot_width/2+dot_margin,
                    getBottom() - dot_height * 1.5f, (getWidth() / 2)
                            + dot_width*1.5f + dot_margin, getBottom()
                            - dot_height * 0.5f, paint);

        }

	}

}
