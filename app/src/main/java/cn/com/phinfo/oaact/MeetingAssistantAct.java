package cn.com.phinfo.oaact;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.heqifuhou.actbase.HttpMyActBase;
import com.heqifuhou.protocolbase.HttpResultBeanBase;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.com.phinfo.adapter.MeetingAssistantAdapter;
import cn.com.phinfo.protocol.MeetingAssListRun;
import cn.com.phinfo.protocol.MeetingAssOperRun;
import cn.com.phinfo.protocol.MettingRecRun;

public class MeetingAssistantAct extends HttpMyActBase implements @Nullable OnItemChildClickListener {

    private final static int ID_GET_LIST = 0x100;
    private final static int ID_OPER_ACCEPT = 0x101;
    private final static int ID_OPER_REJECT = 0x102;

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView = null;
    private MeetingAssistantAdapter adapter = null;

    private int currentPage = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addTextNav("会议助手");
        this.showBackNav();

        this.addViewFillInRoot(R.layout.act_meeting_assistant);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.swipeRefreshLayout = findViewById(R.id.swipeLayout);
        this.swipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 这里的作用是防止下拉刷新的时候还可以上拉加载
                adapter.getLoadMoreModule().setEnableLoadMore(false);
                // 下拉刷新，需要重置页数
                currentPage = 1;
                //请求数据
                MeetingAssistantAct.this.onRefresh();
            }
        });

        adapter = new MeetingAssistantAdapter();
        adapter.setAnimationEnable(true);
        recyclerView.setAdapter(adapter);

        adapter.setEmptyView(R.layout.layout_empty_view);

        // 设置点击事件
        adapter.setOnItemChildClickListener(this);

        // 设置加载更多监听事件
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //上拉加载时取消下拉刷新
                swipeRefreshLayout.setRefreshing(false);
                adapter.getLoadMoreModule().setEnableLoadMore(true);

                onRefresh();
            }
        });
        this.adapter.getLoadMoreModule().setAutoLoadMore(true);
        //当自动加载开启，同时数据不满一屏时，是否继续执行自动加载更多(默认为true)
        this.adapter.getLoadMoreModule().setEnableLoadMoreIfNotFullPage(false);

        this.onRefresh();
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();

        this.quickHttpRequest(ID_GET_LIST, new MeetingAssListRun(this.currentPage));
    }

    @Override
    protected void onHttpResult(int id, HttpResultBeanBase obj, Object requestObj) {
        if (id == ID_GET_LIST) {
            this.swipeRefreshLayout.setRefreshing(false);
            this.adapter.getLoadMoreModule().setEnableLoadMore(true);

            if (obj.isOK()) {
                MeetingAssListRun.MeetingAssItemBean bean = (MeetingAssListRun.MeetingAssItemBean) obj;
                List list = bean.getListData();
                if (this.currentPage == 1) {
                    adapter.setNewInstance(list);
                } else {
                    adapter.addData(list);
                }

                // 注意，默认是25
                if (list.size() < 25) {
                    //如果不够一页,显示没有更多数据布局
                    adapter.getLoadMoreModule().loadMoreEnd();
                } else {
                    adapter.getLoadMoreModule().loadMoreComplete();
                }

                ++currentPage;

            } else {
                showToast("请求数据时出错");
            }
        } else if (id == ID_OPER_ACCEPT) {
            if (obj.isOK()) {
                showToast("操作成功");
                this.currentPage = 1;
                this.onRefresh();
            } else {
                showToast("操作失败");
            }
        } else if (id == ID_OPER_REJECT) {
            if (obj.isOK()) {
                showToast("操作成功");
                this.currentPage = 1;
                this.onRefresh();
            } else {
                showToast("操作失败");
            }
        }
    }


    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        MeetingAssListRun.MeetingAssItem item = (MeetingAssListRun.MeetingAssItem) adapter.getData().get(position);

        switch (view.getId()) {
            case R.id.operAcceptTextView:
                String desc = ((EditText)adapter.getViewByPosition(position, R.id.descEditText)).getText().toString();
                this.quickHttpRequest(ID_OPER_ACCEPT, new MeetingAssOperRun(item.getMeetingId(), desc, "1"));
                break;

            case R.id.confirmRejectTextView:
                EditText descET = (EditText) adapter.getViewByPosition(position, R.id.descEditText);
                if (descET.getText().toString().equalsIgnoreCase(""))  {
                    showToast("请填写拒绝会议的原因");
                } else {
                    this.quickHttpRequest(ID_OPER_REJECT, new MeetingAssOperRun(item.getMeetingId(), descET.getText().toString(), "2"));
                }

                break;

            case R.id.operDetailTextView:
                Intent intent = new Intent(this, MeetingAssDetailAct.class);
                intent.putExtra("item", JSON.toJSONString(adapter.getData().get(position)));
                this.startActivity(intent);
                break;

            case R.id.operRejectTextView:
                adapter.getViewByPosition(position, R.id.operLayout).setVisibility(View.GONE);
                adapter.getViewByPosition(position, R.id.operRejectLayout).setVisibility(View.VISIBLE);
                break;

            case R.id.cancelRejectTextView:
                adapter.getViewByPosition(position, R.id.operLayout).setVisibility(View.VISIBLE);
                adapter.getViewByPosition(position, R.id.operRejectLayout).setVisibility(View.GONE);
                break;
        }

    }
}
