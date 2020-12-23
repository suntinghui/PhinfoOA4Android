package com.heqifuhou.baidu;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * 
 * @author baidu
 * 
 */
public class BaiduLocationClientUtils {
	private LocationClient locationClient = null;
	private LocationClientOption mOption, DIYoption;
	private Object sLock = new Object();

	/***
	 * 
	 * @param locationContext
	 */
	public BaiduLocationClientUtils(Context locationContext) {
		synchronized (sLock) {
			if (locationClient == null) {
				locationClient = new LocationClient(locationContext);
			}
		}
	}

	public LocationClientOption getCurrLocationClientOption() {
		synchronized (sLock) {
			return mOption;
		}
	}

	/***
	 * 
	 * @param listener
	 * @return
	 */

	public boolean registerListener(BDLocationListener listener) {
		boolean isSuccess = false;
		if (listener != null) {
			locationClient.registerLocationListener(listener);
			isSuccess = true;
		}
		return isSuccess;
	}

	public void unregisterListener(BDLocationListener listener) {
		if (listener != null) {
			locationClient.unRegisterLocationListener(listener);
		}
	}

	/***
	 * 
	 * @param option
	 * @return isSuccessSetOption
	 */
	public boolean setLocationOption(LocationClientOption option) {
		boolean isSuccess = false;
		if (option != null) {
			if (locationClient.isStarted())
				locationClient.stop();
			DIYoption = option;
			locationClient.setLocOption(option);
		}
		return isSuccess;
	}

	public LocationClientOption getOption() {
		return DIYoption;
	}

	/***
	 * 
	 * @return DefaultLocationClientOption
	 */
	public LocationClientOption getDefaultLocationClientOption(long nScanSpan) {
		// 设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
		if (nScanSpan > 0 && nScanSpan < 1000) {
			nScanSpan = 1000;
		}
		mOption = new LocationClientOption();
		mOption.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		mOption.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
		mOption.setScanSpan((int) nScanSpan);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		mOption.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		mOption.setIsNeedLocationDescribe(true);// 可选，设置是否需要地址描述
		mOption.setNeedDeviceDirect(false);// 可选，设置是否需要设备方向结果
		mOption.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		mOption.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mOption.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mOption.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mOption.SetIgnoreCacheException(true);// 可选，默认false，设置是否收集CRASH信息，默认收集
		return mOption;
	}
	
	public LocationClientOption getDefaultLBSNoNetClientOption(long nScanSpan) {
		// 设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
		if (nScanSpan > 0 && nScanSpan < 1000) {
			nScanSpan = 1000;
		}
		mOption = new LocationClientOption();
		mOption.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		mOption.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
		mOption.setScanSpan((int) nScanSpan);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		mOption.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
		mOption.setIsNeedLocationDescribe(false);// 可选，设置是否需要地址描述
		mOption.setNeedDeviceDirect(false);// 可选，设置是否需要设备方向结果
		mOption.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		mOption.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mOption.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		mOption.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mOption.SetIgnoreCacheException(true);// 可选，默认false，设置是否收集CRASH信息，默认收集
		return mOption;
	}

	public void start() {
		synchronized (sLock) {
			if (locationClient != null && !locationClient.isStarted()) {
				locationClient.start();
			}
		}
	}

	public void requestLocation() {
		synchronized (sLock) {
			if (locationClient != null && locationClient.isStarted()) {
				locationClient.requestLocation();
			}
		}
	}

	public void stop() {
		synchronized (sLock) {
			if (locationClient != null && locationClient.isStarted()) {
				locationClient.stop();
			}
		}
	}

}
