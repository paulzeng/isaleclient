package com.zjrc.isale.client.ui.widgets.calendar;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjrc.isale.client.R;
import com.zjrc.isale.client.ui.widgets.calendar.MonthCellDescriptor.RangeState;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：日历界面
 */
public class CalendarView extends LinearLayout {
	private TextView title;
	private ImageView preMonth;
	private ImageView nextMonth;
	CalendarGridView grid;
	private CellClickListener cellClickListener = new CellClickedListener();
	private OnDateSelectedListener dateListener;
	private OnMonthChangedListener monthChangedListener;
	private OnInvalidDateSelectedListener invalidDateListener = new DefaultOnInvalidDateSelectedListener();
	final List<MonthCellDescriptor> selectedCellDess = new ArrayList<MonthCellDescriptor>();
	final List<Calendar> selectedCals = new ArrayList<Calendar>();
	final Map<String, List<Calendar>> checkinCalsMap = new HashMap<String, List<Calendar>>();
	final Map<String, List<Calendar>> checkoutCalsMap = new HashMap<String, List<Calendar>>();
	final Map<String, List<Calendar>> vacationCalsMap = new HashMap<String, List<Calendar>>();

	MonthDescriptor currentMonthDes;
	private List<List<MonthCellDescriptor>> currentMonthCells;
	private Locale locale;
	private Calendar today;
	private Calendar monthCounter;
	private DateFormat monthNameFormat;
	private Context context;
	private View root;

	public CalendarView(Context context) {
		super(context);
		this.context = context;
		root = LayoutInflater.from(context).inflate(R.layout.month_inner, this,
				true);
		onFinishInflate();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		root = LayoutInflater.from(context).inflate(R.layout.month_inner, this,
				true);
	}

	private void initData(Context context) {
		locale = Locale.getDefault();
		today = Calendar.getInstance(locale);
		monthCounter = Calendar.getInstance(locale);

		monthNameFormat = new SimpleDateFormat(
				context.getString(R.string.month_name_format), locale);

		final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
		String weekDay[] = { "日", "一", "二", "三", "四", "五", "六" };
		int firstDayOfWeek = today.getFirstDayOfWeek();
		final CalendarRowView headerRow = (CalendarRowView) grid.getChildAt(0);
		for (int offset = 0; offset < 7; offset++) {
			today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
			final TextView textView = (TextView) headerRow.getChildAt(offset);
			if (offset == 0 || offset == 6) {
				textView.setTextColor(Color.RED);
			}
			textView.setText(weekDay[offset]);
		}
		today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);

		initAndUpdateCalendar();
	}

	private void initAndUpdateCalendar() {
		Date date = monthCounter.getTime();
		MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH),
				monthCounter.get(YEAR), date, monthNameFormat.format(date));
		List<List<MonthCellDescriptor>> cells = getMonthCells(month,
				monthCounter);
		currentMonthDes = month;
		currentMonthCells = cells;
		updateCellViews(month, cells);
	}

	public void updateDataSetChanged() {
		updateCellViews(currentMonthDes, currentMonthCells);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		title = (TextView) findViewById(R.id.title);
		preMonth = (ImageView) findViewById(R.id.btn_pre_month);
		nextMonth = (ImageView) findViewById(R.id.btn_next_month);
		grid = (CalendarGridView) findViewById(R.id.calendar_grid);
		preMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previousMonth();
			}
		});
		nextMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextMonth();
			}
		});
		initData(context);
	}

	public void nextMonth() {
		monthCounter.add(MONTH, 1);
		initAndUpdateCalendar();
		if (monthChangedListener != null) {
			monthChangedListener.onChangedToNextMonth(monthCounter.getTime());
		}
		invalidate();
	}

	public void previousMonth() {
		monthCounter.add(MONTH, -1);
		initAndUpdateCalendar();
		if (monthChangedListener != null) {
			monthChangedListener.onChangedToPreMonth(monthCounter.getTime());
		}
		invalidate();
	}

	public void markDatesOfMonth(int year, int month, boolean isCheckin,
			boolean isCheckout, boolean isVacation, Integer... dates) {
		List<Integer> dateList = Arrays.asList(dates);
		List<Calendar> calendars = new ArrayList<Calendar>(dateList.size() + 5);
		Calendar cal = Calendar.getInstance(locale);
		for (Integer day : dateList) {
			cal = Calendar.getInstance(locale);
			cal.set(year, month, day, 0, 0, 0);

			calendars.add(cal);
		}
		String monthLabel = monthNameFormat.format(cal.getTime());
		if (isCheckin) {
			checkinCalsMap.put(monthLabel, calendars);
		}
		if (isCheckout) {
			checkoutCalsMap.put(monthLabel, calendars);
		}
		if (isVacation) {
			vacationCalsMap.put(monthLabel, calendars);
		}
		if (currentMonthDes.getLabel().equals(monthLabel)) {
			initAndUpdateCalendar();
		}
	}

	public void markDatesOfMonth(int year, int month,
			List<List<Calendar>> calendars) {
		Calendar cal = Calendar.getInstance(locale);
		cal.set(year, month, 1);
		String monthLabel = monthNameFormat.format(cal.getTime());
		if (calendars.get(0) != null && calendars.get(0).size() > 0) {
			checkinCalsMap.put(monthLabel, calendars.get(0));
		}
		if (calendars.get(1) != null && calendars.get(1).size() > 0) {
			checkoutCalsMap.put(monthLabel, calendars.get(1));
		}
		if (calendars.get(2) != null && calendars.get(2).size() > 0) {
			vacationCalsMap.put(monthLabel, calendars.get(2));
		}
		if (currentMonthDes.getLabel().equals(monthLabel)) {
			initAndUpdateCalendar();
		}
	}

	public List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month,
			Calendar startCal) {
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(startCal.getTime());
		List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
		cal.set(DAY_OF_MONTH, 1);
		int firstDayOfWeek = cal.get(DAY_OF_WEEK);
		int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
		if (offset > 0) {
			offset -= 7;
		}
		cal.add(Calendar.DATE, offset);

		while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month
				.getYear()) //
				&& cal.get(YEAR) <= month.getYear()) {
			List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
			cells.add(weekCells);
			for (int c = 0; c < 7; c++) {
				Date date = cal.getTime();
				boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
				boolean isSelected = isCurrentMonth
						&& containsDate(selectedCals, cal);
				boolean isSelectable = isCurrentMonth;
				boolean isToday = sameDate(cal, today);

				boolean isCheckIn = checkinCalsContainsDate(cal);
				boolean isCheckOut = checkoutCalsContainsDate(cal);
				boolean isVacation = vacationCalsContainsDate(cal);

				int value = cal.get(DAY_OF_MONTH);

				MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;

				boolean isWeekend = false;
				if (c == 0 || c == 6) {
					isWeekend = true;
				}
				MonthCellDescriptor monthCellDes = new MonthCellDescriptor(
						date, isCurrentMonth, isSelectable, isSelected,
						isToday, isCheckIn, isCheckOut, isVacation, isWeekend,
						value, rangeState);
				weekCells.add(monthCellDes);
				if (isSelected && !containCell(selectedCellDess, monthCellDes)) {
					selectedCellDess.add(monthCellDes);
				}
				cal.add(DATE, 1);
			}
		}
		return cells;
	}

	private boolean checkinCalsContainsDate(Calendar cal) {
		List<Calendar> selectedCals = checkinCalsMap.get(monthNameFormat
				.format(cal.getTime()));
		return (selectedCals != null) ? containsDate(selectedCals, cal) : false;
	}

	private boolean checkoutCalsContainsDate(Calendar cal) {
		List<Calendar> selectedCals = checkoutCalsMap.get(monthNameFormat
				.format(cal.getTime()));
		return (selectedCals != null) ? containsDate(selectedCals, cal) : false;
	}

	private boolean vacationCalsContainsDate(Calendar cal) {
		List<Calendar> selectedCals = vacationCalsMap.get(monthNameFormat
				.format(cal.getTime()));
		return (selectedCals != null) ? containsDate(selectedCals, cal) : false;
	}

	private boolean isPreMonthDate(Date date) {
		boolean isPre = false;
		Calendar dateCal = Calendar.getInstance(locale);
		dateCal.setTime(date);
		if (dateCal.get(YEAR) < monthCounter.get(YEAR)) {
			isPre = true;
		} else if (dateCal.get(YEAR) == monthCounter.get(YEAR)
				&& dateCal.get(MONTH) < monthCounter.get(MONTH)) {
			isPre = true;
		}
		return isPre;
	}

	private boolean isCurrentMonthDate(Date date) {
		boolean isCurrent = false;
		Calendar dateCal = Calendar.getInstance(locale);
		dateCal.setTime(date);
		if (dateCal.get(YEAR) == monthCounter.get(YEAR)
				&& dateCal.get(MONTH) == monthCounter.get(MONTH)) {
			isCurrent = true;
		}
		return isCurrent;
	}

	private boolean isNextMonthDate(Date date) {
		boolean isNext = false;
		isNext = !isPreMonthDate(date) && !isCurrentMonthDate(date);
		return isNext;
	}

	public void updateCellViews(MonthDescriptor month,
			List<List<MonthCellDescriptor>> cells) {
		title.setText(month.getLabel());
		final int numRows = cells.size();
		grid.setNumRows(numRows);
		for (int i = 0; i < 6; i++) {
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
			weekRow.setCellClickListener(cellClickListener);
			if (i < numRows) {
				weekRow.setVisibility(VISIBLE);
				List<MonthCellDescriptor> week = cells.get(i);
				for (int c = 0; c < week.size(); c++) {
					MonthCellDescriptor cell = week.get(c);
					CalendarCellView cellView = (CalendarCellView) weekRow
							.getChildAt(c);

					cellView.setText(Integer.toString(cell.getValue()));
					// cellView.setEnabled(cell.isCurrentMonth());
					cellView.setSelectable(cell.isSelectable());
					cellView.setSelected(cell.isSelected());
					cellView.setCurrentMonth(cell.isCurrentMonth());
					cellView.setCheckIn(cell.isCheckIn());
					cellView.setCheckOut(cell.isCheckOut());
					cellView.setVacation(cell.isVacation());
					cellView.setWeekend(cell.isWeekend());
					cellView.setToday(cell.isToday());
					cellView.setRangeState(cell.getRangeState());
					cellView.setTag(cell);
				}
			} else {
				for (int c = 0; c < 7; c++) {//当该行无信息全部置空，本可以直接GONE掉的，但是部分手机rom在gone掉该行时会有残影，所以暂时置空留白了
					MonthCellDescriptor cell = new MonthCellDescriptor(
							new Date(), false, false, false, false, false,
							false, false, false, 0, RangeState.NONE);
					CalendarCellView cellView = (CalendarCellView) weekRow
							.getChildAt(c);
					cellView.setText("");
					// cellView.setEnabled(cell.isCurrentMonth());
					cellView.setSelectable(cell.isSelectable());
					cellView.setSelected(cell.isSelected());
					cellView.setCurrentMonth(cell.isCurrentMonth());
					cellView.setCheckIn(cell.isCheckIn());
					cellView.setCheckOut(cell.isCheckOut());
					cellView.setVacation(cell.isVacation());
					cellView.setWeekend(cell.isWeekend());
					cellView.setToday(cell.isToday());
					cellView.setRangeState(cell.getRangeState());
					cellView.setTag(cell);
				}
			}
			weekRow.invalidate();
		}
		grid.invalidate();
		invalidate();
	}

	private class CellClickedListener implements CalendarView.CellClickListener {
		@Override
		public void handleClick(View v, MonthCellDescriptor cell) {
			Date clickedDate = cell.getDate();

			if (!cell.isCurrentMonth() || !cell.isSelectable()) {
				if (invalidDateListener != null) {
					invalidDateListener.onInvalidDateSelected(clickedDate);
					if (!cell.isCurrentMonth() && isPreMonthDate(clickedDate)) {
						invalidDateListener.onPreMonthDateSelected(clickedDate);
					} else if (!cell.isCurrentMonth()
							&& isNextMonthDate(clickedDate)) {
						invalidDateListener
								.onNextMonthDateSelected(clickedDate);
					}
				}
			} else {
				boolean wasSelected = doSingleSelectDate(cell);
				if (dateListener != null) {
					if (wasSelected) {
						dateListener.onDateSelected(clickedDate);
					} else {
						dateListener.onDateUnselected(clickedDate);
					}
				}
			}
		}
	}

	public boolean selectDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException(
					"Selected date must be non-null.");
		}
		if (date.getTime() == 0) {
			throw new IllegalArgumentException(
					"Selected date must be non-zero.  " + date);
		}
		MonthCellDescriptor monthCellDescriptor = getMonthCellDesByDate(date);
		if (monthCellDescriptor == null) {
			Calendar newlySelCal = Calendar.getInstance(locale);
			newlySelCal.setTime(date);
			clearOldSelections();
			selectedCals.add(newlySelCal);

			monthCounter.setTime(date);
			initAndUpdateCalendar();

			if (monthChangedListener != null) {
				if (monthCounter.get(MONTH) < today.get(MONTH)) {
					monthChangedListener.onChangedToPreMonth(monthCounter
							.getTime());
				} else {
					monthChangedListener.onChangedToNextMonth(monthCounter
							.getTime());
				}
			}
			return false;
		}
		boolean wasSelected = doSingleSelectDate(monthCellDescriptor);
		return wasSelected;
	}

	private MonthCellDescriptor getMonthCellDesByDate(Date date) {
		Calendar searchCal = Calendar.getInstance(locale);
		searchCal.setTime(date);
		Calendar actCal = Calendar.getInstance(locale);

		for (List<MonthCellDescriptor> weekCells : currentMonthCells) {
			for (MonthCellDescriptor actCell : weekCells) {
				actCal.setTime(actCell.getDate());
				if (sameDate(actCal, searchCal) && actCell.isCurrentMonth()) {
					return actCell;
				}
			}
		}
		return null;
	}

	private boolean doSingleSelectDate(final MonthCellDescriptor cell) {
		boolean wasSelected = false;
		Calendar newlySelCal = Calendar.getInstance(locale);
		newlySelCal.setTime(cell.getDate());

		if (selectedCals.size() > 0 && containsDate(selectedCals, newlySelCal)) {
			clearOldSelections();
			cell.setSelected(false);
			wasSelected = false;
		} else {
			clearOldSelections();

			selectedCals.add(newlySelCal);
			selectedCellDess.add(cell);
			cell.setSelected(true);
			wasSelected = true;
		}
		updateDataSetChanged();
		return wasSelected;
	}

	private static boolean containsDate(List<Calendar> selectedCals,
			Calendar cal) {
		for (Calendar selectedCal : selectedCals) {
			if (sameDate(cal, selectedCal)) {
				return true;
			}
		}
		return false;
	}

	private static boolean sameDate(Calendar cal, Calendar selectedDate) {
		return cal.get(MONTH) == selectedDate.get(MONTH)
				&& cal.get(YEAR) == selectedDate.get(YEAR)
				&& cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
	}

	private boolean containCell(List<MonthCellDescriptor> selectedCells,
			final MonthCellDescriptor cell) {
		for (MonthCellDescriptor selectedCell : selectedCells) {
			if (selectedCell.equals(cell)) {
				return true;
			}
		}
		return false;
	}

	private void clearOldSelections() {
		for (MonthCellDescriptor selectedCell : selectedCellDess) {
			selectedCell.setSelected(false);
		}
		selectedCellDess.clear();
		selectedCals.clear();
	}

	public interface CellClickListener {
		void handleClick(View v, MonthCellDescriptor cell);
	}

	public void setOnDateSelectedListener(OnDateSelectedListener listener) {
		dateListener = listener;
	}

	public void setOnMonthChangedListener(
			OnMonthChangedListener monthChangedListener) {
		this.monthChangedListener = monthChangedListener;
	}

	public void setOnInvalidDateSelectedListener(
			OnInvalidDateSelectedListener listener) {
		invalidDateListener = listener;
	}

	public interface OnDateSelectedListener {
		void onDateSelected(Date date);

		void onDateUnselected(Date date);
	}

	public interface OnInvalidDateSelectedListener {
		void onInvalidDateSelected(Date date);

		void onPreMonthDateSelected(Date date);

		void onNextMonthDateSelected(Date date);
	}

	public interface OnMonthChangedListener {
		void onChangedToPreMonth(Date dateOfMonth);

		void onChangedToNextMonth(Date dateOfMonth);
	}

	private class DefaultOnInvalidDateSelectedListener implements
			OnInvalidDateSelectedListener {
		@Override
		public void onInvalidDateSelected(Date date) {

		}

		@Override
		public void onPreMonthDateSelected(Date date) {
			previousMonth();
		}

		@Override
		public void onNextMonthDateSelected(Date date) {
			nextMonth();
		}
	}
}
