package com.heqifuhou.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import cn.com.phinfo.oaact.R;

public class SlidButton extends View implements OnTouchListener {
	public enum TypeEnum {
		PWD(0), // 0=密码
		OPEN_CLOSE(1);// 关闭打开（单色的）
		private int value = 0;

		private TypeEnum(int value) {// 必须是private的
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}
	private boolean bclickable= true;
	private static final boolean DefaultOpen = true;
	public SlidButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setClickable(boolean clickable){
		this.bclickable = clickable;
	}
	public SlidButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureWidth(int widthMeasureSpec) {
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		// 如果不指定限制，就是默认大小
		int result = bg_on.getWidth();
		// 如果是AT_MOST，specSize 代表的是最大可获得的空间
		switch (specMode) {
		case MeasureSpec.AT_MOST:
			return result;
		case MeasureSpec.EXACTLY:// 显式指定大小，如40dp或fill_parent
			return result = specSize;
		case MeasureSpec.UNSPECIFIED: // 表示该View的大小父视图未定，设置为默认值
			return result;
		}
		return result;
	}

	private int measureHeight(int heightMeasureSpec) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);

		// 如果不指定限制，就是默认大小
		int result = bg_on.getHeight();
		switch (specMode) {
		case MeasureSpec.AT_MOST:
			return result;
		case MeasureSpec.EXACTLY:// return result;
			return result = specSize;
		case MeasureSpec.UNSPECIFIED: // 表示该View的大小父视图未定，设置为默认值
			return result;
		}
		return result;
	}

	public SlidButton(Context context) {
		super(context);
		init();
	}

	private boolean nowChoose = DefaultOpen;// 记录当前按钮是否打开，true为打开，false为关闭
	private boolean onSlip = false;// 记录用户是否在滑动
	private float downX, nowX=-1; // 按下时的x，当前的x
	private Rect btn_on, btn_off;// 打开和关闭状态下，游标的Rect

	private boolean isChgLsnOn = false;// 是否设置监听
	private OnChangedListener changedLis;

	private Bitmap bg_on=null, bg_off=null, slip_btn=null;

	private void init() {
		// 载入图片资源
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.sild_bg_btn);
		setType(TypeEnum.PWD);
		setOnTouchListener(this);
	}
	
	public void setNowChoose(boolean bOpen){
		nowChoose = bOpen;
		invalidate();
	}
	public boolean getNowChoose(){
		return nowChoose;
	}
	public void setType(TypeEnum en) {
		int nOnRes = R.drawable.sild_bg_on;
		int nOffRes = R.drawable.sild_bg_off;
		if (en == TypeEnum.PWD) {

		} else if (en == TypeEnum.OPEN_CLOSE) {
			nOnRes = R.drawable.sild_bg_on;
			nOffRes = R.drawable.sild_bg_off;
		}
		bg_on = BitmapFactory.decodeResource(getResources(), nOnRes);
		bg_off = BitmapFactory.decodeResource(getResources(), nOffRes);
		// 获得需要的Rect数据
		btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		btn_off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;
		if(bg_on==null||bg_off==null||slip_btn==null){
			return;
		}
		if(nowX==-1){
			if(nowChoose){
				nowX = bg_on.getWidth();
			}else{
				nowX = 0;//bg_off.getWidth();
			}
		}
		{
			if (nowX < (bg_on.getWidth() / 2)){ // 滑动到前半段与后半段的背景不同,在此做判断
				canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
			}else{
				canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景
			}

			if (onSlip) {// 是否是在滑动状态,
				if (nowX >= bg_on.getWidth())// 是否划出指定范围,不能让游标跑到外头,必须做这个判断
					x = bg_on.getWidth() - slip_btn.getWidth() / 2;// 减去游标1/2的长度
				else
					x = nowX - slip_btn.getWidth() / 2;
			} else {
				if (nowChoose){// 根据现在的开关状态设置画游标的位置
					x = btn_off.left;
				}else{
					x = btn_on.left;
				}
			}

			if (x < 0) // 对游标位置进行异常判断..
				x = 0;
			else if (x > bg_on.getWidth() - slip_btn.getWidth())
				x = bg_on.getWidth() - slip_btn.getWidth();
			canvas.drawBitmap(slip_btn, x, 2, paint);// 画出游标.
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(!this.bclickable){
			return true;
		}
		switch (event.getAction()) {// 根据动作来执行代码

		case MotionEvent.ACTION_MOVE:// 滑动
			nowX = event.getX();
			break;
		case MotionEvent.ACTION_DOWN:// 按下
			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			onSlip = true;
			downX = event.getX();
			nowX = downX;
			break;
		case MotionEvent.ACTION_UP:// 松开
			onSlip = false;
			boolean lastChoose = nowChoose;
			if (event.getX() >= (bg_on.getWidth() / 2))
				nowChoose = true;
			else
				nowChoose = false;
			if (isChgLsnOn && (lastChoose != nowChoose))// 如果设置了监听器,就调用其方法.
				changedLis.OnChanged(this, nowChoose);
			break;
		default:
			break;
		}
		invalidate();
		return true;
	}

	public void setOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		isChgLsnOn = true;
		changedLis = l;
	}

	public interface OnChangedListener {
		void OnChanged(View v, boolean bChecked);
	}
}
