package cn.com.phinfo.db;

import java.util.List;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.NewsChannelRun.HSelectItem;

public class NewsChanelAddressDB{
	static final String DB_NAME = "HSelectItem";
	private static NewsChanelAddressDB instance = new NewsChanelAddressDB();
	private Context context;
	private NewsChanelAddressDB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static NewsChanelAddressDB getInstance(){
		return instance;
	}
	public void saveToDB(List<HSelectItem> lst) {
		synchronized (NewsChanelAddressDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = xml.edit();
			try{
				editor.putString("body", JSON.toJSONString(lst));
			}catch(Exception e){
				editor.putString("body","");
			}
			editor.commit();
		}
	}
	
	public final List<HSelectItem> getFromDB() {
		synchronized (NewsChanelAddressDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("body", "");
			if(!ParamsCheckUtils.isNull(s)){
				return   JSON.parseArray(s,HSelectItem.class);

			}
			return new Stack<HSelectItem>();
		}
	}
	
	
	


}
