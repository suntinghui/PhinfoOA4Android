package cn.com.phinfo.oaact;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

public class SearchUserAddressAct extends SearchAddressAct {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it = (UnitandaddressItem) adapter.getItem(arg2 - 1);
		it.setIsbSel(!it.getIsbSel());
		if(it.getIsbSel()){
			DataInstance.getInstance().addUnitandaddressItem(it);
		}else{
			DataInstance.getInstance().removeUnitandaddressItem(it);
		}
		this.setResult(RESULT_OK);
		this.finish();
	}
}
