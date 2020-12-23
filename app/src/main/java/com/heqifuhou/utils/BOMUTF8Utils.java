package com.heqifuhou.utils;

public class BOMUTF8Utils {
	public static String DelBOMUTF8(final String s){
		String _s = s;
		for(;;){
			try{
				if(_s != null && _s.startsWith("\ufeff"))  
				{  
					_s =  _s.substring(1);  
				}else{
					break;
				}
			}catch(Exception e){	
			}
		}
		return _s;
	}

}
