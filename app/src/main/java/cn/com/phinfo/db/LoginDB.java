package cn.com.phinfo.db;

import com.alibaba.fastjson.JSON;

import android.content.Context;
import android.content.SharedPreferences;
import cn.com.phinfo.oaact.MyApplet;
import cn.com.phinfo.protocol.LoginRun.LoginResultBean;

public class LoginDB{
	static final String LOGIN_ID = "login1";
	private static LoginDB instance = new LoginDB();
	private Context context;
	private LoginDB(){
		this.context = MyApplet.getInstance().getApplicationContext();
	}
	public static LoginDB getInstance(){
		return instance;
	}
	public void setNull(){
		saveToDB("","",null,false);
	}

	public boolean isTest(){
		synchronized (LoginDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(LOGIN_ID,
							Context.MODE_PRIVATE);
			return xml.getBoolean("bTest",false);
		}
	}

	public void saveToDB(String username,String password,LoginResultBean body,boolean bTest) {
		synchronized (LoginDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(LOGIN_ID,
							Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = xml.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.putBoolean("bTest", bTest);
			try{
				editor.putString("body", JSON.toJSONString(body));
			}catch(Exception e){
				editor.putString("body","");
			}
			editor.commit();
		}
	}
	public final String getUsernameFromDB() {
		synchronized (LoginDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(LOGIN_ID,
							Context.MODE_PRIVATE);
			return xml.getString("username", "");
		}
	}
	public final LoginResultBean getUserBean(){
		synchronized (LoginDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(LOGIN_ID,
							Context.MODE_PRIVATE);
			String s =  xml.getString("body", "");
			try{
				return JSON.parseObject(s,LoginResultBean.class);
			}catch(Exception e){
				
			}
			return null;
		}
	}

	public final String getPasswordFromDB() {
		synchronized (LoginDB.class) {
			SharedPreferences xml = context.getApplicationContext()
					.getSharedPreferences(LOGIN_ID,
							Context.MODE_PRIVATE);
			return xml.getString("password", "");
		}
	}
}
