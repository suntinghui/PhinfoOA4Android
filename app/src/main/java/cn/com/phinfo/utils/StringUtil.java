package cn.com.phinfo.utils;

import java.util.Map;

import cn.hutool.core.map.MapUtil;

public class StringUtil {

    public static String map2GetUrlParam(Map<String, String> map) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String key : map.keySet()) {
                sb.append(key).append("=").append(MapUtil.getStr(map, key, ""));
                sb.append("&");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
