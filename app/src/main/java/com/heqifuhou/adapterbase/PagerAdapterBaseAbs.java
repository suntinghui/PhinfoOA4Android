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
import android.support.v4.view.PagerAdapter;
import android.widget.ImageView;
import cn.com.phinfo.oaact.R;

public abstract class PagerAdapterBaseAbs<E> extends PagerAdapter implements
		IAdapterBase<E>, OnNotifyDataSetChanged {
	private IAdapterListBase<E> adapter = null;

	public PagerAdapterBaseAbs() {
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
	protected final DisplayImageOptions getDisplayImageOptions(final int nRes) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 加载中
				.showStubImage(nRes)
				// 加载为空时
				.showImageForEmptyUri(nRes)
				// 加载失入时
				.showImageOnFail(nRes)
				// 内存缓存
				.cacheInMemory(false)
				// 硬盘中缓存
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
