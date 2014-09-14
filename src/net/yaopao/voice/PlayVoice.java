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
	/** 语音数字数据 */
	private static String[] mNumberVoice = {"100000","100001","100002","100003","100004","100005","100006","100007","100008","100009","100010","100011","100012","100013","100014","100015","100016","100017","100018","100019","100020","100021","100022","100023","100024","100025","100026","100027","100028","100029","100030","100031","100032","100033","100034","100035","100036","100037","100038","100039","100040","100041","100042","100043","100044","100045","100046","100047","100048","100049","100050","100051","100052","100053","100054","100055","100056","100057","100058","100059"};
	private static String[] mVoiceIds = null;
	private static int mPlayCurrent = 0;
	private static ArrayList<String> mVoiceList = new ArrayList<String>();
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
				//先删除已播放完成的第一条
				mVoiceList.remove(0);
				int size = mVoiceList.size();
				//Log.e("","chxy _________size:" + size);
				//恢复控制变量
				mIsPlay = false;
				mVoiceIds = null;
				mPlayCurrent = 0;
				//判断有没有新的记录
				if(size > 0){
					//有新的语音记录
					mHandler.removeMessages(PLAY_VOICE_HANDLER);
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
	 * @param context
	 * @author cxy
	 * @date 2014-9-10
	 */
	public static void StartPlayVoice(String ids,Context context){
		mContext = context;
		//Log.e("","chxy _________ids:" + ids);
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
				//Log.e("","chxy _________data:" + data);
				mVoiceIds = data.split("\\,");
			}
			//Log.e("","chxy _________mPlayCurrent:" + mPlayCurrent);
			path = mVoiceIds[mPlayCurrent];
			playVoiceData(path,context);
		}
		else{
			//有的话,不用管,再handler里面处理
		}
	}
	
	/**
	 * 运动开始语音
	 * @param context
	 * @author cxy
	 * @date 2014-9-11
	 */
	public static void StartSportsVoice(Context context){
		StartPlayVoice("120201",context);
	}
	
	/**
	 * 运动暂停
	 * @param context
	 * @author cxy
	 * @date 2014-9-11
	 */
	public static void PauseSportsVoice(Context context){
		//Log.e("","chxy _____PauseSportsVoice2");
		StartPlayVoice("120202",context);
	}
	
	/**
	 * 运动继续
	 * @param context
	 * @author cxy
	 * @date 2014-9-11
	 */
	public static void ProceedSportsVoice(Context context){
		StartPlayVoice("120203",context);
	}
	
	/**
	 * 运动完成
	 * @param context
	 * @author cxy
	 * @date 2014-9-11
	 */
	public static void CompleteSportsVoice(String distance,String wminute,String wsecond,String aminute,String asecond,Context context){
		//运动完成，运动距离XX.XX公里，用时XX分XX秒，平均速度每公里XX分XX秒。
		//String ids = "120204," + "120211," + 110041 120212
		String disIds = distanceIds(distance);
		String wTimeIds = timeIds(wminute,wsecond);
		String aTimeIds = timeIds(aminute,asecond);
		
		String ids = "";
		ids += "120204,110002,120211," + disIds + ",110002,120212," + wTimeIds + ",110002,120213," + aTimeIds + ",110003";
		
		StartPlayVoice(ids,context);
	}
	
	/**
	 * 计算时间语音id
	 * @param minute
	 * @param second
	 * @return
	 * @author cxy
	 * @date 2014-9-11
	 */
	private static String timeIds(String minute,String second){
		String ids = "";
		int m = Integer.valueOf(minute);
		String mId = mNumberVoice[m];
		int s = Integer.valueOf(second);
		String sId = mNumberVoice[s];
		ids += mId + ",110022," + sId + ",110021";
		return ids;
	}
	
	/**
	 * 计算公里语音id
	 * @param distance
	 * @return
	 * @author cxy
	 * @date 2014-9-11
	 */
	private static String distanceIds(String distance){
		String ids = "";
		//判断
		String[] num = distance.split("\\.");
		
		int n = Integer.valueOf(num[0]);
		String vId = mNumberVoice[n];
		if(null == vId){
			//大于59怎么办呢...
		}
		else{
			ids = vId;
		}
		
		if(num.length > 1){
			//有小数
			int n1 = Integer.valueOf(num[1]);
			if(0 != n1){
				String vId2 = mNumberVoice[n1];
				ids += ",110001," + vId2;
			}
		}
		ids += ",110041";
		return ids;
	}
	
	private static void playVoiceData(String path,Context context){
		try {
			// 重置mediaPlayer实例，reset之后处于空闲状态
			mMediaPlayer.reset();
			//Log.e("","chxy________________mMediaPlayer1"+ path);
			String file = "mp3/" + path + ".mp3";
			// 设置需要播放的音乐文件的路径，只有设置了文件路径之后才能调用prepare
			AssetFileDescriptor fileDescriptor = context.getAssets().openFd(file);
			//Log.e("","chxy _________file:" + file);
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


