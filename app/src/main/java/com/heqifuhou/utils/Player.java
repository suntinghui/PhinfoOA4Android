package com.heqifuhou.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,OnErrorListener,OnPreparedListener {
	private  MediaPlayer mediaPlayer=null;
	private OnErrorListener errlistener=null;
	private OnPreparedListener prelistener=null;
	//当前播放位置
	public int getCurrentPosition(){
		if(mediaPlayer == null){
			return 0;
		}
		return mediaPlayer.getCurrentPosition();
	}
	
	//总的播放时间长度
	public int getDuration(){
		if(mediaPlayer == null){
			return 0;
		}
		return mediaPlayer.getDuration();
	}

	public void playUrl(String url) {
		if(mediaPlayer!=null&&!mediaPlayer.isPlaying()){
			mediaPlayer.start();
			if(this.prelistener!=null){
				this.prelistener.onPrepared(mediaPlayer);
			}
			return;
		}
		if (null == mediaPlayer) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnPreparedListener(this);
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
		}
	}
	
	public void setOnPreparedListener(OnPreparedListener prelistener){
		this.prelistener = prelistener;
	}
	
	public void setOnErrorListener(OnErrorListener errlistener){
		this.errlistener = errlistener;
	}

	public void seekTo(int arg0){
		if(mediaPlayer == null){
			return;
		}
		mediaPlayer.seekTo(arg0);
	}
	public  boolean isPlaying() {
		if (mediaPlayer == null){
			return false;
		}
		else if (mediaPlayer.isPlaying()){
			return true;
		}
		return false;
	}

	// 播放音乐
	public  void play() {
		if (mediaPlayer != null){
			mediaPlayer.start();
		}
	}

	public  void pause() {
		if (mediaPlayer != null){
			mediaPlayer.pause();
		}
	}

	// 停止播放ֹ
	public  void stop() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
			} catch (Exception e) {
			}
			mediaPlayer = null;
		}
	}

	/**
	 * 
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// int currentProgress;
		// try {
		// currentProgress = seekBar.getMax()
		// * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		// Log.e(currentProgress + "% play", percent + " buffer");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		arg0.release();
		if(this.errlistener!=null){
			this.errlistener.onError(arg0, arg1, arg2);
		}
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		if(this.prelistener!=null){
			this.prelistener.onPrepared(arg0);
		}
	}

}
