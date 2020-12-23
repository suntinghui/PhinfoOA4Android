package cn.com.phinfo.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import cn.com.phinfo.protocol.UnitandaddressRun.UnitandaddressItem;

import com.heqifuhou.utils.ParamsCheckUtils;

public class ContactsUtils {
	private static ContactsUtils instance = null;
	private Context context;
	public static ContactsUtils getInstance(Context context) {
		if (instance == null) {
			instance = new ContactsUtils(context);
		}
		return instance;
	}

	private ContactsUtils(Context context) {
		this.context = context;
	}
	public  List<UnitandaddressItem> getRemoveDuplicate() {
		List<UnitandaddressItem> localList=  getLocalContactsInfos();
		List<UnitandaddressItem> SIMList = getSIMContactsInfos();
		for(int i=0;i<localList.size();i++){
			UnitandaddressItem it = localList.get(i);
			isContains(it,SIMList);
		}
		localList.addAll(SIMList);
		return localList;
	}
	
	private boolean isContains(UnitandaddressItem it,List<UnitandaddressItem> ls){
		for(int j=0;j<ls.size();j++){
			UnitandaddressItem jt = ls.get(j);
			if(!ParamsCheckUtils.isNull(it.getMobile())){
				if(!it.getMobile().equals(jt.getMobile())){
					continue;
				}
			}
			if(!ParamsCheckUtils.isNull(it.getFullName())){
				if(!it.getFullName().equals(jt.getFullName())){
					continue;
				}
			}
			ls.remove(j);
			return true;
		}
		return false;
	}

	// ----------------得到本地联系人信息-------------------------------------
	public List<UnitandaddressItem> getLocalContactsInfos() {
		List<UnitandaddressItem> localList = new ArrayList<UnitandaddressItem>();
		
		ContentResolver cr = context.getContentResolver();
		String str[] = { Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.PHOTO_ID };
		Cursor cur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null,
				null, null);

		if (cur != null) {
			while (cur.moveToNext()) {
				UnitandaddressItem contactsInfo = new UnitandaddressItem();
				contactsInfo.setMobile(cur.getString(cur
						.getColumnIndex(Phone.NUMBER)));// 得到手机号码
				contactsInfo.setFullName(cur.getString(cur
						.getColumnIndex(Phone.DISPLAY_NAME)));
				contactsInfo.setDatatype("localUser");
				// contactsInfo.setContactsPhotoId(cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID)));
				long contactid = cur.getLong(cur
						.getColumnIndex(Phone.CONTACT_ID));
				long photoid = cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID));
				// 如果photoid 大于0 表示联系人有头像 ，如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(cr, uri);
					contactsInfo.setLocalPhoto(BitmapFactory
							.decodeStream(input));
				}
				localList.add(contactsInfo);

			}
		}
		cur.close();
		return localList;

	}

	public List<UnitandaddressItem> getSIMContactsInfos() {
		List<UnitandaddressItem> SIMList  = new ArrayList<UnitandaddressItem>();
		ContentResolver cr = context.getContentResolver();
		final String SIM_URI_ADN = "content://icc/adn";// SIM卡
		Uri uri = Uri.parse(SIM_URI_ADN);
		Cursor cursor = cr.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			UnitandaddressItem SIMContactsInfo = new UnitandaddressItem();
			SIMContactsInfo.setFullName(cursor.getString(cursor
					.getColumnIndex("name")));
			SIMContactsInfo.setDatatype("localUser");
			SIMContactsInfo.setMobile(cursor.getString(cursor
					.getColumnIndex("number")));
			SIMList.add(SIMContactsInfo);
		}
		cursor.close();
		return SIMList;
	}
}
