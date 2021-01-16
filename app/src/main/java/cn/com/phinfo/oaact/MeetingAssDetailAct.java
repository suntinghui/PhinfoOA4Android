package cn.com.phinfo.oaact;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.heqifuhou.actbase.MyActBase;

import cn.com.phinfo.protocol.MeetingAssListRun;
import cn.hutool.core.util.StrUtil;

public class MeetingAssDetailAct extends MyActBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addTextNav("会议详情");
        this.showBackNav();

        this.addViewFillInRoot(R.layout.act_meeting_ass_detail);
        LinearLayout layout = this.findViewById(R.id.rootLayout);

        MeetingAssListRun.MeetingAssItem item =  JSON.parseObject(getIntent().getStringExtra("item"), MeetingAssListRun.MeetingAssItem.class);

        layout.addView(this.createView("主题：", StrUtil.emptyToDefault(item.getSubject(), "无")));
        layout.addView(this.createView("会议内容：", StrUtil.emptyToDefault(item.getMeetingContent(), "无")));
        layout.addView(this.createView("创建人：", StrUtil.emptyToDefault(item.getCreatedByName(), "无")));
        layout.addView(this.createView("开始时间：", StrUtil.emptyToDefault(item.getScheduledStart(), "无")));
        layout.addView(this.createView("结束时间：", StrUtil.emptyToDefault(item.getScheduledEnd(), "无")));
        layout.addView(this.createView("地点：", StrUtil.emptyToDefault(item.getLocation(), "无")));
        layout.addView(this.createView("修改时间：", StrUtil.emptyToDefault(item.getModifiedOn(), "无")));
        layout.addView(this.createView("会议室：", StrUtil.emptyToDefault(item.getRoomIdName(), "无")));
        layout.addView(this.createView("是否公开：", item.isPublic()?"是":"否"));
        layout.addView(this.createView("仅允许受邀人：", item.isAllowInviteeCheckin()?"是":"否"));
        layout.addView(this.createView("会议状态：", item.getStatusCode()==0?"草稿":(item.getStatusCode()==1?"发布":"完成")));
        layout.addView(this.createView("接受状态：", item.getReceiveStatusCode()==0?"未接受":(item.getReceiveStatusCode()==1?"接受":"拒绝")));
        layout.addView(this.createView("备注：", StrUtil.emptyToDefault(item.getDescription(), "无")));

    }

    private LinearLayout createView(String key, String Value) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(param);
        layout.setPadding(0, 15, 0, 15);

        TextView keyView = new TextView(this);
        keyView.setText(key);
        keyView.setTextColor(Color.parseColor("#666666"));
        keyView.setTextSize(16.0f);
        keyView.setGravity(Gravity.RIGHT);
        layout.addView(keyView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3.0f));

        TextView valueView = new TextView(this);
        valueView.setText(Value);
        valueView.setTextColor(Color.parseColor("#333333"));
        valueView.setTextSize(16.0f);
        valueView.setGravity(Gravity.LEFT);
        valueView.setPadding(20,0,0,0);
        layout.addView(valueView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2.0f));

        return layout;
    }
}
