package cn.com.phinfo.protocol;import java.util.List;import java.util.Stack;import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class EmailAttacheRun extends QuickRunObjectBase {	public EmailAttacheRun(String Id) {		super(LURLInterface.GET_EMAIL_ATTACHEFILES_GET(Id),null,EmailAttacheResultBean.class);	}	public static class EmailAttacheResultBean extends HttpResultBeanBase{		private List<AttacheFileItem> listData = new Stack<AttacheFileItem>();		public List<AttacheFileItem> getListData() {			return listData;		}		public void setListData(List<AttacheFileItem> listData) {			this.listData = listData;		}	}}