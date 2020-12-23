package cn.com.phinfo.oaact;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.phinfo.protocol.AttentdsettingsRun.AttentdSettingsData;
import cn.com.phinfo.protocol.WorkCheckInRun;
import cn.com.phinfo.protocol.WorkCheckInRun.WorkCheckInRequest;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.MyBaseBitmapAct;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.view.CheckInSuccessDialog;

public class CheckDialogAct extends MyBaseBitmapAct {
	private static int ID_GETLIST = 0x10, ID_GO_SETTING = 0x11,ID_CHECKIN = 0x12;
	private TextView title,startTitle,endTitle;
	private EditText remark;
	private LinearLayout li;
	private String ADDRESS="",TIME="";
	private WorkCheckInRequest request;
	private boolean bBLEATER = true;
	private AttentdSettingsData attendssettnigs;
	private boolean bSubmit = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BitmapDataListInstanceUtils.getRefInstance().clear();
		attendssettnigs = JSON.parseObject(this.getIntent().getExtras().getString("AttentdSettingsData"),AttentdSettingsData.class);
		bBLEATER = this.getIntent().getExtras().getBoolean("BLEATER");
		String s  = this.getIntent().getExtras().getString("WorkCheckInRequest");
		request = JSON.parseObject(s, WorkCheckInRequest.class);
		ADDRESS = this.getIntent().getExtras().getString("ADDRESS");
		TIME = this.getIntent().getExtras().getString("TIME");
		Window window = getWindow();  
        WindowManager.LayoutParams layoutParams = window.getAttributes();  
        //设置窗口的大小及透明度  
        DisplayMetrics dm = getResources().getDisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w =  dm.widthPixels;
        int h =  dm.heightPixels;
//        WindowManager m = getWindowManager();    
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//        int w = d.getWidth();
//        int h = d.getHeight();
        layoutParams.width = (int) (w * 0.9);    //宽度设置为屏幕的0.8   
        layoutParams.height =(int) (h * 0.8);//960+320;//(int) (d.getWidth() * 0.9);   //高度设置为屏幕的1.0   
        layoutParams.alpha = 1.0f;      //设置本身透明度  
        layoutParams.dimAmount = 1.0f;      //设置黑暗度 
        window.setAttributes(layoutParams); 
        this.setMaxPic(1);
		this.addViewFillInRoot(R.layout.act_check_dialog);
		title = (TextView) this.findViewById(R.id.title);
		startTitle =  (TextView) this.findViewById(R.id.startTitle);
		endTitle =  (TextView) this.findViewById(R.id.endTitle);
		remark = (EditText) this.findViewById(R.id.remark);
		li = (LinearLayout) this.findViewById(R.id.li);
		li.addView(this.InitBitmapView(false));
		startTitle.setText(TIME);
		endTitle.setText(ADDRESS);
		if(!bBLEATER){
			title.setText("确定要打早退卡");
		}else{
			title.setText("迟到打卡");
		}
		//取消
		this.findViewById(R.id.btn1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		this.findViewById(R.id.btn2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submit();
			}
		});
	}
    private CheckInSuccessDialog dialog;
    private void showSuccess(){
    	if(dialog!=null&&dialog.isShowing()){
    		return;
    	}
    	dialog = new CheckInSuccessDialog(this,request.getCheckTime(),new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
    	});
    	dialog.show();
    }
    
	public void submit(){
		if(bSubmit){
			return;
		}
		request.setDescripiton(remark.getText().toString().trim());
		if(attendssettnigs.getGlobalSettings().getNeedPhoto()&&BitmapDataListInstanceUtils.getRefInstance().getListRef().isEmpty()){
			showToast("必须要上传照片");
			return;
		}
		if(!BitmapDataListInstanceUtils.getRefInstance().getListRef().isEmpty()){
			String file = BitmapDataListInstanceUtils.getRefInstance().getListRef().get(0).getFileLocalPath();
			request.setFile(file);
		}
		this.quickHttpRequest(ID_CHECKIN, new WorkCheckInRun(request));
		bSubmit = true;
	}
	

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		if(ID_CHECKIN == id){
			bSubmit = false;
			if(obj.isOK()){
				Intent i = new Intent(IBroadcastAction.ACTION_CHECKIN);
				sendBroadcast(i);
				showSuccess();
			}else{
				showToast(obj.getMsg());
			}
		}
	}
}
