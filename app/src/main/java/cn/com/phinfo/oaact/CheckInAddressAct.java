package cn.com.phinfo.oaact;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ZoomControls;
import cn.com.phinfo.adapter.AttendDetailAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.AttendDetailRun;
import cn.com.phinfo.protocol.AttendDetailRun.AttendDetailItem;
import cn.com.phinfo.protocol.AttendDetailRun.AttendDetailResultBean;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.datepickerview.CustomDatePicker;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;
import com.heqifuhou.utils.CalendarUtils;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyDatePick;

public class CheckInAddressAct extends HttpMyActBase implements
		OnItemClickListener, OnClickListener, BDLocationListener {
	private static int ID_GETLIST = 0x10, ID_NOTIFY = 0x12, ID_HISTORY = 0x13;
	private EditText selEdit;
	private TextView title, checkInDate, address, checkCountTip;
	private LocationClient mLocClient;
	private PowerManager.WakeLock mWakeLock;
	private TextureMapView mMapView;
	private BaiduMap mBaiduMap;
	boolean isFirstLoc = true; // 是否首次定位
	private PullToRefreshListView lstView;
	private TextView createCount, createName, dateBtn;
	private RadioGroup rg;
	private int pageNumber;
	private AttendDetailAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("签到");
		this.addViewFillInRoot(R.layout.act_checkaddress);
		this.addBottomView(R.layout.out_check_tools_bar);
		selEdit = (EditText) this.findViewById(R.id.selEdit);
		title = (TextView) this.findViewById(R.id.title);
		checkCountTip = (TextView) this.findViewById(R.id.checkCountTip);
		checkInDate = (TextView) this.findViewById(R.id.checkInDate);
		address = (TextView) this.findViewById(R.id.address);
		this.findViewById(R.id.selBtn).setOnClickListener(this);
		this.findViewById(R.id.checkInBtn).setOnClickListener(this);
		this.initBaidu();
		this.initTab();
		this.initTab2();
	}


	private void initTab2() {
		dateBtn = (TextView) this.findViewById(R.id.dateBtn);
		createCount = (TextView) this.findViewById(R.id.createCount);
		createName = (TextView) this.findViewById(R.id.createName);
		lstView = (PullToRefreshListView) this.findViewById(R.id.lstView);
		lstView.setMode(Mode.PULL_FROM_START);
		dateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 日期
				showMonPicker();
			}
		});
		createName.setText(DataInstance.getInstance().getName());
		final Calendar localCalendar = Calendar.getInstance();
		oldYear = localCalendar.get(Calendar.YEAR);
		oldMonth = localCalendar.get(Calendar.MONTH);
		dateBtn.setText(String.format("%04d-%02d", oldYear, oldMonth + 1));

		adapter = new AttendDetailAdapter();
		lstView.setAdapter(adapter);
		lstView.setOnItemClickListener(this);
	}

	private int oldYear, oldMonth;
	private MyDatePick myDatePick = null;
	private CustomDatePicker customDatePicker1 = null;

	public void showMonPicker() {
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
							oldYear = yyyy;
							oldMonth = MM;
							pageNumber = 1;
							onRefresh();
						}
					}, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd
													// HH:mm，否则不能正常运行
			customDatePicker1.showSpecificDay(false); // 不显示时和分,天
			customDatePicker1.setIsLoop(false); // 不允许循环滚动
		}
		customDatePicker1.show(dateBtn.getText().toString());
		// if (myDatePick != null && myDatePick.isShowing()) {
		// return;
		// }
		// myDatePick = new MyDatePick(this,
		// new DatePickerDialog.OnDateSetListener() {
		// @Override
		// public void onDateSet(DatePicker view, int year,
		// int monthOfYear, int dayOfMonth) {
		// if (oldYear != year || oldMonth != monthOfYear) {
		// oldYear = year;
		// oldMonth = monthOfYear;
		// dateBtn.setText(String.format("%04d-%02d", oldYear,
		// oldMonth + 1));
		// pageNumber = 1;
		// onRefresh();
		// }
		// }
		// }, oldYear, oldMonth, 1, false);
		// myDatePick.show();
	}

	private void initTab() {
		rg = (RadioGroup) this.findViewById(R.id.main_radio);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == R.id.check_btn) {
					findViewById(R.id.tab1).setVisibility(View.VISIBLE);
					findViewById(R.id.tab2).setVisibility(View.GONE);
					addTextNav("签到");
				}
				if (arg1 == R.id.history_btn) {
					findViewById(R.id.tab1).setVisibility(View.GONE);
					findViewById(R.id.tab2).setVisibility(View.VISIBLE);
					addTextNav("足迹");
					pageNumber = 1;
					onRefresh();
				}
			}
		});
	}

	private void initBaidu() {
		mMapView = (TextureMapView) findViewById(R.id.bmap_View);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		// 隐藏比例尺
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // 设置为普通模式的地图
		// 隐藏百度logo ZoomControl
		int count = mMapView.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = mMapView.getChildAt(i);
			if (child instanceof ImageView || child instanceof ZoomControls) {
				child.setVisibility(View.INVISIBLE);
			}
		}
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this.getApplicationContext());
		mLocClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");
		// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);
		// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);
		// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);
		// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);
		// 可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);
		// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);
		// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);
		// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);
		// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);
		// 可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
		mLocClient.setLocOption(option);
		mLocClient.start();
		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent motionEvent) {
				startLBSAddress();
			}
		});
		mHandler.sendEmptyMessage(0);
	}

	private void showTime() {
		final Calendar mCalendar = Calendar.getInstance();
		int mHour = mCalendar.get(Calendar.HOUR);
		int mMinuts = mCalendar.get(Calendar.MINUTE);
		String s = String.format("%02d:%02d", mHour, mMinuts);
		checkInDate.setText(s);
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			showTime();
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	};

	private void startLBSAddress() {
		Intent intent = new Intent(CheckInAddressAct.this, LBSAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, ID_GETLIST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_GETLIST && resultCode == RESULT_OK) {
			title.setText(data.getExtras().getString("Bulding"));
			address.setText(data.getExtras().getString("ADDRESS"));
			return;
		}
		if (ID_NOTIFY == requestCode) {
			// 取前
			List<UnitandaddressItem> lst = DataInstance.getInstance()
					.getUnitandaddressItemList();
			String s = "";
			for (int i = 0; i < lst.size(); i++) {
				if (i > 0) {
					s += ",";
				}
				s += lst.get(i).GET_USER_NAME();
			}
			selEdit.setText(s);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onRefresh() {
		// 查看历史记录
		if (rg.getCheckedRadioButtonId() == R.id.history_btn) {
			String startTime = String.format("%d-%02d-%02d", oldYear,
					oldMonth + 1, 01);
			String endTime = String.format("%d-%02d-%02d", oldYear,
					oldMonth + 1,
					CalendarUtils.getDaysByYearMonth(oldYear, oldMonth + 1));
			this.quickHttpRequest(ID_HISTORY, new AttendDetailRun(pageNumber,
					startTime, endTime));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		AttendDetailItem it = adapter.getItem(arg2 - 1);
		Intent intent = new Intent(this, WebViewRefreshAct.class);
		intent.putExtra("TITLE", "拜访详情");
		intent.putExtra("URL", it.getDetailUrl());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent, ID_NOTIFY);
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (ID_HISTORY == id) {
			if (obj.isOK()) {
				AttendDetailResultBean o = (AttendDetailResultBean) obj;
				if (pageNumber == 1) {
					adapter.clear();
				}
				pageNumber++;
				adapter.addToListBack(o.getListData());
				if (o.getListData().size() < 15) {
					lstView.setMode(Mode.PULL_FROM_START);
				} else {
					lstView.setMode(Mode.BOTH);
				}
				if (adapter.getCount() <= 0) {
					lstView.setEmptyView(this.getEmptyView());
				} else {
					this.removeEmptyView();
				}
			} else {
				showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		if (R.id.selBtn == arg0.getId()) {
			Intent intent = new Intent(this, SelectPersonAct.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivityForResult(intent, ID_NOTIFY);
		}
		if (R.id.checkInBtn == arg0.getId()) {
			if (address.getText().toString().trim().equals("")) {
				showToast("地址不能为空");
				return;
			}
			// if (ParamsCheckUtils.isNull(selEdit.getText().toString().trim()))
			// {
			// showToast("拜访对象不能为空");
			// return;
			// }
			Intent intent = new Intent(this, OutCheckInUploadAct.class);
			intent.putExtra("ADDRESS", address.getText().toString().trim());
			intent.putExtra("CotactName", selEdit.getText().toString().trim());
			intent.putExtra("Bulding", title.getText().toString().trim());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);

		}
	}

	private boolean isLanlngNull(BDLocation loc) {
		if (Math.abs(loc.getLatitude() - 0.0) < 0.000001
				&& Math.abs(loc.getLongitude() - 0.0) < 0.000001) {
			return true;
		}
		return false;
	}

	@Override
	public void onReceiveLocation(final BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null) {
			return;
		}

		if(62==location.getLocType()||63==location.getLocType()||67==location.getLocType()
				||(161<location.getLocType()&&168<location.getLocType())){
			showToast("定位失败，请检查是否打开定位权限");
			return;
		}
		if (isLanlngNull(location)) {
			return;
		}
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 构造定位数据
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		// 设置定位数据
		mBaiduMap.setMyLocationData(locData);
		// 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
		// 当不需要定位图层时关闭定位图层
		// mBaiduMap.setMyLocationEnabled(false);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatus.Builder builder = new MapStatus.Builder();
			builder.target(ll).zoom(18.0f);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newMapStatus(builder.build()));
			List<Poi> poilst = location.getPoiList();
			if (poilst != null && !poilst.isEmpty()) {
				Poi it = poilst.get(0);
				title.setText(it.getName());
			} else {
				if (!ParamsCheckUtils.isNull(location.getLocationDescribe())) {
					title.setText(location.getLocationDescribe());
				}
			}
			// 地址显示
			address.setText(location.getAddrStr());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		if (mWakeLock == null) {
			// 获取唤醒锁,保持屏幕常亮
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					"RecordPlayAct");
			mWakeLock.acquire();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		if (mHandler.hasMessages(0)) {
			mHandler.removeMessages(0);
		}
		DataInstance.getInstance().getUnitandaddressItemList().clear();
	}

}
