package cn.com.phinfo.db;

import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.entity.HomeItem;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.AppModulesRun.AppModules;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

public class NetAppModule2DB{
	static final String DB_NAME = "tab2";
	private static NetAppModule2DB instance = new NetAppModule2DB();
	private Context context;
	private NetAppModule2DB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static NetAppModule2DB getInstance(){
		return instance;
	}
	public void saveToDB(List<AppModules> lst) {
		synchronized (NetAppModule2DB.class) {
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
	
	public final List<AppModules> getFromDB() {
		synchronized (NetAppModule2DB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("body", "");
			List<AppModules> lst = null;
			if(!ParamsCheckUtils.isNull(s)){
				lst =  JSON.parseArray(s,AppModules.class);
			}else{
				lst = new Stack<AppModules>();
			}
			return lst;
		}
	}
	
	
	


}
