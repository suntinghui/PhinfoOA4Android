package cn.com.phinfo.oaact.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.adapter.CheckExpandableAdapter;
import cn.com.phinfo.entity.DataInstance;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.AttendrptRun;
import cn.com.phinfo.protocol.AttendrptRun.AttendrptResultBean;
import cn.com.phinfo.protocol.AttentdsettingsRun;
import cn.com.phinfo.protocol.GetDailyRun;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.LoginRun.LoginResultBean;

import com.datepickerview.CustomDatePicker;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshExpandableListView;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.MyDatePick;

public class CheckIn2BaseAct extends CheckIn3BaseAct {
	private MyDatePick dateR1Pick;
	private ImageView photo_Icon;
	private TextView selDate, UserName, UserTitle;
	private PullToRefreshExpandableListView r2_rootrefres;
	private int year, month, day;
	private CheckExpandableAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.inittab2();
	}

	// tab2
	private void inittab2() {
		this.photo_Icon = (ImageView) this.findViewById(R.id.photo_Icon);
		this.selDate = (TextView) this.findViewById(R.id.selDate);
		this.UserName = (TextView) this.findViewById(R.id.UserName);
		this.UserTitle = (TextView) this.findViewById(R.id.UserTitle);
		this.r2_rootrefres = (PullToRefreshExpandableListView) this
				.findViewById(R.id.r2_rootrefres);
		this.r2_rootrefres.setMode(Mode.DISABLED);
		this.r2_rootrefres.getRefreshableView().setGroupIndicator(null);

		adapter = new CheckExpandableAdapter();
		this.r2_rootrefres.getRefreshableView().setAdapter(adapter);

		this.r2_rootrefres.getRefreshableView().setOnGroupExpandListener(
				new OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						for (int i = 0; i < adapter.getGroupCount(); i++) {
							// ensure only one expanded Group exists at every
							if (groupPosition != i) {
								r2_rootrefres.getRefreshableView()
										.collapseGroup(i);
							}
						}
					}
				});

		this.selDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDatePick();
			}
		});
		setCurrDay();
		requestDataByDate();
	}

	private void requestDataByDate() {
		this.quickHttpRequest(ID_Attendrpt,
				new AttendrptRun(String.valueOf(year), String.valueOf(month),
						DataInstance.getInstance().getUserBody().getUserid()));

	}

	private void setCurrDay() {
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH)+1;
		String date = String.format("%04d-%02d", year, month);
		selDate.setText(date);
	}
	private CustomDatePicker customDatePicker1 = null;
	private void showDatePick() {
		if (customDatePicker1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
					Locale.CHINA);
			String now = sdf.format(new Date());
			customDatePicker1 = new CustomDatePicker(this,
					new CustomDatePicker.ResultHandler() {
						@Override
						public void handle(String time, int yyyy, int MM,
								int dd, int HH, int mm2) {
							selDate.setText(time);
							year = yyyy;
							month = MM;
							requestDataByDate();
						}
					}, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd
													// HH:mm，否则不能正常运行
			customDatePicker1.showSpecificDay(false); // 不显示时和分,天
			customDatePicker1.setIsLoop(false); // 不允许循环滚动
		}
		customDatePicker1.show(selDate.getText().toString());
//		if (dateR1Pick != null && dateR1Pick.isShowing()) {
//			return;
//		}
//		dateR1Pick = new MyDatePick(this, new OnDateSetListener() {
//			@Override
//			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
//				String date = String.format("%04d-%02d", arg1, arg2 + 1);
//				year = arg1;
//				month = arg2+1;
//				selDate.setText(date);
//				requestDataByDate();
//			}
//		}, year, month, day, false);
//		dateR1Pick.show();
	}

	protected void onResume() {
		super.onResume();
		LoginResultBean bean = DataInstance.getInstance().getUserBody();
		if (bean == null) {
			return;
		}
		this.getAsyncAvatar(photo_Icon,
				LURLInterface.GET_AVATAR(bean.getUserid()), bean.getNickname());
		if (ParamsCheckUtils.isNull(bean.getNickname())) {
			return;
		}
		UserName.setText(bean.getNickname());
	}

	protected void onRefresh() {
		super.onRefresh();
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj,
			Object requestObj) {
		super.onHttpResult(id, obj, requestObj);
		if (ID_Attendrpt == id) {
			if (obj.isOK()) {
				AttendrptResultBean o = (AttendrptResultBean) obj;
				adapter.replace(o.getListData());
			} else {
				showToast(obj.getMsg());
			}
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onBroadcastReceiverListener(Context context, Intent intent) {
		super.onBroadcastReceiverListener(context, intent);
	};

}
