package cn.com.phinfo.oaact;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import cn.com.phinfo.adapter.CommentAdapter;
import cn.com.phinfo.adapter.NewsAttacheAdapter;
import cn.com.phinfo.adapter.NewsOtherAdapter;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileItem;
import cn.com.phinfo.protocol.AttacheFileRun.AttacheFileResultBean;
import cn.com.phinfo.protocol.BaseInfoRun;
import cn.com.phinfo.protocol.BaseInfoRun.BaseInfoItem;
import cn.com.phinfo.protocol.BaseInfoRun.BaseInfoResultBean;
import cn.com.phinfo.protocol.CommentListRun;
import cn.com.phinfo.protocol.CommentListRun.CommentItem;
import cn.com.phinfo.protocol.CommentListRun.CommentListResultBean;
import cn.com.phinfo.protocol.NewsAddContentRun;
import cn.com.phinfo.protocol.NewsAddContentRun.NewsAddContentResultBean;
import cn.com.phinfo.protocol.NewsAttacheFileRun;
import cn.com.phinfo.protocol.NewsContentRun;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsItem;
import cn.com.phinfo.protocol.NewsDefaultListRun.NewsResultBean;
import cn.com.phinfo.protocol.NewsLikeActionRun;
import cn.com.phinfo.protocol.RelatedGetListRun;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.diykeyboard.KeyMapDailog;
import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.Mode;
import com.heqifuhou.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.heqifuhou.pulltorefresh.PullToRefreshScrollView;
import com.heqifuhou.utils.FileUtils;
import com.heqifuhou.view.NoScrollListView;

public class NewsDetailAct extends HttpMyActBase implements OnClickListener, OnItemClickListener {
	private static int PERPAGE_SIZE = 15;
	private static int ID_GETNEWS = 0x10, ID_LIKEACTION = 0x12, ID_BASEINFO = 0x13, ID_NEWS_ADD = 0x14,
			ID_NEWS_OTHER = 0x15, ID_GETCOMMENT = 0x16, ID_NEWS_ATTACHE = 0x17;
	private NewsItem it;
	private WebView webview = null;
	private TextView goodCount, title, deptName, createdOn;
	private BaseInfoItem currBaseInfoItem = null;
	private KeyMapDailog dialog;
	// 其他的新闻
	private NoScrollListView scrollListView;
	private NewsOtherAdapter newOtherAdapter;
	// 附件
	private NoScrollListView attacheListView;
	private NewsAttacheAdapter newattacheAdapter;

	private NoScrollListView refreshListView;
	private CommentAdapter commentAdapter = null;
	private int pageNumber = 1;

	private PullToRefreshScrollView scrollview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.it = JSON.parseObject(this.getIntent().getExtras().getString("NewsItem"), NewsItem.class);
		this.addTextNav("");
		this.addViewFillInRoot(R.layout.act_newsdetail);
		this.addBottomView(R.layout.news_detail_tools_bar);
		this.scrollview = (PullToRefreshScrollView) this.findViewById(R.id.scrollview);
		webview = (WebView) this.findViewById(R.id.webview);
		goodCount = (TextView) this.findViewById(R.id.goodCount);
		title = (TextView) this.findViewById(R.id.title);
		deptName = (TextView) this.findViewById(R.id.deptName);
		createdOn = (TextView) this.findViewById(R.id.createdOn);
		scrollListView = (NoScrollListView) this.findViewById(R.id.scrollListView);
		scrollListView.setOnItemClickListener(this);
		newOtherAdapter = new NewsOtherAdapter();
		scrollListView.setAdapter(newOtherAdapter);
		refreshListView = (NoScrollListView) this.findViewById(R.id.refreshListView);
		commentAdapter = new CommentAdapter();
		refreshListView.setAdapter(commentAdapter);
		this.scrollview.setMode(Mode.PULL_FROM_END);
		refreshListView.setOnItemClickListener(this);
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
				dialog = new KeyMapDailog("", new KeyMapDailog.SendBackListener() {
					@Override
					public void sendBack(final String inputText) {
						dialog.hideProgressdialog();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {

								quickHttpRequest(ID_NEWS_ADD, new NewsAddContentRun(it.getContentId(), "", inputText));
								dialog.dismiss();
							}
						}, 10);
					}
				});

				dialog.show(getSupportFragmentManager(), "dialog");
			}
		});

		attacheListView = (NoScrollListView) this.findViewById(R.id.attacheListView);
		newattacheAdapter = new NewsAttacheAdapter();
		attacheListView.setAdapter(newattacheAdapter);
		attacheListView.setOnItemClickListener(this);
		onRefresh();
	}

	protected void onRefresh() {
		this.quickHttpRequest(ID_GETNEWS, new NewsContentRun(it.getContentId()));
		this.quickHttpRequest(ID_BASEINFO, new BaseInfoRun(it.getContentId()));
		this.quickHttpRequest(ID_NEWS_OTHER, new RelatedGetListRun(it.getContentId()));
		this.quickHttpRequest(ID_NEWS_ATTACHE, new NewsAttacheFileRun(it.getContentId()));
		this.getComment();
		title.setText(it.getTitle());
		deptName.setText(it.getDeptName());
		createdOn.setText(it.getCreatedOn());
	}

	private void getComment() {
		this.quickHttpRequest(ID_GETCOMMENT, new CommentListRun(pageNumber, it.getContentId()));
	}

	@Override
	protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
		if (ID_GETNEWS == id) {
			webview.loadDataWithBaseURL(null, obj.get2Str(), "text/html", "utf-8", null);
			return;
		}
		if (ID_BASEINFO == id) {
			if (obj.isOK()) {
				BaseInfoResultBean o = (BaseInfoResultBean) obj;
				currBaseInfoItem = o.getBaseInfoItem();
				if (currBaseInfoItem != null) {
					goodCount.setText(String.valueOf(currBaseInfoItem.getLikecount()));
				}
			}
			return;
		}
		if (ID_LIKEACTION == id) {
			if (obj.isOK()) {
				boolean bLike = (boolean) requestObj;
				if (bLike) {
					if (currBaseInfoItem != null) {
						currBaseInfoItem.setLikecount(currBaseInfoItem.getLikecount() + 1);
						goodCount.setText(String.valueOf(currBaseInfoItem.getLikecount()));
					}
				} else {
					this.showToast("评价提交成功");
				}
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		if (ID_NEWS_ADD == id) {
			if (obj.isOK()) {
				NewsAddContentResultBean o = (NewsAddContentResultBean)obj;
				commentAdapter.addToListHead(CommentItem.init(o));
				this.showToast("评论提交成功");
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		if (ID_NEWS_OTHER == id) {
			if (obj.isOK()) {
				NewsResultBean o = (NewsResultBean) obj;
				newOtherAdapter.replaceListRef(o.getListData());
			} else {
				this.showToast(obj.getMsg());
			}
			return;
		}
		if (ID_GETCOMMENT == id) {
			if (obj.isOK()) {
				CommentListResultBean o = (CommentListResultBean) obj;
				if (pageNumber == 1) {
					commentAdapter.clear();
				}
				commentAdapter.addToListBack(o.getListData());
				pageNumber++;
				if (o.getListData().size() < PERPAGE_SIZE) {
					this.scrollview.setMode(Mode.DISABLED);
				} else {
					scrollview.setMode(Mode.PULL_FROM_END);
				}
				refreshListView.post(new Runnable() {
					@Override
					public void run() {
						commentAdapter.notifyDataSetChanged();
					}
				});
			} else {
				this.showToast(obj.getMsg());
			}
			if (commentAdapter.getCount() <= 0) {
				refreshListView.setVisibility(View.GONE);
			} else {
				refreshListView.setVisibility(View.VISIBLE);
			}
			return;
		}
		if(id == ID_NEWS_ATTACHE){
			if (obj.isOK()) {
				AttacheFileResultBean o = (AttacheFileResultBean) obj;
				newattacheAdapter.replaceListRef(o.getListData());
			} else {
				this.showToast(obj.getMsg());
			}
			
		}
	}

	public void onHttpForResult(int id, HttpResultBeanBase obj, Object requestObj) {
		super.onHttpForResult(id, obj, requestObj);
		this.scrollview.onRefreshComplete();
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

	private void likeAction(boolean bLike) {
		String s = bLike ? "Like" : "dislike";
		this.quickHttpRequest(ID_LIKEACTION, new NewsLikeActionRun(it.getContentId(), s), bLike);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getAdapter() == newOtherAdapter) {
			NewsItem it = newOtherAdapter.getItem(arg2);
			Intent intent = new Intent(this, NewsDetailAct.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("NewsItem", JSON.toJSONString(it));
			startActivity(intent);
			return;
		}

		if (arg0.getAdapter() == commentAdapter) {
			CommentItem commIT = commentAdapter.getItem(arg2);
			Intent intent = new Intent(this, CommentAct.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("NewsItem", JSON.toJSONString(it));
			intent.putExtra("CommentItem", JSON.toJSONString(commIT));
			startActivity(intent);
		}
		// 附件
		if (arg0.getAdapter() == newattacheAdapter) {

/**
			AttacheFileItem attacheItem = newattacheAdapter.getItem(arg2);
			Intent intent = new Intent(this, FileShowAct.class);
			intent.putExtra("AttacheFileItem", JSON.toJSONString(attacheItem));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
 **/

/**
			AttacheFileItem attacheItem = newattacheAdapter.getItem(arg2);
			Log.e("yao", JSON.toJSONString(attacheItem));
//			Log.e("yao", attacheItem.getLink());
			FileDisplayActivity.actionStart(NewsDetailAct.this, attacheItem.getLink(),attacheItem.getName()+"."+attacheItem.getFileExtension());
//			FileDisplayActivity.actionStart(NewsDetailAct.this,"http://res.cfastech.com/textdoc.docx","yao");
**/
			AttacheFileItem attacheItem = newattacheAdapter.getItem(arg2);
			Log.e("yao", JSON.toJSONString(attacheItem));
			FileUtils.downloadAndOpenFile(NewsDetailAct.this, attacheItem);

		}

	}

}
