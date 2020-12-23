package cn.com.phinfo.adapter;

import java.util.List;

import cn.com.phinfo.protocol.RoomAppointRun.RoomAppointItem;

import com.heqifuhou.ioscalendar.CalendarAdapter;

public class RoomAppointAdapter extends CalendarAdapter {
	public RoomAppointAdapter(int jumpMonth, int jumpYear, int year_c,
			int month_c, int day_c) {
		super(jumpMonth, jumpYear, year_c, month_c, day_c);
	}
	public void setRoomStatus(List<RoomAppointItem> lst){
		for(int i=0;i<this.getCount();i++){
			DayNumber it = this.getItem(i);
			it.setSel(isFoundRoom(lst, it.getTime()));
		}
		this.notifyDataSetChanged();
	}
	
	public boolean isFoundRoom(List<RoomAppointItem> lst,long l)
	{
		for(int i=0;i<lst.size();i++){
			RoomAppointItem it = lst.get(i);
			if(it.isBetween(l)){
				return true;
			}
		}
		return false;
	}

}
