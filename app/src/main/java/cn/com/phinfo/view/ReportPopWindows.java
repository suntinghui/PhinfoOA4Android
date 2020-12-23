package cn.com.phinfo.view;

import com.heqifuhou.textdrawable.TextDrawable;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import cn.com.phinfo.oaact.R;

public class ReportPopWindows extends PopupWindow implements OnClickListener {
	private View parent;
	private View view;
	private OnPopupWindowsItemListener l = null;

	public ReportPopWindows(final Activity mContext, View parent) {
		this.parent = parent;
		view = View.inflate(mContext, R.layout.pop_report_popupwindows, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.slide_in_from_bottom));
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		view.findViewById(R.id.btn1).setOnClickListener(this);
		view.findViewById(R.id.btn2).setOnClickListener(this);
		view.findViewById(R.id.btn3).setOnClickListener(this);
		view.findViewById(R.id.btn4).setOnClickListener(this);
		// 日
		ImageView img = (ImageView) view.findViewById(R.id.day);
		Drawable drawable = TextDrawable.builder().buildRound("日",
				Color.parseColor("#ff40b1eb"));
		img.setImageDrawable(drawable);
		// 周
		img = (ImageView) view.findViewById(R.id.week);
		drawable = TextDrawable.builder().buildRound("周",
				Color.parseColor("#ff40b1eb"));
		img.setImageDrawable(drawable);
		// 月
		img = (ImageView) view.findViewById(R.id.month);
		drawable = TextDrawable.builder().buildRound("月",
				Color.parseColor("#ff40b1eb"));
		img.setImageDrawable(drawable);
		//年
		img = (ImageView) view.findViewById(R.id.year);
		drawable = TextDrawable.builder().buildRound("年",
				Color.parseColor("#ff40b1eb"));
		img.setImageDrawable(drawable);
		view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(
				this);
//		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
//		lp.alpha = 0.6f;
//		mContext.getWindow().addFlags(
//				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		mContext.getWindow().setAttributes(lp);
//		setOnDismissListener(new PopupWindow.OnDismissListener() {
//			// 在dismiss中恢复透明度
//			public void onDismiss() {
//				WindowManager.LayoutParams lp = mContext.getWindow()
//						.getAttributes();
//				lp.alpha = 1f;
//				mContext.getWindow().addFlags(
//						WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//				mContext.getWindow().setAttributes(lp);
//			}
//		});
	}

	public void show() {
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();
	}

	public void setOnPopupWindowsItemListener(OnPopupWindowsItemListener l) {
		this.l = l;
	}

	public interface OnPopupWindowsItemListener {
		void onPopupWindowsItem(int pos);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.item_popupwindows_cancel:
			break;
		case R.id.btn1:
			l.onPopupWindowsItem(1);
			break;
		case R.id.btn2:
			l.onPopupWindowsItem(2);
			break;
		case R.id.btn3:
			l.onPopupWindowsItem(3);
			break;
		case R.id.btn4:
			l.onPopupWindowsItem(4);
			break;
		default:
			break;
		}
		dismiss();
	}
}
