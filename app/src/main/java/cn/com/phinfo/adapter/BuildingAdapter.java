package cn.com.phinfo.adapter;import android.view.LayoutInflater;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.widget.TextView;import cn.com.phinfo.oaact.R;import cn.com.phinfo.protocol.AttendsettingsListRun.AttendSettingsItem;import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;public class BuildingAdapter extends MyImgAdapterBaseAbs<AttendSettingsItem>{	private OnClickListener listener;	public BuildingAdapter(OnClickListener listener){		this.listener = listener;	}	@Override	public View getView(int arg0, View arg1, ViewGroup arg2) {		Holder hoder = null;		if (arg1 == null) {			hoder = new Holder();			arg1 = LayoutInflater.from(arg2.getContext()).inflate(					R.layout.adapter_attendsettings_item, null);			hoder.title = (TextView) arg1.findViewById(R.id.title);			hoder.btnX = arg1.findViewById(R.id.btn_x);			hoder.des = (TextView) arg1.findViewById(R.id.des);			arg1.setTag(hoder);		}else{			hoder = (Holder) arg1.getTag();		}		AttendSettingsItem it = this.getItem(arg0);		hoder.title.setText(it.getName());		hoder.des.setText(it.getLocation());		hoder.btnX.setTag(it);		hoder.btnX.setOnClickListener(listener);		return arg1;	}	class Holder{		TextView title,des;		View btnX;	}}