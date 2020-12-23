package cn.com.phinfo.oaact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar.LayoutParams;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.com.phinfo.adapter.CheckAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.base.CheckIn2BaseAct;
import cn.com.phinfo.protocol.AttentdsettingsRun.LocationsItem;
import cn.com.phinfo.protocol.AttentdsettingsRun.UserSettingsItem;
import cn.com.phinfo.protocol.AttentdsettingsRun.WifisItem;
import cn.com.phinfo.protocol.GetDailyRun;
import cn.com.phinfo.protocol.GetDailyRun.DailyItem;
import cn.com.phinfo.protocol.GetDailyRun.DailyResultBean;
import cn.com.phinfo.protocol.AttentdsettingsRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UserInfoRun;
import cn.com.phinfo.protocol.UserInfoRun.ItemUserInfo;
import cn.com.phinfo.protocol.UserInfoRun.UeserInfoResultBean;
import cn.com.phinfo.protocol.WorkCheckInRun;
import cn.com.phinfo.protocol.WorkCheckInRun.WorkCheckInRequest;

import com.alibaba.fastjson.JSON;
import com.datepickerview.CustomDatePicker;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshScrollView;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.CheckInSuccessDialog;
import com.heqifuhou.view.CircleImageView;
import com.heqifuhou.view.MyDatePick;
import com.heqifuhou.view.RadarView;

public class CheckInAct extends CheckIn2BaseAct implements OnItemClickListener {
	private static final int ID_GETINFO = 0x21;
	private LinearLayout emptyLst;
	private PullToRefreshScrollView refresh1;
	private int todayCount = 0;
	private String today;
	private CircleImageView photo;
	private TextView name, group, dateBtn;
	private ListView lst;
	private CheckAdapter adapter = null;
	private MyDatePick dateR1Pick;
	private View CheckInBtn = null, radarBtn = null;
	private TextView CheckDateTime = null, CheckText = null, tip = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.inittab1();
		onRefresh();
	}

	private CustomDatePicker customDatePicker1 = null;
	private void showR1DatePick() {
		if (customDatePicker1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
					Locale.CHINA);
			String now = sdf.format(new Date());
			customDatePicker1 = new CustomDatePicker(this,
					new CustomDatePicker.ResultHandler() {
						@Override
						public void handle(String time, int yyyy, int MM,
								int dd, int HH, int mm2) {
							dateBtn.setText(time);
							if (today.equals(time)) {
								quickHttpRequest(ID_GO_SETTING,
										new AttentdsettingsRun());
							} else {
								quickHttpRequest(ID_GETLIST, new GetDailyRun(
										time), time);
							}
						}
					}, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd
													// HH:mm，否则不能正常运行
			customDatePicker1.showSpecificTime(false); // 不显示时和分
			customDatePicker1.setIsLoop(false); // 不允许循环滚动
		}
		customDatePicker1.show(dateBtn.getText().toString());

		// if (dateR1Pick != null && dateR1Pick.isShowing()) {
		// return;
		// }
		// dateR1Pick = new MyDatePick(this, new OnDateSetListener() {
		// @Override
		// public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3)
		// {
		// String date = String.format("%04d-%02d-%02d", arg1, arg2 + 1,
		// arg3);
		// dateBtn.setText(date);
		// if(today.equals(date)){
		// quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
		// }else{
		// quickHttpRequest(ID_GETLIST, new GetDailyRun(date), date);
		// }
		//
		// // quickHttpRequest(ID_GETLIST, new AttendDetailRun(1,
		// // date, date, "2", "", ""), date);
		// }
		// }, year, month, day);
		// dateR1Pick.show();
	}

	private void inittab1() {
		tip = (TextView) this.findViewById(R.id.tip);
		emptyLst = (LinearLayout) this.findViewById(R.id.emptyLst);
		refresh1 = (PullToRefreshScrollView) this
				.findViewById(R.id.r1_rootrefres);
		refresh1.setMode(Mode.PULL_DOWN_TO_REFRESH);
		refresh1.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				setCurrDay();
				quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
			}
		});
		photo = (CircleImageView) this.findViewById(R.id.photo);
		name = (TextView) this.findViewById(R.id.name);
		dateBtn = (TextView) this.findViewById(R.id.dateBtn);
		group = (TextView) this.findViewById(R.id.group);
		dateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 查看历史记录
				showR1DatePick();
			}
		});
		lst = (ListView) this.findViewById(R.id.lst);
		adapter = new CheckAdapter();
		lst.setAdapter(adapter);
		lst.setOnItemClickListener(this);

		// 初始化数据
		name.setText(DataInstance.getInstance().getUserBody().getNickname());
		this.getAsyncAvatar(
				photo,
				LURLInterface.GET_AVATAR(DataInstance.getInstance()
						.getUserBody().getUserid()), DataInstance.getInstance()
						.getUserBody().getNickname());
		setCurrDay();
	}

	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(0);
	}

	protected void onStop() {
		super.onStop();
		if (mHandler != null && mHandler.hasMessages(0)) {
			mHandler.removeMessages(0);
		}
	}

	private int year, month, day;

	private void setCurrDay() {
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		String date = String.format("%04d-%02d-%02d", year, month + 1, day);
		today = date;
		dateBtn.setText(date);
		// quickHttpRequest(ID_GETLIST, new GetDailyRun(date), date);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			Calendar calendar = Calendar.getInstance();
			int h = calendar.get(Calendar.HOUR_OF_DAY);
			int m = calendar.get(Calendar.MINUTE);
			int s = calendar.get(Calendar.SECOND);
			String text = String.format("%02d:%02d:%02d", h, m, s);
			if (CheckDateTime != null) {
				CheckDateTime.setText(text);
			}
			mHandler.sendEmptyMessageDelayed(0, 200);
		}
	};

	protected void onRefresh() {
		super.onRefresh();
		this.quickHttpRequest(ID_GETINFO, new UserInfoRun());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (adapter == arg0.getAdapter()) {
			DailyItem it = adapter.getItem(arg2);
			Intent intent = new Intent(this, WebViewRefreshAct.class);
			intent.putExtra("TITLE", it.getBuildingName());
			intent.putExtra("URL", it.getDetailUrl());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	}

	private CheckInSuccessDialog dialog;

	private void showSuccDialog() {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		dialog = new CheckInSuccessDialog(this, request.getCheckTime(),
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						setCurrDay();
						quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
					}
				});
		dialog.show();
	}

	class Holder {
		// 雷达
		RadarView radarBtn;
		View root, CheckInBtn, emptypace;
		TextView date, title, title1, address, outBag, laterBag, updateAddress,
				wifi, updateReplacement, CheckText, CheckDateTime, tip;

	}

	private Holder[] holder = null;

	private void initBtn(int nNeedcount) {
		emptyLst.removeAllViews();
		holder = new Holder[nNeedcount];
		for (int i = 0; i < nNeedcount; i++) {
			holder[i] = new Holder();
			holder[i].root = this
					.getLayoutInflater(R.layout.r1_checkin_item_btn);
			emptyLst.addView(holder[i].root, LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			holder[i].date = (TextView) holder[i].root.findViewById(R.id.date);
			holder[i].radarBtn = (RadarView) holder[i].root
					.findViewById(R.id.radarBtn);
			holder[i].radarBtn.start();
			holder[i].title = (TextView) holder[i].root
					.findViewById(R.id.title);
			holder[i].title1 = (TextView) holder[i].root
					.findViewById(R.id.title1);
			holder[i].address = (TextView) holder[i].root
					.findViewById(R.id.address);
			holder[i].outBag = (TextView) holder[i].root
					.findViewById(R.id.outBag);
			holder[i].laterBag = (TextView) holder[i].root
					.findViewById(R.id.laterBag);
			holder[i].updateAddress = (TextView) holder[i].root
					.findViewById(R.id.updateAddress);
			holder[i].wifi = (TextView) holder[i].root.findViewById(R.id.wifi);
			holder[i].updateReplacement = (TextView) holder[i].root
					.findViewById(R.id.updateReplacement);
			holder[i].tip = (TextView) holder[i].root.findViewById(R.id.tip);
			holder[i].CheckInBtn = (LinearLayout) holder[i].root
					.findViewById(R.id.CheckInBtn);
			holder[i].emptypace = holder[i].root.findViewById(R.id.emptypace);
			holder[i].CheckText = (TextView) holder[i].root
					.findViewById(R.id.CheckText);
			holder[i].CheckDateTime = (TextView) holder[i].root
					.findViewById(R.id.CheckDateTime);

			if (i % 2 == 0) {
				holder[i].date.setBackgroundResource(R.drawable.oval_afb2b5_bg);
				holder[i].date.setText("上");
			} else {
				holder[i].date.setBackgroundResource(R.drawable.oval_55bdf2_bg);
				holder[i].date.setText("下");
			}
		}
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			// 自由打卡不存在上下班时间
		} else {
			String[] userSettingsTimes = new String[] {
					attendssettnigs.getUserSettings().getStartTime1(),
					attendssettnigs.getUserSettings().getEndTime1(),
					attendssettnigs.getUserSettings().getStartTime2(),
					attendssettnigs.getUserSettings().getEndTime2(),
					attendssettnigs.getUserSettings().getStartTime3(),
					attendssettnigs.getUserSettings().getEndTime3() };
			for (int i = 0; i < nNeedcount; i++) {
				if (i % 2 == 0) {
					holder[i].title1.setText("上班时间" + userSettingsTimes[i]);
				} else {
					holder[i].title1.setText("下班时间" + userSettingsTimes[i]);
				}
			}
		}
	}

	private void showEmptyBtn(List<DailyItem> lst) {
		int count = lst.size();
		//如果自由打卡，就不当前已打卡次数+1
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			count = count+1;
		}
		this.initBtn(count);
		todayCount = lst.size();
		// 找到已经打卡数
		for (int i = 0; i < lst.size(); i++) {
			DailyItem it = lst.get(i);
			if ("-1".equals(it.getAbnormalCode())) {
				todayCount = i;
				break;
			}
		}
		// 将已有的数据赋值进去，没有的数据也会返回
		for (int i = 0; i < lst.size(); i++) {
			DailyItem it = lst.get(i);
			holder[i].root.setTag(it);
			// 可以打卡
			if ("-1".equals(it.getAbnormalCode())) {
				// 默认都是不显示的
				continue;
			}
			// 0 – 上班打卡
			// 1 – 下班打卡
			// 2 - 外勤上班打卡
			// 3 - 外勤下班打开
			if ("2".equals(it.getCheckType()) || "3".equals(it.getCheckType())) {
				holder[i].outBag.setVisibility(View.VISIBLE);
				holder[i].address.setVisibility(View.VISIBLE);
				holder[i].wifi.setVisibility(View.GONE);
				// 防止地址和建筑是同一个名
				String s = it.getLocation();
				if (!it.getBuildingName().toLowerCase()
						.equals(it.getLocation().toLowerCase())) {
					s += it.getBuildingName();
				}
				holder[i].address.setText(s);
			}
			// 内勤的
			else {
				// 地址的
				if (ParamsCheckUtils.isNull(it.getWifiName())) {
					holder[i].address.setVisibility(View.VISIBLE);
					holder[i].wifi.setVisibility(View.GONE);
					holder[i].address.setText(it.getLocation()
							+ it.getBuildingName());
				}
				// wifi的
				else {
					holder[i].address.setVisibility(View.GONE);
					holder[i].wifi.setVisibility(View.VISIBLE);
					holder[i].wifi.setText(it.getWifiName() + "("
							+ it.getWifiAddress() + ")");
				}
				holder[i].outBag.setVisibility(View.GONE);
			}
			holder[i].title.setText("打卡时间" + it.getCheckTime());
			holder[i].updateReplacement.setVisibility(View.GONE);
			holder[i].updateAddress.setVisibility(View.GONE);
			if ("3".equals(attendssettnigs.getUserSettings()
					.getShiftMethodCode())) {
				// 自由打卡不存在上下班时间,也不存在迟到、早退、缺卡的说法
				holder[i].laterBag.setVisibility(View.GONE);
			} else {
				if ("1".equals(it.getAbnormalCode())) {
					holder[i].laterBag.setVisibility(View.VISIBLE);
					holder[i].laterBag.setText("迟到");
				} else if ("2".equals(it.getAbnormalCode())) {
					holder[i].laterBag.setVisibility(View.VISIBLE);
					holder[i].laterBag.setText("早退");
				} else if ("3".equals(it.getAbnormalCode())) {
					holder[i].laterBag.setVisibility(View.VISIBLE);
					holder[i].laterBag.setText("缺卡");
					holder[i].address.setVisibility(View.GONE);
					holder[i].wifi.setVisibility(View.GONE);
				} else {
					holder[i].laterBag.setVisibility(View.GONE);
				}
				String[] userSettingsTimes = new String[] {
						attendssettnigs.getUserSettings().getStartTime1(),
						attendssettnigs.getUserSettings().getEndTime1(),
						attendssettnigs.getUserSettings().getStartTime2(),
						attendssettnigs.getUserSettings().getEndTime2(),
						attendssettnigs.getUserSettings().getStartTime3(),
						attendssettnigs.getUserSettings().getEndTime3() };
				if (i % 2 == 0) {
					holder[i].title1.setText("(上班时间" + userSettingsTimes[i]
							+ ")");
				} else {
					holder[i].title1.setText("(下班时间" + userSettingsTimes[i]
							+ ")");
				}
			}
		}
		// 已经打卡数据补全，并且不显示
		int index = todayCount - 1;
		int nNeedCount = attendssettnigs.getUserSettings().getCheckTimes2Int();
		if (index >= 0 && index < nNeedCount) {
			// 重新打卡（只有最后一条记录才会显示）
			holder[index].updateAddress.setTag(lst.get(index));
			holder[index].updateAddress.setVisibility(View.VISIBLE);
			holder[index].updateAddress
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							DailyItem it = (DailyItem) arg0.getTag();
							checkRegisterAttence(it.getAttendanceEmpId());
						}
					});
		}
		// 不管是不是自由打卡，当前的肯定是要显示的
		if (todayCount >= 0 && todayCount < nNeedCount) {
			holder[todayCount].root.setVisibility(View.VISIBLE);
		}
		// 自由打卡不显示后面的样式
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			for (int i = todayCount + 1; i < count; i++) {
				// 自由打卡
				holder[i].root.setVisibility(View.GONE);
			}
		} 
		//非自由打卡拉大一下间距
		else {
			// 调整间距，对于没有打卡的需要拉大一点间距。
			// 实际自由打卡的用户后面都不显示，自然也不需要这点间距。
			for (int i = 0; i < nNeedCount; i++) {
				if (i <= todayCount) {
					holder[i].emptypace.setVisibility(View.GONE);
				} else {
					holder[i].emptypace.setVisibility(View.VISIBLE);
				}
			}
		}
		// 设置当前雷达
		setCurrRadarBtn();
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpResult(id, obj, requestObj);
		if (ID_GETINFO == id) {
			if (obj.isOK()) {
				UeserInfoResultBean o = (UeserInfoResultBean) obj;
				if (!o.getData().isEmpty()) {
					ItemUserInfo userInfo = o.getData().get(0);
					group.setText(userInfo.getDeptName());
				}
			}
		}
		// 重复判断一下
		if (ID_GO_SETTING == id) {
			//规则读取失败
			if (obj.isOK()) {
				adapter.setAttentdSettingsData(attendssettnigs);
				showEmptyBtn(attendssettnigs.getAttendData());
				attendssettnigsStates();
				adapter.clear();
				emptyLst.setVisibility(View.VISIBLE);
				lst.setVisibility(View.GONE);
			}
		}
		// 取得列表
		if (id == ID_GETLIST) {
			if (obj.isOK()) {
				DailyResultBean o = (DailyResultBean) obj;
				adapter.replaceListRef(o.getListData());
				emptyLst.setVisibility(View.GONE);
				lst.setVisibility(View.VISIBLE);
				// }
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		// 提交
		if (ID_CHECKIN == id) {
			bSubmit = false;
			if (obj.isOK()) {
				showSuccDialog();
			} else {
				showToast(obj.getMsg());
			}
			return;
		}

	}

	private void setCurrRadarBtn() {
		if (radarBtn != null) {
			radarBtn.setVisibility(View.GONE);
		}
		if (CheckInBtn != null) {
			CheckInBtn.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
		}
		// 自由打卡不限定打卡次数
		// 已经打够卡了。后面的不需要执行了
		int nNeedcount = attendssettnigs.getUserSettings().getCheckTimes2Int();
		if (todayCount >= nNeedcount) {
			return;
		}
		CheckDateTime = holder[todayCount].CheckDateTime;
		CheckInBtn = holder[todayCount].CheckInBtn;
		CheckText = holder[todayCount].CheckText;
		tip = holder[todayCount].tip;
		radarBtn = holder[todayCount].radarBtn;
		radarBtn.setVisibility(View.VISIBLE);
	}

	private void showCurrChekinBtn() {
		radarBtn.setVisibility(View.GONE);
		this.tip.setVisibility(View.VISIBLE);
		this.CheckInBtn.setVisibility(View.VISIBLE);
	}


	// 设置是内，还是外
	private void attendssettnigsStates() {
		// 启动态检查
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				attendssettnigsStates();
			}
		}, 3000);
		// 下面的都会在动态检查的响应下做事
		if (attendssettnigs == null) {
			// 没有取到规则,重新取一下
			onRefresh();
			return;
		}
		if (loc == null) {
			return;
		}
		if (holder == null) {
			return;
		}
		// 自由打卡不限定打卡次数(自由打卡返回的nNeedcount值里面加了很多值）
		// 已经打够卡了。后面的不需要执行了
		int nNeedcount = attendssettnigs.getUserSettings().getCheckTimes2Int();
		if (todayCount >= nNeedcount) {
			return;
		}
		// 没有网络连接
		if (checkNetConnected(this) == 3) {
			showToast("没有检测到网络，请检查网络连接");
			return;
		}
		// 显示当前按钮
		showCurrChekinBtn();
		UserSettingsItem userIt = attendssettnigs.getUserSettings();
		int state = userIt.getCurrState(todayCount);
		if (todayCount % 2 == 0) {
			this.CheckText.setText("上班打卡");
		} else {
			this.CheckText.setText("下班打卡");
		}
		// 自由打卡全是正常的
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			// if (todayCount % 2 == 0) {
			// this.CheckText.setText("上班打卡");
			// } else {
			// this.CheckText.setText("下班打卡");
			// }
			this.CheckInBtn
					.setBackgroundResource(R.drawable.oval_55bdf2_selector);
		} else {
			// -1迟到，0，正常，1早退
			if (state == -1) {
				// 客户不让提示这种信息了，只显示上下班打卡
				// this.CheckText.setText("迟到打卡");
				this.CheckInBtn
						.setBackgroundResource(R.drawable.oval_ed6d00_selector);
			}
			// 1早退
			else if (state == 1) {
				// 客户不让提示这种信息了，只显示上下班打卡
				// this.CheckText.setText("早退打卡");
				this.CheckInBtn
						.setBackgroundResource(R.drawable.oval_ed6d00_selector);
			}
			// 0正常
			else {
				// if (todayCount % 2 == 0) {
				// this.CheckText.setText("上班打卡");
				// } else {
				// this.CheckText.setText("下班打卡");
				// }
				this.CheckInBtn
						.setBackgroundResource(R.drawable.oval_55bdf2_selector);
			}
		}
		// 0正在找状态 1wifi，2LBS，3OUT
		CRetCurrCheckState rtState = getCurrCheckState();
		if (rtState.state == 1) {
			tip.setText("已在考勤范围内:" + rtState.wifiItem.getName());
			this.CheckInBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					checkRegisterAttence();
				}
			});
			return;
		}
		if (rtState.state == 2) {
			tip.setText(Html.fromHtml("已在考勤范围内："
					+ "<a style='color:blue;' href='#'>重新定位</a>"));
			textHtmlClick(tip, new ClickableSpan() {
				@Override
				public void onClick(View arg0) {
					setReLoc();
				}
			});
			this.CheckInBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					checkRegisterAttence();
				}
			});
			return;
		}

		tip.setText(Html.fromHtml("当前考勤不在考勤范围内:"
				+ "<a style='color:blue;' href='#'>查看办公WI-FI</a>"));
		textHtmlClick(tip, new ClickableSpan() {
			@Override
			public void onClick(View arg0) {
				if (android.os.Build.VERSION.SDK_INT > 10) {
					// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
					startActivity(new Intent(
							android.provider.Settings.ACTION_SETTINGS));
				} else {
					startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
			}
		});
		// 自由打卡全是正常的
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			this.CheckInBtn
					.setBackgroundResource(R.drawable.oval_3bc2b5_selector);
		} else {
			// 打外勤
			if (state == 0) {
				this.CheckInBtn
						.setBackgroundResource(R.drawable.oval_3bc2b5_selector);
			}
		}
		this.CheckInBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkRegisterAttence();
			}
		});
	}

	private void textHtmlClick(TextView tv, ClickableSpan myURLSpan) {
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		CharSequence text = tv.getText();
		if (text instanceof Spannable) {
			int end = text.length();
			Spannable sp = (Spannable) text;
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.clearSpans();// should clear old spans
			for (URLSpan url : urls) {
				style.setSpan(myURLSpan, sp.getSpanStart(url),
						sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
			tv.setText(style);
		}
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		refresh1.onRefreshComplete();
	}

	private WorkCheckInRequest request;

	private void checkRegisterAttence() {
		checkRegisterAttence("");
	}

	public class CRetCurrCheckState {
		public int state = 0;
		public WifisItem wifiItem;
		public LocationsItem locationsItem;
	}

	// 0正在找状态 1wifi，2LBS，3OUT
	private CRetCurrCheckState getCurrCheckState() {
		if (checkNetConnected(this) == 2) {
			// 判断是否是我们期望的网
			WifiInfo wifi = getLocalMacAddress();
			WifisItem it = attendssettnigs.isWifiWork(
					wifi.getSSID().replace("\"", ""), wifi.getBSSID());
			if (it != null) {
				CRetCurrCheckState rt = new CRetCurrCheckState();
				rt.state = 1;
				rt.wifiItem = it;
				return rt;
			}
		}
		// 判断是否在设定的范围之内
		double distanceRange = attendssettnigs.getGlobalSettings()
				.getDistanceRange();
		LocationsItem it = attendssettnigs.isInDistanceRange(loc.getLatitude(),
				loc.getLongitude(), distanceRange);
		if (it != null) {
			CRetCurrCheckState rt = new CRetCurrCheckState();
			rt.state = 2;
			rt.locationsItem = it;
			return rt;
		}
		CRetCurrCheckState rt = new CRetCurrCheckState();
		rt.state = 3;
		rt.locationsItem = it;
		return rt;
	}

	private boolean bSubmit = false;

	private void checkRegisterAttence(String id) {
		if (bSubmit) {
			return;
		}
		//没有网络
		if (checkNetConnected(this) == 3) {
			showToast("没有检测到网络，请检查网络连接");
			return;
		}
		int nReadTodayCount = todayCount;
		// 如果是更新打卡，则次数少一次
		if (!ParamsCheckUtils.isNull(id)) {
			nReadTodayCount = Math.max(0, todayCount - 1);
		}
		request = WorkCheckInRequest.init(loc);
		// 0上班
		request.setCheckType(nReadTodayCount % 2 == 0 ? "0" : "1");
		request.setId(id);
		request.setOnTime(attendssettnigs.getUserSettings().getOnTime(
				nReadTodayCount));
		CRetCurrCheckState rtState = getCurrCheckState();
		// 不在工作地址
		if (rtState.state == 3) {
			// -1迟到，0，正常，1早退
			int satus = attendssettnigs.getUserSettings().getCurrState(
					nReadTodayCount);
			if (satus == -1) {
				request.setAbnormalCode("1");
			} else if (satus == 1) {
				request.setAbnormalCode("2");
			}else{
				request.setAbnormalCode("0");
			}
			request.setCheckType(nReadTodayCount % 2 == 0 ? "2" : "3");
			Intent intent = new Intent(this, CheckInLBSAct.class);
			intent.putExtra("WorkCheckInRequest", JSON.toJSONString(request));
			intent.putExtra("AttentdSettingsData",
					JSON.toJSONString(attendssettnigs));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return;
		}
		// wifi
		if (rtState.state == 1) {
			request.setWifiName(rtState.wifiItem.getName());
			request.setWifiAddress(rtState.wifiItem.getAddress());
		}
		// 在工作地点
		else if (rtState.state == 2) {
			request.setBuildingName(rtState.locationsItem.getName());
		}
		// 自由打卡
		if ("3".equals(attendssettnigs.getUserSettings().getShiftMethodCode())) {
			// 想打卡就打卡
			this.quickHttpRequest(ID_CHECKIN, new WorkCheckInRun(request));
			bSubmit = true;
			return;
		}
		// -1迟到，0，正常，1早退
		int satus = attendssettnigs.getUserSettings().getCurrState(
				nReadTodayCount);
		// 0，正常直接打卡
		if (satus == 0) {
			this.quickHttpRequest(ID_CHECKIN, new WorkCheckInRun(request));
			bSubmit = true;
			return;
		}
		// 迟到或者早退都要弹出提示
		boolean bLeater = true;
		if (satus == -1) {
			request.setAbnormalCode("1");
			bLeater = true;
		} else if (satus == 1) {
			request.setAbnormalCode("2");
			bLeater = false;
		}else{
			request.setAbnormalCode("0");
		}
		Intent intent = new Intent(this, CheckDialogAct.class);
		intent.putExtra("BLEATER", bLeater);
		intent.putExtra("WorkCheckInRequest", JSON.toJSONString(request));
		if (rtState.state == 1) {
			intent.putExtra("ADDRESS", request.getWifiName());
		} else {
			String s = request.getLocation();
			if (!request.getBuildingName().toLowerCase()
					.equals(request.getLocation().toLowerCase())) {
				s += request.getBuildingName();
			}
			intent.putExtra("ADDRESS", s);
		}
		intent.putExtra("TIME", request.getCheckTime());
		intent.putExtra("AttentdSettingsData",
				JSON.toJSONString(attendssettnigs));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private int checkNetConnected(Context context) {
		try{
			ConnectivityManager ConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = ConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo!=null&&mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
				if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					return 1; // 返回1，连接的是移动网络
				} else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					return 2; // 返回2，连接的是wifi
				}
			} else {
				return 3; // 返回3，没有连接。
			}	
		}catch(Exception e){
			
		}
		return 3;
	}

	private WifiInfo getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info;
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		// 注册事件
		if (IBroadcastAction.ACTION_CHECKIN.equals(intent.getAction())) {
			setCurrDay();
			quickHttpRequest(ID_GO_SETTING, new AttentdsettingsRun());
			return;
		}
		super.onBroadcastReceiverListener(context, intent);
	}

}
