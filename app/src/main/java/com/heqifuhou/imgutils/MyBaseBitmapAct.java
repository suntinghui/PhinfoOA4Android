package com.heqifuhou.imgutils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.heqifuhou.actbase.HttpLoginMyActBase;

public abstract class MyBaseBitmapAct extends HttpLoginMyActBase implements IPopMenuUtils{
	private IPopMenuUtils utils = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		utils = new PopMenuUtils(this);
		super.onCreate(savedInstanceState);
		utils.setMaxPic(Integer.MAX_VALUE);
		
	}
	public View  InitBitmapView(boolean bTakePic) {
		return utils.InitBitmapView(bTakePic);
	}
	public View InitBitmapView() {
		return utils.InitBitmapView();
	}
	protected void onPause() {
		super.onPause();
	}
	public void onNotifyDataSetChanged()
	{
		utils.onNotifyDataSetChanged();
	}
	protected void onResume() {
		super.onResume();
		onNotifyDataSetChanged();
	}

	public void setMaxPic(int nPic){
		utils.setMaxPic(nPic);
	}
	public void pickPhoto(){
		utils.pickPhoto();
	}
	public void showDialog()
	{
		utils.showDialog();
	}
	public void takePhoto()
	{
		utils.takePhoto();
	}
	public void setNumColumns(int numColumns){
		utils.setNumColumns(numColumns);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		onBitmapUtilsMyActivityResult(requestCode, resultCode, data);
	}
	public void onBitmapUtilsMyActivityResult(int requestCode, int resultCode, Intent data) {
		utils.onBitmapUtilsMyActivityResult(requestCode, resultCode, data);
	}
}
