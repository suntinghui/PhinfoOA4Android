package cn.com.phinfo.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.com.phinfo.oaact.MyApplet;

public class DBVerXml {
	private static final String VER_XML = "dbverxml";
	private SharedPreferences prefer = null;
	public DBVerXml() {
		Context context = MyApplet.getInstance();
		prefer = context.getSharedPreferences(VER_XML,
				Context.MODE_PRIVATE);
	}

	public final int getVer() {
			int n=  prefer.getInt("ver",0);
			return n;
	}

	public void saveVer(int ver) {
			Editor edit = prefer.edit();
			edit.putInt("ver", ver);
			edit.commit();
	}

}
