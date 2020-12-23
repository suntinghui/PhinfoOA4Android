package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.protocol.UserInfoRun;
import cn.com.phinfo.protocol.UserInfoRun.ItemUserInfo;
import cn.com.phinfo.protocol.UserInfoRun.UeserInfoResultBean;

import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

public class PersonTabAct extends HttpMyActBase implements OnClickListener {
	private final int ID_Avator = 0x11, ID_Exit = 0x12, ID_UPDATE = 0x13;
	private ImageView photo;
	private TextView name_txt, accout_txt, title_txt, mobile_txt, email_txt;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav("个人信息");
		this.addViewFillInRoot(R.layout.act_persontab);
		this.findViewById(R.id.photo_li).setOnClickListener(this);
		this.findViewById(R.id.Name).setOnClickListener(this);
		this.findViewById(R.id.accout).setOnClickListener(this);
		this.findViewById(R.id.qrcode).setOnClickListener(this);
		this.findViewById(R.id.t_title).setOnClickListener(this);
		this.findViewById(R.id.mobile).setOnClickListener(this);
		this.findViewById(R.id.email).setOnClickListener(this);

		this.name_txt = (TextView) this.findViewById(R.id.name_txt);
		this.accout_txt = (TextView) this.findViewById(R.id.accout_txt);
		this.title_txt = (TextView) this.findViewById(R.id.title_txt);
		this.mobile_txt = (TextView) this.findViewById(R.id.mobile_txt);
		this.email_txt = (TextView) this.findViewById(R.id.email_txt);
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(0x10, new UserInfoRun());
		this.hideLoading();
	}
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.photo_li:// 照片
			startPhotos();
			break;
		case R.id.Name:// 名字
			break;
		case R.id.accout:// 账号
			break;
		case R.id.qrcode:// 二维码
			break;
		case R.id.t_title:// 职位
			break;
		case R.id.mobile:// 手机号
			startChangeMobileAct();
			break;
		case R.id.email:// 邮箱
			break;
		default:
			break;
		}
	}
	private void startChangeMobileAct() {
		Intent intent = new Intent(this, ChangeMobileAct.class);
		intent.putExtra("MOBILE", this.mobile_txt.getText().toString().trim());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivityForResult(intent,0x100);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0x100){
			if(resultCode == RESULT_OK){
				onRefresh();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startPhotos() {
		Intent intent = new Intent(this, PersonPhoto.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	@Override
	public void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (0x10 == id) {
			UeserInfoResultBean o = (UeserInfoResultBean) obj;
			if (!o.getData().isEmpty()) {
				ItemUserInfo it = o.getData().get(0);
				this.name_txt.setText(it.getFullName());
				this.accout_txt.setText(DataInstance.getInstance().getUserBody().getUserName());
				this.title_txt.setText(it.getJobTitle());
				this.mobile_txt.setText(it.getMobile());
				this.email_txt.setText(it.getEmailAddress());
			}
		}
	}

}
