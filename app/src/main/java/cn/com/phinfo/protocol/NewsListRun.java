package cn.com.phinfo.protocol;import com.heqifuhou.protocolbase.QuickRunObjectBase;import cn.com.phinfo.protocol.NewsDefaultListRun.NewsResultBean;public class NewsListRun extends QuickRunObjectBase {	public NewsListRun(final int PageNumber,String tag,int contentTypeCode) {		this(PageNumber,tag,"",contentTypeCode);	}	public NewsListRun(final int PageNumber,String tag,String Search,int contentTypeCode) {		super(LURLInterface.GET_URL_NEWS_GETLIST(PageNumber,tag,Search,contentTypeCode),null,NewsResultBean.class);	}	}