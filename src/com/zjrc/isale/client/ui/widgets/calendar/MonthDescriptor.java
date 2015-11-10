package com.zjrc.isale.client.ui.widgets.calendar;

import java.util.Date;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：日历月份描述信息
 */
class MonthDescriptor {
	private final int month;
	private final int year;
	private final Date date;
	private String label;

	public MonthDescriptor(int month, int year, Date date, String label) {
		this.month = month;
		this.year = year;
		this.date = date;
		this.label = label;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public Date getDate() {
		return date;
	}

	public String getLabel() {
		return label;
	}

	void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "MonthDescriptor{" + "label='" + label + '\'' + ", month="
				+ month + ", year=" + year + '}';
	}
}
