package cn.com.phinfo.oaact.base;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.phinfo.adapter.UFileAttacheAdapter.OnUFileItemClickListener;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.FileSearchRun.UFileItem;

import com.heqifuhou.view.PopupWindows;
import com.heqifuhou.view.PopupWindows.OnPopupWindowsItemListener;

public abstract class UMyCanMoveFileListBaseAct extends UMyFileListBaseAct implements
		OnItemClickListener, OnUFileItemClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected void filePopAction(final UFileItem it) {
		if (repayPop != null && repayPop.isShowing()) {
			return;
		}
		repayPop = new PopupWindows(this, this.findViewById(R.id.root),
				new String[] { "发送给联系人", "发邮件", "重命名", "移动", "删除", "链接分享" });
		repayPop.show();
		repayPop.setOnPopupWindowsItemListener(new OnPopupWindowsItemListener() {
			@Override
			public void onPopupWindowsItem(int pos) {
				switch (pos) {
				case 0:// 发送给联系人
					break;
				case 1:// 发邮件
					sendEmail(it);
					break;
				case 2:// 重命名
					renameFile(it);
					break;
				case 3:// 移动
					remove(it);
					break;
				case 4:// 删除
					delFile(it);
					break;
				case 5:// 链接分享
					break;
				}
			}
		});
	}

}
