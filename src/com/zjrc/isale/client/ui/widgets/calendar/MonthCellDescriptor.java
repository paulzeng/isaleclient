package com.zjrc.isale.client.ui.widgets.calendar;

import java.util.Date;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：日历日期描述信息
 */
class MonthCellDescriptor {
	public enum RangeState {
		NONE, FIRST, MIDDLE, LAST
	}

	private final Date date;
	private final int value;
	private final boolean isCurrentMonth;
	private boolean isSelected;
	private final boolean isToday;
	private final boolean isSelectable;
	private boolean isCheckIn;
	private boolean isCheckOut;

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

	private boolean isVacation;

	private boolean isWeekend;

	public boolean isWeekend() {
		return isWeekend;
	}

	public void setWeekend(boolean isWeekend) {
		this.isWeekend = isWeekend;
	}

	private RangeState rangeState;

	MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable,
			boolean selected, boolean today, int value, RangeState rangeState) {
		this.date = date;
		isCurrentMonth = currentMonth;
		isSelectable = selectable;
		isSelected = selected;
		isToday = today;
		this.isCheckIn = false;
		this.isCheckOut = false;
		this.isVacation = false;
		this.isWeekend = false;
		this.value = value;
		this.rangeState = rangeState;
	}

	MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable,
			boolean selected, boolean today, boolean isCheckIn,
			boolean isCheckOut, boolean isVacation, boolean isWeekend,
			int value, RangeState rangeState) {
		this.date = date;
		isCurrentMonth = currentMonth;
		isSelectable = selectable;
		isSelected = selected;
		isToday = today;
		this.isCheckIn = isCheckIn;
		this.isCheckOut = isCheckOut;
		this.isVacation = isVacation;
		this.value = value;
		this.rangeState = rangeState;
		this.isWeekend = isWeekend;
	}

	public Date getDate() {
		return date;
	}

	public boolean isCurrentMonth() {
		return isCurrentMonth;
	}

	public boolean isSelectable() {
		return isSelectable;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public boolean isToday() {
		return isToday;
	}

	public RangeState getRangeState() {
		return rangeState;
	}

	public void setRangeState(RangeState rangeState) {
		this.rangeState = rangeState;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "MonthCellDescriptor{" + "date=" + date + ", value=" + value
				+ ", isCurrentMonth=" + isCurrentMonth + ", isSelected="
				+ isSelected + ", isToday=" + isToday + ", isSelectable="
				+ isSelectable + ", isCheckIn=" + isCheckIn + ", isCheckOut="
				+ isCheckOut + ",isVacation=" + isVacation + ", rangeState="
				+ rangeState + '}';
	}
}
