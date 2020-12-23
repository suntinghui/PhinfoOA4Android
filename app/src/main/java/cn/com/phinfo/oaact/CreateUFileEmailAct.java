package cn.com.phinfo.oaact;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;

//写邮件
public class CreateUFileEmailAct extends CreateEmailAct {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s = this.getIntent().getExtras().getString("FileItem");
		FileItem _it = JSON.parseObject(s,
				FileItem.class);
		_it.setIsLocalFile(false);
		BitmapDataListInstanceUtils.getRefInstance().add(_it);
		adapter.notifyDataSetChanged();
		showAttacheView();
	}
}
