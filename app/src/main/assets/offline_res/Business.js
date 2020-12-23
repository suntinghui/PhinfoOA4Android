function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = location.search.substr(1).match(reg);
    if (r != null) return unescape(decodeURI(r[2])); return null;
}
function getCookie(c_name) {
    //debugger;
    var i, x, y, ARRcookies = document.cookie.split(";");
    for (i = 0; i < ARRcookies.length; i++) {
        x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
        y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
        x = x.replace(/^\s+|\s+$/g, "");
        if (x == c_name) {
            //return unescape(y);
            return decodeURIComponent(y);
        }
    }
}
function isNumeric(input) {
    var RE = /^-{0,1}\d*\.{0,1}\d+$/;
    return (RE.test(input));
}
function isDate(input) {
    var str = input;
    if (str.length != 0) {
        var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/;
        var r = str.match(reg);
        if (r == null)
            return false;//请将“日期”改成你需要验证的属性名称!
        else
        {
            var d = new Date(r[1], r[3] - 1, r[4]);
            return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
        }
    }
    return true;
}
function toChineseCapitalizedNumber(num,callbak) {

    var url = "/_serverUtil/action/ToCapitalizedNumber?num="+num;
    jQuery.ajax({
        async: false, cache: false, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
            // alert("0");
        },
        success: function (data, textStatus) {
            callbak(data);
        },
        type: "POST",
        url: url
    });
}
function onLeaveOnChange()
{
    var leaveType = jQuery("#LeaveType").val();
    if (leaveType == "") {
        alert("请选择请假类型");
        return false;
    }
    var startDay= jQuery("#StartTime").val();
    var startTime = jQuery("#StartTime_time").val();
    var startTime1 = startDay + " " + startTime;

    if (startTime == "") {
        alert("请选择请假时间请输入");
        return false;
    }
    var leavedays = jQuery("#LeaveDuration").val();
    if (leavedays == "") {
        return false;
    }
    calculateLeaveDays(leaveType, startTime1, leavedays, setLeaveEndTime);
}
jQuery(document).ready(function () {
    jQuery("#LeaveType").change(function () {
        onLeaveOnChange();
    });
    jQuery("#StartTime").change(function () {
        onLeaveOnChange();
    });
    jQuery("#LeaveDuration").change(function () {
        onLeaveOnChange();
    });
});
function setLeaveEndTime(data) {
    if (data) {
        jQuery("#EndTime").val(data.endDay);
        jQuery("#EndTime_time").val(data.endTime);
    }
}
/*计算请假天数，或者请假截止日期*/
function calculateLeaveDays(leaveType, startTime, leavedays,callbak) {

    var url = "/apps/HrProcessor.ashx?cmd=calculateleave&leavetype=" + leaveType + "&startTime=" + startTime + "&leavedays=" + leavedays;
    jQuery.ajax({
        async: false, cache: false, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
            // alert("0");
        },
        success: function (data, textStatus) {
            callbak(data);
        },
        type: "POST",
        url: url
    });
}
function orderDinner() {
    var loginName = getCookie("SM_USER_NAME");
    window.open("http://192.168.0.43/refectory/oalogin.do?gzh=" + loginName, "网上订餐")
    //window.open(url, "网上订餐");
}
var dialogs = {};
function showPopDialog(url, dialogName, dialogTitle, height, width) {
    var c = b = null;
    c = dialogName;
    b = url;
    var dialog = null;
    if (!dialogs[c]) {
        dialog = new NonaccessibleDialog(c, dialogTitle, b, height, width); //CollaborationAccessibleDialog
        dialogs[c] = dialog;
    }
    else {
        dialog = dialogs[c];
        dialog.url = b;
    }
    dialog.display();
}
//var jq = jQuery;
function trackClickLink(id)
{

}
//签名验证
function showUserSignature(eleName)
{

    // var input = prompt('输入您的签名密码', '');
    // if (input != null) {
    //check password and load user signature
    var url = "/SecurityProcessor.ashx?cmd=validateSignature&pwd=";
    jQuery.ajax({
        async: false, cache: false, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
            // alert("0");
        },
        success: function (data, textStatus) {
            //返回json格式 {'status':'success','signatureid':''}
            //{'status':'failure','signatureid':'','msg':'加载签名失败！'}
            //debugger;
            if (data.status == "success") {
                jQuery("#" + eleName).val(data.signatureid);
                var imgId = "#sign_" + eleName;
                jQuery(imgId).attr("src", "/apps/wf/GetUSignature.ashx?id=" + data.signatureid);
                jQuery(imgId).show();
            }
            else {
                alert(data.msg);
            }

        },
        type: "GET",
        url: url
    });
    //}//end
}
/*People Picker*/
function getAllRoles(renderCallback) {

    var url = "/SecurityProcessor.ashx?cmd=getallroles";
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getAllPublicGroups(renderCallback) {

    var url = "/SecurityProcessor.ashx?cmd=getallpublicgroups";
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getPersonalGroups(renderCallback) {

    var url = "/SecurityProcessor.ashx?cmd=getallpersonalgroups";
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
/*
dformat 1 或者空 原始json数据
        2 option 层级数据
*/
function getDepartments(renderCallback,dformat) {

    var dataT = "json";
    var rformat = "1";
    if (dformat == "2") {
        dataT = "html";
        rformat = "2";
    }

    var url = "/SecurityProcessor.ashx?cmd=getalldepartments&rformat=" + rformat;
    jQuery.ajax({ async: true, cache: false, dataType: dataT, error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
/*END*/
function getSystemUser(renderCallback) {
    var userId = jQuery("#id").val();
    if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getsystemuser&id=" + userId;
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getCurrentUser(renderCallback) {
    //var userId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getcurrentuser";
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getAllSystemUsers(renderCallback) {
    
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getallusers";
    jQuery.ajax({
        async: true,
        cache: true,
        dataType: "json",
        //data: {name:"John",location:"Boston"},
        error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            //alert("1");
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getRoleSystemUsers(renderCallback,roleId) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getroleusers&id=" + roleId;
    jQuery.ajax({ async: true, cache: true, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getPersonalGroupUsers(renderCallback, id) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getpersonalgroupusers&id=" + id;
    jQuery.ajax({ async: true, cache: true, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}         
function getPublicGroupUsers(renderCallback, id) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getpublicgroupusers&id=" + id;
    jQuery.ajax({ async: true, cache: true, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getDepartmentUsers(renderCallback, id) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getdepartmentusers&id=" + id;
    jQuery.ajax({ async: true, cache: true, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            //alert('ol1');
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
            //debugger;           
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function getDepartmentUsers(renderCallback, id) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=getdepartmentusers&id=" + id;
    jQuery.ajax({
        async: true, cache: true, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            //alert('ol1');
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
            //debugger;           
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
/*
type,同事，个人，公用
dept,部门
srch
*/
function searchAllUsers(renderCallback, srch) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/SecurityProcessor.ashx?cmd=searchusers&search=" +encodeURIComponent(srch);
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
/*
type,同事，个人，公用
dept,部门
srch
*/
function getAddressLists(renderCallback, type, dept, srch) {
    //var roleId = jQuery("#id").val();
    //if (userId == undefined) return;
    var url = "/apps/CommandProcessor.ashx?cmd=getaddresslist&type=" + type + "&dept=" + dept + "&srch=" + srch;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function widgetWFWaitingTask(renderCallback,filterType) {
    
    //if (userId == undefined) return;
    var url = "/apps/CommandProcessor.ashx?cmd=getwaitingwftask&filterType=" + filterType;
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
/*
taskName:read, approve
contentType:news,notice,email
//0 - draft,1- publish , 2 not publish
contentType 1 - News 2 notice 3,Email
*/
function widgetTopNewsTask(renderCallback, taskName, contentType,containerId,categoryId,isImportant) {

    //if (userId == undefined) return;
    var url = "/apps/CommandProcessor.ashx?cmd=gettopnews&taskName=" + taskName + "&contentType=" + contentType;
    if (categoryId)
        url += "&folderId=" + categoryId;
    if (isImportant) {
        url = "/apps/CommandProcessor.ashx?cmd=getimportanttopnews&taskName=" + taskName + "&contentType=" + contentType;
    }
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data, containerId);
        },
        type: "GET",
        url: url
    });
}
//获取用户待办事务统计
function widgetTopWaitingWfTask(renderCallback, taskName, contentType, containerId, categoryId) {

    //if (userId == undefined) return;
    var url = "/apps/CommandProcessor.ashx?cmd=getwftaskstatistic&taskName=" + taskName + "&contentType=" + contentType;
    
    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data, containerId);
        },
        type: "GET",
        url: url
    });
}
function statWaitingWfTask()
{
    var url = "/apps/CommandProcessor.ashx?cmd=statwftask";

    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            //renderCallback(data, containerId);
        },
        type: "GET",
        url: url
    });
}
function fillNewsGrid(data, containerId) {
    var html = "";
    var dataLength = 6;
    var mode = 2;
    var emptyRow = 6;
    if (data == null || data.length == 0) {
        //html = '<table cellspacing="0" cellpadding="0" border="0" class="list"><tbody><tr class="headerRow"><th scope="col" class="noRowsHeader">没有可显示的记录</th></tr>  </tbody></table>';
        //jQuery("#newsgrid_body").html(html);
        //return;
    }
    else {
        dataLength = data.length;
        emptyRow = 6 - dataLength;
        //html = " <tbody>";
        for (var i = 0; i < dataLength; i++) {
            var item = data[i];
            if ((mode % 2) > 0)
                html += '<tr class="dataRow even">';
            else
                html += '<tr class="dataRow odd">';
            html += '<td class="dataCell" style="width:10px;"><img width="3" height="3" src="/img/fk.png" /></td>';
            html += '<td class="dataCell" style="width:60px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">' + item.folderName + '</td>'
            html += '<td class="dataCell">';
            html += '<a title="" style="text-decoration: none;" class="actionLink" href="/apps/scontent/PreviewContent.aspx?id=' + item.id + '" target="_blank">';
            html += item.title + '</a>'
            html += '   </td>'
            //html += '<td></td>'    
            html += '<td class="dataCell"  style="width:80px;">' + item.createdBy + '</td>'
            html += '<td class="dataCell" style="width:80px;">' + item.date + '</td>'
            html += '</tr>'
            mode++;
        }
    }
    
    for (var i = 0; i < emptyRow; i++) {
        if ((mode % 2) > 0)
            html += '<tr class="dataRow even">';
        else
            html += '<tr class="dataRow odd">';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '</tr>'
        mode++;
    }
    //html += " </tbody>";
    jQuery("#" + containerId).html(html);
    //jQuery("#newsgrid_body").html(html);
}
function fillWFWaitingTaskGrid(data) {
    var html = "";
    var dataLength = 6;
    var mode = 2;
    var emptyRow = 6;
    /*
    if (data == null || data.length == 0) {
        html = '<table cellspacing="0" cellpadding="0" border="0" class="list"><tbody><tr class="headerRow"><th scope="col" class="noRowsHeader">没有可显示的记录</th></tr>  </tbody></table>';
        jQuery("#PendingProcessWorkitemsList_body").html(html);
        return;
    }
    */
    //html = " <tbody>";
    if (data) {
        dataLength = data.length;
        emptyRow = 6 - dataLength;
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            if ((mode % 2) > 0)
                html += '<tr class="dataRow even">';
            else
                html += '<tr class="dataRow odd">';
            //if (item.remindTimes > 0)
            //    html += '<td class="dataCell" style="width:16px;"></td>';
            //else
                html += '<td class="dataCell" style="width:10px;"><img width="3" height="3" src="/img/fk.png" /></td>';

            html += '<td class="dataCell">';
            
            html += '<a title="" target="_blank" style="text-decoration: none;" class="actionLink" href="/apps/wf/InstanceDetail.aspx?t=122&source=l&id=' + item.id + '&retURL=/122/o?src=2">';
            html += item.name + '</a>'
            if (item.remindTimes > 0)
                html += '<img alt="催办" width="16" height="16" src="/img/icon/custom51_100/bell16.png" style="vertical-align: middle;" />';
            html += '   </td>'
            html += '<td class="dataCell"  style="width:80px;">' + item.createdBy + '</td>'
            html += '<td class="dataCell" style="width:80px;">' + item.date + '</td>'
            html += '</tr>'
        }
    }
    //html += " </tbody>";
    
    for (var i = 0; i < emptyRow; i++) {
        if ((mode % 2) > 0)
            html += '<tr class="dataRow even">';
        else
            html += '<tr class="dataRow odd">';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '</tr>'
        mode++;
    }
    jQuery("#PendingProcessWorkitemsList_gridbody").html(html);
}
function fillTopUserWfTasksGrid(data, containerId) {
    var html = "";
    var dataLength = 6;
    var mode = 2;
    var emptyRow = 6;
    if (data == null || data.length == 0) {
        //html = '<table cellspacing="0" cellpadding="0" border="0" class="list"><tbody><tr class="headerRow"><th scope="col" class="noRowsHeader">没有可显示的记录</th></tr>  </tbody></table>';
        //jQuery("#newsgrid_body").html(html);
        //return;
    }
    else {
        dataLength = data.length;
        emptyRow = 6 - dataLength;
        //html = " <tbody>";
        for (var i = 0; i < dataLength; i++) {
            var item = data[i];
            if ((mode % 2) > 0)
                html += '<tr class="dataRow even">';
            else
                html += '<tr class="dataRow odd">';
            html += '<td class="dataCell" style="width:10px;"><img width="3" height="3" src="/img/fk.png" /></td>';
            html += '<td class="dataCell"  style="width:80px;">' + item.dept + '</td>'
            html += '<td class="dataCell" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">' + item.fullName + '</td>'
            //html += '<td class="dataCell">';
            //html += '<a title="" style="text-decoration: none;" class="actionLink" href="/apps/scontent/PreviewContent.aspx?id=' + item.id + '" target="_blank">';
            //html += item.title + '</a>'
            //html += '   </td>'
            //html += '<td></td>'    
            
            html += '<td class="dataCell" style="width:40px;">' + item.notStartedWfTasks + '</td>'
            html += '</tr>'
            mode++;
        }
    }

    for (var i = 0; i < emptyRow; i++) {
        if ((mode % 2) > 0)
            html += '<tr class="dataRow even">';
        else
            html += '<tr class="dataRow odd">';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '<td class="dataCell">&nbsp;</td>';
        html += '</tr>'
        mode++;
    }
    //html += " </tbody>";
    jQuery("#" + containerId).html(html);
    //jQuery("#newsgrid_body").html(html);
}
function publishWFProcess(procId, state) {
    if (state == 1) {
        if ((Modal.confirm && Modal.confirm('是否确定发布此流程吗？')) || (!Modal.confirm && window.confirm('是否确定发布此流程吗？'))) {
            var url = "/apps/CommandProcessor.ashx?cmd=publishprocess&id="+procId;
            jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
                success: function (data, textStatus) {
                    // debugger;
                    //renderCallback(data);
                    window.location.reload();
                },
                type: "GET",
                url: url
            });
         }
    }
    if (state == 0) {
        if ((Modal.confirm && Modal.confirm('是否确定停用此流程吗？停用不影响目前在用事务，仅不能创建新事务')) || (!Modal.confirm && window.confirm('是否确定停用此流程吗？'))) {
            var url = "/apps/CommandProcessor.ashx?cmd=stopprocess&id=" + procId;
            jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
                success: function (data, textStatus) {
                    // debugger;
                    //renderCallback(data);
                    window.location.reload();
                },
                type: "GET",
                url: url
            });
        }
    }
    return false;
}
function deleteInnerEmails(ids, ltags, renderCallback) {
    var url = "/apps/CommandProcessor.ashx?cmd=deleteEmail&ids=" + ids + "&ltags=" + ltags;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            //{"totalRows":1,"pageNumber":2,"data":[{"id":"s"},{"id":"s2"}]}
            //renderCallback(data);
            //window.location.reload();
        },
        type: "POST",
        url: url
    });
}
function doEmailAction(action,ids, ltags, renderCallback) {
    var url = "/msg/mailAction.ashx?cmd=" + action + "&ids=" + ids + "&ltags=" + ltags;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            //{"totalRows":1,"pageNumber":2,"data":[{"id":"s"},{"id":"s2"}]}

            //renderCallback(data);
            //window.location.reload();
        },
        type: "POST",
        url: url
    });
}
function getUnreadEmailQty(renderCallback) {
    var url = "/apps/CommandProcessor.ashx?cmd=getUnreadEmailQty";
  
    jQuery.ajax({
        async: true, cache: false, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {   
            renderCallback(data);         
        },
        type: "GET",
        url: url
    });
}
function getInnerEmails(tag, srch, renderCallback,pageNum) {
    var url = "/apps/CommandProcessor.ashx?cmd=getinneremails&ltags=" + tag;
    if (pageNum)
        url = url + "&pageNumber=" + pageNum;
    if (srch)
        url = url + "&search=" + srch;
    if (tag == "sent" || tag == "draft")
        jQuery("#colName_1").html("收件人");
    else
        jQuery("#colName_1").html("发件人");
    
    jQuery.ajax({
        async: true, cache: false, dataType: "json",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
            // debugger;
            //{"totalRows":1,"pageNumber":2,"data":[{"id":"s"},{"id":"s2"}]}
            renderCallback(data);
            //window.location.reload();
        },
        type: "GET",
        url: url
    });
}
function renderMailbox(d) {
    var html = "";
    
    for (var i = 0; i < d.data.length; i++) {
        var item = d.data[i];
       // html += '<tr>'
        html += '<tr class="dataRow even  first " onmouseover="if (window.hiOn){hiOn(this);} " onmouseout="if (window.hiOff){hiOff(this);} "';
        html += ' onblur="if (window.hiOff){hiOff(this);}" onfocus="if (window.hiOn){hiOn(this);}">';

        
        html += '<td class="dataCell  tdnowrap" ><img align="absmiddle" class="ico_email dragmsg" src="/s.gif" title="a0O9000000NyFKMEA3"  width="16" /><input id="msg_' + item.emailId + '" name="msg_id" type="checkbox" value="' + item.emailId + '" />';
        
        html += '</td>';
        //html += ' <td class="dataCell  tdnowrap"  colspan="1">';
        //html += ' <a class="star_off" href="#" id="star_a0O9000000NyFKMEA3" onclick="starOnOff(this,\'\'); return false;"  style="border: 1px solid transparent;">';
        //html += '  <img src="/s.gif" width="16" /></a>';
        //html += ' </td>';
        html += '<td class="dataCell">';
        if (!item.isRead) {
            html += '<img align="absmiddle" src="/img/unreademail.png" title="未读邮件"  width="16" />&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        else
            html += '<img align="absmiddle" src="/img/vcssap__scriptaculous/vcssap__quickreply_icon.png" title="未读邮件"  width="16" />&nbsp;&nbsp;&nbsp;&nbsp;';
        
        if (item.mailTag == "sent" || item.mailTag == "draft") //已发,草稿
        {
            html += item.toUserNames + item.toGroupNames + '</td>';
        }
        else
            html += item.fromName + '</td>';

        //html += '<td class="dataCell"></td>'
        html += '<td class="dataCell">';
        if(item.mailTag=="draft")
            html += '<a title="" class="actionLink" href="/msg/compose.aspx?t=InboxEmail&ltags=' + item.mailTag + '&id=' + item.emailId + '">';
        else
            html += '<a title="" class="actionLink" href="/msg/mailView.aspx?t=InboxEmail&ltags=' + item.mailTag + '&id=' + item.emailId + '">';
        html += item.subject + '</a>'
        html += '   </td>'
        //html += ' <td class="dataCell  tdnowrap"></td>';
       // html += '<td class="dataCell">' + item.createdBy + '</td>'
        html += '<td class="dataCell">' + item.createdOn + '</td>'
        html += '</tr>'
    }
    jQuery("#mails_body").html(html);
}
function getUploadAttaches(renderCallback) {
    var url = "/apps/CommandProcessor.ashx?cmd=loaduploadattaches";
   
    jQuery.ajax({
        async: true, cache: false, dataType: "html",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
            renderCallback(data);
         },
        type: "GET",
        url: url
    });
}
function removeUploadAttaches(fileName) {
    var url = "/apps/CommandProcessor.ashx?cmd=removeuploademailattachfile&fileName=" + fileName;

    jQuery.ajax({
        async: true, cache: false, dataType: "html",
        error: function (request, textStatus, errorThrown) {
            errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            //messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
           // renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function publishOfficeNotice(id, state) {
    var url = "";
    var msg = "";
    if (state == 1) {
        url = "/apps/CommandProcessor.ashx?cmd=publishofficenotice&statuscode=1&id=" + id;
        msg = '确定要发布吗？';
    }
    if (state == 2) {
        url = "/apps/CommandProcessor.ashx?cmd=publishofficenotice&statuscode=2&id=" + id;
        msg = '确定要不批准吗？';
    }
    if ((Modal.confirm && Modal.confirm(msg)) || (!Modal.confirm && window.confirm(msg))) {
        
        jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
            success: function (data, textStatus) {
                // debugger;
                //renderCallback(data);
                window.location.reload();
            },
            type: "GET",
            url: url
        });
    }
    return false;
}

function sentTestSms(mobile, msg, msgType) {

    var url = "/apps/CommandProcessor.ashx?cmd=sendtestsms";

    jQuery.ajax({ async: true, cache: false,
        data: {
            testMobile: mobile,
            testmsg: msg,
            smstype: msgType
        },
        dataType: "json",
        error: function (request, textStatus, errorThrown) {
            //debugger;
            //errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            // messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]);
        },
        success: function (data, textStatus) {
            if (data.status == "failure") {
                alert(data.msg);
                // messageOptions_handleAjaxErrors(data.errors);
                //alert(data.msg);
                //jq("#detailErrors").html(data.msg);
            } else {
                alert("发送成功！");
                //jQuery("#sendOrderSms").overlay("close");
            }
            jq("#detailErrors").html(data.msg);
        },
        type: "POST",
        url: url
    });
}


function getAllWfProcess(renderCallback,  categoryId) {

    //if (userId == undefined) return;
    var url = "/WFPageProcessor.ashx?cmd=queryprocess&folderid=" + categoryId+"&statecode=1";

    jQuery.ajax({ async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function hrCheckIn(t,conf,callback) {
    var cmd = "checkin";
    if (t == 2)
        cmd = "checkout";
    var url1 = "/apps/HrProcessor.ashx?cmd=" + cmd;

    if (conf) {
        url1 += "&confirm=1";
    }

    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            //debugger;
            //renderCallback(data);
            if (data.status == "success") {
                callback(data);
            }
            else {
                if (data.action == "9999") {
                    var c1 = window.confirm(data.msg);
                    if (c1) {
                        hrCheckIn(t, 1, callback);
                    }
                }
                else {
                    alert(data.msg);
                    callback(data);
                }
            }
        },
        type: "GET",
        url: url1
    });
}

/*content browser*/
function contentItemSelect(itemId,blockId)
{
    document.getElementById("pageBlock_" + blockId).value = itemId;
}
function contentLinkMore(blockId, type)
{
    var id= document.getElementById("pageBlock_" + blockId).value;
    window.open("/apps/scontent/contentListMore.aspx?t=home&contentTypeCode="+type+"&folderId="+id);
}


function updateContentAccess(id, rightCode, callback) {
    var cmd = "updatecontentaccess";
    var url1 = "/apps/CommandProcessor.ashx?cmd=" + cmd;
    url1 += "&id=" + id;
    //url1 += "&objectType=" + objType;
   // url1 += "&objectId=" + objectId;
    url1 += "&rightCode=" + rightCode;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            //debugger;
            //renderCallback(data);
            //if (data.status == "success") {
            callback(data);
            //}
            //else {

            //}
        },
        type: "POST",
        url: url1
    });
}
/*增加目录权限*/
function addContentAccess(entityId,objType,objectId,rightCode,callback) {
    var cmd = "addcontentaccess";
    var url1 = "/apps/CommandProcessor.ashx?cmd=" + cmd;
    url1 += "&entityId=" + entityId;
    //url1 += "&objectType=" + objType;
    url1 += "&objectId=" + objectId;
    url1 += "&rightCode=" + rightCode;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            //debugger;
            //renderCallback(data);
            //if (data.status == "success") {
                callback(data);
            //}
            //else {
              
            //}
        },
        type: "POST",
        url: url1
    });
}

function approvePlan(id, state) {
    var url = "";
    var msg = "";
    if (state == 1) {
        url = "/apps/CommandProcessor.ashx?cmd=approveplan&StateCode=1&id=" + id;
        msg = '确定要批准通过吗？';
    }
    if (state == 2) {
        url = "/apps/CommandProcessor.ashx?cmd=approveplan&StateCode=2&id=" + id;
        msg = '确定要审批不通过吗？';
    }
    if ((Modal.confirm && Modal.confirm(msg)) || (!Modal.confirm && window.confirm(msg))) {

        jQuery.ajax({
            async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
            success: function (data, textStatus) {
                 window.location.reload();
            },
            type: "GET",
            url: url
        });
    }
    return false;
}
/*日志评鉴*/
function getUserJournal(year,userId,renderCallback) {

    var url = "/apps/CommandProcessor.ashx?cmd=getUserJournal&year="+year+"&userId="+userId;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function saveUserJournal(id,year,month,userId, comment,score, renderCallback) {

    var url = "/apps/CommandProcessor.ashx?cmd=savejournalcomment&id=" + id + "&JournalYear=" + year + "&JournalMonth=" + month + "&userId=" + userId + "&comment=" +encodeURIComponent(comment) + "&score=" + score;
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            // renderCallback(data);
            filterJournal();
        },
        type: "POST",
        url: url
    });
}
function getSubordinates(renderCallback) {

    var url = "/_ui/user/subordinates/ListServlet";
    jQuery.ajax({
        async: true, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}
function checkResourceFree(resourceId, startTime, endTime, renderCallback) {

    var url = "/apps/CommandProcessor.ashx?cmd=checkresourcefree&resourceId=" + resourceId + "&startTime=" + startTime + "&endTime=" + endTime;
    jQuery.ajax({
        async: false, cache: false, dataType: "json", error: function (request, textStatus, errorThrown) { errorThrown = String(errorThrown).replace(/&/g, "&amp;").replace(/"/g, "&quot;").replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); messageOptions_handleAjaxErrors([errorThrown ? errorThrown : textStatus]); },
        success: function (data, textStatus) {
            // debugger;
            renderCallback(data);
        },
        type: "GET",
        url: url
    });
}

/*多选控件*/
function synMutipleCheckboxValue(cId,vValue)
{
    var selValue = "";
    var sort = 0;
    jQuery("input[name='"+cId+"_m']").each(function (index, value) {
        var isSelect = jQuery(this).attr("checked");
        if (isSelect) {
            var v = jQuery(this).val();
            if (v != "") {
                if (sort > 0) {
                    selValue += ",";
                }
                selValue += v;
                sort++;
            }
        }
    });
    jQuery("#" + cId).val(selValue);
}