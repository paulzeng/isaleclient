package com.zjrc.isale.client.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.CommonUtils;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.ui.activity.AttenceListActivity;
import com.zjrc.isale.client.ui.activity.AttenceSubmitActivity;
import com.zjrc.isale.client.ui.activity.LoginActivity;
import com.zjrc.isale.client.ui.activity.MainActivity;
import com.zjrc.isale.client.ui.activity.PlanDelayDetailActivity;
import com.zjrc.isale.client.ui.activity.PlanDelaySubmitActivity;
import com.zjrc.isale.client.ui.activity.PlanDetailActivity;
import com.zjrc.isale.client.ui.activity.PlanListActivity;
import com.zjrc.isale.client.ui.activity.PlanPatrolSubmitActivity;
import com.zjrc.isale.client.ui.activity.PlanSubmitActivity;
import com.zjrc.isale.client.ui.activity.SuggestionDetailActivity;
import com.zjrc.isale.client.ui.activity.SuggestionListActivity;
import com.zjrc.isale.client.ui.activity.SuggestionSubmitActivity;
import com.zjrc.isale.client.ui.activity.TravelDetailActivity;
import com.zjrc.isale.client.ui.activity.TravelExecutionActivity;
import com.zjrc.isale.client.ui.activity.TravelListActivity;
import com.zjrc.isale.client.ui.activity.TravelSubmitActivity;
import com.zjrc.isale.client.ui.activity.TravelSubmitDetailActivity;
import com.zjrc.isale.client.ui.activity.VacationListActivity;
import com.zjrc.isale.client.ui.activity.VacationSubmitActivity;
import com.zjrc.isale.client.ui.activity.WorkItemActivity;
import com.zjrc.isale.client.ui.activity.WorkListActivity;
import com.zjrc.isale.client.ui.activity.WorkReportSubmitActivity;
import com.zjrc.isale.client.ui.widgets.CustomDatePicker;
import com.zjrc.isale.client.ui.widgets.calendar.CalendarView;
import com.zjrc.isale.client.ui.widgets.calendar.CalendarView.OnDateSelectedListener;
import com.zjrc.isale.client.ui.widgets.calendar.CalendarView.OnMonthChangedListener;
import com.zjrc.isale.client.util.LogUtil;
import com.zjrc.isale.client.util.ScreenAdaptation;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：我的工作fragment
 */
public class WorksFragment extends BaseFragment {
	public static final String TAG = "WorksFragment";
	public static final int REQUEST_CODE_REFRESH = 1;
	private View v;// 页面缓存

	protected PopupWindow calendarPopupWindow;
	protected PopupWindow menuPopupWindow;

	private View calendar_layout;// 日历面板弹窗布局
	private CalendarView calendar;// 日历控件
	private RelativeLayout rl_date;
	private TextView tv_today_day;// 日期
	private TextView tv_today_mouth;// 年月
	private TextView tv_today_week;// 周
	private TextView tv_works_to_today;// 回到今天

	private TextView btn_works_checkin;// 签到
	private TextView btn_works_checkout;// 签退
	private TextView btn_works_vacation;// 申请休假
	private RefreshEventReceiver receiver;
	private List<ItemDescriptor> itemdescriptors = new ArrayList<ItemDescriptor>();// 底部item集合
	private Date today;// 今天日期
	private Date currentDate;// 今天日期
	private Date currentMonth;// 今天日期
	private Calendar cal;// 日历日期转换类

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (v == null) {
			v = inflater.inflate(R.layout.works_fragment, container, false);
			today = Calendar.getInstance().getTime();
			currentDate = today;
			currentMonth = today;
			initDate(v);// 初始化时间栏
			initAttendanceBar(v);
			try {
				setDate(today);// 设置今天时间
				initWorks(v, loaditems());// 初始化底部items
			} catch (Exception e) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				startActivity(intent);
                ISaleApplication.getInstance().dropTask();
                Toast.makeText(getActivity(), "身份认证过期，请重新登录", Toast.LENGTH_LONG)
						.show();
			}
			sendQueryDateInfo(today);// 获取当天信息
		}
		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);// 先移除
		}
		ScreenAdaptation.SubViewAdaption(container);
		return v;
	}

	private void addReceiver() {
		receiver = new RefreshEventReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.PLAN_ADD_ACTION);
		filter.addAction(Constant.PLAN_DELAY_ACTION);
		filter.addAction(Constant.PLAN_DELETE_ACTION);
		filter.addAction(Constant.PLAN_EXECUTION_ACTION);
		filter.addAction(Constant.PLAN_MODIFY_ACTION);
		filter.addAction(Constant.PLAN_AUDIT_ACTION);

		filter.addAction(Constant.TRAVEL_ADD_ACTION);
		filter.addAction(Constant.TRAVEL_ARRIVE_ACTION);
		filter.addAction(Constant.TRAVEL_DELETE_ACTION);
		filter.addAction(Constant.TRAVEL_LEAVE_ACTION);
		filter.addAction(Constant.TRAVEL_MODIFY_ACTION);
		filter.addAction(Constant.TRAVEL_START_ACTION);
		filter.addAction(Constant.TRAVEL_AUDIT_ACTION);

		filter.addAction(Constant.ATTENCE_CHECKIN_ACTION);
		filter.addAction(Constant.ATTENCE_CHECKOUT_ACTION);

		filter.addAction(Constant.VACATION_ADD_ACTION);
		filter.addAction(Constant.VACATION_AUDIT_ACTION);

		filter.addAction(Constant.SUGGESTION_ADD_ACTION);
		filter.addAction(Constant.SUGGESTION_DELETE_ACTION);
		filter.addAction(Constant.SUGGESTION_MODIFY_ACTION);

		filter.addAction(Constant.WORKREPORT_ADD_ACTION);

		this.getActivity().registerReceiver(receiver, filter);
	}

	@Override
	public void onRecvData(XmlNode response) {

	}

	/**
	 * 解析设置当日考勤信息
	 * 
	 * @param noderecords
	 */
	private void reSetattence(JsonObject noderecords) {
		if (noderecords != null) {// 考勤
			boolean ischeckin = false;
			boolean ischeckout = false;
			String checkInInfo = "";
			String checkOutInfo = "";

			if (noderecords.get("onwork").getAsString().equals("0")) {
				ischeckin = true;
				checkInInfo = "已签到";
			} else if (noderecords.get("onwork").getAsString().equals("1")) {
				ischeckin = false;
				checkInInfo = "签到";
			} else if (noderecords.get("onwork").getAsString().equals("2")) {
				ischeckin = true;
				checkInInfo = "已休假";
			}
			if (noderecords.get("offwork").getAsString().equals("0")) {
				checkOutInfo = "已签退";
				ischeckout = true;
			} else if (noderecords.get("offwork").getAsString().equals("1")) {
				checkOutInfo = "签退";
				ischeckout = false;
			} else if (noderecords.get("offwork").getAsString().equals("2")) {
				checkOutInfo = "已休假";
				ischeckout = true;
			}
			setAttendanceBar(!ischeckin, checkInInfo, !ischeckout,
					checkOutInfo, true, "");

		}
	}

	/**
	 * 解析重置当天拜访/差旅/反馈/汇报信息
	 * 
	 * @param response
	 */
	private void reSetWorksItem(JsonObject response) {
		itemdescriptors.clear();
		reSetPlans(response.getAsJsonArray("plans"));
		reSetTravels(response.getAsJsonArray("travels"));
		reSetSuggestions(response.getAsJsonArray("suggestions"));
		reSetWorkReport(response.getAsJsonArray("workreports"));
		setDate(currentDate);
		initWorks(v, itemdescriptors);
	}

	private void reSetPlans(JsonArray nodeplans) {
			if (nodeplans != null) {
				if (nodeplans.size() > 0) {
					for (JsonElement record : nodeplans) {
                        JsonObject obj = (JsonObject) record;
						ItemDescriptor itemDescriptor = new ItemDescriptor();
						itemDescriptor.type = Item_Type.PLAN;
						itemDescriptor.state =obj.get("planstate").getAsString();
						itemDescriptor.signstate = obj.get("signstate").getAsString();
						itemDescriptor.signresult = obj.get("signresult").getAsString();
						itemDescriptor.content =obj.get("terminalname").getAsString();
						itemDescriptor.patrolid =obj.get("patrolid").getAsString();
						itemDescriptor.plantype =obj.get("plantype").getAsString();
						itemDescriptor.id =obj.get("id").getAsString();
						itemdescriptors.add(itemDescriptor);
					}
				} else {
					ItemDescriptor itemDescriptor = new ItemDescriptor();
					itemDescriptor.type = Item_Type.PLAN;
					itemDescriptor.content = "";
					itemdescriptors.add(itemDescriptor);
				}
			} else {
				ItemDescriptor itemDescriptor = new ItemDescriptor();
				itemDescriptor.type = Item_Type.PLAN;
				itemDescriptor.content = "";
				itemdescriptors.add(itemDescriptor);
			}
	}

	/**
	 * 解析当天差旅信息
	 * 
	 * @param nodetravels
	 */
	private void reSetTravels(JsonArray nodetravels) {
			if (nodetravels != null) {
				if (nodetravels.size() > 0) {
					for (JsonElement record : nodetravels) {
                        JsonObject obj = (JsonObject) record;
						ItemDescriptor itemDescriptor = new ItemDescriptor();
						itemDescriptor.type = Item_Type.TRAVEL;
						itemDescriptor.state =obj.get("state").getAsString();
						itemDescriptor.content = (obj.get("province").getAsString()!= null ?obj.get("province").getAsString(): "")
								+ (obj.get("city").getAsString()!= null ?obj.get("city").getAsString(): "")
								+ (obj.get("zone").getAsString()!= null ?obj.get("zone").getAsString() : "");
						itemDescriptor.id =obj.get("id").getAsString();
						itemDescriptor.arrivetraceid = obj.get("arrivetraceid").getAsString();
						itemDescriptor.leavetraceid = obj.get("leavetraceid").getAsString();
						itemDescriptor.date =obj.get("date").getAsString();
						itemDescriptor.begindate =obj.get("begindate").getAsString();
						itemDescriptor.enddate =obj.get("enddate").getAsString();
						itemdescriptors.add(itemDescriptor);

					}
				} else {
					ItemDescriptor itemDescriptor = new ItemDescriptor();
					itemDescriptor.type = Item_Type.TRAVEL;
					itemDescriptor.content = "";
					itemdescriptors.add(itemDescriptor);
				}

			} else {
				ItemDescriptor itemDescriptor = new ItemDescriptor();
				itemDescriptor.type = Item_Type.TRAVEL;
				itemDescriptor.content = "";
				itemdescriptors.add(itemDescriptor);
			}
	}

	/**
	 * 解析当天反馈信息
	 * 
	 * @param nodesuggestions
	 */
	private void reSetSuggestions(JsonArray nodesuggestions) {
			if (nodesuggestions != null) {
				if (nodesuggestions.size() > 0) {
					for (JsonElement record : nodesuggestions) {
                        JsonObject obj = (JsonObject) record;
						ItemDescriptor itemDescriptor = new ItemDescriptor();
						itemDescriptor.type = Item_Type.SUGGESTION;
						itemDescriptor.content =obj.get("title").getAsString();
						itemDescriptor.id =obj.get("id").getAsString();
						itemdescriptors.add(itemDescriptor);
					}

				} else {
					ItemDescriptor itemDescriptor = new ItemDescriptor();
					itemDescriptor.type = Item_Type.SUGGESTION;
					itemDescriptor.content = "";
					itemdescriptors.add(itemDescriptor);
				}

			} else {
				ItemDescriptor itemDescriptor = new ItemDescriptor();
				itemDescriptor.type = Item_Type.SUGGESTION;
				itemDescriptor.content = "";
				itemdescriptors.add(itemDescriptor);

			}
	}

	/**
	 * 解析当天汇报信息
	 * 
	 * @param nodeworkreports
	 */
	private void reSetWorkReport(JsonArray nodeworkreports) {
			if (nodeworkreports != null) {
				if (nodeworkreports.size() > 0) {
					for (JsonElement record : nodeworkreports) {
                        JsonObject obj = (JsonObject) record;
						ItemDescriptor itemDescriptor = new ItemDescriptor();
						itemDescriptor.type = Item_Type.WORKREPORT;
						itemDescriptor.content = obj.get("title").getAsString();
						itemDescriptor.id = obj.get("id").getAsString();
						itemdescriptors.add(itemDescriptor);
					}

				} else {
					ItemDescriptor itemDescriptor = new ItemDescriptor();
					itemDescriptor.type = Item_Type.WORKREPORT;
					itemDescriptor.content = "";
					itemdescriptors.add(itemDescriptor);
				}

			} else {
				ItemDescriptor itemDescriptor = new ItemDescriptor();
				itemDescriptor.type = Item_Type.WORKREPORT;
				itemDescriptor.content = "";
				itemdescriptors.add(itemDescriptor);
			}
	}

	/**
	 * 解析标记日历考勤信息
	 */
	private void reMarkMonth(JsonObject response) {
        JsonObject mouthinfo = response.getAsJsonObject("body");
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentMonth);
		List<List<Calendar>> data = new ArrayList<List<Calendar>>();
		String onwork = mouthinfo.get("onwork").getAsString();
		List<Calendar> onworks = new ArrayList<Calendar>();
		LogUtil.d(TAG, "onwork:" + onwork);
		if (onwork != null && !onwork.equals("")) {
			String[] works = onwork.split(",");
			for (int i = 0; i < works.length; i++) {
				if (!works[i].equals("")) {
					Calendar calCache = Calendar.getInstance();
					calCache.set(cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH), Integer.valueOf(works[i]),
							0, 0, 0);
					onworks.add(calCache);
				}

			}
		}
		data.add(onworks);
        String offwork = mouthinfo.get("offwork").getAsString();

        LogUtil.d(TAG, "offwork:" + offwork);
		List<Calendar> offworks = new ArrayList<Calendar>();
		if (offwork != null && !offwork.equals("")) {
			String[] works = offwork.split(",");
			for (int i = 0; i < works.length; i++) {
				if (!works[i].equals("")) {
					Calendar calCache = Calendar.getInstance();
					calCache.set(cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH), Integer.valueOf(works[i]),
							0, 0, 0);
					offworks.add(calCache);
				}
			}
		}
		data.add(offworks);
        String vacation = mouthinfo.get("vacation").getAsString();

        LogUtil.d(TAG, "vacation:" + vacation);
		List<Calendar> vacations = new ArrayList<Calendar>();
		if (vacation != null && !vacation.equals("")) {
			String[] works = vacation.split(",");
			for (int i = 0; i < works.length; i++) {
				if (!works[i].equals("")) {
					Calendar calCache = Calendar.getInstance();
					calCache.set(cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH), Integer.valueOf(works[i]),
							0, 0, 0);
					vacations.add(calCache);
				}
			}
		}
		data.add(vacations);
		if (data != null && data.size() > 1) {
			calendar.markDatesOfMonth(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), data);
		}
	}

	/*
	 * 重置顶部title
	 * 
	 * @see com.zjrc.isale.client.ui.fragment.BaseFragment#reSet_TitleBar_Main()
	 */
	@Override
	public void reSet_TitleBar_Main() {
		iv_title_add.setVisibility(View.VISIBLE);
		iv_title_selector.setVisibility(View.GONE);
		tv_titlebar_title.setText("销售管家");
		btn_titlebar_filter.setVisibility(View.GONE);
	}

	/*
	 * 重置顶部右侧按钮
	 * 
	 * @see
	 * com.zjrc.isale.client.ui.fragment.BaseFragment#reSet_TitleBar_Right_Btn
	 * (boolean)
	 */
	@Override
	public void reSet_TitleBar_Right_Btn(boolean isMenuOpen) {
        if(iv_title_add==null) {
            if(getRl_titlebar_main()!=null){
                setRl_titlebar_main(getRl_titlebar_main());
            }else{
                setRl_titlebar_main(((MainActivity)getActivity()).getRl_titlebar_main());
            }
        }
        if (isMenuOpen) {
            iv_title_add.setVisibility(View.VISIBLE);
            iv_title_add.setBackgroundResource(R.drawable.v2_title_close);
        } else {
            iv_title_add.setVisibility(View.VISIBLE);
            iv_title_add.setBackgroundResource(R.drawable.v2_title_add);
        }
	}

	/**
	 * 初始化日期栏
	 * 
	 * @param v
	 */
	private void initDate(View v) {
		rl_date = (RelativeLayout) v.findViewById(R.id.rl_date);
		tv_today_day = (TextView) v.findViewById(R.id.tv_works_day);
		tv_today_mouth = (TextView) v.findViewById(R.id.tv_works_mouth);
		tv_today_week = (TextView) v.findViewById(R.id.tv_works_week);
		tv_works_to_today = (TextView) v.findViewById(R.id.tv_works_to_today);
	}

	/**
	 * 设置我的工作首页日期
	 * 
	 * @param date
	 */
	private void setDate(Date date) {
		cal = Calendar.getInstance();
		cal.setTime(date);
		tv_today_day.setText(cal.get(Calendar.DAY_OF_MONTH) + "");
		tv_today_mouth.setText(cal.get(Calendar.YEAR) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "");
		String weekday[] = getResources().getStringArray(
				R.array.calendar_weekday);
		Calendar cal_today = Calendar.getInstance();
		cal_today.setTime(today);
		if (cal.get(Calendar.YEAR) == cal_today.get(Calendar.YEAR)
				&& cal.get(Calendar.MONTH) == cal_today.get(Calendar.MONTH)
				&& cal.get(Calendar.DATE) == cal_today.get(Calendar.DATE)) {// 判断所选日期是否是今天
			rl_date.setBackgroundResource(R.drawable.v2_calendar_today);
			tv_today_mouth.setTextColor(getResources().getColor(
					R.color.v2_white));
			tv_today_week.setTextColor(getResources()
					.getColor(R.color.v2_white));
			tv_today_week.setText("星期"
					+ weekday[cal.get(Calendar.DAY_OF_WEEK) - 1] + "   今天");
			tv_works_to_today.setVisibility(View.GONE);
		} else {
			rl_date.setBackgroundColor(getResources().getColor(
					R.color.v2_gray_cccccc));
			tv_today_mouth.setTextColor(getResources().getColor(R.color.white));
			tv_today_week.setTextColor(getResources().getColor(R.color.white));
			tv_today_week.setText("星期"
					+ weekday[cal.get(Calendar.DAY_OF_WEEK) - 1]);
			tv_works_to_today.setVisibility(View.VISIBLE);
			SimpleDateFormat sdf = new SimpleDateFormat(getResources()
					.getString(R.string.year_mouth_day_name_format));
			tv_works_to_today.setText("今天  " + sdf.format(today));
		}
	}

	private void initAttendanceBar(View v) {
		btn_works_checkin = (TextView) v.findViewById(R.id.btn_works_checkin);
		btn_works_checkout = (TextView) v.findViewById(R.id.btn_works_checkout);
		btn_works_vacation = (TextView) v.findViewById(R.id.btn_works_vacation);
	}

	private void setAttendanceBar(boolean isCheckinEnable, String checkin,
			boolean isCheckOutEnable, String checkout,
			boolean isVacationEnable, String vacation) {
		Calendar cal_today = Calendar.getInstance();
		cal_today.setTime(today);
		if (cal.get(Calendar.YEAR) == cal_today.get(Calendar.YEAR)
				&& cal.get(Calendar.MONTH) == cal_today.get(Calendar.MONTH)
				&& cal.get(Calendar.DATE) == cal_today.get(Calendar.DATE)) {
			btn_works_checkin.setEnabled(isCheckinEnable);
			btn_works_checkout.setEnabled(isCheckOutEnable);
			btn_works_vacation.setEnabled(isVacationEnable);
		} else {
			btn_works_checkin.setEnabled(false);
			btn_works_checkout.setEnabled(false);
			btn_works_vacation.setEnabled(true);
		}
		if (!checkin.equals(""))
			btn_works_checkin.setText(checkin);
		if (!checkout.equals(""))
			btn_works_checkout.setText(checkout);
		if (!vacation.equals(""))
			btn_works_vacation.setText(vacation);
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isNotFastDoubleClick()) {
			switch (v.getId()) {
			case R.id.rl_date:// 弹出日历
				currentDate = today;
				currentMonth = today;
				sendQueryMonthInfoUltra(today, true);
				showCalendar();
				break;
			case R.id.tv_works_to_today:// 回到今天
				currentDate = today;
				currentMonth = today;
				cal.setTime(currentDate);
				sendQueryDateInfo(currentDate);
				break;
			case R.id.ll_works_date_pre:// 前一天
			case R.id.iv_works_date_pre:// 前一天
				cal.setTime(currentDate);
				cal.add(Calendar.DATE, -1);
				currentDate = cal.getTime();
				sendQueryDateInfo(currentDate);
				break;
			case R.id.ll_works_date_next:// 后一天
			case R.id.iv_works_date_next:// 后一天
				cal.setTime(currentDate);
				cal.add(Calendar.DATE, +1);
				currentDate = cal.getTime();
				sendQueryDateInfo(currentDate);
				break;
			case R.id.rl_works_checkin_layout:// 考勤列表
				Intent intent_to_attentcelist = new Intent();
				intent_to_attentcelist.setClass(getActivity(),
						AttenceListActivity.class);
				startActivity(intent_to_attentcelist);
				break;
			case R.id.btn_works_checkin:// 考勤登记
				Intent intent_to_addattence = new Intent();
				intent_to_addattence.setClass(getActivity(),
						AttenceSubmitActivity.class);
				intent_to_addattence.putExtra("oper", "1");
				intent_to_addattence.putExtra("method", "checkin");
				startActivityForResult(intent_to_addattence,
						REQUEST_CODE_REFRESH);
				break;
			case R.id.rl_works_checkout_layout:// 考勤列表
				Intent intent_to_attentcelist2 = new Intent();
				intent_to_attentcelist2.setClass(getActivity(),
						AttenceListActivity.class);
				startActivity(intent_to_attentcelist2);
				break;
			case R.id.btn_works_checkout:// 考勤登记
				Intent intent_to_addattence2 = new Intent();
				intent_to_addattence2.setClass(getActivity(),
						AttenceSubmitActivity.class);
				intent_to_addattence2.putExtra("oper", "1");
				intent_to_addattence2.putExtra("method", "checkout");
				startActivityForResult(intent_to_addattence2,
						REQUEST_CODE_REFRESH);
				break;
			case R.id.rl_works_vacation_layout:// 休假列表
				Intent intent_to_vacationlist = new Intent();
				intent_to_vacationlist.setClass(getActivity(),
						VacationListActivity.class);
				startActivity(intent_to_vacationlist);
				break;
			case R.id.btn_works_vacation:// 申请休假
				Intent intent_add_vacation = new Intent();
				intent_add_vacation.setClass(getActivity(),
						VacationSubmitActivity.class);
				startActivity(intent_add_vacation);
				break;
			case R.id.btn_titlebar_add:
				try {
					showPopupMenu();
				} catch (ClassNotFoundException e) {
					LogUtil.e(TAG, e.toString());
				}
				break;
			default:
				hidepopuWindow();
				break;
			}
		}
	}

	/**
	 * 弹出日历
	 */
	private void showCalendar() {
		if (calendarPopupWindow == null) {
			LayoutInflater tempInflater = LayoutInflater.from(getActivity());
			calendar_layout = (View) tempInflater.inflate(R.layout.calendar,
					null);
			calendar = (CalendarView) calendar_layout
					.findViewById(R.id.calendar);
			initEvent(calendar);
			calendarPopupWindow = new PopupWindow(calendar_layout,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			calendarPopupWindow.setFocusable(true);
			// popupWindow.setAnimationStyle(R.style.popwin_anim_style);
			ColorDrawable dw = new ColorDrawable(0x80000000);
			calendarPopupWindow.setBackgroundDrawable(dw);
			calendarPopupWindow.update();
		}
		calendarPopupWindow.showAsDropDown(rl_titlebar_main);
	}

	/**
	 * 设置日历监听
	 * 
	 * @param calendar
	 */
	private void initEvent(final CalendarView calendar) {

		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
			}

			@Override
			public void onDateSelected(Date date) {
				cal.setTime(date);
				currentDate = date;
				sendQueryDateInfo(currentDate);
				hidepopuWindow();
			}
		});

		calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
			@Override
			public void onChangedToPreMonth(Date dateOfMonth) {
				currentMonth = dateOfMonth;
				sendQueryMonthInfoUltra(dateOfMonth, true);

			}

			@Override
			public void onChangedToNextMonth(Date dateOfMonth) {
				currentMonth = dateOfMonth;
				sendQueryMonthInfoUltra(dateOfMonth, true);
			}
		});
	}

	CustomDatePicker datePicker;

	/**
	 * 初始化底部items
	 * 
	 * @param v
	 * @param itemdescriptors
	 */
	private void initWorks(View v, final List<ItemDescriptor> itemdescriptors) {
		LinearLayout ll_item_layout = (LinearLayout) v
				.findViewById(R.id.ll_item_layout);
		ll_item_layout.removeAllViews();
		int y = itemdescriptors.size();
		for (int i = 0; i < y; i++) {
			View view = getActivity().getLayoutInflater().inflate(
					R.layout.works_fragment_item, null);
			ImageView image = (ImageView) view.findViewById(R.id.iv_works_item);
			TextView title = (TextView) view.findViewById(R.id.tv_works_item);
			TextView c = (TextView) view.findViewById(R.id.tv_works_item_msg);
			TextView action1 = (TextView) view
					.findViewById(R.id.tv_works_item_action1);
			TextView action2 = (TextView) view
					.findViewById(R.id.tv_works_item_action2);

			View line = (View) view.findViewById(R.id.tv_works_item_line);
			RelativeLayout rl_works_item = (RelativeLayout) view
					.findViewById(R.id.rl_works_item);
			RelativeLayout rl_works_item_detail = (RelativeLayout) view
					.findViewById(R.id.rl_works_item_detail);
			final ItemDescriptor itemDescriptor = itemdescriptors.get(i);
			switch (itemDescriptor.type) {
			case PLAN:
				image.setImageResource(R.drawable.v2_work_visit_icon);
				if (itemDescriptor.content != null
						&& !itemDescriptor.content.equals("")) {
					c.setText(itemDescriptor.content);
					if (itemDescriptor.plantype.equalsIgnoreCase("0")) {// 临时任务
						if (itemDescriptor.state.equalsIgnoreCase("0")) {// 未执行
							action2.setText("执行");
							action1.setText("取消");
							action2.setVisibility(View.VISIBLE);
							action1.setVisibility(View.VISIBLE);
							line.setVisibility(View.VISIBLE);

							action2.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("operate", "submit");
										bundle.putString("planid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanPatrolSubmitActivity.class);
										startActivityForResult(intent,
												REQUEST_CODE_REFRESH);
									}
								}
							});
							action1.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("operate", "submit");
										bundle.putString("planid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanDelaySubmitActivity.class);
										startActivityForResult(intent,
												REQUEST_CODE_REFRESH);
									}
								}
							});

						} else if (itemDescriptor.state.equalsIgnoreCase("1")) {// 已执行
							action1.setText("详情");
							action1.setVisibility(View.VISIBLE);
							action2.setVisibility(View.GONE);
							line.setVisibility(View.GONE);

							action1.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("patrolid",
												itemDescriptor.patrolid);
										bundle.putString("planid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanDetailActivity.class);
										startActivity(intent);
									}
								}
							});

						} else if (itemDescriptor.state.equalsIgnoreCase("2")) {// 推迟执行
							action1.setText("推迟详情");
							action1.setVisibility(View.VISIBLE);
							action2.setVisibility(View.GONE);
							line.setVisibility(View.GONE);
							action1.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("planid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanDelayDetailActivity.class);
										startActivity(intent);
									}
								}
							});
						} else if (itemDescriptor.state.equalsIgnoreCase("3")) {// 取消执行
							action1.setText("取消详情");
							action2.setVisibility(View.GONE);
							line.setVisibility(View.GONE);
							action1.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("planid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanDelayDetailActivity.class);
										startActivity(intent);
									}
								}
							});
						}

					} else {// 常规任务
						if (itemDescriptor.signstate.equalsIgnoreCase("0")) {// 未审批
							action2.setText("修改");
							action1.setText("删除");
							action2.setVisibility(View.VISIBLE);
							action1.setVisibility(View.VISIBLE);
							line.setVisibility(View.VISIBLE);
							action2.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("operate", "modify");
										bundle.putString("planid",
												itemDescriptor.id);
										bundle.putString("terminalid", "");
										bundle.putString("terminalname", "");
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												PlanSubmitActivity.class);
										startActivityForResult(intent,
												REQUEST_CODE_REFRESH);
									}
								}
							});
							action1.setOnClickListener(new TextView.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										showAlertDialog(
												"提示",
												"确认删除此任务吗？",
												new View.OnClickListener() {
													@Override
													public void onClick(
															View arg0) {
														requestDeletePlan(itemDescriptor.id);
														alertDialog.cancel();
													}
												}, "确认",
												new View.OnClickListener() {
													@Override
													public void onClick(
															View arg0) {
														alertDialog.cancel();
													}
												}, "取消");
									}
								}
							});

						} else if (itemDescriptor.signstate
								.equalsIgnoreCase("1")) {// 已审批
							if (itemDescriptor.signresult.equalsIgnoreCase("1")) {// 审批通过
								if (itemDescriptor.state.equalsIgnoreCase("0")) {// 未执行
									action2.setText("执行");
									action1.setText("取消");
									action2.setVisibility(View.VISIBLE);
									action1.setVisibility(View.VISIBLE);
									line.setVisibility(View.VISIBLE);

									action2.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											if (CommonUtils
													.isNotFastDoubleClick()) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putString("operate",
														"submit");
												bundle.putString("planid",
														itemDescriptor.id);
												intent.putExtras(bundle);
												intent.setClass(
														getActivity(),
														PlanPatrolSubmitActivity.class);
												startActivityForResult(intent,
														REQUEST_CODE_REFRESH);
											}
										}
									});
									action1.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											if (CommonUtils
													.isNotFastDoubleClick()) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putString("operate",
														"submit");
												bundle.putString("planid",
														itemDescriptor.id);
												intent.putExtras(bundle);
												intent.setClass(
														getActivity(),
														PlanDelaySubmitActivity.class);
												startActivityForResult(intent,
														REQUEST_CODE_REFRESH);
											}
										}
									});

								} else if (itemDescriptor.state
										.equalsIgnoreCase("1")) {// 已执行
									action1.setText("详情");
									action1.setVisibility(View.VISIBLE);
									action2.setVisibility(View.GONE);
									line.setVisibility(View.GONE);

									action1.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											if (CommonUtils
													.isNotFastDoubleClick()) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putString("planid",
														itemDescriptor.id);
												bundle.putString("patrolid",
														itemDescriptor.patrolid);
												intent.putExtras(bundle);
												intent.setClass(
														getActivity(),
														PlanDetailActivity.class);
												startActivity(intent);
											}
										}
									});

								} else if (itemDescriptor.state
										.equalsIgnoreCase("2")) {// 推迟执行
									action1.setText("推迟详情");
									action1.setVisibility(View.VISIBLE);
									action2.setVisibility(View.GONE);
									line.setVisibility(View.GONE);
									action1.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											if (CommonUtils
													.isNotFastDoubleClick()) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putString("planid",
														itemDescriptor.id);
												intent.putExtras(bundle);
												intent.setClass(
														getActivity(),
														PlanDelayDetailActivity.class);
												startActivity(intent);
											}
										}
									});
								}else if (itemDescriptor.state
										.equalsIgnoreCase("3")){//取消执行
									action1.setText("取消详情");
									action1.setVisibility(View.VISIBLE);
									action2.setVisibility(View.GONE);
									line.setVisibility(View.GONE);
									action1.setOnClickListener(new TextView.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											if (CommonUtils.isNotFastDoubleClick()) {
												Intent intent = new Intent();
												Bundle bundle = new Bundle();
												bundle.putString("planid",
														itemDescriptor.id);
												intent.putExtras(bundle);
												intent.setClass(getActivity(),
														PlanDelayDetailActivity.class);
												startActivity(intent);
											}
										}
									});
								}

							} else {// 审批未通过
								action1.setText("不同意");
								action2.setVisibility(View.GONE);
								action1.setVisibility(View.VISIBLE);
								line.setVisibility(View.GONE);
//								action1.setOnClickListener(new TextView.OnClickListener() {
//
//									@Override
//									public void onClick(View arg0) {
//										if (CommonUtils.isNotFastDoubleClick()) {
//											showAlertDialog(
//													"提示",
//													"确认删除此任务吗？",
//													new View.OnClickListener() {
//														@Override
//														public void onClick(
//																View arg0) {
//															requestDeletePlan(itemDescriptor.id);
//															alertDialog
//																	.cancel();
//														}
//													}, "确认",
//													new View.OnClickListener() {
//														@Override
//														public void onClick(
//																View arg0) {
//															alertDialog
//																	.cancel();
//														}
//													}, "取消");
//										}
//									}
//								});
							}
						}
					}
					// rl_works_item_detail
					// .setOnClickListener(new RelativeLayout.OnClickListener()
					// {
					//
					// @Override
					// public void onClick(View arg0) {
					// Intent intent = new Intent();
					// Bundle bundle = new Bundle();
					// bundle.putString("operate", "view");
					// bundle.putString("planid",
					// itemDescriptor.id);
					// bundle.putString("terminalid", "");
					// bundle.putString("terminalname", "");
					// intent.putExtras(bundle);
					// intent.setClass(getActivity(),
					// PlanSubmitActivity.class);
					// startActivity(intent);
					// }
					// });
				} else {
					c.setText(Html
							.fromHtml("<u><font color=\"#5E76FF\">新建拜访</font></u>"));
					c.setOnClickListener(new TextView.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (CommonUtils.isNotFastDoubleClick()) {
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										PlanSubmitActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("operate", "insert");
								bundle.putString("terminalid", "");
								bundle.putString("terminalname", "");
								intent.putExtras(bundle);
								startActivityForResult(intent,
										REQUEST_CODE_REFRESH);
							}
						}
					});
					action2.setVisibility(View.GONE);
					action1.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
				}
				title.setText("拜访");
				break;
			case TRAVEL:
				image.setImageResource(R.drawable.v2_work_travel_icon);
				if (itemDescriptor.content != null
						&& !itemDescriptor.content.equals("")) {
					c.setText(itemDescriptor.content);
					LogUtil.d(TAG, "c: " + itemDescriptor.content);// (0为未审批，1为审批通过，2为审批未通过，3为已开始,4已到达,5已离开,6已结束)
					if (itemDescriptor.state.equals("0")) {// 未审批
						action2.setVisibility(View.VISIBLE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.VISIBLE);
						action2.setText("修改");
						action1.setText("删除");
						action2.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									Bundle bundle = new Bundle();
									bundle.putString("operate", "modify");
									bundle.putString("travelid",
											itemDescriptor.id);
									intent.putExtras(bundle);
									intent.setClass(getActivity(),
											TravelSubmitActivity.class);
									startActivityForResult(intent,
											REQUEST_CODE_REFRESH);
								}
							}
						});
						action1.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									showAlertDialog(
											"提示",
											"确认删除此差旅吗？",
											new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													requestDeleteTravel(itemDescriptor.id);
													alertDialog.cancel();
												}
											}, "确认",
											new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													alertDialog.cancel();
												}
											}, "取消");
								}
							}
						});

					} else if (itemDescriptor.state.equals("1")) {// 审批通过
						action1.setVisibility(View.VISIBLE);
						action2.setVisibility(View.GONE);
						line.setVisibility(View.GONE);
						action1.setText("开始");
						final String beginDate = itemDescriptor.begindate;
						action1.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									datePicker = new CustomDatePicker(
											getActivity(), "请选择差旅实际结束时间",
											new View.OnClickListener() {

												@Override
												public void onClick(View arg0) {
													String sEndDate = datePicker
															.getYear()
															+ "-"
															+ datePicker
																	.getMonth()
															+ "-"
															+ datePicker
																	.getDay();
													if (!"".equalsIgnoreCase(sEndDate)) {
														SimpleDateFormat sdf = new SimpleDateFormat(
																"yyyy-MM-dd");
														try {
															Date dBegindate = sdf
																	.parse(beginDate);
															Date dEnddate = sdf
																	.parse(sEndDate);
															int iDays = (int) ((dEnddate
																	.getTime() - dBegindate
																	.getTime()) / 1000 / 60 / 60 / 24) + 1;
															if (iDays <= 0) {
																showAlertDialog(
																		"提示",
																		"结束日期不能小于当前日期,请重新选择!",
																		new View.OnClickListener() {
																			@Override
																			public void onClick(
																					View arg0) {
																				alertDialog
																						.cancel();
																			}
																		},
																		"确定",
																		null,
																		null);
															} else {
																Log.d(TAG,
																		"sEndDate: "
																				+ sEndDate);
																requestUpdateTravel(
																		itemDescriptor.id,
																		"start",
																		sEndDate);
															}
														} catch (Exception e) {
														}
													}
													datePicker.dismiss();
												}
											}, new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													datePicker.dismiss();
												}
											}, null);
									datePicker.show();
								}
							}
						});
					} else if (itemDescriptor.state.equals("2")) {// 为审批未通过
						action2.setVisibility(View.GONE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.GONE);
						action1.setText("不同意");
						action1.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									intent.setClass(getActivity(),
											TravelSubmitDetailActivity.class);
									intent.putExtra("corptravelid",
											itemDescriptor.id);
									startActivityForResult(intent,
											REQUEST_CODE_REFRESH);
								}
							}
						});
					} else if (itemDescriptor.state.equals("3")) {// 已开始
						action2.setVisibility(View.VISIBLE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.VISIBLE);
						action2.setText("到达");
						action2.setOnClickListener(new TextView.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									intent.setClass(getActivity(),
											TravelExecutionActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("state", "started");
									bundle.putString("corptravelid",
											itemDescriptor.id);
									intent.putExtras(bundle);
									startActivity(intent);
								}
							}
						});
						action1.setText("修改");
						final String beginDate = itemDescriptor.begindate;
						action1.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {

									datePicker = new CustomDatePicker(
											getActivity(), "请选择差旅实际结束时间",
											new View.OnClickListener() {

												@Override
												public void onClick(View arg0) {
													String sEndDate = datePicker
															.getYear()
															+ "-"
															+ datePicker
																	.getMonth()
															+ "-"
															+ datePicker
																	.getDay();
													if (!"".equalsIgnoreCase(sEndDate)) {
														SimpleDateFormat sdf = new SimpleDateFormat(
																"yyyy-MM-dd");
														try {
															Date dBegindate = sdf
																	.parse(beginDate);
															Date dEnddate = sdf
																	.parse(sEndDate);
															int iDays = (int) ((dEnddate
																	.getTime() - dBegindate
																	.getTime()) / 1000 / 60 / 60 / 24) + 1;
															if (iDays <= 0) {
																showAlertDialog(
																		"提示",
																		"结束日期不能小于当前日期，请重新选择!",
																		new View.OnClickListener() {
																			@Override
																			public void onClick(
																					View arg0) {
																				alertDialog
																						.cancel();
																			}
																		},
																		"确定",
																		null,
																		null);
															} else {
																Log.d(TAG,
																		"beginDate: "
																				+ beginDate);
																requestUpdateTravel(
																		itemDescriptor.id,
																		"update",
																		sEndDate);
															}
														} catch (Exception e) {
														}
													}
													datePicker.dismiss();
												}
											}, new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													datePicker.dismiss();
												}
											}, null);
									datePicker.show();
								}
							}
						});
					} else if (itemDescriptor.state.equals("4")) {// 已到达
						action2.setVisibility(View.VISIBLE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.VISIBLE);
						action2.setText("离开");
						action2.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									intent.setClass(getActivity(),
											TravelExecutionActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("state", "started");
									bundle.putString("corptravelid",
											itemDescriptor.id);
									intent.putExtras(bundle);
									startActivityForResult(intent,
											REQUEST_CODE_REFRESH);
								}
							}
						});
						action1.setText("修改");
						final String beginDate = itemDescriptor.begindate;
						action1.setOnClickListener(new TextView.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									datePicker = new CustomDatePicker(
											getActivity(), "请选择差旅实际结束时间",
											new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													String sEndDate = datePicker
															.getYear()
															+ "-"
															+ datePicker
																	.getMonth()
															+ "-"
															+ datePicker
																	.getDay();
													if (!"".equalsIgnoreCase(sEndDate)) {
														SimpleDateFormat sdf = new SimpleDateFormat(
																"yyyy-MM-dd");
														try {
															Date dBegindate = sdf
																	.parse(beginDate);
															Date dEnddate = sdf
																	.parse(sEndDate);
															int iDays = (int) ((dEnddate
																	.getTime() - dBegindate
																	.getTime()) / 1000 / 60 / 60 / 24) + 1;
															if (iDays <= 0) {
																showAlertDialog(
																		"提示",
																		"结束日期不能小于当前日期，请重新选择!",
																		new View.OnClickListener() {
																			@Override
																			public void onClick(
																					View arg0) {
																				alertDialog
																						.cancel();
																			}
																		},
																		"确定",
																		null,
																		null);
															} else {
																Log.d(TAG,
																		"beginDate: "
																				+ beginDate);
																requestUpdateTravel(
																		itemDescriptor.id,
																		"update",
																		sEndDate);
															}
														} catch (Exception e) {
														}
													}
													datePicker.dismiss();
												}
											}, new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													datePicker.dismiss();
												}
											}, null);
									datePicker.show();
								}
							}
						});

					} else if (itemDescriptor.state.equals("5")) {// 已离开
						action1.setText("已离开");
						action2.setVisibility(View.GONE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.GONE);
						action1.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									intent.setClass(getActivity(),
											TravelDetailActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("corptravelid",
											itemDescriptor.id);
									intent.putExtras(bundle);
									startActivity(intent);
								}
							}
						});

					} else if (itemDescriptor.state.equals("6")) {// 已结束
						action1.setText("已结束");
						action2.setVisibility(View.GONE);
						action1.setVisibility(View.VISIBLE);
						line.setVisibility(View.GONE);
						action1.setOnClickListener(new Button.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if (CommonUtils.isNotFastDoubleClick()) {
									Intent intent = new Intent();
									intent.setClass(getActivity(),
											TravelDetailActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("corptravelid",
											itemDescriptor.id);
									intent.putExtras(bundle);
									startActivityForResult(intent,
											REQUEST_CODE_REFRESH);
								}
							}
						});
					}

				} else {// 无差旅信息
					c.setText(Html
							.fromHtml("<u><font color=\"#5E76FF\">新建差旅</font></u>"));
					c.setOnClickListener(new TextView.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (CommonUtils.isNotFastDoubleClick()) {
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										TravelSubmitActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("operate", "insert");
								intent.putExtras(bundle);
								startActivityForResult(intent,
										REQUEST_CODE_REFRESH);
							}
						}
					});
					action2.setVisibility(View.GONE);
					action1.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
				}
				title.setText("差旅");
				break;
			case SUGGESTION:
				image.setImageResource(R.drawable.v2_work_suggestion_icon);
				if (itemDescriptor.content != null
						&& !itemDescriptor.content.equals("")) {
					c.setText(itemDescriptor.content);
					action2.setVisibility(View.GONE);
					action1.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
					rl_works_item_detail
							.setOnClickListener(new RelativeLayout.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										Bundle bundle = new Bundle();
										bundle.putString("suggestionid",
												itemDescriptor.id);
										intent.putExtras(bundle);
										intent.setClass(getActivity(),
												SuggestionDetailActivity.class);
										startActivity(intent);
									}
								}
							});
				} else {
					c.setText(Html
							.fromHtml("<u><font color=\"#5E76FF\">新建反馈</font></u>"));
					c.setOnClickListener(new TextView.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (CommonUtils.isNotFastDoubleClick()) {
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										SuggestionSubmitActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("operate", "insert");
								intent.putExtras(bundle);
								startActivityForResult(intent,
										REQUEST_CODE_REFRESH);
							}
						}
					});
				}
				title.setText("反馈");
				action2.setVisibility(View.GONE);
				action1.setVisibility(View.GONE);
				line.setVisibility(View.GONE);
				break;
			case WORKREPORT:
				image.setImageResource(R.drawable.v2_work_workreport_icon);
				if (itemDescriptor.content != null
						&& !itemDescriptor.content.equals("")) {
					action2.setVisibility(View.GONE);
					action1.setVisibility(View.GONE);
					line.setVisibility(View.GONE);
					c.setText(itemDescriptor.content);
					rl_works_item_detail
							.setOnClickListener(new RelativeLayout.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (CommonUtils.isNotFastDoubleClick()) {
										Intent intent = new Intent();
										intent.setClass(getActivity(),
												WorkItemActivity.class);
										intent.putExtra("corpworkreportid",
												itemDescriptor.id);
										getActivity().startActivity(intent);
									}
								}
							});
				} else {
					c.setText(Html
							.fromHtml("<u><font color=\"#5E76FF\">新建汇报</font></u>"));
					c.setOnClickListener(new TextView.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (CommonUtils.isNotFastDoubleClick()) {
								Intent intent = new Intent();
								intent.setClass(getActivity(),
										WorkReportSubmitActivity.class);
								startActivityForResult(intent,
										REQUEST_CODE_REFRESH);
							}
						}
					});
				}
				title.setText("汇报");
				action2.setVisibility(View.GONE);
				action1.setVisibility(View.GONE);
				line.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			rl_works_item
					.setOnClickListener(new RelativeLayout.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if (CommonUtils.isNotFastDoubleClick()) {
								Intent intent = new Intent();
								switch (itemDescriptor.type) {
								case PLAN:
									intent.setClass(getActivity(),
											PlanListActivity.class);
									break;
								case TRAVEL:
									intent.setClass(getActivity(),
											TravelListActivity.class);
									break;
								case SUGGESTION:
									intent.setClass(getActivity(),
											SuggestionListActivity.class);
									break;
								case WORKREPORT:
									intent.setClass(getActivity(),
											WorkListActivity.class);
									break;
								default:
									break;
								}
								startActivity(intent);
							}
						}
					});

			ll_item_layout.addView(view);
		}
		ScreenAdaptation.SubViewAdaption(ll_item_layout);
	}

	/**
	 * 测试items
	 * 
	 * @return
	 */
	private List<ItemDescriptor> loaditems() {
		for (int i = 0; i < 4; i++) {
			ItemDescriptor itemdescriptor = new ItemDescriptor();
			if (i == 0) {
				itemdescriptor.type = Item_Type.PLAN;
				itemdescriptor.content = "";
			}
			if (i == 1) {
				itemdescriptor.type = Item_Type.TRAVEL;
				itemdescriptor.content = "";
			}
			if (i == 2) {
				itemdescriptor.type = Item_Type.SUGGESTION;
				itemdescriptor.content = "";
			}
			if (i == 3) {
				itemdescriptor.type = Item_Type.WORKREPORT;
				itemdescriptor.content = "";
			}
			itemdescriptors.add(itemdescriptor);
		}
		return itemdescriptors;
	}

	/**
	 * items类型 拜访/差旅/反馈/汇报
	 */
	enum Item_Type {
		PLAN, TRAVEL, SUGGESTION, WORKREPORT
	}

	/**
	 * 底部items表述者
	 */
	class ItemDescriptor {
		Item_Type type;
		String state;
		String signstate;
		String signresult;
		String patrolid;
		String content;
		String id;
		String date;
		String plantype;
		String begindate;
		String enddate;
		String arrivetraceid;
		String leavetraceid;
	}

	/**
	 * 弹出右侧menu弹窗
	 * 
	 * @throws ClassNotFoundException
	 */
	private void showPopupMenu() throws ClassNotFoundException {
		if (menuPopupWindow == null) {
			LayoutInflater tempInflater = LayoutInflater.from(getActivity());
			View v = (View) tempInflater.inflate(R.layout.works_menu, null);
			GridView works = (GridView) v.findViewById(R.id.gv_works);
			GridView business = (GridView) v.findViewById(R.id.gv_business);
			String items_top[] = getResources().getStringArray(
					R.array.works_menu_top);
			String items_top_class_name[] = getResources().getStringArray(
					R.array.works_menu_top_class_name);
			String items_bottom[] = getResources().getStringArray(
					R.array.works_menu_bottom);
			String items_bottom_class_name[] = getResources().getStringArray(
					R.array.works_menu_bottom_class_name);
			final List<MenuItem> top_items = new ArrayList<MenuItem>();
			final List<MenuItem> bottom_items = new ArrayList<MenuItem>();
			for (int i = 0; i < items_top.length; i++) {
				MenuItem menuitem = new MenuItem(items_top[i],
						Class.forName(items_top_class_name[i]));
				top_items.add(menuitem);
			}
			for (int i = 0; i < items_bottom.length; i++) {
				MenuItem menuitem = new MenuItem(items_bottom[i],
						Class.forName(items_bottom_class_name[i]));
				bottom_items.add(menuitem);
			}
			works.setAdapter(new MenuAdapter(top_items));
			business.setAdapter(new MenuAdapter(bottom_items));
			menuPopupWindow = new PopupWindow(v, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			menuPopupWindow.setFocusable(true);
			// popupWindow.setAnimationStyle(R.style.popwin_anim_style);
			ColorDrawable dw = new ColorDrawable(0x80000000);
			menuPopupWindow.setBackgroundDrawable(dw);
			menuPopupWindow.update();
		}
		menuPopupWindow.showAsDropDown(rl_titlebar_main);
	}

	class MenuItem {
		String name;
		Class className;

		MenuItem(String name, Class className) {
			this.name = name;
			this.className = className;
		}
	}

	/**
	 * menu菜单gridview适配器
	 */
	class MenuAdapter extends BaseAdapter {
		List<MenuItem> items;

		MenuAdapter(List<MenuItem> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			View v = getActivity().getLayoutInflater().inflate(
					R.layout.works_menu_item, null);
			Button button = (Button) v.findViewById(R.id.btn_menu_item);
			button.setText(items.get(arg0).name);
			button.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (CommonUtils.isNotFastDoubleClick()) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("operate", "insert");
						intent.putExtras(bundle);
						intent.setClass(getActivity(),
								items.get(arg0).className);
						startActivity(intent);
						hidepopuWindow();
					}
				}
			});

			return v;
		}

	}

	private void sendQueryDateInfo(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(
				R.string.year_mouth_day_name_format));
		String mDate = sdf.format(date);
        ISaleApplication application = (ISaleApplication)getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("companyid",XmlValueUtil.encodeString(application.getConfig()
                    .getCompanyid()));
            params.put("date",mDate);
            request("login!homepageDetail?code=" + Constant.DATE_INFO, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private void sendQueryDateInfoNormal(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(
				R.string.year_mouth_day_name_format));
		String mDate = sdf.format(date);
        ISaleApplication application = (ISaleApplication)getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("companyid",XmlValueUtil.encodeString(application.getConfig()
                    .getCompanyid()));
            params.put("date",mDate);
            request("login!homepageDetail?code=" + Constant.DATE_INFO, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	private void sendQueryMonthInfoUltra(Date date, boolean needProgress) {
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(
				R.string.year_mouth_name_format));
		String mDate = sdf.format(date);
		LogUtil.d(TAG, "mDate:" + mDate);
        ISaleApplication application = (ISaleApplication)getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("month", mDate);
            request("login!calendarMonth?code=" + Constant.MONTH_INFO, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

	public void requestDeletePlan(String id) {
        ISaleApplication application = (ISaleApplication) getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("operate", "delete");
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("terminalid","");
            params.put("plandate", "");
            params.put("plancontent", "");
            params.put("planstate", "0");
            params.put("plancomment", "");
            params.put("planid", XmlValueUtil.encodeString(id));
            request("plan!submit?code=" + Constant.PLAN_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	public void requestDeleteTravel(String id) {
        ISaleApplication application = (ISaleApplication) getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", XmlValueUtil.encodeString(application.getConfig()
                    .getUserid()));
            params.put("operate", "");
            params.put("date", "");
            params.put("province", "");
            params.put("city", "");
            params.put("zone", "");
            params.put("desc", "");
            params.put("days", "");
            params.put("begindate", "");
            params.put("enddate", "");
            params.put("corptravelid", id);
            request("travel!submit?code=" + Constant.TRAVEL_SUBMIT, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	public void requestUpdateTravel(String id, String operate,
			String actualenddate) {
        ISaleApplication application = (ISaleApplication) getActivity().getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("actualenddate",XmlValueUtil.encodeString(actualenddate));
            params.put("operate",XmlValueUtil.encodeString(operate));
            params.put("corptravelid", XmlValueUtil.encodeString(id));
            request("travel!submitActualEndDate?code=" + Constant.TRAVEL_UPDATE, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
	}

	@Override
	public void onRefresh() {
		sendQueryDateInfo(currentDate);
		super.onRefresh();
	}

	public boolean hidepopuWindow() {
		if (calendarPopupWindow != null && calendarPopupWindow.isShowing()) {
			calendarPopupWindow.dismiss();
			calendarPopupWindow = null;
			return true;
		} else {
			if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
				menuPopupWindow.dismiss();
				menuPopupWindow = null;
				return true;
			} else
				return false;
		}

	}

	@Override
	public void onDestroy() {
		try {
			this.getActivity().unregisterReceiver(receiver);
		} catch (Exception e) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			startActivity(intent);
			Toast.makeText(getActivity(), "程序出现异常，请重新登录", Toast.LENGTH_LONG)
					.show();
		}
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private class RefreshEventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(Constant.PLAN_ADD_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_DELAY_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_DELETE_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_EXECUTION_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.PLAN_AUDIT_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_ADD_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_ARRIVE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_DELETE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_LEAVE_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_START_ACTION)
					|| action.equalsIgnoreCase(Constant.TRAVEL_AUDIT_ACTION)
					|| action.equalsIgnoreCase(Constant.ATTENCE_CHECKIN_ACTION)
					|| action
							.equalsIgnoreCase(Constant.ATTENCE_CHECKOUT_ACTION)
					|| action.equalsIgnoreCase(Constant.VACATION_ADD_ACTION)
					|| action.equalsIgnoreCase(Constant.VACATION_AUDIT_ACTION)
					|| action.equalsIgnoreCase(Constant.SUGGESTION_ADD_ACTION)
					|| action
							.equalsIgnoreCase(Constant.SUGGESTION_DELETE_ACTION)
					|| action
							.equalsIgnoreCase(Constant.SUGGESTION_MODIFY_ACTION)
					|| action.equalsIgnoreCase(Constant.WORKREPORT_ADD_ACTION)) {
				sendQueryDateInfo(currentDate);
			}
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.TRAVEL_SUBMIT.equals(code)) {
                Toast.makeText(getActivity(), "差旅计划删除成功!", Toast.LENGTH_SHORT)
                        .show();
                sendQueryDateInfo(currentDate);
            }else if (Constant.TRAVEL_UPDATE.equals(code)) {// 差旅更新结束时间
                sendQueryDateInfo(currentDate);
            }else if(Constant.PLAN_SUBMIT.equals(code)){
                Toast.makeText(getActivity(), "拜访计划删除成功!", Toast.LENGTH_SHORT)
                            .show();
                sendQueryDateInfo(currentDate);
            }else if(Constant.DATE_INFO.equals(code)){
                JsonObject infoObj = response.getAsJsonObject("body");
                    reSetattence(infoObj);// 解析重置每日考勤信息
                    reSetWorksItem(infoObj);// 解析重置每日拜访/差旅/反馈/汇报信息
            }else if(Constant.MONTH_INFO.equals(code)){
                    reMarkMonth(response);// 解析标记日历每月考勤信息
            }
        }
    }
}
