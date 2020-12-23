package com.heqifuhou.draggridView;

import android.widget.AdapterView;
public abstract class MyGridViewAdapterBaseAbs<E> extends GridViewAdapterBaseAbs<E> {
    protected int hidePosition = AdapterView.INVALID_POSITION;
    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
        	this.getListRef().add(destPos+1, getItem(draggedPos));
            remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
        	this.getListRef().add(destPos, getItem(draggedPos));
           remove(draggedPos+1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }
}
