package cn.com.phinfo.protocol;import java.util.List;import java.util.Stack;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class RolesRun extends QuickRunObjectBase {	public RolesRun(){		this("");	}	public RolesRun(final String search) {		super(LURLInterface.GET_URL_ROLES(search),null,RolesResultBean.class);	}	public static class RolesResultBean extends HttpResultBeanBase{		private List<RolesItem> listData = new Stack<RolesItem>();		public List<RolesItem> getListData() {			return listData;		}		public void setListData(List<RolesItem> listData) {			this.listData = listData;		}	}		public static class RolesItem{		private String roleId;		private String name;		public String getRoleId() {			return roleId;		}		public void setRoleId(String roleId) {			this.roleId = roleId;		}		public String getName() {			return name;		}		public void setName(String name) {			this.name = name;		}	}}