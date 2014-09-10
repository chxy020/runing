package net.yaopao.voice;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayVoice {
	
	private static String[] mVoiceIds = null;
	private static int mPlayCurrent = 0;
	private static ArrayList mVoiceList = new ArrayList();
	private static Context mContext = null;
	private static boolean mIsPlay = false;
	/** 启动播放音频 */
	private static MediaPlayer mMediaPlayer = new MediaPlayer();
	/** 播放句柄标识 */
	private static int PLAY_VOICE_HANDLER = 1;
	private static Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//判断当前语音有没有播完
			int len = mVoiceIds.length - 1;
			if(len > mPlayCurrent){
				mPlayCurrent++;
				String path = mVoiceIds[mPlayCurrent];
				playVoiceData(path,mContext);
			}
			else{
				//判断有没有新的记录
				//先删除已播放完成的第一条
				mVoiceList.remove(0);
				int size = mVoiceList.size();
				if(size > 0){
					mVoiceIds = null;
					mPlayCurrent = 0;
					mHandler.removeMessages(PLAY_VOICE_HANDLER);
					mIsPlay = false;
					StartPlayVoice(null,mContext);
				}
			}
		}
	};
		
	public PlayVoice() {
	}
	
	
		
	/**
	 * 播放语音
	 * @param ids语音编码用,分割
	 * @author cxy
	 * @date 2014-9-10
	 */
	public static void StartPlayVoice(String ids,Context context){
		mContext = context;
		
		if(null != ids){
			//保存语音编码到队列
			mVoiceList.add(ids);
		}
		//判断当前是否再播放语音
		//boolean b = mHandler.hasMessages(PLAY_VOICE_HANDLER);
		if(!mIsPlay){
			String path = "";
			//没有播放语音
			if(null == mVoiceIds && 0 == mPlayCurrent){
				//从头开始播放
				//取第一条数据
				String data = mVoiceList.get(0).toString();
				mVoiceIds = data.split("\\,");
			}
			path = mVoiceIds[mPlayCurrent];
			playVoiceData(path,context);
		}
		else{
			//有的话,不用管,再handler里面处理
		}
	}
	
	private static void playVoiceData(String path,Context context){
		try {
			// 重置mediaPlayer实例，reset之后处于空闲状态
			mMediaPlayer.reset();
			//Log.e("","chxy________________mMediaPlayer1"+ path);
			String file = "mp3/" + path + ".mp3";
			// 设置需要播放的音乐文件的路径，只有设置了文件路径之后才能调用prepare
			AssetFileDescriptor fileDescriptor = context.getAssets().openFd(file);
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
			//Log.e("","chxy________________mMediaPlayer2");
			// 准备播放，只有调用了prepare之后才能调用start
			mMediaPlayer.prepare();
			// 开始播放
			mMediaPlayer.start();
			mIsPlay = true;
			//Log.e("","chxy________________mMediaPlayer3");
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mHandler.sendEmptyMessageDelayed(PLAY_VOICE_HANDLER,100);
				}
			});
		} catch (IOException e) {
		}
	}
	
}


