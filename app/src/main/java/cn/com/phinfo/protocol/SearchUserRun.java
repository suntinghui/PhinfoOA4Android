package cn.com.phinfo.protocol;import java.util.List;import java.util.Stack;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class SearchUserRun extends QuickRunObjectBase {	public SearchUserRun(final String search,final int pageNumber, final String businessUnitId) {		super(LURLInterface.GET_URL_USER_SEARCH(search,pageNumber,businessUnitId),null,SearchUserResultBean.class);	}		public static class SearchUserResultBean extends HttpResultBeanBase{		private List<SearchUserItem> listData = new Stack<SearchUserItem>();		public List<SearchUserItem> getListData() {			return listData;		}		public void setListData(List<SearchUserItem> listData) {			this.listData = listData;		}	}		public static class SearchUserItem{			}}