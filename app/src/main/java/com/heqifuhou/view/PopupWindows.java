package com.heqifuhou.view;import android.app.Activity;import android.graphics.drawable.BitmapDrawable;import android.view.Gravity;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.view.ViewGroup.LayoutParams;import android.view.WindowManager;import android.view.animation.AnimationUtils;import android.widget.Button;import android.widget.PopupWindow;import android.widget.TextView;import cn.com.phinfo.oaact.R;import com.heqifuhou.utils.Utils;public class PopupWindows extends PopupWindow implements OnClickListener {	private View parent;	private View view;	private OnPopupWindowsItemListener l = null;	public PopupWindows(final Activity mContext, View parent, String[] ItemList) {		this.parent = parent;		view = View				.inflate(mContext, R.layout.pop_item_popupwindows, null);		view.startAnimation(AnimationUtils.loadAnimation(mContext,				R.anim.slide_in_from_bottom));		setWidth(LayoutParams.FILL_PARENT);		setHeight(LayoutParams.FILL_PARENT);		setBackgroundDrawable(new BitmapDrawable());		setFocusable(true);		setOutsideTouchable(true);		setContentView(view);		ViewGroup li = (ViewGroup) view.findViewById(R.id.ll_popup);		for (int i = 0; i < ItemList.length; i++) {			if (i > 0) {				TextView v = new TextView(mContext);				v.setBackgroundColor(0xFFf3f3f3);				li.addView(v, LayoutParams.FILL_PARENT,						Utils.dip2px(mContext, 1));			}			Button child = new Button(mContext);			child.setTextColor(0xFF585858);			child.setTextSize(18);			child.setText(ItemList[i]);			child.setBackgroundResource(R.drawable.bt_nobgd);			child.setOnClickListener(this);			child.setTag(i);			li.addView(child, LayoutParams.FILL_PARENT,					Utils.dip2px(mContext, 55));		}		view.findViewById(R.id.item_popupwindows_cancel).setOnClickListener(				this);		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();		lp.alpha = 0.6f;		mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);		mContext.getWindow().setAttributes(lp);		setOnDismissListener(new PopupWindow.OnDismissListener() {			// 在dismiss中恢复透明度			public void onDismiss() {				WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();				lp.alpha = 1f;				mContext.getWindow().addFlags(						WindowManager.LayoutParams.FLAG_DIM_BEHIND);				mContext.getWindow().setAttributes(lp);			}		});	}	public void show() {		showAtLocation(parent, Gravity.BOTTOM, 0, 0);		update();	}	public void setTextColorByIndex(int index,int color){		ViewGroup li = (ViewGroup) view.findViewById(R.id.ll_popup);		if(index>=0&&index<li.getChildCount()){			Button b = (Button)li.getChildAt(index);			b.setTextColor(color);		}			}	public void setOnPopupWindowsItemListener(OnPopupWindowsItemListener l) {		this.l = l;	}	public interface OnPopupWindowsItemListener {		void onPopupWindowsItem(int pos);	}	@Override	public void onClick(View arg0) {		switch (arg0.getId()) {		case R.id.item_popupwindows_cancel:			break;		default:			if (l != null) {				int pos = (Integer)arg0.getTag();				l.onPopupWindowsItem(pos);			}			break;		}		dismiss();	}}