package cn.com.phinfo.db;

import java.util.List;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.entity.IPListItem;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.LURLInterface;

public class SettingDB{
	static final String DB_NAME = "setting";
	private static SettingDB instance = new SettingDB();
	private Context context;
	private SettingDB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static SettingDB getInstance(){
		return instance;
	}

	public void saveToDB(List<IPListItem> lst) {
		synchronized (SettingDB.class) {
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

	
	public final List<IPListItem> getFromDB() {
		synchronized (SettingDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("body", "");
			if(!ParamsCheckUtils.isNull(s)){
				return  JSON.parseArray(s,IPListItem.class);
			}
			return new Stack<IPListItem>();
		}
	}
	
	public final void replace(IPListItem oldIt,IPListItem newIt){
		List<IPListItem> ls = SettingDB.getInstance().getFromDB();
		if(oldIt!=null){
			for(int i=0;i<ls.size();i++){
				IPListItem it = ls.get(i);
				//如果在里面发现旧的，删了
				if(it.getIp().equals(oldIt.getIp())&&it.getName().equals(oldIt.getName())){
					it.setIp(newIt.getIp());
					it.setName(newIt.getName());
					break;
				}
			}
		}else{
			ls.add(newIt);
		}
		SettingDB.getInstance().saveToDB(ls);
	}
	
	public final void delItem(IPListItem it){
		List<IPListItem> ls = SettingDB.getInstance().getFromDB();
		for(int i=0;i<ls.size();i++){
			if(ls.get(i).getIp().equals(it.getIp())){
				ls.remove(i);
				break;
			}
		}
		SettingDB.getInstance().saveToDB(ls);
		//看看当前的是不是
		IPListItem currIt = getCurrFromDB();
		if(currIt!=null){
			if(it.getIp().equals(currIt.getIp())){
				setCurr(new IPListItem());
			}
		}
	}
	public final boolean setCurr(IPListItem newIt){
		IPListItem _it = getCurrFromDB();
		//如果旧的和新的相同
		if((_it.getName().equals(newIt.getName()))&&(_it.getIp().equals(newIt.getIp()))){
			return false;
		}
		_setCurr(newIt);
		return true;	
	}
	private final void _setCurr(IPListItem it){
		synchronized (SettingDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = xml.edit();
			try{
				editor.putString("curr", JSON.toJSONString(it));
			}catch(Exception e){
				editor.putString("curr","");
			}
			editor.commit();
		}
	}
	public final IPListItem getCurrFromDB() {
		synchronized (SettingDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("curr", "");
			if(!ParamsCheckUtils.isNull(s)){
				return  JSON.parseObject(s,IPListItem.class);
			}
			return new IPListItem();
		}
	}


}
