package cn.com.phinfo.protocol;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class DelMeetingRun extends QuickRunObjectBase {	public DelMeetingRun(final String Id) {		super(LURLInterface.GET_URL_DEL(Id),null,DelMeetingResultBean.class);	}	public static class DelMeetingResultBean extends HttpResultBeanBase{			}}