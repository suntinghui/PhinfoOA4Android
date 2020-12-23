package com.heqifuhou.draggridView;

import android.content.Context;
import android.util.AttributeSet;

public class NoScrollDragGridView extends DragGridView {
    public NoScrollDragGridView(Context context) {
        super(context);
    }

    public NoScrollDragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollDragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override 
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

        int expandSpec = MeasureSpec.makeMeasureSpec( 
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
        super.onMeasure(widthMeasureSpec, expandSpec); 
    } 
}