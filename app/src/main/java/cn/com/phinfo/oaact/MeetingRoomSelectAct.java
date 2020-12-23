package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.MeetingRoomAdapter;
import cn.com.phinfo.protocol.RoomListRun;
import cn.com.phinfo.protocol.RoomListRun.RoomListItem;
import cn.com.phinfo.protocol.RoomListRun.RoomListResultBean;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshListView;

//选择问题
public class MeetingRoomSelectAct extends HttpMyActBase implements OnItemClickListener {
	private static final int ID_GET = 0x10,ID_GET_ROOMLSIT=0x11;
	private PullToRefreshListView mList = null;
	private MeetingRoomAdapter adapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("会议室选择");
		this.addViewFillInRoot(R.layout.act_select);
		mList = (PullToRefreshListView) this.findViewById(R.id.refreshListView);
		mList.setMode(Mode.DISABLED);
		adapter = new MeetingRoomAdapter();
		mList.setAdapter(adapter);
		mList.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GET_ROOMLSIT, new RoomListRun());
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		RoomListItem it = adapter.getItem(arg2-1);
		Intent data = new Intent();
		data.putExtra("RoomListItem", it);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		// 取得列表
		if (ID_GET_ROOMLSIT == id) {
			RoomListResultBean o = (RoomListResultBean)obj;
			adapter.replaceListRef(o.getListData());
			return;
		}
		if(id == ID_GET){
			
		}
	}
}
