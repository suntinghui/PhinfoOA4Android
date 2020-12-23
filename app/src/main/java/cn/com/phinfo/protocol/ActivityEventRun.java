package cn.com.phinfo.protocol;import java.text.ParseException;import java.text.SimpleDateFormat;import java.util.Calendar;import java.util.Date;import java.util.List;import java.util.Stack;import com.alibaba.fastjson.annotation.JSONField;import com.heqifuhou.protocolbase.HttpResultBeanBase;import com.heqifuhou.protocolbase.QuickRunObjectBase;public class ActivityEventRun extends QuickRunObjectBase {	public ActivityEventRun(String startTime,String endTime) {		super(LURLInterface.GET_URL_Activity_Event(startTime,endTime),null,ActivityEventResultBean.class);	}		public static class ActivityEventResultBean extends HttpResultBeanBase{		private List<ActivityEventItem> listData = new Stack<ActivityEventItem>();		public List<ActivityEventItem> getListData() {			return listData;		}		public void setListData(List<ActivityEventItem> listData) {			this.listData = listData;		}	}		public static class ActivityEventItem{		private String id;		private String subject;		private String createdOn;		private String createdBy;		private String description;		private String location;		private String scheduledStart;		private String scheduledEnd;		private String calendarType;		private String displayStatus;		public String getId() {			return id;		}		public void setId(String id) {			this.id = id;		}		public String getSubject() {			return subject;		}		public void setSubject(String subject) {			this.subject = subject;		}		public String getCreatedOn() {			return createdOn;		}		public void setCreatedOn(String createdOn) {			this.createdOn = createdOn;		}		public String getCreatedBy() {			return createdBy;		}		public void setCreatedBy(String createdBy) {			this.createdBy = createdBy;		}		public String getDescription() {			return description;		}		public void setDescription(String description) {			this.description = description;		}		public String getLocation() {			return location;		}		public void setLocation(String location) {			this.location = location;		}		public String getScheduledStart() {			return scheduledStart;		}		public void setScheduledStart(String scheduledStart) {			this.scheduledStart = scheduledStart;		}		public String getScheduledEnd() {			return scheduledEnd;		}		public void setScheduledEnd(String scheduledEnd) {			this.scheduledEnd = scheduledEnd;		}		public String getCalendarType() {			return calendarType;		}		public void setCalendarType(String calendarType) {			this.calendarType = calendarType;		}		public String getDisplayStatus() {			return displayStatus;		}		public void setDisplayStatus(String displayStatus) {			this.displayStatus = displayStatus;		}				@JSONField(serialize=false)		private long startTime=0,endTime=0;		public boolean isBetween(long l){			if(startTime==0){				startTime =getTime(scheduledStart);			}			if(endTime == 0){				endTime = getTime(scheduledEnd);			}			if(l>=startTime&&l<=endTime){				return true;			}			return false;		}		@JSONField(serialize=false)		private static long getTime(String date){			try {				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");				Date d = sdf.parse(date);				Calendar ca = Calendar.getInstance();				ca.setTime(d);				ca.set(Calendar.HOUR_OF_DAY, 0);				ca.set(Calendar.MINUTE,0);				ca.set(Calendar.SECOND, 0);				return ca.getTime().getTime();			} catch (ParseException e) {				return 0;			}		}	}}