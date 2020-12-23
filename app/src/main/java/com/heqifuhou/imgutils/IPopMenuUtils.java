package com.heqifuhou.imgutils;

import android.content.Intent;
import android.view.View;

public interface IPopMenuUtils {
	View  InitBitmapView();
	View InitBitmapView(boolean bTakePic);
	void pickPhoto();
	void takePhoto();
	void showDialog();
	void setNumColumns(int numColumns);
	void setMaxPic(int nPic);
	void onNotifyDataSetChanged();
	void onBitmapUtilsMyActivityResult(int requestCode, int resultCode, Intent data);
}
