package com.zjrc.isale.client.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;
import com.zjrc.isale.client.R;
import com.zjrc.isale.client.application.ISaleApplication;
import com.zjrc.isale.client.bean.Config;
import com.zjrc.isale.client.bean.Constant;
import com.zjrc.isale.client.service.AuditNotifyService;
import com.zjrc.isale.client.ui.fragment.AuditFragment;
import com.zjrc.isale.client.ui.fragment.BaseFragment;
import com.zjrc.isale.client.ui.fragment.BusinessFragment;
import com.zjrc.isale.client.ui.fragment.CustomerFragment;
import com.zjrc.isale.client.ui.fragment.NoticeFragment;
import com.zjrc.isale.client.ui.fragment.ProfileFragment;
import com.zjrc.isale.client.ui.fragment.RosterFragment;
import com.zjrc.isale.client.ui.fragment.SettingFragment;
import com.zjrc.isale.client.ui.fragment.WorksFragment;
import com.zjrc.isale.client.ui.widgets.CustomAlertDialog;
import com.zjrc.isale.client.ui.widgets.menudrawer.BaseMenuActivity;
import com.zjrc.isale.client.ui.widgets.menudrawer.Item;
import com.zjrc.isale.client.ui.widgets.menudrawer.MenuDrawer;
import com.zjrc.isale.client.ui.widgets.menudrawer.Position;
import com.zjrc.isale.client.ui.widgets.menudrawer.User;
import com.zjrc.isale.client.util.UpdateUtil;
import com.zjrc.isale.client.util.xml.XmlNode;
import com.zjrc.isale.client.util.xml.XmlValueUtil;

import java.util.HashMap;

/**
 * @项目名称：销售管家客户端
 * @版本号：V2.00
 * @创建者: 贺彬
 * @功能描述：主界面
 */
public class MainActivity extends BaseMenuActivity implements OnClickListener {

	private static final String STATE_CURRENT_FRAGMENT = "STATE_CURRENT_FRAGMENT";
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private String mCurrentFragmentTag;// 当前fragment分页标记
	private ImageView iv_title_menu_alert;// 顶部menu导航按钮上的红点，当存在未处理审批时显示
	private TextView tv_titlebar_title; // title名称;
	private RelativeLayout rl_titlebar_main;

    public RelativeLayout getRl_titlebar_main() {
        return rl_titlebar_main;
    }

    public void setRl_titlebar_main(RelativeLayout rl_titlebar_main) {
        this.rl_titlebar_main = rl_titlebar_main;
    }

    private CustomAlertDialog alertDialog;
	private boolean isAudit = false;

    public static final String FILTER = "com.zjrc.isale.client_AUDIT_NOTIFICATION";
    public static final String REFETCH_FILTER = "com.zjrc.isale.client_AUDIT_REFETCH";


    // 上一次未审批数
    private int mVacation = -1;
    private int mTravel = -1;
    private int mPlan = -1;
    private int mNotice = -1;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==1) {
			       // 重新获取未审批数量
	            sendQuery();
			}else{
			int total = (Integer) msg.obj;

			// 更新侧栏未审批数
			((Item) mAdapter.getItem(4)).mNum = total;
			mAdapter.notifyDataSetChanged();

			// 更新菜单按钮红点
			if (total > 0) {
				iv_title_menu_alert.setVisibility(View.VISIBLE);
			} else {
				iv_title_menu_alert.setVisibility(View.GONE);
			}
			}
		}
	};

	private BroadcastReceiver auditRecivier = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Message msg = Message.obtain(mHandler, 0);
			msg.obj = intent.getIntExtra("total", 0);
			mHandler.sendMessage(msg);
		}
	};

    @Override
    protected void onCreate(final Bundle inState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(inState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_normal_main);
        final View btn_titlebar_add = findViewById(R.id.btn_titlebar_add);
        UpdateUtil updateUtil = new UpdateUtil(this);
        updateUtil.setOnCancelEventListener(new UpdateUtil.OnCancelEventListener() {
            @Override
            public void onCancel() {
                btn_titlebar_add.setVisibility(View.GONE);
                init(inState);
            }
        });

        if (updateUtil.checkUpdate(true, false)) {
            btn_titlebar_add.setVisibility(View.GONE);
        } else {
            init(inState);
        }
    }

    private void init(Bundle inState) {
        tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);
        iv_title_menu_alert = (ImageView) findViewById(R.id.iv_titlebar_menu_alert);
        rl_titlebar_main = (RelativeLayout) findViewById(R.id.rl_titlebar_main);

        initFragments(inState);

        ISaleApplication application = (ISaleApplication) getApplication();
        if (application != null) {
            Config config = application.getConfig();
            isAudit = config.getNeedaudit() && config.getIsaudit();
        }

        if (isAudit) {
            registerReceiver(auditRecivier, new IntentFilter(
                    AuditNotifyService.FILTER));
            registerReceiver(refetchReceiver, new IntentFilter(REFETCH_FILTER));
            sendQuery();
        }
    }

	/**
	 * 初始化fragment
	 *
	 * @param inState
	 */
	private void initFragments(Bundle inState) {
		mFragmentManager = getSupportFragmentManager();
		if (inState != null) {
			mCurrentFragmentTag = inState.getString(STATE_CURRENT_FRAGMENT);
		} else {
			mCurrentFragmentTag = ((Item) mAdapter.getItem(1)).mTitle;
			attachFragment(mMenuDrawer.getContentContainer().getId(),
					getFragment(mCurrentFragmentTag), mCurrentFragmentTag);
			commitTransactions();
			tv_titlebar_title.setText("销售管家");
		}
		mMenuDrawer
				.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
					@Override
					public void onDrawerStateChange(int oldState, int newState) {
						if (newState == MenuDrawer.STATE_CLOSING) {
							commitTransactions();
							getFragment(mCurrentFragmentTag)
									.reSet_TitleBar_Right_Btn(false);

						} else if (newState == MenuDrawer.STATE_OPEN) {

							getFragment(mCurrentFragmentTag)
									.reSet_TitleBar_Right_Btn(true);

							sendQuery();
						} else if (newState == MenuDrawer.STATE_CLOSED) {

						}
					}
					@Override
					public void onDrawerSlide(float openRatio, int offsetPixels) {
						// Do nothing
					}
				});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(STATE_CURRENT_FRAGMENT, mCurrentFragmentTag);
	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	protected FragmentTransaction ensureTransaction() {
		if (mFragmentTransaction == null) {
			mFragmentTransaction = mFragmentManager.beginTransaction();
			// mFragmentTransaction
			// .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		return mFragmentTransaction;
	}

	private BaseFragment getFragment(String tag) {
		BaseFragment f = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
		if (f == null) {
			if (tag.equals("我的信息")) {
				f = new ProfileFragment();
			} else if (tag.equals("我的工作")) {
				f = new WorksFragment();
			} else if (tag.equals("我的业务")) {
				f = new BusinessFragment();
			} else if (tag.equals("我的客户")) {
				f = new CustomerFragment();
			} else if (tag.equals("我的审批")) {
				f = new AuditFragment();
			} else if (tag.equals("通讯录")) {
				f = new RosterFragment();
			} else if (tag.equals("新闻公告")) {
				f = new NoticeFragment();
			} else if (tag.equals("设置")) {
				f = new SettingFragment();
			}
		}
        if (f!=null) {
            f.setRl_titlebar_main(rl_titlebar_main);
        }
		try {
			f.reSet_TitleBar_Main();// 重刷新title
		} catch (Exception e) {
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			Toast.makeText(this, "未知错误[" + e.toString() + "],请重新登录",
					Toast.LENGTH_LONG).show();
			startActivity(intent);
            ISaleApplication.getInstance().dropTask();
        }
		return f;
	}

	protected void attachFragment(int layout, BaseFragment f, String tag) {
		if (f != null) {
			if (f.isDetached()) {
				ensureTransaction();
				mFragmentTransaction.attach(f);
			} else if (!f.isAdded()) {
				ensureTransaction();
				mFragmentTransaction.add(layout, f, tag);
			}
		}
	}

	protected void detachFragment(BaseFragment f) {
		if (f != null && !f.isDetached()) {
			ensureTransaction();
			mFragmentTransaction.detach(f);
		}
	}

	protected void commitTransactions() {
		if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
			mFragmentTransaction.commit();
			mFragmentTransaction = null;
		}
	}

	/*
	 * 侧滑菜单导航items点击监听
	 */
	@Override
	protected void onMenuItemClicked(int position, Object item) {
		if (item instanceof Item) {
			Item the_item = (Item) item;
			if (mCurrentFragmentTag != null
					&& !mCurrentFragmentTag.equals(the_item.mTitle)) {
				if (mCurrentFragmentTag != null)
					detachFragment(getFragment(mCurrentFragmentTag));
				attachFragment(mMenuDrawer.getContentContainer().getId(),
						getFragment(the_item.mTitle), the_item.mTitle);
				mCurrentFragmentTag = the_item.mTitle;
			}
		} else if (item instanceof User) {
			User the_user = (User) item;
			if (mCurrentFragmentTag != null
					&& !mCurrentFragmentTag.equals(the_user.mTitle)) {
				if (mCurrentFragmentTag != null)
					detachFragment(getFragment(mCurrentFragmentTag));
				attachFragment(mMenuDrawer.getContentContainer().getId(),
						getFragment(the_user.mTitle), the_user.mTitle);
				mCurrentFragmentTag = the_user.mTitle;
			}
		}
		mMenuDrawer.toggleMenu();
	}

	/*
	 * 顶部title按钮封装，所有子fragment的顶部按钮都有不同，但都有此处统一分发点击事件;
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_titlebar_main:
			break;
		case R.id.rl_titlebar_menu:
			mMenuDrawer.toggleMenu();
			break;
		case R.id.btn_titlebar_add:
			if (mMenuDrawer.isMenuVisible()) {
				mMenuDrawer.toggleMenu();
			} else {
				getFragment(mCurrentFragmentTag).onClick(v);
			}
			break;
		case R.id.tv_titlebar_selector:
			getFragment(mCurrentFragmentTag).onClick(v);
			break;
		case R.id.rl_date:// 我的工作fragment时间选择
			getFragment(mCurrentFragmentTag).onClick(v);
			break;
		default:
			getFragment(mCurrentFragmentTag).onClick(v);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			mMenuDrawer.toggleMenu();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 返回按鈕(控制所有子fragment状态的物理返回按鈕事件)
	 */
	@Override
	public void onBackPressed() {
		if (mMenuDrawer.isMenuVisible()) {
			mMenuDrawer.closeMenu();
		} else if (!getFragment(mCurrentFragmentTag).onBackPressed()) {
			exitApplication();
		}
	}

	private void exitApplication() {
		alertDialog = new CustomAlertDialog(this, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MainActivity.this.finish();
				alertDialog.cancel();
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alertDialog.cancel();
			}
		});
		alertDialog.show();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		getFragment(mCurrentFragmentTag).onActivityResult(arg0, arg1, arg2);

	}

	/*
	 * 友盟统计
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

    public void sendQuery() {
         ISaleApplication application = (ISaleApplication)getApplication();
        if (application != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("audituserid", XmlValueUtil.encodeString(application.getConfig().getUserid()));
            request("audit!unauditBadgesCount?code=" + Constant.UNAUDIT_BADGES, params, FLAG_SHOW_PROGRESS | FLAG_SHOW_ERROR | FLAG_CANCEL);
        }
    }

    @Override
    public void onRecvData(XmlNode response) {

    }

    // 重新获取广播接收者，供审批成功/新增审批任务成功后使用
    private BroadcastReceiver refetchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain(mHandler, 1);
            mHandler.sendMessage(msg);
        }
    };

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isAudit) {
			unregisterReceiver(refetchReceiver);
			unregisterReceiver(auditRecivier);
		}
	}
    @Override
    public void onRecvData(JsonObject response) {
        if (response != null) {
            String code = response.get("code").getAsString();
            if (Constant.UNAUDIT_BADGES.equals(code)) {
                JsonObject jsonObject = response.getAsJsonObject("body");
                int vacation = Integer.parseInt(jsonObject.get("vacation").getAsString());
                int travel = Integer.parseInt(jsonObject.get("travel").getAsString());
                int plan = Integer.parseInt(jsonObject.get("plan").getAsString());
                int notice = Integer.parseInt(jsonObject.get("notice").getAsString());
                int total = Integer.parseInt(jsonObject.get("totalnum").getAsString());
                // 只在和上次审批数不一致时发送广播
                if (vacation != mVacation || travel != mTravel || plan != mPlan || notice != mNotice) {
                    Intent intent = new Intent(FILTER);
                    intent.putExtra("total", total);
                    intent.putExtra("vacation", vacation);
                    intent.putExtra("travel", travel);
                    intent.putExtra("plan", plan);
                    intent.putExtra("notice", notice);
                    // 通知UI更新
                    sendBroadcast(intent);
                    mVacation = vacation;
                    mTravel = travel;
                    mPlan = plan;
                    mNotice = notice;
                    // 存入application，供审批页面初始化时使用
                    ISaleApplication application = ISaleApplication.getInstance();
                    application.setUnAuditNumbers(new int[] {total, plan, travel, notice, vacation});
                }
            }
        }
    }
}
