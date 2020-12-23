package com.heqifuhou.adapterbase.base;



public interface IAdapterListBase<E> extends IAdapterBase<E> {

	int getCount();

	E getItem(int position);

	long getItemId(int position);

}
