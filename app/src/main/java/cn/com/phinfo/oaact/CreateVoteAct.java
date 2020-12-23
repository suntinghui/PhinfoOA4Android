package cn.com.phinfo.oaact;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.UpdatePullRun;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.heqifuhou.actbase.HttpLoginMyActBase;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.netbase.MyNetUtil;
import com.heqifuhou.netbase.MyStaticHttpPost;
import com.heqifuhou.protocolbase.FastHttpResultParserBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.HttpThread;
import com.heqifuhou.protocolbase.IHttpPacketBase;
import com.heqifuhou.protocolbase.IHttpParserBase;
import com.heqifuhou.protocolbase.MyStaticHttpPostMultipart;
import com.heqifuhou.protocolbase.HttpThread.IHttpRunnable;
import com.heqifuhou.utils.ParamsCheckUtils;

public class CreateVoteAct extends HttpLoginMyActBase {
	private int ID_LBS = 0x10, ID_REPORT = 0x11, ID_SEND_NEW = 0x12,
			ID_SEND_UPDATE = 0x13;
	private EditText edit;
	private TextView visible, address;
	private LatLng currLatLng;
	private String Bulding = "", visibleResult = "0", groupidResult = "";
	private ViewGroup liLayout;
	private RadioGroup SelBtn;
	private RadioButton selBtn1, selBtn2;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendNew();
			}
		}, "发布投票", "发送");
		this.addViewFillInRoot(R.layout.act_create_vote);
		edit = (EditText) this.findViewById(R.id.edit);
		liLayout = (ViewGroup) this.findViewById(R.id.liLayout);
		address = (TextView) this.findViewById(R.id.address);
		visible = (TextView) this.findViewById(R.id.visible);
		SelBtn = (RadioGroup) this.findViewById(R.id.SelBtn);
		selBtn1 = (RadioButton) this.findViewById(R.id.selBtn1);
		selBtn2 = (RadioButton) this.findViewById(R.id.selBtn2);
		// 定位位置
		this.findViewById(R.id.addressbtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(CreateVoteAct.this,
								LBSAddressAct.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, ID_LBS);
					}
				});
		// 是否可见
		this.findViewById(R.id.visibleBtn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(CreateVoteAct.this,
								ReportVisibleAct.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, ID_REPORT);
					}
				});

		// 添加新的选项目
		this.findViewById(R.id.btnAdd).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						View v = getLayoutInflater(R.layout.in_vote_item);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(0, 0, 0, 2);
						liLayout.addView(v, lp);
						View btnX = v.findViewById(R.id.btnX);
						btnX.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								View delV = (View) arg0.getParent();
								liLayout.removeView(delV);
							}
						});
					}
				});
		this.findViewById(R.id.btnAdd).callOnClick();
		this.findViewById(R.id.btnAdd).callOnClick();
	}

	
	// 创建新
	public void sendNew() {
		String content = edit.getText().toString();
		if (ParamsCheckUtils.isNull(content)) {
			showToast("内容不能为空");
			return;
		}
		
//		JSONArray options = new JSONArray();
//		for (int i = 0; i < liLayout.getChildCount(); i++) {
//			EditText ed = (EditText) liLayout.getChildAt(i);
//			String s = ed.getText().toString();
//			if (!ParamsCheckUtils.isNull(s)) {
//				JSONObject optionIT = new JSONObject();
//				optionIT.put("sortNumber", i+1);
//				optionIT.put("option", s);
//				options.add(optionIT);
//			}
//		}
//		if (options.isEmpty()) {
//			showToast("选项目内容不能为空");
//			return;
//		}	
//		JSONObject poll = new JSONObject();
//		poll.put("options", options);
//		poll.put("title", content);
//		poll.put("polltype", SelBtn.getCheckedRadioButtonId()==R.id.selBtn1?"0":"1");
//		
//		JSONObject send = new JSONObject();
//		send.put("poll", poll);
//		send.put("location", Bulding);
//		if (currLatLng != null) {
//			send.put("Longitude", currLatLng.longitude);
//			send.put("Latitude", currLatLng.latitude);
//		}
//		send.put("visible", visibleResult);
//		send.put("groupid", groupidResult);
//		final String sSend = send.toJSONString();
//		this.quickHttpRequest(ID_SEND_NEW, new IHttpRunnable(){
//			@Override
//			public HttpResultBeanBase onRun(HttpThread t) throws Exception {
//				// 判断是否有网络
//				if (!MyNetUtil.IsCanConnectNet(MyApplet.getInstance())) {
//					return null;
//				}
//				if (t.isStopRuning()) {
//					return new HttpResultBeanBase();
//				}
//				byte[] b =  MyStaticHttpPost.postJsonStream(LURLInterface.GET_URL_POLL_UPDATE()
//						,sSend.getBytes());
//				if (ParamsCheckUtils.isNull(b)) {
//					return new HttpResultBeanBase();
//				}
//				String s = new String(b, "utf-8");
//				HttpResultBeanBase obj  =  new FastHttpResultParserBase(HttpResultBeanBase.class).parserResult(s);
//				//Log.i("TAG", "返回：" + s);
//				if (t.isStopRuning()) {
//					return new HttpResultBeanBase();
//				}
//				return obj;
//			}
//		});
//		Map<String, Object> has = new HashMap<String, Object>();
//		for (int i = 0; i < liLayout.getChildCount(); i++) {
//			EditText ed = (EditText) liLayout.getChildAt(i);
//			String s = ed.getText().toString();
//			if (!ParamsCheckUtils.isNull(s)) {
//				has.put("poll.options["+i+"].sortNumber", i+1);
//				has.put("poll.options["+i+"].option", s);
//			}
//		}
//		if (has.isEmpty()) {
//			showToast("选项目内容不能为空");
//			return;
//		}
//	
//		has.put("poll.title", content);
//		has.put("poll.polltype", SelBtn.getCheckedRadioButtonId()==R.id.selBtn1?"0":"1");


		JSONArray options = new JSONArray();
		for (int i = 0; i < liLayout.getChildCount(); i++) {
			EditText ed = (EditText) liLayout.getChildAt(i).findViewById(R.id.edit);
			String s = ed.getText().toString();
			if (!ParamsCheckUtils.isNull(s)) {
				JSONObject optionIT = new JSONObject();
				optionIT.put("sortNumber", i+1);
				optionIT.put("option", s);
				options.add(optionIT);
			}
		}
		if (options.isEmpty()) {
			showToast("选项目内容不能为空");
			return;
		}	
		JSONObject poll = new JSONObject();
		poll.put("options", options);
		poll.put("title", content);
		poll.put("polltype", SelBtn.getCheckedRadioButtonId()==R.id.selBtn1?"0":"1");
		
		Map<String, Object> has = new HashMap<String, Object>();
		has.put("poll", poll.toJSONString());
		has.put("location", Bulding);
		if (currLatLng != null) {
			has.put("Longitude", currLatLng.longitude);
			has.put("Latitude", currLatLng.latitude);
		}
		
		 has.put("visible", visibleResult);
		 has.put("groupid", groupidResult);
		this.quickHttpRequest(ID_SEND_NEW, new UpdatePullRun(has));

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ID_LBS && resultCode == RESULT_OK) {
			Bulding = data.getExtras().getString("Bulding");
			address.setText(Bulding);
			currLatLng = data.getExtras().getParcelable("LatLng");
			return;
		}
		// 选择的东东
		if (requestCode == ID_REPORT && resultCode == RESULT_OK) {
			visibleResult = data.getExtras().getString("VISIBLE");
			groupidResult = data.getExtras().getString("GROUPID");
			if (visibleResult.equals("0")) {
				visible.setText("所有人能看");
			} else if (visibleResult.equals("1")) {
				visible.setText("仅自己可见");
			} else if (visibleResult.equals("3")) {
				visible.setText("指定分组可见");
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if (id == ID_SEND_NEW) {
			if (obj.isOK()) {
				showToast(obj.getMsg());
				this.finish();
				BitmapDataListInstanceUtils.getRefInstance().clear();
				this.sendBroadcast(new Intent(IBroadcastAction.ACTION_SHARE));
			} else {
				showToast(obj.getMsg());
			}
			return;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		BitmapDataListInstanceUtils.getRefInstance().clear();
	}
}
