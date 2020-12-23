package cn.com.phinfo.oaact;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.phinfo.db.SettingDB;
import cn.com.phinfo.entity.IPListItem;
import cn.com.phinfo.protocol.LURLInterface;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.MyActBase;
import com.heqifuhou.utils.ParamsCheckUtils;

//添加
public class AddSettingAct extends MyActBase {
	private EditText name, iptxt;
	private IPListItem oldIt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addTextNav(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				save();
			}
		}, "新添服务器", "完成");
		this.addViewFillInRoot(R.layout.act_addsetting);
		name = (EditText) this.findViewById(R.id.name);
		iptxt = (EditText) this.findViewById(R.id.iptxt);

		if (this.getIntent() != null && this.getIntent().getExtras() != null) {
			String s = this.getIntent().getExtras().getString("M");
			oldIt = JSON.parseObject(s, IPListItem.class);
			name.setText(oldIt.getName());
			iptxt.setText(oldIt.getIp());
		}
	}

	private void save() {
		String ipStr = iptxt.getText().toString().trim();
		String nameStr = name.getText().toString().trim();
		if (ParamsCheckUtils.isNull(ipStr)) {
			this.showToast("主机不能为空");
			return;
		}
		IPListItem it = new IPListItem();
		it.setIp(ToDBC(ipStr));
		it.setName(nameStr);
		SettingDB.getInstance().replace(oldIt, it);

		// 第一个添加的默认就是
		if (SettingDB.getInstance().setCurr(it)) {
			LURLInterface.init();
		}
		this.finish();
	}

	/**
	 * 转半角的函数(DBC case)<br/>
	 * <br/>
	 * 全角空格为12288，半角空格为32 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
	 * 
	 * @param input
	 *            任意字符串
	 * @return 半角字符串
	 * 
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				// 全角空格为12288，半角空格为32
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				// 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
