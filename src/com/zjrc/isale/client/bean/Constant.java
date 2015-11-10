package com.zjrc.isale.client.bean;

/**
 * 项目名称：销售管家 版本号：V1.00 创建者: 高林荣 功能描述：常量定义类
 */

public class Constant {
    public static final String USER_AGENT = "ISALE_ANDROID_CLIENT";
    public static final String API_URL_PREFIX = "http://221.181.41.162:8080/isale_api/api/";
//    public static final String API_URL_PREFIX = "http://111.1.58.110:9020/isalews/api/";
//    public static final String API_URL_PREFIX = "http://120.199.26.226:9685/isale/api/";
	// 配置名称
	public static final String PREFERENCE_NAME = "ISALE";
	// 短信拦截标志
	public static final String SMS_HEAD = "RC_ISALE_LOCATION_HEAD";
	// 服务器地址
	public static final String SERVER_IP = "SERVER_IP";
	// 服务器端口
	public static final String SERVER_PORT = "SERVER_PORT";
	// 手机号码
	public static final String PHONE_NO = "PHONE_NO";
	// 登录密码
	public static final String PASSWORD = "PASSWORD";
	// 是否记住密码
	public static final String SAVE_PASSWORD = "SAVE_PASSWORD";
	// 是否自动登录
	public static final String AUTO_LOGIN = "AUTO_LOGIN";
	// 是否首次登录
    public static final String FIRST_LOGIN = "FIRST_LOGIN";
    // 用户ID
    public static final String USER_ID = "USER_ID";
    // 区域
    public static final String AREAS = "AREAS";
    // 审核人
    public static final String AUDITS = "AUDITS";
    // 网店类型
    public static final String TERMINAL_TYPES = "TERMINAL_TYPES";
	// 请假上报广播action
	public static final String VACATION_ADD_ACTION = "com.zjrc.isale.client.VACATION_ADD";
	// 汇报上报广播action
	public static final String WORKREPORT_ADD_ACTION = "com.zjrc.isale.client.WORKREPORT_ADD";
	// 差旅上报广播action
	public static final String TRAVEL_ADD_ACTION = "com.zjrc.isale.client.TRAVEL_ADD";
	// 差旅修改上报广播action
	public static final String TRAVEL_MODIFY_ACTION = "com.zjrc.isale.client.TRAVEL_MODIFY";
	// 差旅删除上报广播action
	public static final String TRAVEL_DELETE_ACTION = "com.zjrc.isale.client.TRAVEL_DELETE";
	// 差旅开始上报广播action
	public static final String TRAVEL_START_ACTION = "com.zjrc.isale.client.TRAVEL_START";
	// 差旅到达上报广播action
	public static final String TRAVEL_ARRIVE_ACTION = "com.zjrc.isale.client.TRAVEL_ARRIVE";
	// 差旅离开上报广播action
	public static final String TRAVEL_LEAVE_ACTION = "com.zjrc.isale.client.TRAVEL_LEAVE";
	// 反馈上报广播action
	public static final String SUGGESTION_ADD_ACTION = "com.zjrc.isale.client.SUGGESTION_ADD";
	// 反馈修改广播action
	public static final String SUGGESTION_MODIFY_ACTION = "com.zjrc.isale.client.SUGGESTION_MODIFY";
	// 反馈删除广播action
	public static final String SUGGESTION_DELETE_ACTION = "com.zjrc.isale.client.SUGGESTION_DELETE";
	// 拜访上报广播action
	public static final String PLAN_ADD_ACTION = "com.zjrc.isale.client.PLAN_ADD";
	// 拜访修改广播action
	public static final String PLAN_MODIFY_ACTION = "com.zjrc.isale.client.PLAN_MODIFY";
	// 拜访删除广播action
	public static final String PLAN_DELETE_ACTION = "com.zjrc.isale.client.PLAN_DELETE";
	// 拜访执行广播action
	public static final String PLAN_EXECUTION_ACTION = "com.zjrc.isale.client.PLAN_EXECUTION";
	// 拜访取消延迟广播action
	public static final String PLAN_DELAY_ACTION = "com.zjrc.isale.client.PLAN_DELAY";
	// 签到广播action
	public static final String ATTENCE_CHECKIN_ACTION = "com.zjrc.isale.client.ATTENCE_CHECKIN";
	// 签退广播action
	public static final String ATTENCE_CHECKOUT_ACTION = "com.zjrc.isale.client.ATTENCE_CHECKOUT";
	// 拜访审批广播action
	public static final String PLAN_AUDIT_ACTION = "com.zjrc.isale.client.PLAN_AUDIT";
	// 差旅审批广播action
	public static final String TRAVEL_AUDIT_ACTION = "com.zjrc.isale.client.TRAVEL_AUDIT";
	// 公告审批广播action
	public static final String NOTICE_AUDIT_ACTION = "com.zjrc.isale.client.NOTICE_AUDIT";
	// 请假审批广播action
	public static final String VACATION_AUDIT_ACTION = "com.zjrc.isale.client.VOCATION_AUDIT";

	public static final String LOGIN = "1001";

	public static final String PASSWORD_MODIFY = "1002";

	public static final String PASSWORD_GET = "1003";

	public static final String LOGIN_BYSSOID = "1004";

	// 区域
	public static final String AREA_LIST = "2001";

	// 审批人列表
	public static final String AUDIT_LIST = "9001";
	// 产品品牌
	public static final String BRAND_LIST = "2002";

	// 产品类别
	public static final String CATEGORY_LIST = "2003";

	// 产品
	public static final String PRODUCT_LIST = "2004";

	// 竞品
	public static final String CONTENDPRODUCT_LIST = "2005";
	// 竞品删除
	public static final String CONTENDPRODUCT_DELETE = "2005";

	// 网点类型
	public static final String TERMINALTYPE_LIST = "2006";

	// 网点相关信息
	public static final String TERMINAL_LIST = "2007";

	public static final String TERMINAL_DETAIL = "2008";

	// 新闻公告相关
	public static final String NOTICE_LIST = "3001";

	public static final String NOTICE_DETAIL = "3002";

	public static final String NOTICE_READ = "3003";

	// 投诉建议相关信息
	public static final String SUGGESTION_LIST = "3004";

	public static final String SUGGESTION_DETAIL = "3005";

	public static final String SUGGESTION_SUBMIT = "3006";

	// 获取通讯录列表
	public static final String CONTACT_LIST = "3007";

	public static final String CONTACT_DETAIL = "3008";

	public static final String WORKREPORT_LIST = "4001";

	public static final String WORKREPORT_SUBMIT = "4003";

	public static final String WORKREPORT_ITEM = "4002";

	public static final String ATTENCE_LIST = "4004";

	public static final String ATTENCE_ITEM = "4005";

	public static final String ATTENCE_SUBMIT = "4006";

	public static final String VACATION_LIST = "4007";

	public static final String VACATION_ITEM = "4008";

	public static final String VACATION_SUBMIT = "4009";

	public static final String TRAVEL_LIST = "4010";

	public static final String TRAVEL_QUERY = "4011";

	public static final String TRAVEL_SUBMIT = "4012";

	public static final String TRAVELTRACE_QUERY = "4014";

	public static final String TRAVELTRACE_SUBMIT = "4015";

	public static final String TERMINAL_SUBMIT = "4016";

	public static final String TRAVEL_UPDATE = "4024";

	public static final String PLAN_LIST = "5001";

	public static final String PLAN_QUERY = "5002";

	public static final String PLAN_SUBMIT = "5003";

	public static final String PATROL_QUERY = "5005";

	public static final String PATROL_SUBMIT = "5006";

	public static final String SALE_LIST = "6001";

	public static final String SALE_DETAIL = "6002";

	public static final String SALE_SUBMIT = "6003";

	public static final String STOCK_LIST = "6004";

	public static final String STOCK_DETAIL = "6005";

	public static final String STOCK_SUBMIT = "6006";

	public static final String ORDER_LIST = "6007";

	public static final String ORDER_DETAIL = "6008";

	public static final String ORDER_SUBMIT = "6009";

	public static final String BACKORDER_LIST = "6010";

	public static final String BACKORDER_DETAIL = "6011";

	public static final String BACKORDER_SUBMIT = "6012";

	public static final String PROMOTION_LIST = "6013";

	public static final String PROMOTION_DETAIL = "6014";

	public static final String PROMOTION_SUBMIT = "6015";

	public static final String CONTENDPROMOTION_LIST = "6016";

	public static final String CONTENDPROMOTION_ITEM = "6017";

	public static final String CONTENDPROMOTION_SUBMIT = "6018";

	public static final String CONTENDPRODUCT_SUBMIT = "6019";

	public static final String TERMINALPRODUCT_SUBMIT = "6020";

	public static final String TERMINALPRODUCT_QUERY = "6021";

	public static final String ORDER_ACCOUNT = "6025";

	public static final String CONTENDPRODUCT_DETAIL = "6027";

	public static final String TERMINALPRODUCT_LIST = "6029";

	public static final String FILE_UPLOAD = "7001";

	public static final String FILE_DOWNLOAD = "7002";

	// 心跳包——请求
	public static final String SOCKET_PULSE = "7003";

	// 定位客户端登录
	public static final String LOCATION_LOGIN = "8001";
	// 定位任务——推送
	public static final String LOCATION_TASK_PUSH = "8002";
	// 定位上报——请求
	public static final String SUBMIT_LOCATION = "8003";

	// 审批--未审批请假列表
	public static final String UNAUDIT_VACATION_LIST = "9002";
	// 审批--差旅审批
	public static final String AUDIT_VACATION = "9003";
	// 审批--未审批差旅列表
	public static final String UNAUDIT_TRAVEL_LIST = "9004";
	// 审批--差旅审批
	public static final String AUDIT_TRAVEL = "9005";
	// 审批--未审批拜访列表
	public static final String UNAUDIT_PLAN_LIST = "9006";
	// 审批--拜访审批
	public static final String AUDIT_PLAN = "9007";
	// 审批--未审批公告列表
	public static final String UNAUDIT_NOTICE_LIST = "9008";
	// 审批--公告审批
	public static final String AUDIT_NOTICE = "9009";
	// 审批--待审核数
	public static final String UNAUDIT_BADGES = "9010";
	// 我的工作首页-每日考勤/拜访/出差/反馈/汇报
	public static final String DATE_INFO = "9011";
	// 我的工作首页-每月考勤
	public static final String MONTH_INFO = "9012";
	// 审批--未审批列表
	public static final String UNAUDIT_LIST = "9013";
	// 收到短信消息
	public static final int SMS_RECEIVE = 0x10000001;

	// 位置改变消息
	public static final int LOCATION_CHANGE = 0x20000001;
	// 上报位置消息
	public static final int LOCATION_SEND = 0x20000002;

	// 数据交互成功消息
	public static final int TASK_SUCCESS = 0x30000001;
	// 数据交互失败消息
	public static final int TASK_FAILED = 0x30000002;

	// 请求唤醒消息
	public static final int WAKE_LOCK_TIME = 0x40000001;

	public static final int RESULT_SELECT_POSITION = 0x50000001;

	public static final int TRAVELLIST_BTN_CLICK = 0x50000002;

	public static final int RESULT_TRAVELLIST_REFRESH = 0x50000003;

	public static final int PLANLIST_BTN_CLICK = 0x50000004;

	public static final int RESULT_PLANLIST_REFRESH = 0x50000005;

	public static final int RESULT_TEMINAL_SELECT = 0x50000006;

	public static final int RESULT_PATROL_DOORPHOTO = 0x50000007;

	public static final int RESULT_SUGGESTION_DOORPHOTO = 0x20000007;

	public static final int RESULT_PATROL_PRODUCTPHOTO = 0x50000008;

	public static final int RESULT_PATROL_CONTENDPRODUCTPHOTO = 0x50000009;

	public static final int RESULT_PRODUCTITEM_REFRESH = 0x50000010;

	public static final int RESULT_BRAND_SELECT = 0x50000011;

	public static final int RESULT_CATEGORY_SELECT = 0x50000012;

	public static final int RESULT_PRODUCT_SELECT = 0x50000013;

	public static final int RESULT_PROMOTION_PHOTO = 0x50000014;

	public static final int RESULT_CONTENDPRODUCT_PHOTO = 0x50000015;

	public static final int SUGGESTIONLIST_BTN_CLICK = 0x50000016;

	public static final int RESULT_SUGGESTIONLIST_REFRESH = 0x50000017;

	public static final int TERMINALLIST_BTN_CLICK = 0x50000018;

	public static final int RESULT_TERMINALLIST_REFRESH = 0x50000019;

	public static final int ONGOING_NOTIFICATION = 0x50000020;

	public static final int BARCODE_SCAN = 0x50000021;

	public static final int ORDERLIST_REFRESH = 0x50000022;

	public static final int SALELIST_REFRESH = 0x50000023;

	public static final int STOCKLIST_REFRESH = 0x50000024;

	public static final int BACKORDERLIST_REFRESH = 0x50000025;

	public static final int PROMOTIONLIST_REFRESH = 0x50000026;

	public static final int WORKLIST_ADD = 0x50000027;

	public static final int RESULT_WORKLIST_REFRESH = 0x50000028;

	public static final int ATTANCE_ADD = 0x50000029;

	public static final int CONTENDPRODUCTLIST_ADD = 0x50000029;

	public static final int RESULT_ATTANCELIST_REFRESH = 0x50000030;

	public static final int ATTANCE_SEARCH = 0x50000101;

	public static final int RESULT_ATTANCELIST_SEARCH = 0x50000102;

	public static final int VACATION_ADD = 0x50000031;

	public static final int RESULT_VACATIONLIST_REFRESH = 0x50000032;

	public static final int VACATION_SEARCH = 0x50000036;

	public static final int RESULT_VACATION_SEARCH = 0x50000037;

	public static final int PLAN_ADD = 0x50000033;

	public static final int RESULT_PLAN_ADD = 0x50000040;

	public static final int PLAN_SEARCH = 0x50000038;

	public static final int RESULT_PLAN_SEARCH = 0x50000039;

	public static final int PLANLIST_REFRESH = 0x50000044;

	public static final int PLANPATROL_SUBMIT = 0x50000045;

	public static final int RESULT_PLANPATROL_SUBMIT = 0x50000046;

	public static final int PLANDELAY = 0x50000047;

	public static final int RESULT_PLANDELAY = 0x50000048;

	public static final int PLANSUBMIT = 0x50000049;

	public static final int RESULT_PLANSUBMIT = 0x50000049;

	public static final int WORKREPORT_ADD = 0x50000050;

	public static final int RESULT_WORKREPORT_ADD = 0x50000051;

	public static final int WORKREPORT_SEARCH = 0x50000052;

	public static final int RESULT_WORKREPORT_SEARCH = 0x50000053;

	public static final int TRAVEL_SEARCH = 0x50000054;

	public static final int RESULT_TRAVEL_SEARCH = 0x50000055;

	public static final int TRAVEL_ADD = 0x50000056;

	public static final int RESULT_TRAVEL_ADD = 0x50000057;

	public static final int TRAVEL_SUBMIT_DETAIL = 0x50000058;

	public static final int RESULT_TRAVEL_SUBMIT_DETAIL = 0x50000059;

	public static final int TRAVEL_MODEFY = 0x50000060;

	public static final int RESULT_TRAVEL_MODEFY = 0x50000061;

	public static final int TRAVEL_DESTINATION = 0x50000062;

	public static final int RESULT_TRAVEL_DESTINATION = 0x50000063;

	public static final int TRAVEL_EXECUTION = 0x50000064;

	public static final int RESULT_TRAVEL_EXECUTION = 0x50000065;

	public static final int CONTENDPRODUCT_BTN_CLICK = 0x50000034;

	public static final int AUDITNOTICE_LIST_BTN_CLICK = 0x50000035;

	public static final int RESULT_NOTICELIST_REFRESH = 0x50000026;

	public static final int RESULT_AUDITLIST_REFRESH = 0x50000100;

	public static final int TERMINALPRODUCTLIST_REFRESH = 0x50000101;

	public static final int RESULT_PRODUCTLIST_REFRESH = 0x50000102;

	public static final int RESULT_BRANDLIST_REFRESH = 0x50000103;

	public static final int RESULT_CATEGORYLIST_REFRESH = 0x50000104;

    public static final int TIME_OUT=5000;
}
