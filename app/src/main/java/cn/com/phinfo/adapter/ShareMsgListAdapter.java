package cn.com.phinfo.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.phinfo.oaact.R;
import cn.com.phinfo.protocol.LURLInterface;
import cn.com.phinfo.protocol.SearchChatterRun.PollOption;
import cn.com.phinfo.protocol.SearchChatterRun.SearchChatterItem;

import com.heqifuhou.adapterbase.MyImgAdapterBaseAbs;
import com.heqifuhou.imgutils.BitmapDataListInstanceUtils;
import com.heqifuhou.imgutils.FileItem;
import com.heqifuhou.imgutils.PhotoAct;
import com.heqifuhou.utils.ParamsCheckUtils;

//通知
public class ShareMsgListAdapter extends MyImgAdapterBaseAbs<SearchChatterItem> {
	private Activity act;
	private OnClickListener listener;
	public ShareMsgListAdapter(Activity act,OnClickListener listener){
		this.act = act;
		this.listener = listener;
	}
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final ViewHolder hoder;
		if (arg1 == null) {
			hoder = new ViewHolder();
			arg1 = LayoutInflater.from(arg2.getContext()).inflate(
					R.layout.adapter_chartter_item, null);
			hoder.voteBtn = (TextView) arg1.findViewById(R.id.vote_btn);
			hoder.photo = (ImageView) arg1.findViewById(R.id.photo);
			hoder.thumbnail = (GridView) arg1.findViewById(R.id.thumbnail);
			hoder.Name = (TextView) arg1.findViewById(R.id.Name);
			hoder.date = (TextView) arg1.findViewById(R.id.date);
			hoder.address = (TextView) arg1.findViewById(R.id.address);
			hoder.comm = (TextView) arg1.findViewById(R.id.comm);
			hoder.content = (TextView) arg1.findViewById(R.id.content);
			hoder.good = (TextView) arg1.findViewById(R.id.good);
			hoder.share= (TextView) arg1.findViewById(R.id.share);
			hoder.voteLst = (ListView) arg1.findViewById(R.id.voteLst);
			hoder.pollTitle =  (TextView) arg1.findViewById(R.id.pollTitle);
			hoder.peopCount =  (TextView) arg1.findViewById(R.id.peopCount);
			hoder.poll = arg1.findViewById(R.id.poll);
			arg1.setTag(hoder);
		} else {
			hoder = (ViewHolder) arg1.getTag();
		}
		final SearchChatterItem it = this.getItem(arg0);
		hoder.Name.setText(it.getOwningUserName());
		hoder.date.setText(it.getCreatedOn().trim());
		if(ParamsCheckUtils.isNull(it.getLocation())){
			hoder.address.setVisibility(View.GONE);
		}else{
			hoder.address.setVisibility(View.VISIBLE);
			hoder.address.setText(it.getLocation());
		}
		if(!ParamsCheckUtils.isNull(it.getNumOfComment())){
			hoder.comm.setText(it.getNumOfComment());
		}else{
			hoder.comm.setText("0");
		}
		if(!ParamsCheckUtils.isNull(it.getNumOfLike())){
			hoder.good.setText(it.getNumOfLike());
		}else{
			hoder.good.setText("0");
		}
		if(!ParamsCheckUtils.isNull(it.getNumOfForward())){
			hoder.share.setText(it.getNumOfForward());
		}else{
			hoder.share.setText("0");
		}
		this.getAsyncAvatar(hoder.photo,LURLInterface.GET_AVATAR(it.getOwningUser()),it.getOwningUserName());
		
		
		//投票的
		if("30400".equals(it.getChatterTypeCode())){
			hoder.thumbnail.setVisibility(View.GONE);
			hoder.poll.setVisibility(View.VISIBLE);
			hoder.content.setText(it.getOwningUserName()+"发起了一个投票【"+it.getDescription()+"】");
			hoder.pollTitle.setText(it.getPoll().getTitle());
			hoder.peopCount.setText("参与人数:"+it.getPoll().getTotalPeoples());
			hoder.myShareVoteAdapter = new ShareVoteAdapter(!it.isHasOptions());
			hoder.voteLst.setAdapter(hoder.myShareVoteAdapter);
			hoder.myShareVoteAdapter.replaceListRef(it.getPoll().getOptions());
			//已经投了
			if(it.isHasOptions()){
				hoder.voteBtn.setText("已投票");
				hoder.voteBtn.setEnabled(false);
				hoder.voteLst.setOnItemClickListener(null);
			}else{
				hoder.voteBtn.setText("投票");
				hoder.voteBtn.setEnabled(true);
				hoder.voteLst.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						PollOption itOption = hoder.myShareVoteAdapter.getItem(arg2);
						//0是单选
						hoder.myShareVoteAdapter.setSel(itOption,!"0".equals(it.getPoll().getPollType()));
					}
				});
				hoder.voteBtn.setTag(it);
				hoder.voteBtn.setOnClickListener(listener);
			}
		}
		else{
			hoder.thumbnail.setVisibility(View.VISIBLE);
			hoder.poll.setVisibility(View.GONE);
			hoder.content.setText(it.getDescription());
			hoder.thumbnail.setTag(it);
			hoder.myQThumbnailAdapter = new MyQThumbnailAdapter();
			hoder.thumbnail.setAdapter(hoder.myQThumbnailAdapter);
			hoder.myQThumbnailAdapter.replaceListRef(it.getThumbnailPicUrls());
			hoder.thumbnail.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// 将大图放进去可以显示
					SearchChatterItem it = (SearchChatterItem) arg0.getTag();
					List<FileItem> lstRef = it.getFileItemList();
					BitmapDataListInstanceUtils.getRefInstance().clear();
					BitmapDataListInstanceUtils.getRefInstance().addAll(lstRef);
					Intent i = new Intent(act, PhotoAct.class);
					i.putExtra("isNoDel", true);
					i.putExtra("ID", arg2);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					act.startActivity(i);
				}
			});	
		}
		return arg1;
	}

	class ViewHolder {
		public TextView Name, date,address, comm, good, content,share,pollTitle,peopCount;
		public ImageView photo;
		public GridView thumbnail;
		public MyQThumbnailAdapter myQThumbnailAdapter=null;
		public ShareVoteAdapter myShareVoteAdapter=null;
		public TextView voteBtn;
		public ListView voteLst;
		public View poll;
	}
}
