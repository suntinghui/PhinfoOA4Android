package cn.com.phinfo.protocol;import com.heqifuhou.protocolbase.QuickRunObjectBase;import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileResultBean;public class NewsAttacheFileRun extends QuickRunObjectBase {	public NewsAttacheFileRun(String pid) {		super(LURLInterface.GET_NEWS_ATTACHEFILES(pid), null, AttacheFileResultBean.class);	}}