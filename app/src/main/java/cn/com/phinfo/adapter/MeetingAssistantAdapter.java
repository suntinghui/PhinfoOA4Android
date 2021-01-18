package cn.com.phinfo.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.MeetingAssListRun;
import cn.hutool.core.util.StrUtil;

public class MeetingAssistantAdapter extends BaseQuickAdapter<MeetingAssListRun.MeetingAssItem, BaseViewHolder> implements LoadMoreModule {


    /**
     * 构造方法，此示例中，在实例化Adapter时就传入了一个List。
     * 如果后期设置数据，不需要传入初始List，直接调用 super(layoutResId); 即可
     */
    public MeetingAssistantAdapter() {
        super(R.layout.item_meeting_assistant);
        regClickListener();
    }

    public MeetingAssistantAdapter(List<MeetingAssListRun.MeetingAssItem> list) {
        super(R.layout.item_meeting_assistant, list);
        regClickListener();
    }

    private void regClickListener() {
        addChildClickViewIds(R.id.operAcceptTextView, R.id.operRejectTextView, R.id.operDetailTextView,
                R.id.cancelRejectTextView, R.id.confirmRejectTextView);
    }

    /**
     * 在此方法中设置item数据
     */
    @Override
    protected void convert(@NotNull BaseViewHolder holder, MeetingAssListRun.MeetingAssItem item) {
        holder.setText(R.id.titleTextView, StrUtil.emptyToDefault(item.getName(), "无"));
        holder.setText(R.id.createNameTextView, StrUtil.emptyToDefault(item.getCreatedByName(), "无"));
        holder.setText(R.id.dateStartTextView, StrUtil.emptyToDefault(item.getScheduledStart(), "无"));
        holder.setText(R.id.dateEndTextView, StrUtil.emptyToDefault(item.getScheduledEnd(), "无"));
        holder.setText(R.id.addressTextView, StrUtil.emptyToDefault(item.getRoomIdName(), "无"));


        switch(item.getReceiveStatusCode()) {
            case 0:// 未处理
                holder.setGone(R.id.acceptedTextView, true);
                holder.setGone(R.id.rejectedTextView, true);
                holder.setVisible(R.id.descLayout, true);
                holder.setVisible(R.id.operLayout, true);
                holder.setGone(R.id.operRejectLayout, true);
                holder.setGone(R.id.operDetailTextView, true);
                break;

            case 1:// 已接受
                holder.setVisible(R.id.acceptedTextView, true);
                holder.setGone(R.id.rejectedTextView, true);
                holder.setGone(R.id.descLayout, true);
                holder.setGone(R.id.operLayout, true);
                holder.setGone(R.id.operRejectLayout, true);
                holder.setVisible(R.id.operDetailTextView, true);
                break;

            case 2:// 已拒绝
                holder.setGone(R.id.acceptedTextView, true);
                holder.setVisible(R.id.rejectedTextView, true);
                holder.setGone(R.id.descLayout, true);
                holder.setGone(R.id.operLayout, true);
                holder.setGone(R.id.operRejectLayout, true);
                holder.setVisible(R.id.operDetailTextView, true);
                break;

            default:
                break;
        }

    }

}
