package cn.com.phinfo.db;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

public class OfftenAddressDB{
	static final String DB_NAME = "OfftenAddressDB";
	private static OfftenAddressDB instance = new OfftenAddressDB();
	private Context context;
	private OfftenAddressDB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static OfftenAddressDB getInstance(){
		return instance;
	}
	
	public void append(UnitandaddressItem it){
		boolean bFound = false;
		List<UnitandaddressItem> ls = getFromDB();
		for(int i=0;i<ls.size();i++){
			UnitandaddressItem oldIt = ls.get(i);
			if(oldIt.getId().equals(it.getId())){
				oldIt.addCount();
				bFound = true;
				break;
			}
		}
		if(!bFound){
			ls.add(it);
		}
		//清除超过50个的
		if(ls.size()>50){
			//排序
			Collections.sort(ls, new Comparator<UnitandaddressItem>() {
				@Override
				public int compare(UnitandaddressItem arg0, UnitandaddressItem arg1) {
					return arg1.getnCount()-arg0.getnCount();
				}
			});
			for(int i=ls.size()-1;i>50;i--){
				ls.remove(i);
			}
		}
		saveToDB(ls);
	}
	
	public final List<UnitandaddressItem> getData() {
		List<UnitandaddressItem> ls = getFromDB();
		//排序
		Collections.sort(ls, new Comparator<UnitandaddressItem>() {
			@Override
			public int compare(UnitandaddressItem arg0, UnitandaddressItem arg1) {
				return arg0.getnCount()-arg1.getnCount();
			}
		});
		return ls;
	}
	private void saveToDB(List<UnitandaddressItem> lst) {
		synchronized (OfftenAddressDB.class) {
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
	
	private final List<UnitandaddressItem> getFromDB() {
		synchronized (OfftenAddressDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(DB_NAME,
							Context.MODE_PRIVATE);
			String s  = xml.getString("body", "");
			if(!ParamsCheckUtils.isNull(s)){
				return JSON.parseArray(s,UnitandaddressItem.class);
			}
			return new Stack<UnitandaddressItem>();
		}
	}
	
	
	


}
