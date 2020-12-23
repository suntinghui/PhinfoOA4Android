package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.utils.BaseUtil;
import com.heqifuhou.view.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.com.phinfo.db.OfftenAddressDB;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

public class AddressDetailAct extends HttpMyActBase implements OnClickListener{
	private UnitandaddressItem addressItem = new UnitandaddressItem();
	private CircleImageView photoImg;
	private TextView name,deptName,email,mobile,fax,workPhone,address;
	private boolean ISAPPEND = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("");
		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("UnitandaddressItem");
			addressItem = JSON.parseObject(s, UnitandaddressItem.class);
		}
		this.addViewFillInRoot(R.layout.act_address_detail);
		this.photoImg = (CircleImageView) this.findViewById(R.id.icon_avatar);
		this.mobile = (TextView) this.findViewById(R.id.mobile);
		this.email = (TextView) this.findViewById(R.id.email);
		this.name = (TextView) this.findViewById(R.id.name);
		this.deptName = (TextView) this.findViewById(R.id.deptName);
		this.fax = (TextView) this.findViewById(R.id.fax);
		this.workPhone = (TextView) this.findViewById(R.id.workPhone);
		this.address = (TextView) this.findViewById(R.id.address);
		this.findViewById(R.id.msgAction).setOnClickListener(this);
		this.findViewById(R.id.callAction).setOnClickListener(this);
		this.findViewById(R.id.emailAction).setOnClickListener(this);
		this.findViewById(R.id.shareAction).setOnClickListener(this);
		this.findViewById(R.id.workPhoneAction).setOnClickListener(this);
		this.findViewById(R.id.mobileAction).setOnClickListener(this);
		if(ISAPPEND){
			OfftenAddressDB.getInstance().append(addressItem);
		}
	}
	
	protected void onRefresh() {
		
	}
	protected void onResume(){
		super.onResume();
		showInfo();
	}
	
	private void showInfo(){
//		this.getAsyncBitMap(photoImg, addressItem.getAvatar(), R.drawable.icon_avatar);
		this.getAsyncAvatar(photoImg, LURLInterface.GET_AVATAR(addressItem.getUserId()),addressItem.GET_USER_NAME() );
		name.setText(addressItem.getFullName());
		deptName.setText(addressItem.getDeptName());
		email.setText(addressItem.getEmailAddress());
		mobile.setText(addressItem.getMobile());
		fax.setText(addressItem.getFax());
		workPhone.setText(addressItem.getWorkPhone());
		address.setText(addressItem.getAddress());
		
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		//短信
		case R.id.msgAction:
			sendMsgAction();
			break;
		//手机
		case R.id.mobileAction:
		case R.id.callAction:
			BaseUtil.Call(this, addressItem.getMobile());
			break;
			//工作电话
		case R.id.workPhoneAction:
			BaseUtil.Call(this, addressItem.getWorkPhone());
			break;
			//邮件
		case R.id.emailAction:
			break;
			//分享
		case R.id.shareAction:
			break;
		}
	}
	//发送短信
	private void sendMsgAction(){
		Uri smsUri = Uri.parse("smsto:" + addressItem.getMobile());
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
		startActivity(intent);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		// TODO Auto-generated method stub
		
	}


}
