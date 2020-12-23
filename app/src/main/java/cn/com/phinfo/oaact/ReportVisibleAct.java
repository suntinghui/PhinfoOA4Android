package cn.com.phinfo.oaact;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import cn.com.phinfo.adapter.VisibleGroupAdapter;
import cn.com.phinfo.protocol.GroupRun;
import cn.com.phinfo.protocol.GroupRun.GroupItem;
import cn.com.phinfo.protocol.GroupRun.GroupResultBean;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

//关于可见性
public class ReportVisibleAct extends HttpMyActBase implements OnClickListener,OnItemClickListener{
	private ListView list;
	private ImageView pic1,pic2,pic3,share_btn;
	private View btn1,btn2,btn3,addGroup;
	private String visible="0";
	private VisibleGroupAdapter adapter=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.putExtra("VISIBLE", visible);
				i.putExtra("GROUPID", adapter.getGroupAll());
				setResult(RESULT_OK,i);
				finish();
			}
		},"可见性","确定");
		this.addViewFillInRoot(R.layout.act_report_sel);
		list = (ListView) this.findViewById(R.id.list);
		adapter = new VisibleGroupAdapter(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		pic1 = (ImageView) this.findViewById(R.id.pic1);
		pic2 = (ImageView) this.findViewById(R.id.pic2);
		pic3 = (ImageView) this.findViewById(R.id.pic3);
		share_btn = (ImageView) this.findViewById(R.id.share_btn);
		
		btn1 = this.findViewById(R.id.btn1);
		btn2 = this.findViewById(R.id.btn2);
		btn3 = this.findViewById(R.id.btn3);
		addGroup = this.findViewById(R.id.addGroup);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		addGroup.setOnClickListener(this);
		
		list.setVisibility(View.GONE);
		addGroup.setVisibility(View.GONE);
		pic1.setImageResource(R.drawable.tick_hover);
		pic2.setImageResource(R.drawable.tick_n);
		pic3.setImageResource(R.drawable.tick_n);
		
		visible = "0";
		onRefresh();
	}

	protected void onRefresh(){
		this.quickHttpRequest(0x10, new GroupRun());
		
	}
	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(id == 0x10){
			if(obj.isOK()){
				GroupResultBean o = (GroupResultBean)obj;
				adapter.replaceListRef(o.getListData());
			}else{
				showToast(obj.getMsg());
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn1:
			visible = "0";
			addGroup.setVisibility(View.GONE);
			list.setVisibility(View.GONE);
			share_btn.setImageResource(R.drawable.share_btn_down);
			pic1.setImageResource(R.drawable.tick_hover);
			pic2.setImageResource(R.drawable.tick_n);
			pic3.setImageResource(R.drawable.tick_n);
			break;
		case R.id.btn2:
			visible = "1";
			addGroup.setVisibility(View.GONE);
			list.setVisibility(View.GONE);
			share_btn.setImageResource(R.drawable.share_btn_down);
			pic1.setImageResource(R.drawable.tick_n);
			pic2.setImageResource(R.drawable.tick_hover);
			pic3.setImageResource(R.drawable.tick_n);
			break;
		case R.id.btn3:
			visible = "3";
			addGroup.setVisibility(View.VISIBLE);
			list.setVisibility(View.VISIBLE);
			share_btn.setImageResource(R.drawable.share_btn_up);
			pic1.setImageResource(R.drawable.tick_n);
			pic2.setImageResource(R.drawable.tick_n);
			pic3.setImageResource(R.drawable.tick_hover);
			break;
		case R.id.addGroup:
			Intent intent = new Intent(this,AddGroupNameAct.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
	}
	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
		if(intent.getAction().equals(IBroadcastAction.ACTION_ADD_GROUP)){
			onRefresh();
			return;
		}
	};
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		GroupItem  it = adapter.getItem(arg2);
		it.setbSel(!it.isbSel());
		adapter.notifyDataSetChanged();
	}
}
