package cn.com.phinfo.protocol;

import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.QuickRunObjectBase;

import java.util.ArrayList;
import java.util.List;

public class MeetingAssOperRun extends QuickRunObjectBase {

    public MeetingAssOperRun(String id, String desc, String status) {
        super(LURLInterface.GET_URL_OPER_MEETING(id, desc, status), null, HttpResultBeanBase.class);
    }



}
