package cn.com.phinfo.protocol;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class NotifyReadRun extends QuickRunObjectBase {	public NotifyReadRun(String instanceName, String processInstanceId, String members, String Message) {		super(LURLInterface.GET_URL_notfiyread(instanceName, processInstanceId, members, Message));	}}