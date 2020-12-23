package cn.com.phinfo.entity;

import java.io.Serializable;
import java.util.UUID;

import com.alibaba.fastjson.annotation.JSONField;

public class FilePickerItem implements Serializable{
	private String guid;
	private String filePicker;
	public FilePickerItem(){
		
	}
	public FilePickerItem(String filePicker){
		this.filePicker = filePicker;
		this.guid =  UUID.randomUUID().toString();
	}
	public String getGuid(){
		return this.guid;
	}
	public String getFilePicker() {
		return filePicker;
	}

	public void setFilePicker(String filePicker) {
		this.filePicker = filePicker;
	}
	
	@JSONField(serialize = false)
	public String getLocalFileUri() {
		return "file:///" + filePicker;
	}
	@JSONField(serialize = false)
	public String getLocalName(){
		return this.getFileName(filePicker);
	}
	@JSONField(serialize = false)
	private String getFileName(String pathandname){  
        int start=pathandname.lastIndexOf("/");  
        int end=pathandname.lastIndexOf(".");  
        if(start!=-1 && end!=-1){  
            return pathandname.substring(start+1,end);    
        }else{  
            return null;  
        }  
          
    } 
}
