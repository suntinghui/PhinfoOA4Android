package cn.com.phinfo.protocol;import cn.com.phinfo.protocol.FileSearchRun.UFileItem;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class UDirCreateRun extends QuickRunObjectBase {	public UDirCreateRun(final String ParentId,final String Name) {		super(LURLInterface.GET_DIR_CREATE(ParentId,Name),null,UDirCreateResultBean.class);	}	public static class UDirCreateResultBean extends HttpResultBeanBase{		private UFileItem data = new UFileItem();		public UFileItem getData() {			return data;		}		public void setData(UFileItem data) {			this.data = data;		}	}}