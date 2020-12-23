package com.heqifuhou.protocolbase;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.utils.ParamsCheckUtils;

//{"status":1,"msg":"","userid":"27a909e1-9fb4-4c67-afcd-3dc9032fdd6b","nickname":"管理员","userName":"管理员","access_token":"27a909e1-9fb4-4c67-afcd-3dc9032fdd6b","expires_in":""}
//返回数据的基类
public class HttpResultBeanBase implements IResultCode{
	private int status=-99;
	private String msg;
	protected String json;
	private int rowsPerPage;
	private int page;
	private String str;
	public int getRowsPerPage() {
		return rowsPerPage;
	}
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getJson() {
		return json;
	}
	public HttpResultBeanBase(){
	}
	public HttpResultBeanBase initWithStr(String json){
		try{
			//测试一下是不是json
			JSON.parse(json);
			this.json = json;
			this.status=1;
		}catch(Exception e){
			this.status=0;
			str = json;
		}
		return this;
	}
	public String get2Str(){
		return str;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		if(ParamsCheckUtils.isNull(msg)){
			msg="网络错误,请稍后再试";
		}
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public boolean isStr(){
		return !ParamsCheckUtils.isNull(json);
	}
	public boolean isOK(){
		return 1==status;
	}
	public void setOK(){
		status = 1;
	}

	public boolean isError(){
		return 0==status;
	}
	
//	private Header head = new Header();
//	public Header getHead() {
//		return head;
//	}
//	public void setHead(Header head) {
//		this.head = head;
//	}
//
//	
//	@JSONField(serialize=false)
//	public final String getMsg(){
//		if(head!=null&&!ParamsCheckUtils.isNull(this.head.getReturnMessage())){
//			return this.head.getReturnMessage();
//		}
//		return "请求错误,请稍候再试";
//	}
//	@JSONField(serialize=false)
//	public final boolean isCODE_200(){
//		if(head!=null){
//			return CODE_200==head.getReturnState();
//		}
//		return false;
//	}
//	@JSONField(serialize=false)
//	public final boolean isCODE_801(){
//		if(head!=null){
//			return CODE_801==head.getReturnState();
//		}
//		return false;
//	}
//	@JSONField(serialize=false)
//	public final boolean isCODE_401(){
//		if(head!=null){
//			return CODE_401==head.getReturnState();
//		}
//		return false;
//	}
//	
//
//
//
//	//header
//	public static class Header{
//		private int ReturnState = Integer.MIN_VALUE;
//		private String ReturnMessage="";
//
//
//		public int getReturnState() {
//			return ReturnState;
//		}
//		public void setReturnState(int returnState) {
//			ReturnState = returnState;
//		}
//		public String getReturnMessage() {
//			return ReturnMessage;
//		}
//		public void setReturnMessage(String returnMessage) {
//			ReturnMessage = returnMessage;
//		}
//		@JSONField(serialize=false)
//		public final boolean isCODE_0(){
//			return CODE_200==ReturnState;
//		}
//		@JSONField(serialize=false)
//		public final boolean isCODE_801(){
//			return CODE_801==ReturnState;
//		}
//	}
}
