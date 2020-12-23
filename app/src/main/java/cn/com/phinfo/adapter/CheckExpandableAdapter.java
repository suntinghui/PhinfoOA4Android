package cn.com.phinfo.adapter;

import java.util.List;
import java.util.Stack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.AttendrptRun.AttendItem;

public class CheckExpandableAdapter extends BaseExpandableListAdapter {
    private List<AttendItem> listData = new Stack<AttendItem>();
    public  void replace(List<AttendItem> listData){
    	if(listData==null){
    		this.listData.clear();
    	}else{
    		this.listData = listData;
    	}
    	this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return listData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(groupPosition).getDays().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(groupPosition).getDays().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        GroupHolder holder = null;
        if(view == null){
            holder = new GroupHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_expandlist_group, null);
            holder.groupName = (TextView)view.findViewById(R.id.tv_group_name);
            holder.tv_group_count = (TextView)view.findViewById(R.id.tv_group_count);
            holder.arrow = (ImageView) view.findViewById(R.id.arrow);
            view.setTag(holder);
        }else{
            holder = (GroupHolder)view.getTag();
        }

        //判断是否已经打开列表
//        if(isExpanded){
//            holder.arrow.setBackgroundResource(R.drawable.arrow);
//        }else{
//            holder.arrow.setBackgroundResource(R.drawable.share_btn_down);
//        }
        holder.tv_group_count.setText(String.valueOf(this.getChildrenCount(groupPosition))+"次");
        holder.groupName.setText(listData.get(groupPosition).getFieldName());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        ChildHolder holder = null;
        if(view == null){
            holder = new ChildHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_expandlist_item, null);
            holder.childName = (TextView)view.findViewById(R.id.tv_child_name);
            view.setTag(holder);
        }else{
            holder = (ChildHolder)view.getTag();
        }
        holder.childName.setText(listData.get(groupPosition).getDays().get(childPosition));
        return view;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder{
        public TextView groupName,tv_group_count;
        public ImageView arrow;
    }

    class ChildHolder{
        public TextView childName;
    }
}
