package cn.com.phinfo.oaact;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.diykeyboard.KeyMapDailog;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshScrollView;
import com.heqifuhou.utils.ParamsCheckUtils;
import com.heqifuhou.view.CircleImageView;
import com.heqifuhou.view.NoScrollListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.CommentAdapter;
import cn.com.phinfo.protocol.BaseInfoRun;
import cn.com.phinfo.protocol.BaseInfoRun.BaseInfoItem;
import cn.com.phinfo.protocol.BaseInfoRun.BaseInfoResultBean;
import cn.com.phinfo.protocol.CommentListRun;
import cn.com.phinfo.protocol.CommentListRun.CommentItem;
import cn.com.phinfo.protocol.CommentListRun.CommentListResultBean;
import cn.com.phinfo.protocol.NewsAddContentRun;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsItem;
import cn.com.phinfo.protocol.NewsLikeActionRun;

public class CommentAct extends HttpMyActBase implements OnClickListener,OnItemClickListener {
	private static int PERPAGE_SIZE=15;
	private static int ID_LIKEACTION=0x12,ID_BASEINFO=0x13,ID_NEWS_ADD=0x14,ID_GETCOMMENT=0x16;
	private NewsItem it;
	private CommentItem commentIT;
	private KeyMapDailog dialog;
	private NoScrollListView refreshListView;
	private CommentAdapter commentAdapter = null;
	private int pageNumber =1;
	private PullToRefreshScrollView scrollview;
	private TextView goodCount;
	private BaseInfoItem currBaseInfoItem=null;
	
	private CircleImageView icon_avatar;
	private TextView date,count,comment,like,name;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.it = JSON.parseObject(this.getIntent().getExtras().getString("NewsItem"), NewsItem.class);
		this.commentIT = JSON.parseObject(this.getIntent().getExtras().getString("CommentItem"), CommentItem.class);
		this.addTextNav("");
		this.addViewFillInRoot(R.layout.act_comment_detail);
		this.addBottomView(R.layout.news_detail_tools_bar);
		goodCount = (TextView) this.findViewById(R.id.goodCount);
		this.scrollview = (PullToRefreshScrollView) this.findViewById(R.id.scrollview);
		refreshListView = (NoScrollListView) this.findViewById(R.id.refreshListView);
		icon_avatar = (CircleImageView) this.findViewById(R.id.icon_avatar);
		
		date = (TextView) this.findViewById(R.id.date);
		count = (TextView) this.findViewById(R.id.count);
		comment = (TextView) this.findViewById(R.id.comment);
		name = (TextView) this.findViewById(R.id.name);
		like = (TextView) this.findViewById(R.id.like);
		date.setText(commentIT.getCreatedon());
		count.setText("0"+"条评论");
		comment.setText(commentIT.getComment());
		like.setText("0");
		name.setText(commentIT.getCreatedbyname());
		
		commentAdapter = new CommentAdapter();
		refreshListView.setOnItemClickListener(this);
		refreshListView.setAdapter(commentAdapter);
		this.scrollview.setMode(Mode.PULL_FROM_END);
		this.scrollview.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshBase refreshView) {
				getComment();
				hideLoading(true);
			}
		});
		goodCount.setOnClickListener(this);
		this.findViewById(R.id.newsDislike).setOnClickListener(this);
		this.findViewById(R.id.edit_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			      dialog = new KeyMapDailog("回复\""+commentIT.getCreatedbyname()+"\":", new KeyMapDailog.SendBackListener() {
	                    @Override
	                    public void sendBack(final String inputText) {
	                        dialog.hideProgressdialog();
	                        new Handler().postDelayed(new Runnable() {
	                            @Override
	                            public void run() {
	                                quickHttpRequest(ID_NEWS_ADD, new NewsAddContentRun(it.getContentId(),commentIT.getParentid(),inputText));
	                                dialog.dismiss();
	                            }
	                        }, 10);
	                    }
	                });

	                dialog.show(getSupportFragmentManager(), "dialog");
			}
		});
		onRefresh();
	}
	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		this.scrollview.onRefreshComplete();
	}
	protected void onRefresh() {
		this.getComment();
	}
	
	private void getComment(){
		this.quickHttpRequest(ID_BASEINFO, new BaseInfoRun(it.getContentId()));
		this.quickHttpRequest(ID_GETCOMMENT, new CommentListRun(pageNumber,it.getContentId(),commentIT.getParentid()));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if(ID_BASEINFO == id){
			if(obj.isOK()){
				BaseInfoResultBean o = (BaseInfoResultBean)obj;
				currBaseInfoItem = o.getBaseInfoItem();
				if(currBaseInfoItem!=null){
					goodCount.setText(String.valueOf(currBaseInfoItem.getLikecount()));
				}
			}
			return;
		}
		if(ID_LIKEACTION == id){
			if(obj.isOK()){
				boolean bLike = (boolean) requestObj;
				if(bLike){
					if(currBaseInfoItem!=null){
						currBaseInfoItem.setLikecount(currBaseInfoItem.getLikecount()+1);
						goodCount.setText(String.valueOf(currBaseInfoItem.getLikecount()));
					}
				}else{
					this.showToast("评价提交成功");
				}
			}else{
				this.showToast(obj.getMsg());
			}
			return;
		}
		if(ID_NEWS_ADD == id){
			if(obj.isOK()){
				this.showToast("评论提交成功");
			}else{
				this.showToast(obj.getMsg());
			}
			return;
		}
		if(ID_GETCOMMENT == id){
			if(obj.isOK()){
				CommentListResultBean o = (CommentListResultBean)obj;
				if(pageNumber == 1){
					commentAdapter.clear();
				}
				commentAdapter.addToListBack(o.getListData());
				pageNumber++;
				if(o.getListData().size()<PERPAGE_SIZE){
					this.scrollview.setMode(Mode.DISABLED);
				}else{
					scrollview.setMode(Mode.PULL_FROM_END);
				}
				refreshListView.post(new Runnable() {
                    @Override
                    public void run() {
                    	commentAdapter.notifyDataSetChanged();
                    }
                });
			}else{
				this.showToast(obj.getMsg());
			}
			if (commentAdapter.getCount() <= 0) {
				refreshListView.setVisibility(View.GONE);
			}else{
				refreshListView.setVisibility(View.VISIBLE);
			}
			return;
		}
	}
	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.newsDislike:
			likeAction(false);
			break;
		case R.id.goodCount:
			likeAction(true);
			break;
		}
	}
	
	private void likeAction(boolean bLike){
		String s = bLike?"Like":"dislike";
		this.quickHttpRequest(ID_LIKEACTION, new NewsLikeActionRun(commentIT.getParentid(),s ),bLike);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		CommentItem commIT = commentAdapter.getItem(arg2);
		Intent intent = new Intent(this,CommentAct.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("NewsItem", JSON.toJSONString(it));
		intent.putExtra("CommentItem", JSON.toJSONString(commIT));
		startActivity(intent);	
	}

}
