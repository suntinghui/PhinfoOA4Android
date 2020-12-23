package cn.com.phinfo.protocol;import java.util.List;import java.util.Stack;import cn.com.phinfo.protocol.NewsAddContentRun.NewsAddContentResultBean;import com.alibaba.fastjson.JSON;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class CommentListRun extends QuickRunObjectBase {	public CommentListRun(final int PageNumber,String id){		this(PageNumber,id,"");	}	public CommentListRun(final int PageNumber,String id,String parentid) {		super(LURLInterface.GET_URL_NEWS_COMMENT_GETLIST(id,parentid,PageNumber),null,CommentListResultBean.class);	}		public static class CommentListResultBean extends HttpResultBeanBase{		List<CommentItem> listData = new Stack<CommentItem>();		public List<CommentItem> getListData() {			return listData;		}		public void setListData(List<CommentItem> listData) {			this.listData = listData;		}	}		public static class CommentItem{		private String commentid;		private String contentid;		private String createdby;		private String parentid;		private String comment;		private String createdon;		private String modifiedon;		private String modifiedby;		private String rownumber;		private String createdbyname;		public CommentItem(){					}		public static CommentItem init(NewsAddContentResultBean it){			String s = JSON.toJSONString(it);			return JSON.parseObject(s, CommentItem.class);		}		public String getCommentid() {			return commentid;		}		public void setCommentid(String commentid) {			this.commentid = commentid;		}		public String getContentid() {			return contentid;		}		public void setContentid(String contentid) {			this.contentid = contentid;		}		public String getCreatedby() {			return createdby;		}		public void setCreatedby(String createdby) {			this.createdby = createdby;		}		public String getParentid() {			return parentid;		}		public void setParentid(String parentid) {			this.parentid = parentid;		}		public String getComment() {			return comment;		}		public void setComment(String comment) {			this.comment = comment;		}		public String getCreatedon() {			return createdon;		}		public void setCreatedon(String createdon) {			this.createdon = createdon;		}		public String getModifiedon() {			return modifiedon;		}		public void setModifiedon(String modifiedon) {			this.modifiedon = modifiedon;		}		public String getModifiedby() {			return modifiedby;		}		public void setModifiedby(String modifiedby) {			this.modifiedby = modifiedby;		}		public String getRownumber() {			return rownumber;		}		public void setRownumber(String rownumber) {			this.rownumber = rownumber;		}		public String getCreatedbyname() {			return createdbyname;		}		public void setCreatedbyname(String createdbyname) {			this.createdbyname = createdbyname;		}	}	}