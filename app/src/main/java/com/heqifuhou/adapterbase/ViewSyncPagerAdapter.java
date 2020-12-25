package com.heqifuhou.adapterbase;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

// ViewPager适配器
public class ViewSyncPagerAdapter extends PagerAdapter {
    private View[] views;
    public ViewSyncPagerAdapter(View[] views) {
        this.views = views;
    }
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	   if (observer != null) {
	       super.unregisterDataSetObserver(observer);
	   }
	}
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
		View view = views[position];
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeAllViews();
		}
		container.addView(view);
		return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views[position]);
    }

    @Override
    public int getCount() {
        return views.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
