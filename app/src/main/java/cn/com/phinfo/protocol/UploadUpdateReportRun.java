package cn.com.phinfo.protocol;import java.util.IdentityHashMap;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;//更新报告内容public class UploadUpdateReportRun extends QuickRunObjectBase {	public UploadUpdateReportRun(final String worklogid,final IdentityHashMap<String, Object> has) {		super(LURLInterface.GET_URL_REPORT_UPDATE(worklogid),null,has,HttpResultBeanBase.class);	}}