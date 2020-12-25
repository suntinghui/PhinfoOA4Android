package com.heqifuhou.adapterbase;

import java.util.Comparator;
import java.util.List;

import com.heqifuhou.adapterbase.base.AdapterListBase;
import com.heqifuhou.adapterbase.base.AdapterListBase.OnNotifyDataSetChanged;
import com.heqifuhou.adapterbase.base.IAdapterBase;
import com.heqifuhou.adapterbase.base.IAdapterListBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.database.DataSetObserver;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import cn.com.phinfo.oaact.R;

public class FragmentPagerAdapterBase<E extends Fragment> extends FragmentPagerAdapter implements
		IAdapterBase<E>, OnNotifyDataSetChanged {
	private IAdapterListBase<E> adapter = null;

	public FragmentPagerAdapterBase(FragmentManager fm) {
		super(fm);
		adapter = new AdapterListBase<E>(this);
	}
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	   if (observer != null) {
	       super.unregisterDataSetObserver(observer);
	   }
	}

	@Override
	public void notifyDataListSetChanged() {
		this.notifyDataSetChanged();
	}

	@Override
	public void setMaxCount(int nMaxCount) {
		adapter.setMaxCount(nMaxCount);
	}

	@Override
	public int getMaxCount() {
		return adapter.getMaxCount();
	}

	@Override
	public void remove(int position) {
		adapter.remove(position);
	}

	@Override
	public int getCount() {
		return adapter.getCount();
	}

	@Override
	public void clear() {
		adapter.clear();
	}

	@Override
	public void replaceListRef(List<E> list) {
		adapter.replaceListRef(list);
	}

	@Override
	public void addToListHead(List<E> pItemList) {
		adapter.addToListHead(pItemList);
	}

	@Override
	public void addToListHead(E pItem) {
		adapter.addToListHead(pItem);
	}

	@Override
	public void addToListBack(List<E> list) {
		adapter.addToListBack(list);
	}

	@Override
	public void removeListBack(int nRemoveCount) {
		adapter.removeListBack(nRemoveCount);
	}

	@Override
	public void addToListBackWithOutNotifyData(E e) {
		adapter.addToListBackWithOutNotifyData(e);
	}

	@Override
	public void addToListBack(E e) {
		adapter.addToListBack(e);
	}

	@Override
	public List<E> getListCopy() {
		return adapter.getListCopy();
	}

	@Override
	public List<E> getListRef() {
		return adapter.getListRef();
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void tryRemove(Object object) {
		adapter.tryRemove(object);
	}

	@Override
	public void addToListBackWithOutNotifyData(List<E> list) {
		adapter.addToListBackWithOutNotifyData(list);
	}

	@Override
	public void sort(Comparator<?> comp) {
		adapter.sort(comp);
	}
	// ////////////////////////////////////////////////////////////////////////////////////////
	@Override  
    public Fragment getItem(int arg0) {  
       return adapter.getItem(arg0);  
    } 

	@Override
	public void rePlace(int position, E it) {
		adapter.rePlace(position, it);
	}
	// ////////////////////////////////////////////////////////////////////////////////////////
	protected final DisplayImageOptions getDisplayImageOptions(final int nRes) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// ���杞戒腑
				.showStubImage(nRes)
				// ���杞戒负绌烘��
				.showImageForEmptyUri(nRes)
				// ���杞藉け��ユ��
				.showImageOnFail(nRes)
				// ���瀛�缂�瀛�
				.cacheInMemory(false)
				// 纭����涓�缂�瀛�
				.cacheOnDisc(true).build();
		return options;
	}

	protected final void getAsyncBitMap(final ImageView imgView,
			final String picUrl) {
		this.getAsyncBitMap(imgView, picUrl, R.drawable.default_image_layer);// ,R.drawable.default_img);
	}

	protected final void getAsyncBitMap(final ImageView imgView,
			final String picUrl, final int nRes) {
		ImageLoader.getInstance().displayImage(picUrl, imgView,
				getDisplayImageOptions(nRes));
	}


}
