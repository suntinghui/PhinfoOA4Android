package cn.com.phinfo.entity;

import com.baidu.mapapi.search.core.PoiInfo;

public class ExPoiInfo extends PoiInfo{
	public static ExPoiInfo init(PoiInfo it){
		ExPoiInfo info = new ExPoiInfo();
		info.address = it.address;
		info.name = it.name;
		info.location = it.location;
		return info;
	}
	private double size=0;
	private boolean select=false;
	public boolean getSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
}
