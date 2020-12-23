package cn.com.phinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;
import com.heqifuhou.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
public class SelectPersonGridAdapter extends MyImgAdapterBaseAbs<UnitandaddressItem> {
	private static int nPicMax = 10;
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder holder = null;
		if (arg1 == null) {
			holder = new Holder();
			arg1 = LayoutInflater.from(arg2.getContext()).inflate(
					R.layout.adapter_selectperson_item, null);
			holder.icon_avatar = (CircleImageView) arg1.findViewById(R.id.icon_avatar);
			holder.name = (TextView) arg1.findViewById(R.id.name);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		if (arg0 < super.getCount()) {
			UnitandaddressItem it = this.getItem(arg0);
			holder.name.setText(it.GET_USER_NAME());
			this.getAsyncAvatar(holder.icon_avatar, LURLInterface.GET_AVATAR(it.getSystemUserId()),it.GET_FIRST_NAME());
		}
		else if(arg0 == (super.getCount()))
		{
			ImageLoader.getInstance().cancelDisplayTask(holder.icon_avatar);
			holder.icon_avatar.setImageResource(R.drawable.group_add);
			holder.name.setText("添加");
			if (arg0 == nPicMax) {
				holder.icon_avatar.setVisibility(View.GONE);
			}
		}else{
			ImageLoader.getInstance().cancelDisplayTask(holder.icon_avatar);
			holder.icon_avatar.setImageResource(R.drawable.group_delete);
			holder.name.setText("删除");
			if (arg0 == nPicMax) {
				holder.icon_avatar.setVisibility(View.GONE);
			}
		}
		return arg1;
	}

	public class Holder {
		TextView name;
		CircleImageView icon_avatar;
	}

	public int getCount() {
		if (super.getCount() >= nPicMax) {
			return super.getCount();
		}
		return (super.getCount() + 2);
	}

	public boolean isDel(int idx){
		if(idx == super.getCount()+1){
			return true;
		}
		return false;
	}
	public boolean isAdd(int idx){
		if(idx == super.getCount()){
			return true;
		}
		return false;
	}
	public boolean isImgShow(int idx) {
		if (idx >= 0 && idx < super.getCount()) {
			return false;
		}
		return true;
	}

};