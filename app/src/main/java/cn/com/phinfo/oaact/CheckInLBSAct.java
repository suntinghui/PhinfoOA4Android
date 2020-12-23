package cn.com.phinfo.oaact;

import java.util.Calendar;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;
import cn.com.phinfo.protocol.AttentdsettingsRun.AttentdSettingsData;
import cn.com.phinfo.protocol.WorkCheckInRun;
import cn.com.phinfo.protocol.WorkCheckInRun.WorkCheckInRequest;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.CheckInSuccessDialog;

public class CheckInLBSAct extends MyBaseBitmapAct implements
		 OnClickListener, BDLocationListener {
	private static int ID_GETLIST = 0x10, ID_GO_SETTING = 0x11,ID_CHECKIN = 0x12;
	private TextView address;
	private LocationClient mLocClient;
	private PowerManager.WakeLock mWakeLock;
	private TextureMapView mMapView;
	private BaiduMap mBaiduMap;
	private TextView title;
	private ViewGroup li;
	private TextView btn;
	private EditText subject;
	private AttentdSettingsData attendssettnigs;
	private WorkCheckInRequest request;
	private boolean bSubmit = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		attendssettnigs = JSON.parseObject(this.getIntent().getExtras().getString("AttentdSettingsData"), AttentdSettingsData.class);
		request = JSON.parseObject(this.getIntent().getExtras().getString("WorkCheckInRequest"),WorkCheckInRequest.class);
		this.addTitleView(R.layout.nav_f5f5f5_btn);
		this.addTextNav( "考勤打卡");
		this.addViewFillInRoot(R.layout.act_check_lbs);
		address = (TextView) this.findViewById(R.id.address);
		subject = (EditText) this.findViewById(R.id.subject);
		title =  (TextView) this.findViewById(R.id.title);
		li = (ViewGroup) this.findViewById(R.id.li);
		li.addView(this.InitBitmapView(false),LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		this.setNumColumns(1);
		this.btn = (TextView) this.findViewById(R.id.btn);
		this.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//提交
				submit();
			}
		});
		this.setMaxPic(1);
		this.initBaidu();
		this.findViewById(R.id.reBuildAddress).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				bFisrt = false;
			}
		});
	}
	
	private void submit(){
		if(bSubmit){
			return;
		}
		request.setDescripiton(subject.getText().toString().trim());
		if(attendssettnigs.getGlobalSettings().getNeedPhoto()&&BitmapDataListInstanceUtils.getRefInstance().getListRef().isEmpty()){
			showToast("必须要上传照片");
			return;
		}
		if(!BitmapDataListInstanceUtils.getRefInstance().getListRef().isEmpty()){
			String file = BitmapDataListInstanceUtils.getRefInstance().getListRef().get(0).getFileLocalPath();
			request.setFile(file);
		}
		this.quickHttpRequest(ID_CHECKIN, new WorkCheckInRun(request));
		bSubmit = true;
	}


	protected void onStop() {
		super.onStop();
		if (mHandler != null && mHandler.hasMessages(0)) {
			mHandler.removeMessages(0);
		}
	}

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			Calendar calendar = Calendar.getInstance();
			int h = calendar.get(Calendar.HOUR_OF_DAY);
			int m = calendar.get(Calendar.MINUTE);
			int s = calendar.get(Calendar.SECOND);
			String text = String.format("%02d:%02d:%02d", h, m, s);
			btn.setText(text+"   "+"外勤打卡");
			mHandler.sendEmptyMessageDelayed(0, 200);
		}
	};

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

	}




	protected void onRefresh() {

	}

    private CheckInSuccessDialog dialog;
    private void showSuccess(){
    	if(dialog!=null&&dialog.isShowing()){
    		return;
    	}
    	dialog = new CheckInSuccessDialog(this,request.getCheckTime(),new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
    	});
    	dialog.show();
    }

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(ID_CHECKIN == id){
			bSubmit = false;
			if(obj.isOK()){
				Intent i = new Intent(IBroadcastAction.ACTION_CHECKIN);
				sendBroadcast(i);
				showSuccess();
			}else{
				showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
	private boolean isLanlngNull(BDLocation loc){
		if(Math.abs(loc.getLatitude() - 0.0) < 0.000001 && Math.abs(loc.getLongitude() - 0.0) < 0.000001){
			return true;
		}
		return false;
	}
	private boolean bFisrt = false;
	@Override
	public void onReceiveLocation(BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null) {
			return;
		}
		if(62==location.getLocType()||63==location.getLocType()||67==location.getLocType()
				||(161<location.getLocType()&&168<location.getLocType())){
			showToast("定位失败，请检查是否打开定位权限");
			return;
		}
		if(isLanlngNull(location)){
			return;
		}
		//
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
		mBaiduMap.setMyLocationEnabled(false);
		if(!bFisrt){
			bFisrt =true;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			
			MapStatus.Builder builder = new MapStatus.Builder();
			builder.target(ll).zoom(18.0f);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newMapStatus(builder.build()));
			showMap(location.getLatitude(),location.getLongitude(),location.getAddrStr());
			// 地址显示
			address.setText( location.getAddrStr());
			List<Poi> poilst = location.getPoiList();
			if (poilst!=null&&!poilst.isEmpty()) {
				Poi it = poilst.get(0);
				title.setText(it.getName());
				request.setBuildingName(it.getName());
			}else{
				if(!ParamsCheckUtils.isNull(location.getLocationDescribe())){
					title.setText(location.getLocationDescribe());
					request.setBuildingName(location.getLocationDescribe());
				}
			}
			double x_pi = 3.14159265358979324 * 3000.0 / 180.0; 
		    double x = location.getLongitude() - 0.0065, y = location.getLatitude() - 0.006;  
		    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
		    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
		    double gg_lon = z * Math.cos(theta);  
		    double gg_lat = z * Math.sin(theta); 
			request.setLatitude(String.valueOf(gg_lat));
			request.setLongitude(String.valueOf(gg_lon));
			request.setLocation(location.getAddrStr());
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
		mHandler.sendEmptyMessage(0);
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
	}
    private void showMap(double latitude, double longtitude, String address) {
    	mBaiduMap.clear();
        LatLng llA = new LatLng(latitude, longtitude);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_local))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 24.0f);
        mBaiduMap.animateMapStatus(u);
    }
}
