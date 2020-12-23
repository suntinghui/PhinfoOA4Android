package com.heqifuhou.adapterbase;

import java.util.Comparator;
import java.util.List;

import android.database.DataSetObserver;
import android.widget.BaseAdapter;

import com.heqifuhou.adapterbase.base.AdapterListBase;
import com.heqifuhou.adapterbase.base.IAdapterListBase;
import com.heqifuhou.adapterbase.base.AdapterListBase.OnNotifyDataSetChanged;



public abstract class MyAdapterBaseAbs<E> extends BaseAdapter implements IAdapterListBase<E>,OnNotifyDataSetChanged{
	private IAdapterListBase<E> mAdapter = null;
	public MyAdapterBaseAbs(){
		mAdapter = new AdapterListBase<E>(this);
	}
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	   if (observer != null) {
	       super.unregisterDataSetObserver(observer);
	   }
	}
	@Override
	public int getCount() {
		return mAdapter.getCount();
	}

	@Override
	public E getItem(int arg0) {
		return mAdapter.getItem(arg0);
	}

	@Override
	public void rePlace(int position, E it) {
		mAdapter.rePlace(position, it);
	}
	@Override
	public long getItemId(int arg0) {
		return mAdapter.getItemId(arg0);
	}
	@Override
	public void setMaxCount(int nMaxCount) {
		mAdapter.setMaxCount(nMaxCount);
	}

	@Override
	public int getMaxCount() {
		return mAdapter.getMaxCount();
	}

	@Override
	public void remove(int position) {
		mAdapter.remove(position);
	}
	@Override
	public void tryRemove(Object object)
	{
		mAdapter.tryRemove(object);
	}

	@Override
	public void clear() {
		mAdapter.clear();
	}

	@Override
	public void replaceListRef(List<E> list) {
		mAdapter.replaceListRef(list);
	}

	@Override
	public void addToListHead(List<E> pItemList) {
		mAdapter.addToListHead(pItemList);
	}

	@Override
	public void addToListHead(E pItem) {
		mAdapter.addToListHead(pItem);
	}

	@Override
	public void addToListBack(List<E> list) {
		mAdapter.addToListBack(list);
	}
	
	@Override
	public void addToListBackWithOutNotifyData(final List<E> list){
		mAdapter.addToListBackWithOutNotifyData(list);
	}
	@Override
	public void removeListBack(int nRemoveCount) {
		mAdapter.removeListBack(nRemoveCount);
	}

	@Override
	public void addToListBackWithOutNotifyData(E e) {
		mAdapter.addToListBackWithOutNotifyData(e);
	}

	@Override
	public void addToListBack(E e) {
		mAdapter.addToListBack(e);
	}

	@Override
	public List<E> getListCopy() {
		return mAdapter.getListCopy();
	}

	@Override
	public List<E> getListRef() {
		return mAdapter.getListRef();
	}
	@Override
	public void notifyDataListSetChanged() {
		this.notifyDataSetChanged();
	}
	@Override
	public void sort(Comparator<?> comp){
		mAdapter.sort(comp);
		this.notifyDataSetChanged();
	}
}
