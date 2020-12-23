package com.heqifuhou.imgutils;

import java.util.List;
import java.util.Stack;

import com.heqifuhou.adapterbase.base.AdapterListBase.OnNotifyDataSetChanged;

public class BitmapDataListInstanceUtils {
	// 共享的列表
	private List<OnNotifyDataSetChanged> adList = new Stack<OnNotifyDataSetChanged>();
	private List<FileItem> bitmapList = new Stack<FileItem>();
	private static BitmapDataListInstanceUtils instance = null;
	private BitmapDataListInstanceUtils() {
	}
//	//换个玩法
	public static BitmapDataListInstanceUtils Instance(){
		instance = new BitmapDataListInstanceUtils();
		return instance;
	}
//	public static void InstanceByRef(BitmapDataListInstanceUtils utils){
//		if(utils!=null){
//			instance =utils;	
//		}
//	}
	public static BitmapDataListInstanceUtils getRefInstance() {
		if(instance == null){
			Instance();
		}
		return instance;
	}
//	public List<BitmapUploadItem> getListCopy(){
//		List<BitmapUploadItem> lst = new Stack<BitmapUploadItem>();
//		lst.addAll(bitmapList);
//		return lst;
//	}
//	public void addListCopy(List<BitmapUploadItem> lst){
//		if(lst!=null){
//			bitmapList.addAll(lst);
//		}
//	}

	public void setNull() {
		instance = null;
		adList.clear();
	}


	public List<FileItem> getListRef() {
		return bitmapList;
	}

	public void Del(int index) {
		bitmapList.remove(index);
	}
	
	public void Del(FileItem f){
		for(int i=0;i<bitmapList.size();i++)
		{
			if(bitmapList.get(i).isEquals(f)){
				bitmapList.remove(i);
				break;
			}
		}
	}
	public void addAll(List<FileItem> lst){
		this.bitmapList.addAll(lst);
	}
	public void add(FileItem fit){
		this.bitmapList.add(fit);
	}
	public void clear(){
		bitmapList.clear();
	}
}
