package com.heqifuhou.draggridView;

import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;

import android.widget.AdapterView;
import android.widget.BaseAdapter;

public abstract class GridViewAdapterBaseAbs<E> extends MyImgAdapterBaseAbs<E> {
    protected int hidePosition = AdapterView.INVALID_POSITION;
    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public abstract void removeView(int pos);

    //更新拖动时的gridView
    public abstract void swapView(int draggedPos, int destPos);
}
