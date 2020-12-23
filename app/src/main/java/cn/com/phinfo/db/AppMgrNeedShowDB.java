package cn.com.phinfo.db;

import java.util.LinkedHashSet;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.oaact.MyApplet;

public class AppMgrNeedShowDB{
	static final String DB_NAME = "appmgr2";
	private static AppMgrNeedShowDB instance = new AppMgrNeedShowDB();
	private Context context;
	private AppMgrNeedShowDB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static AppMgrNeedShowDB getInstance(){
		return instance;
	}
	public void saveToDB(LinkedHashSet<Integer> lst) {
		this._saveToDB("body",lst);
	}
	
	public final LinkedHashSet<Integer> getFromDB(){
		return this._getFromDB("body");
	}

	public void _saveToDB(String key,LinkedHashSet<Integer> lst) {
		synchronized (AppMgrNeedShowDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor editor = xml.edit();
			try{
				editor.putString(key, JSON.toJSONString(lst));
			}catch(Exception e){
				editor.putString(key,"");
			}
			editor.commit();
		}
	}
	
	public final LinkedHashSet<Integer> _getFromDB(String key) {
		synchronized (AppMgrNeedShowDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString(key, "");
			LinkedHashSet<Integer> has = new LinkedHashSet<Integer>();
			if(!ParamsCheckUtils.isNull(s)){
				List<Integer> lst =  JSON.parseArray(s,Integer.class);
				has.addAll(lst);
			}
			return has;
		}
	}
	
	
	


}
