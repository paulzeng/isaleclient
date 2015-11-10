package com.zjrc.isale.client.ui.widgets;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.widgets.wheelview.NumericWheelAdapter;
import com.zjrc.isale.client.ui.widgets.wheelview.OnWheelChangedListener;
import com.zjrc.isale.client.ui.widgets.wheelview.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDatePicker extends Dialog {
	private Context context;
	private static int START_YEAR = 1990, END_YEAR = 2100;
	private String title;
	private View.OnClickListener positiveListener;
	private View.OnClickListener negtiveListener;
	private String date;

	public CustomDatePicker(Context context, String title,
			View.OnClickListener positiveListener,
			View.OnClickListener negtiveListener, String date) {
		super(context);
		this.context = context;
		this.title = title;
		this.positiveListener = positiveListener;
		this.negtiveListener = negtiveListener;
		this.date = date;
	}

	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private int year;
	private int month;
	private int day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.time_layout);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		((TextView) findViewById(R.id.tv_title)).setText(title);
		showDateTimePicker();
	}

	private void splitDate(String date) {
		String[] times = date.split("-");
		year = Integer.valueOf(times[0]);
		month = Integer.valueOf(times[1])-1;
		day = Integer.valueOf(times[2]);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	private void showDateTimePicker() {
		if (date != null && !date.equalsIgnoreCase("")) {
			splitDate(date);
		} else {
			Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DATE);
		}

		Log.i("info", "date:" + date + " year:" + year + " month:" + month
				+ " day:" + day);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		this.setTitle("请选择日期与时间");
		// 年
		wv_year = (WheelView) findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);
		// 日
		wv_day = (WheelView) findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 16;
		textSize = dip2px(context, textSize);

		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		Button btn_sure = (Button) findViewById(R.id.btn_alertdialog_positive);
		Button btn_cancel = (Button) findViewById(R.id.btn_alertdialog_negative);

		// 确定
		btn_sure.setOnClickListener(positiveListener);
		// // TODO Auto-generated method stub
		// // 如果是个数,则显示为"02"的样式
		// String parten = "00";
		// DecimalFormat decimal = new DecimalFormat(parten);
		// // 设置日期的显示
		// // tv_time.setText((wv_year.getCurrentItem() + START_YEAR) + "-"
		// // + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
		// // + decimal.format((wv_day.getCurrentItem() + 1)) + " "
		// // + decimal.format(wv_hours.getCurrentItem()) + ":"
		// // + decimal.format(wv_mins.getCurrentItem()));

		// CustomDatePicker.this.dismiss();
		// 取消
		btn_cancel.setOnClickListener(negtiveListener);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// // 如果是个数,则显示为"02"的样式
	// String parten = "00";
	// DecimalFormat decimal = new DecimalFormat(parten);
	// // 设置日期的显示
	// // tv_time.setText((wv_year.getCurrentItem() + START_YEAR) + "-"
	// // + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
	// // + decimal.format((wv_day.getCurrentItem() + 1)) + " "
	// // + decimal.format(wv_hours.getCurrentItem()) + ":"
	// // + decimal.format(wv_mins.getCurrentItem()));

	// CustomDatePicker.this.dismiss();

	public String getYear() {
		return String.valueOf(wv_year.getCurrentItem() + START_YEAR);
	}

	public String getMonth() {
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		return decimal.format((wv_month.getCurrentItem() + 1));
	}

	public String getDay() {
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		return decimal.format((wv_day.getCurrentItem() + 1));
	}
}
