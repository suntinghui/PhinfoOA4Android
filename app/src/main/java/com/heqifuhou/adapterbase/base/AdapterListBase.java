package com.heqifuhou.adapterbase.base;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;


public class AdapterListBase<E> implements IAdapterListBase<E>{
	private OnNotifyDataSetChanged listener = null;
	private List<E> mList = new Stack<E>();
	private byte[] sLock = new byte[0];
	private int mCount = Integer.MAX_VALUE;// 无限定数
	public AdapterListBase(OnNotifyDataSetChanged l){
		listener = l;
	}
	
	// 设置最大的容量
	public  void setMaxCount(final int nMaxCount) {
		this.mCount = nMaxCount;
	}
	// 取得设置最大的容量
	public  int getMaxCount() {
		return this.mCount;
	}

	// 根据位置移除
	public  void remove(int position) {
		synchronized (sLock) {
			if (position >= 0 && position < mList.size()) {
				mList.remove(position);
				if(listener!=null){
					listener.notifyDataListSetChanged();
				}
			}
		}
	}
	// 根据位置移除
	public  void rePlace(int position,E it) {
		synchronized (sLock) {
			if (position >= 0 && position < mList.size()) {
				//替换列表中指定位置的元素
				mList.set(position, it);
				if(listener!=null){
					listener.notifyDataListSetChanged();
				}
			}
		}
	}

	public int getCount() {
		synchronized (sLock) {
			keepListNotEmpty();
			return mList.size();
		}
	}

	public E getItem(int position) {
		synchronized (sLock) {
			keepListNotEmpty();
			if (position >= 0 && position < mList.size()) {
				return mList.get(position);
			}
			return null;
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public  void clear() {
		synchronized (sLock) {
			keepListNotEmpty();
			mList.clear();
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}
	public  void replaceListRef(final List<E> list) {
		synchronized (sLock) {
			if (list == null) {
				keepListNotEmpty();
			} else {
				mList = list;
			}
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}

	public  void addToListHead(final List<E> pItemList) {
		synchronized (sLock) {
			keepListNotEmpty();
			for (int i = pItemList.size() - 1; i >= 0; i--) {
				mList.add(0, pItemList.get(i));
			}
			if ((this.mCount != Integer.MAX_VALUE)) {
				int n = mList.size() - mCount;
				while (n-- > 0) {
					mList.remove(mList.size() - 1);
				}
			}
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}

	public  void addToListHead(final E pItem) {
		synchronized (sLock) {
			keepListNotEmpty();
			mList.add(0, pItem);
			if ((this.mCount != Integer.MAX_VALUE)) {
				int n = mList.size() - mCount;
				while (n-- > 0) {
					mList.remove(mList.size() - 1);
				}
			}
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}

	public  void addToListBack(final List<E> list) {
		synchronized (sLock) {
			addToListBackWithOutNotifyData(list);
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}
	public void addToListBackWithOutNotifyData(final List<E> list){
		synchronized (sLock) {
			keepListNotEmpty();
			mList.addAll(list);
			// 从头移除超过值
			if ((this.mCount != Integer.MAX_VALUE)) {
				int n = mList.size() - mCount;
				while (n-- > 0) {
					mList.remove(0);
				}
			}
		}
	}

	public  void removeListBack(final int nRemoveCount) {
		removeListBack(nRemoveCount, true);
	}
	
	public void tryRemove(Object object){
		keepListNotEmpty();
		if(mList.contains(object)){
			mList.remove(object);
			if(listener!=null){
				listener.notifyDataListSetChanged();
			}
		}
	}

	private void removeListBack(final int nRemoveCount, final boolean bNotify) {
		synchronized (sLock) {
			keepListNotEmpty();
			int n = Math.min(mList.size(), nRemoveCount);
			while (n-- > 0) {
				mList.remove(0);
			}
			if (bNotify) {
				if(listener!=null){
					listener.notifyDataListSetChanged();
				}
			}
		}
	}

	public  void addToListBackWithOutNotifyData(final E e) {
		synchronized (sLock) {
			keepListNotEmpty();
			mList.add(e);
			// 从头移除超过值
			if ((this.mCount != Integer.MAX_VALUE)) {
				int n = mList.size() - mCount;
				while (n-- > 0) {
					mList.remove(0);
				}
			}
		}
	}

	public  void addToListBack(final E e) {
		addToListBackWithOutNotifyData(e);
		if(listener!=null){
			listener.notifyDataListSetChanged();
		}
	}

	public  List<E> getListCopy() {
		synchronized (sLock) {
			List<E> mListRt = new Stack<E>();
			mListRt.addAll(mList);
			return mListRt;
		}
	}

	public  List<E> getListRef() {
		synchronized (sLock) {
			return mList;
		}
	}

	private  void keepListNotEmpty() {
		if (mList == null) {
			mList = new Stack<E>();
		}
	}
	public interface OnNotifyDataSetChanged{
		void notifyDataListSetChanged();
	}
	
	public void sort(Comparator comp){
		keepListNotEmpty();
		Collections.sort(mList,comp);
	}
}
