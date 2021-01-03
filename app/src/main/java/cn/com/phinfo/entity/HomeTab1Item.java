package cn.com.phinfo.entity;

import java.io.Serializable;
import java.util.Random;

import com.alibaba.fastjson.annotation.JSONField;
import com.heqifuhou.utils.ParamsCheckUtils;

import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.MessageStaticsListRun;

public class HomeTab1Item implements Serializable{
	private int id;
	private int nResID;
	private String text;
	private String des;
	private String modifiedOn;
	private String badgeCount;
	//此数用来记在本地，存储被点击的次数
	private int clickCount=0;
	//此处表示是否被选中为显示的
	private boolean bCheck = false;
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	public int getClickCount() {
		return clickCount;
	}
	@JSONField(serialize=false)
	public HomeTab1Item addClickCount() {
		this.clickCount += 1;
		return this;
	}


	public HomeTab1Item(){
	}

	public static HomeTab1Item createWithModel(MessageStaticsListRun.MessageStaticItem item) {
		HomeTab1Item itemView = new HomeTab1Item();

		itemView.setId(Integer.parseInt(item.getModuleId()));
		itemView.setText(item.getModuleName());
		itemView.setDes("点击查看详情");
		itemView.setModifiedOn(item.getModifiedOn());
		itemView.setBadgeCount(item.getQuantity());

		return itemView;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getBadgeCount() {
		return badgeCount;
	}

	public void setBadgeCount(String badgeCount) {
		this.badgeCount = badgeCount;
	}

	public int getnResID() {
		return nResID;
	}

	public void setnResID(int nResID) {
		this.nResID = nResID;
	}

	public boolean isbCheck() {
		return bCheck;
	}

	public String getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}
