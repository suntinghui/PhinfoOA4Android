package com.heqifuhou.tab;

import java.util.Stack;

import com.heqifuhou.actbase.IBroadcastAction;
import com.heqifuhou.tab.base.AnimalResBase;
import com.heqifuhou.tab.base.OnTabActivityResultListener;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
//tab里放着tabGroup，tabGroup 里放着tabChildBase 
//其他的类不要继承，防止出问题
public abstract class MyActTabGroupBaseAbs extends ActivityGroup implements IBroadcastAction,OnTabActivityResultListener {
	private ViewGroup countView;
	private Stack<Record> statusStack = new Stack<Record>();
	protected void onResume(){
		super.onResume();
	}
	protected void onPause(){
		super.onPause();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.registerReceiver(exitRev, new IntentFilter(ACTION_EXIT));
		countView = new FrameLayout(this);
		this.setContentView(countView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		//启动最新的CLS
	    Class<?> cls = getFirstChildTabActCls();
	    if(cls!=null){
	        Intent intent = new Intent(this, cls);
	        startSubActivity(intent); 
	    }
	}

	public void startSubActivity(Intent intent) {
		startSubActivity(intent, false);
	}

	public void startSubActivity(Intent intent, boolean bAnimal) {
		// instance new Animal
		String name = intent.getComponent().getClassName();
		Window win = getLocalActivityManager().startActivity(name, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		View newV = win.getDecorView();

		Activity newAct = getLocalActivityManager().getActivity(name);
		boolean bUserAnimal = false;
		if (bAnimal && newAct != null && newAct instanceof AnimalResBase) {
			bUserAnimal = true;
		}
		if (bUserAnimal) {
			AnimalResBase animalBase = (AnimalResBase) newAct;
			int nOut = animalBase.getNextOutAnimalResID();
			int nIn = animalBase.getNextInAnimalResID();
			if (nOut > 0 && statusStack.size() > 0) {
				Animation animation = AnimationUtils.loadAnimation(this, nOut);
				final View vi = statusStack.peek().getView();
				vi.setAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						vi.setVisibility(View.INVISIBLE);
					}
				});
			}
			if (nIn > 0) {
				Animation animation = AnimationUtils.loadAnimation(this, nIn);
				newV.setAnimation(animation);
			}
		} else {
			if (statusStack.size() > 0) {
				statusStack.peek().getView().setVisibility(View.INVISIBLE);
			}
		}
		// addView
		countView.addView(newV, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// 添加进栈里
		statusStack.add(new Record(name, newV, newAct));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return onKeyBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyBack() {
		if (statusStack.size() <= 1) {
			return false;
		}
		// remove view
		final Record removeRecord = statusStack.pop();
		Activity oldAct = removeRecord.getActivity();
		boolean bAninmal = false;
		if (oldAct != null && oldAct instanceof AnimalResBase) {
			bAninmal = true;
		}
		if (bAninmal) {
			AnimalResBase animalBase = (AnimalResBase) oldAct;
			int nOut = animalBase.getPreOutAnimalResID();
			int nIn = animalBase.getPreInAnimalResID();
			if (nOut > 0) {
				Animation animation = AnimationUtils.loadAnimation(this, nOut);
				removeRecord.getView().setAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						getLocalActivityManager().destroyActivity(removeRecord.getId(), true);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});

			}
			if (nIn > 0) {
				Animation animation = AnimationUtils.loadAnimation(this, nIn);
				final View vi = statusStack.peek().getView();
				vi.setAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
						vi.setVisibility(View.VISIBLE);
					}
				});
			}
		} else {
			if (statusStack.size() > 0) {
				statusStack.peek().getView().setVisibility(View.VISIBLE);
			}
		}
		countView.removeView(removeRecord.getView());
		getLocalActivityManager().destroyActivity(removeRecord.getId(), true);
		return true;
	}

	private static final class Record {
		private String id;
		private View mV;
		private Activity mAct;

		public Record(String id, View v, Activity act) {
			this.id = id;
			this.mV = v;
			this.mAct = act;
		}

		public final String getId() {
			return this.id;
		}

		public final Activity getActivity() {
			return mAct;
		}

		public final View getView() {
			return this.mV;
		}
	}

	private BroadcastReceiver exitRev = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_EXIT)) {
				((Activity) context).finish();
				return;
			}
		}
	};

	protected void onDestroy() {
		this.unregisterReceiver(exitRev);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Activity act = getLocalActivityManager().getCurrentActivity();
		if(act!=null){
			return act.onCreateOptionsMenu(menu);
		}
		return true; 
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Activity act = getLocalActivityManager().getCurrentActivity();
		if(act!=null){
			return act.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		 Activity subActivity = getLocalActivityManager().getCurrentActivity();
		 if(subActivity instanceof OnTabActivityResultListener){
			 ((OnTabActivityResultListener) subActivity).onTabActivityResult(requestCode, resultCode, data);
		 }
	}
	
	protected  abstract Class<?> getFirstChildTabActCls();
	
}

