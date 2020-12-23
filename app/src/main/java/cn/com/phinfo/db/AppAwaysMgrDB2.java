package cn.com.phinfo.db;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.entity.HomeItem;
import cn.com.phinfo.oaact.MyApplet;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

public class AppAwaysMgrDB2{
	static final String DB_NAME = "awaysshowappmgr";
	private static AppAwaysMgrDB2 instance = new AppAwaysMgrDB2();
	private Context context;
	private AppAwaysMgrDB2(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static AppAwaysMgrDB2 getInstance(){
		return instance;
	}
	
	public void add2DB(HomeItem it){
		List<HomeItem> lst =  getFromDB();
		for(int i=0;i<lst.size();i++){
			if(lst.get(i).getId() == it.getId()){
				it.addClickCount();
				saveToDB(lst);
				return;
			}
		}
		lst.add(it);
		saveToDB(lst);
	}
	private void saveToDB(List<HomeItem> lst) {
		synchronized (AppAwaysMgrDB2.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor editor = xml.edit();
			try{
				editor.putString("body", JSON.toJSONString(lst));
			}catch(Exception e){
				editor.putString("body","");
			}
			editor.commit();
		}
	}
	
	public final List<HomeItem> getFromDB() {
		synchronized (AppAwaysMgrDB2.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("body", "");
			if(!ParamsCheckUtils.isNull(s)){
				return JSON.parseArray(s,HomeItem.class);
			}
			return  new Stack<HomeItem>();
		}
	}
	
	
	


}
