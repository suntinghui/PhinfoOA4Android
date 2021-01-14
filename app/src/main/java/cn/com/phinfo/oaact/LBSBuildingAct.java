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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;
import cn.com.phinfo.adapter.BuilderBaiduMapAdapter;
import cn.com.phinfo.entity.ExPoiInfo;
import cn.com.phinfo.protocol.AddAttendsettingsRun;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class LBSBuildingAct extends HttpMyActBase implements
		OnItemClickListener, OnClickListener, BDLocationListener {
	private static String KEYWORD = "公安";
	private static final int  ID_ADD = 0x10;
	private LocationClient mLocClient;
	private PowerManager.WakeLock mWakeLock;
	private TextureMapView mMapView;
	private BaiduMap mBaiduMap;
	private ListView listView;
	boolean isFirstLoc = true; // 是否首次定位
	private boolean changeState = true;// 当滑动地图时再进行附近搜索
	private TextView refreshText;
	private BuilderBaiduMapAdapter adapter = null;
	private PoiSearch mPoiSearch;
	private LatLng currLatlng;
	private String currBuilding,currAddress;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("地址选择");

		this.addViewFillInRoot(R.layout.act_lbsaddress);
		listView = (ListView) this.findViewById(R.id.lst);
		refreshText = (TextView) findViewById(R.id.bmap_refresh);
		this.initBaidu();
		this.poiSearchInit();
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
		adapter = new BuilderBaiduMapAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	/**
	 * 根据关键字查找附近的位置信息
	 */
	private class MyGetPoiSearchResult implements OnGetPoiSearchResultListener {
		@Override
		public void onGetPoiResult(PoiResult poiResult) {
			adapter.clear();
			if(poiResult!=null&&poiResult.getAllPoi()!=null){
				for(PoiInfo it: poiResult.getAllPoi()){
					ExPoiInfo  _it = ExPoiInfo.init(it);
					double d = DistanceUtil.getDistance(it.location,
							currLatlng);
					_it.setSize(d);
					adapter.addToListBack(_it);
				}
				adapter.sort();
			}
			ExPoiInfo _it = new ExPoiInfo();
			_it.setSize(0);
			_it.address = currAddress;
			_it.name = currBuilding;
			_it.location = currLatlng;
			adapter.addToListHead(_it);

			refreshText.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

		}

		@Override
		public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

		}

		@Override
		public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
			// TODO Auto-generated method stub

		}
	}


	/**
	 * 监听位置发生了变化
	 */
	private class MyMapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus) {
			if (changeState) {
				adapter.clear();
				refreshText.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}

		}

		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

		}

		@Override
		public void onMapStatusChange(MapStatus mapStatus) {

		}

		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			if (changeState) {
				mPoiSearch.searchNearby(new PoiNearbySearchOption()
						.keyword(KEYWORD)
						.location(mapStatus.target)
						.radius(8000)
						.pageCapacity(10000));
			}
		}
	}

	protected void onRefresh() {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		changeState = false;
		ExPoiInfo info = adapter.getItem(position);
		info.setSelect(true);
		adapter.notifyDataSetChanged();
		LatLng llA = info.location;
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
		mBaiduMap.animateMapStatus(u);
		showPoiMap(info.location.latitude,info.location.longitude,info.address);
		//百度转gg
		double x_pi = 3.14159265358979324 * 3000.0 / 180.0; 
	    double x = info.location.longitude - 0.0065, y = info.location.latitude - 0.006;  
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
	    double gg_lon = z * Math.cos(theta);  
	    double gg_lat = z * Math.sin(theta);  
		this.quickHttpRequest(ID_ADD, new AddAttendsettingsRun(info.name,info.address,String.valueOf(gg_lon),String.valueOf(gg_lat)));
		//提交数据
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(ID_ADD==id){
			if(obj.isOK()){
				this.finish();
				sendBroadcast(new Intent(IBroadcastAction.ACTION_SETTING_CHECKING));
				return;
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
//	private LatLng latlng = null;
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
			currLatlng = new LatLng(location.getLatitude(),location.getLongitude());
			currAddress = location.getAddrStr();
			currBuilding = location.getBuildingName();
			
			if(ParamsCheckUtils.isNull(currBuilding)){
				List<Poi> poilst = location.getPoiList();
				if (poilst!=null&&!poilst.isEmpty()) {
					Poi it = poilst.get(0);
					currBuilding = it.getName();
				}else{
					currBuilding = location.getLocationDescribe();
				}
			}
			if(ParamsCheckUtils.isNull(currAddress)){
				currAddress = currBuilding;
			}
			if(ParamsCheckUtils.isNull(currBuilding)){
				currBuilding = "";
				currAddress ="";
			}
			MapStatus.Builder builder = new MapStatus.Builder();
			builder.target(currLatlng).zoom(18.0f);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newMapStatus(builder.build()));
			showPoiMap(location.getLatitude(),location.getLongitude(),currAddress);
			mPoiSearch.searchNearby(new PoiNearbySearchOption()
			.keyword(KEYWORD).location(new LatLng(location.getLatitude(), location.getLongitude())).radius(8000)
			.pageCapacity(10000));
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
	}
    private void showPoiMap(double latitude, double longtitude, String address) {
    	mBaiduMap.clear();
    	//当前地址
    	addMarkersToMap(currLatlng,currAddress);
    	
        LatLng llA = new LatLng(latitude, longtitude);
        OverlayOptions ooA = new MarkerOptions().position(llA).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_local))
                .zIndex(4).draggable(true);
        mBaiduMap.addOverlay(ooA);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(llA, 17.0f);
        mBaiduMap.animateMapStatus(u);
    }
	// 在地图上添加marker
	private void addMarkersToMap(LatLng latlng,String title) {
		if(ParamsCheckUtils.isNull(title)){
			return;
		}
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.radio_icon_checked);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(latlng).icon(
				bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		
		Button button = new Button(getApplicationContext());  
		button.setBackgroundResource(R.drawable.popup);  
		button.getBackground().setAlpha(255);//透明度0~255透明度值 ，值越小越透明
		button.setTextColor(0xff333333);
		button.setText(title.trim());
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory  
	                .fromView(button);  
	    // infowindow点击事件  
        final OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {  
            @Override  
            public void onInfoWindowClick() {   
                //隐藏InfoWindow  
                mBaiduMap.hideInfoWindow();  
            }  
        }; 
    	//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
		InfoWindow mInfoWindow = new InfoWindow(bitmapDescriptor,latlng, -47,infoWindowClickListener);  
		//显示InfoWindow  
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
}
