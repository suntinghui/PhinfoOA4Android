package cn.com.phinfo.oaact;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;
import cn.com.phinfo.adapter.BaiduMapAdapter;

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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

public class LBSAddressAct extends HttpMyActBase implements
		OnItemClickListener, OnClickListener, BDLocationListener {
	private static int ID_GETLIST = 0x10;
	private TextView address;
	private LocationClient mLocClient;
	private PowerManager.WakeLock mWakeLock;
	private TextureMapView mMapView;
	private BaiduMap mBaiduMap;
	private ListView listView;
	boolean isFirstLoc = true; // 是否首次定位
	private boolean changeState = true;// 当滑动地图时再进行附近搜索
	private TextView refreshText, title;
	private BaiduMapAdapter adapter = null;
	private PoiSearch mPoiSearch;
	private LatLng currLatlng;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startResult();
			}
		}, "地图微调", "确定");
		this.addViewFillInRoot(R.layout.act_lbsaddress);
		listView = (ListView) this.findViewById(R.id.lst);
		address = (TextView) this.findViewById(R.id.address);
		title = (TextView) this.findViewById(R.id.title);
		refreshText = (TextView) findViewById(R.id.bmap_refresh);
		this.initBaidu();
		this.poiSearchInit();
	}

	protected void startResult() {
		Intent i = new Intent();
		i.putExtra("ADDRESS", address.getText().toString());
		i.putExtra("Bulding", title.getText().toString());
		i.putExtra("LatLng", currLatlng);
		setResult(RESULT_OK, i);
		finish();
	}

	private void initBaidu() {
		mMapView = (TextureMapView) findViewById(R.id.bmap_View);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		// 隐藏比例尺
		mMapView.showScaleControl(false);
		mMapView.showZoomControls(false);
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // 设置为普通模式的地图
		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent motionEvent) {
				changeState = true;
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new MyMapStatusChangeListener());
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

	private void poiSearchInit() {
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new MyGetPoiSearchResult());
		adapter = new BaiduMapAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	/**
	 * 根据关键字查找附近的位置信息
	 */
	private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {
		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			if (poiResult != null && poiResult.getAllPoi() != null
					&& !poiResult.getAllPoi().isEmpty()) {
				adapter.addToListBack(poiResult.getAllPoi());
				refreshText.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			} else {
				refreshText.setText("附近没有找到...");
			}

		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

		}

		@Override
		public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

		}

	}

	/**
	 * 监听位置发生了变化
	 */
	private class MyMapStatusChangeListener implements  BaiduMap.OnMapStatusChangeListener {

		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus) {
			// if (changeState) {
			// adapter.clear();
			// refreshText.setVisibility(View.VISIBLE);
			// listView.setVisibility(View.GONE);
			// refreshText.setText("正在刷新...");
			// }

		}

		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

		}

		@Override
		public void onMapStatusChange(MapStatus mapStatus) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			// if (changeState) {
//			mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("小区")
//					.location(mapStatus.target).radius(1000));
			// }
			LatLng target = mBaiduMap.getMapStatus().target;
			goPoiSearch(mapStatus.target);
			adapter.clear();
			refreshText.setText("正在刷新...");
			refreshText.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
	}
	private void goPoiSearch(LatLng ltg) {
		mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("小区")
				.location(ltg).radius(1500).pageCapacity(100000));
	}
	protected void onRefresh() {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		adapter.setSelection(position);
		changeState = false;
		PoiInfo info = adapter.getItem(position);
		currLatlng = info.location;
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(currLatlng,
				17.0f);
		mBaiduMap.animateMapStatus(u);
		showMap(info.location.latitude, info.location.longitude, info.address);
		address.setText(info.address);
		title.setText(info.name);
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// map view 销毁后不在处理新接收的位置
		if (location == null || mMapView == null) {
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
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			currLatlng = ll;
			MapStatus.Builder builder = new MapStatus.Builder();
			builder.target(ll).zoom(18.0f);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newMapStatus(builder.build()));
			showMap(location.getLatitude(), location.getLongitude(),
					location.getAddrStr());

			mPoiSearch.searchNearby(new PoiNearbySearchOption()
					.keyword("小区")
					.location(
							new LatLng(location.getLatitude(), location
									.getLongitude())).radius(1000));

			// 地址显示
			address.setText(location.getAddrStr());
			List<Poi> poilst = location.getPoiList();
			if (poilst != null && !poilst.isEmpty()) {
				Poi it = poilst.get(0);
				title.setText(it.getName());
			}
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

	@SuppressLint("InvalidWakeLockTag")
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		if (mWakeLock == null) {
			// 获取唤醒锁,保持屏幕常亮
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "RecordPlayAct");
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
	}

	private void showMap(double latitude, double longtitude, String address) {
		mBaiduMap.clear();
		LatLng llA = new LatLng(latitude, longtitude);
		OverlayOptions ooA = new MarkerOptions()
				.position(llA)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_local)).zIndex(4)
				.draggable(true);
		mBaiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
		mBaiduMap.animateMapStatus(u);
	}
}
