package cn.com.phinfo.protocol;import java.util.IdentityHashMap;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class UploadHeadImgRun extends QuickRunObjectBase {	public UploadHeadImgRun(final IdentityHashMap<String, Object> has) {		super(LURLInterface.GET_URL_uploadavatar(),null,has,HttpResultBeanBase.class);	}}