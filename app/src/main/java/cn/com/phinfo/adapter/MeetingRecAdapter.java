package cn.com.phinfo.adapter;

import java.util.List;

import cn.com.phinfo.protocol.MettingRecRun.MeetingRecItem;

import com.heqifuhou.ioscalendar.CalendarAdapter;

public class MeetingRecAdapter extends CalendarAdapter {
	public MeetingRecAdapter(int jumpMonth, int jumpYear, int year_c,
			int month_c, int day_c) {
		super(jumpMonth, jumpYear, year_c, month_c, day_c);
	}
	public void setRoomStatus(List<MeetingRecItem> lst){
		for(int i=0;i<this.getCount();i++){
			DayNumber it = this.getItem(i);
			it.setSel(isFoundRoom(lst, it.getTime()));
		}
		this.notifyDataSetChanged();
	}
	
	public boolean isFoundRoom(List<MeetingRecItem> lst,long l)
	{
		for(int i=0;i<lst.size();i++){
			MeetingRecItem it = lst.get(i);
			if(it.isBetween(l)){
				return true;
			}
		}
		return false;
	}

}
