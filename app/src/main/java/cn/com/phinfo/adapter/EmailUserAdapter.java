package cn.com.phinfo.adapter;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.TextView;import cn.com.phinfo.oaact.R;import cn.com.phinfo.protocol.EmailInfoRun.ToUsersItem;import com.heqifuhou.adapterbase.MyAdapterBaseAbs;public class EmailUserAdapter extends MyAdapterBaseAbs<ToUsersItem>{	@Override	public View getView(int arg0, View arg1, ViewGroup arg2) {		Holder hoder = null;		if (arg1 == null) {			hoder = new Holder();			arg1 = LayoutInflater.from(arg2.getContext()).inflate(					R.layout.adapter_email_top_user_item, null);			hoder.title = (TextView) arg1.findViewById(R.id.title);			arg1.setTag(hoder);		}else{			hoder = (Holder) arg1.getTag();		}		ToUsersItem it = this.getItem(arg0);		hoder.title.setText(it.getName());		return arg1;	}	class Holder{		public TextView title;	}}