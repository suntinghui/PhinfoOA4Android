package cn.com.phinfo.protocol;import java.util.HashMap;import java.util.List;import java.util.Stack;import cn.com.phinfo.protocol.ActivityEventGetlistRun.ActivityEventGetlistItem;import com.alibaba.fastjson.annotation.JSONField;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class CreateCalendarRun extends QuickRunObjectBase {	public CreateCalendarRun(final String subject, final String ScheduledStart,			final String ScheduledEnd, final String isAllDayEvent,			final String recurrenceType, final String Location,			final String description,final String calendarType,			final String displayStatus,final String reminderTime,			final String invtee) {		super(LURLInterface.GET_URL_CREATECALENDAR(),				new HashMap<String, Object>() {					private static final long serialVersionUID = 1L;					{						put("subject", subject);						put("ScheduledStart", ScheduledStart);						put("ScheduledEnd", ScheduledEnd);						put("isAllDayEvent", isAllDayEvent);						put("recurrenceType", recurrenceType);						put("Location", Location);						put("description", description);						put("calendarType", calendarType);						put("displayStatus", displayStatus);						put("reminderTime", reminderTime);						put("invtee", invtee);					}				}, CreateCalendarResultBean.class);	}	public static class CreateCalendarResultBean extends HttpResultBeanBase {		private List<ActivityEventGetlistItem> data = new Stack<ActivityEventGetlistItem>();		public List<ActivityEventGetlistItem> getData() {			return data;		}		public void setData(List<ActivityEventGetlistItem> data) {			this.data = data;		}		@JSONField(serialize=false)		public ActivityEventGetlistItem getActivityEventGetlistItem(){			if(this.data.isEmpty()){				return null;			}			return this.data.get(0);		}	}}