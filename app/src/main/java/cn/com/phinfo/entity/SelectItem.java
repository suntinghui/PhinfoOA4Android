package cn.com.phinfo.entity;

import java.io.Serializable;

public class SelectItem implements Serializable{
	public SelectItem(){
	}
	public SelectItem(int id,String text){
		this.id=id;
		this.text = text;
	}
	private int id=0;
	private String text;
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
}
