package cn.com.phinfo.protocol;

import com.heqifuhou.protocolbase.HttpResultBeanBase;
import com.heqifuhou.protocolbase.QuickRunObjectBase;

import java.util.List;
import java.util.Stack;

/**
 *
 */
public class MessageStaticsListRun extends QuickRunObjectBase {

    public MessageStaticsListRun() {
        super(LURLInterface.GET_URL_MESSAGE_STATICS_LIST(),null, MessageStaticsListRun.MessageStaticResultBean.class);
    }

    public static class MessageStaticResultBean extends HttpResultBeanBase {
        private List<MessageStaticItem> listDta = new Stack<MessageStaticItem>();

        public List<MessageStaticItem> getListDta() {
            return listDta;
        }

        public void setListDta(List<MessageStaticItem> listDta) {
            this.listDta = listDta;
        }
    }

    public static class MessageStaticItem {
        private String Name;
        private String ObjectTypeCode;
        private String Quantity;
        private String ModifiedOn;
        private String ModuleName;
        private String Title;
        private String ModuleId;
        private String Icon;
        private String Type;
        private String LinkUrl;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getObjectTypeCode() {
            return ObjectTypeCode;
        }

        public void setObjectTypeCode(String objectTypeCode) {
            ObjectTypeCode = objectTypeCode;
        }

        public String getQuantity() {
            return Quantity;
        }

        public void setQuantity(String quantity) {
            Quantity = quantity;
        }

        public String getModifiedOn() {
            return ModifiedOn;
        }

        public void setModifiedOn(String modifiedOn) {
            ModifiedOn = modifiedOn;
        }

        public String getModuleName() {
            return ModuleName;
        }

        public void setModuleName(String moduleName) {
            ModuleName = moduleName;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getModuleId() {
            return ModuleId;
        }

        public void setModuleId(String moduleId) {
            ModuleId = moduleId;
        }

        public String getIcon() {
            return Icon;
        }

        public void setIcon(String icon) {
            Icon = icon;
        }

        public String getType() {
            return Type;
        }

        public void setType(String type) {
            Type = type;
        }

        public String getLinkUrl() {
            return LinkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            LinkUrl = linkUrl;
        }
    }
}
