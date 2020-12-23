package cn.com.phinfo.adapter;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import cn.com.phinfo.oaact.R;import com.baidu.mapapi.search.core.PoiInfo;import com.heqifuhou.adapterbase.MyAdapterBaseAbs;public class BaiduMapAdapter extends MyAdapterBaseAbs<PoiInfo> {	private int selectPosition;	@Override	public View getView(int arg0, View arg1, ViewGroup arg2) {		Holder hoder = null;		if (arg1 == null) {			hoder = new Holder();			arg1 = LayoutInflater.from(arg2.getContext()).inflate(					R.layout.adapter_baidumap_item, null);			hoder.name = (TextView) arg1					.findViewById(R.id.adapter_baidumap_location_name);			hoder.address = (TextView) arg1					.findViewById(R.id.adapter_baidumap_location_address);			hoder.checked = (ImageView) arg1					.findViewById(R.id.adapter_baidumap_location_checked);			arg1.setTag(hoder);		} else {			hoder = (Holder) arg1.getTag();		}		PoiInfo poiInfo = this.getItem(arg0);		if (arg0 == selectPosition) {			hoder.checked.setVisibility(View.VISIBLE);		} else {			hoder.checked.setVisibility(View.GONE);		}		hoder.name.setText(poiInfo.name);		hoder.address.setText(poiInfo.address);		return arg1;	}	public void setSelection(int selectPosition) {		if(this.selectPosition!=selectPosition){			this.selectPosition = selectPosition;			this.notifyDataSetChanged();		}	}	class Holder {		public TextView name, address;		public ImageView checked;	}}