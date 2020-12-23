package cn.com.phinfo.oaact.base;


import java.util.List;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.heqifuhou.baidu.BaiduLocationClientUtils;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.ParamsCheckUtils;

public class LBSAct  extends MyBaseBitmapAct implements BDLocationListener{
	protected static final int ID_IMG_ZIP = 0x3,ID_IMG_SUBMIT = 0x4,ID_T = 0x8;
	private BaiduLocationClientUtils mmlocationClientUtils;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mmlocationClientUtils == null) {
			mmlocationClientUtils = new BaiduLocationClientUtils(this);
			mmlocationClientUtils.registerListener(this);
			mmlocationClientUtils.setLocationOption(mmlocationClientUtils.getDefaultLocationClientOption(1000));
			mmlocationClientUtils.start();
//			showLoading();
		}
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
	}
	
	protected void onDestroy(){
		if (mmlocationClientUtils != null) {
			mmlocationClientUtils.unregisterListener(this);
			mmlocationClientUtils.stop();
			mmlocationClientUtils = null;
		}
		super.onDestroy();
	}

	private boolean bFirst = false;
	protected BDLocation loc = null;
	@Override
	public void onReceiveLocation(BDLocation loc) {
		if (loc == null) {
			return;
		}
		if(62==loc.getLocType()||63==loc.getLocType()||67==loc.getLocType()
				||(161<loc.getLocType()&&168<loc.getLocType())){
			showToast("定位失败，请检查是否打开定位权限");
			return;
		}
		if(isLanlngNull(loc)){
			return;
		}
		// 如果返回的地址是空的，则重新等待定位
		if (ParamsCheckUtils.isNull(loc.getAddress())) {
			return;
		}
		if(ParamsCheckUtils.isNull(loc.getBuildingName())){
			List<Poi> poilst = loc.getPoiList();
			if (poilst!=null&&!poilst.isEmpty()) {
				Poi it = poilst.get(0);
				loc.setBuildingName(it.getName());
			}else{
				String s = loc.getLocationDescribe();
				loc.setBuildingName(s);
			}
		}
		if(!bFirst){
			hideLoading();
			this.loc = loc;
			bFirst = true;
		}
	}
	
	protected void setReLoc(){
		bFirst = false;
		showLoading();
	}
	public boolean isLanlngNull(BDLocation loc){
		if(Math.abs(loc.getLatitude() - 0.0) < 0.000001 && Math.abs(loc.getLongitude() - 0.0) < 0.000001){
			return true;
		}
		return false;
	}
}
