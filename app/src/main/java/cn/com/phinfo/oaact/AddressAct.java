package cn.com.phinfo.oaact;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.MyActBase;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import cn.com.phinfo.adapter.AddressFridesBaseAdapter;
import cn.com.phinfo.db.OfftenAddressDB;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

public class AddressAct extends MyActBase implements OnClickListener, OnItemClickListener {
	private ListView refresh;
	private AddressFridesBaseAdapter adapter = null;
	private ImageView awayLeft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("通讯录");

		boolean showBack = this.getIntent().getBooleanExtra("showBack", false);
		if (showBack) {
			this.showBackNav();
		} else {
			this.hideBackNav();
		}


		this.addViewFillInRoot(R.layout.act_offten_address);
		refresh = (ListView) this.findViewById(R.id.refresh);
		awayLeft = (ImageView) this.findViewById(R.id.awayNext);
		this.findViewById(R.id.department).setOnClickListener(this);
		this.findViewById(R.id.group).setOnClickListener(this);
		this.findViewById(R.id.away).setOnClickListener(this);
		this.findViewById(R.id.mobile).setOnClickListener(this);
		this.findViewById(R.id.contacts).setOnClickListener(this);
		adapter = new AddressFridesBaseAdapter();
		refresh.setAdapter(adapter);
		refresh.setOnItemClickListener(this);
		showData();
		this.findViewById(R.id.queryBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startSearchAddressAct();
			}
		});
		refresh.setFocusable(false);
	}

	private void showData() {
		List<UnitandaddressItem> ls = OfftenAddressDB.getInstance().getData();
		adapter.replaceListRef(ls);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.department:
			startDepartmentAct();
			break;
		case R.id.group:
			startGroupAct();
			break;
		case R.id.away:
			showAway();
			break;
		case R.id.mobile:
			startMobileAct();
			break;
		case R.id.contacts:
			startAddressContactsAct();
			break;
		}
	}

	private void showAway() {
		if (refresh.getVisibility() == View.GONE) {
			refresh.setVisibility(View.VISIBLE);
			awayLeft.setImageResource(R.drawable.share_btn_down);
		} else {
			refresh.setVisibility(View.GONE);
			awayLeft.setImageResource(R.drawable.arrow);
		}
	}

	protected void onResume() {
		super.onResume();
		showData();
	}
	private void startAddressContactsAct() {
		Intent intent = new Intent(this, AddressContactsAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	private void startMobileAct() {
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setData(Contacts.People.CONTENT_URI);
//		startActivity(intent);
		Intent intent = new Intent(this, AddressLocalAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}
	
	private void startSearchAddressAct() {
		Intent intent = new Intent(this, SearchAddressAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	private void startGroupAct() {
		Intent intent = new Intent(this, AddressGroupAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	private void startDepartmentAct() {
		Intent intent = new Intent(this, AddressDepartmentAct.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	protected void onRefresh() {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UnitandaddressItem it = adapter.getItem(arg2);
		Intent intent = new Intent(this, AddressDetailAct.class);
		intent.putExtra("UnitandaddressItem", JSON.toJSONString(it));
		this.startActivity(intent);
	}
}
