package cn.com.phinfo.adapter;import cn.com.phinfo.adapter.base.DepartmentBaseAdapter;import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;public class AddressDepartmentAdapter extends DepartmentBaseAdapter<UnitandaddressItem>{	@Override	protected String onGetText(UnitandaddressItem it) {		return it.getName();	}}