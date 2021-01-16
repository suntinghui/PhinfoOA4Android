package cn.com.phinfo.protocol;

import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.QuickRunObjectBase;

import java.util.ArrayList;
import java.util.List;

public class MeetingAssListRun extends QuickRunObjectBase {

    public MeetingAssListRun(int page) {
        super(LURLInterface.GET_URL_MY_MEETING_LIST(page), null, MeetingAssItemBean.class);
    }

    public static class MeetingAssItemBean extends HttpResultBeanBase {

        private List<MeetingAssItem> listData = new ArrayList<>();

        public List<MeetingAssItem> getListData() {
            return listData;
        }

        public void setListData(List<MeetingAssItem> listData) {
            this.listData = listData;
        }
    }

    public static class MeetingAssItem {
        private String MeetingId;
        private String Subject;
        private String Name;
        private String MeetingContent;
        private String OwningUser;
        private String CreatedByName;
        private String OwningUserName;
        private String ScheduledStart;
        private String ScheduledEnd;
        private String Location;
        private String ModifiedOn;
        private String RoomId;
        private String RoomIdName;
        private boolean IsPublic;
        private boolean AllowInviteeCheckin;
        private int StatusCode;
        private int ReceiveStatusCode;
        private String Description;

        public String getMeetingId() {
            return MeetingId;
        }

        public void setMeetingId(String meetingId) {
            MeetingId = meetingId;
        }

        public String getSubject() {
            return Subject;
        }

        public void setSubject(String subject) {
            Subject = subject;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getMeetingContent() {
            return MeetingContent;
        }

        public void setMeetingContent(String meetingContent) {
            MeetingContent = meetingContent;
        }

        public String getOwningUser() {
            return OwningUser;
        }

        public void setOwningUser(String owningUser) {
            OwningUser = owningUser;
        }

        public String getCreatedByName() {
            return CreatedByName;
        }

        public void setCreatedByName(String createdByName) {
            CreatedByName = createdByName;
        }

        public String getOwningUserName() {
            return OwningUserName;
        }

        public void setOwningUserName(String owningUserName) {
            OwningUserName = owningUserName;
        }

        public String getScheduledStart() {
            return ScheduledStart;
        }

        public void setScheduledStart(String scheduledStart) {
            ScheduledStart = scheduledStart;
        }

        public String getScheduledEnd() {
            return ScheduledEnd;
        }

        public void setScheduledEnd(String scheduledEnd) {
            ScheduledEnd = scheduledEnd;
        }

        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
        }

        public String getModifiedOn() {
            return ModifiedOn;
        }

        public void setModifiedOn(String modifiedOn) {
            ModifiedOn = modifiedOn;
        }

        public String getRoomId() {
            return RoomId;
        }

        public void setRoomId(String roomId) {
            RoomId = roomId;
        }

        public String getRoomIdName() {
            return RoomIdName;
        }

        public void setRoomIdName(String roomIdName) {
            RoomIdName = roomIdName;
        }

        public boolean isPublic() {
            return IsPublic;
        }

        public void setPublic(boolean aPublic) {
            IsPublic = aPublic;
        }

        public boolean isAllowInviteeCheckin() {
            return AllowInviteeCheckin;
        }

        public void setAllowInviteeCheckin(boolean allowInviteeCheckin) {
            AllowInviteeCheckin = allowInviteeCheckin;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public void setStatusCode(int statusCode) {
            StatusCode = statusCode;
        }

        public int getReceiveStatusCode() {
            return ReceiveStatusCode;
        }

        public void setReceiveStatusCode(int receiveStatusCode) {
            ReceiveStatusCode = receiveStatusCode;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }
    }


}
