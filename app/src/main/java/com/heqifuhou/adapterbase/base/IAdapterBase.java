package com.heqifuhou.adapterbase.base;

import java.util.Comparator;
import java.util.List;

public interface IAdapterBase<E> {
	void setMaxCount(final int nMaxCount);

	// 取得设置最大的容量
	int getMaxCount();

	// 根据位置移除
	void remove(int position);
	void tryRemove(Object object);
	void clear();

	void rePlace(int position,E it);
	void replaceListRef(final List<E> list);

	void addToListHead(final List<E> pItemList);

	void addToListHead(final E pItem);

	void addToListBack(final List<E> list);
	void addToListBackWithOutNotifyData(final List<E> list);

	void removeListBack(final int nRemoveCount);

	void addToListBackWithOutNotifyData(final E e);

	void addToListBack(final E e);

	List<E> getListCopy();

	List<E> getListRef();
	void sort(Comparator<?> comp);
}
