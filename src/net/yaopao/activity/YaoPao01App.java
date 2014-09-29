package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.yaopao.assist.LogtoSD;
import net.yaopao.assist.Variables;
import net.yaopao.db.DBManager;
import net.yaopao.listener.NetworkStateReceiver;
import net.yaopao.voice.PlayVoice;
import net.yaopao.voice.VoiceUtil;
import android.R.id;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class YaoPao01App extends Application {
	public static SharedPreferences sharedPreferences;
	public static NetworkStateReceiver mReceiver;
	public static YaoPao01App instance;
	public static LocationManager locationManager;
	public static int rank;
	public static LogtoSD lts = new LogtoSD();
	public static Location loc;
	public static DBManager db;
	public static DecimalFormat df;
	public static VoiceUtil voice;
	//请打开GPS功能。
	public static String openGps ="110101";
	//GPS信号较弱。
	public static String weekGps ="110102";
	//请启用数据流量。
	public static String openNetwork ="110112";
	
	public LocationListener locationlisten;
	//

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {

		instance = this;
		db = new DBManager(this);
		Log.v("wy", "app");
		Variables.pid = getImeiCode();
		Variables.ua = this.getOptVer() + ",a_0.9.1";
		Log.v("wy", "pid=" + Variables.pid + " ua=" + Variables.ua);
		getPreference();
		initGPS();
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		voice = new VoiceUtil();
		//注册广播  
//        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        registerReceiver(gpsReceiver, new IntentFilter(BaseActivity.registerAction));
	};

	private void initGPS() {
		Log.v("wy", "initGPS");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationlisten = new LocationListener() {

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.v("wy", "gps StatusChanged");
				if (status == LocationProvider.OUT_OF_SERVICE
						|| status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
					if (SportRecordActivity.gpsV != null) {
						Variables.gpsStatus = 0;
						SportRecordActivity.gpsV
								.setBackgroundResource(R.drawable.gps_1);
					}
				}
			}

			public void onProviderEnabled(String arg0) {
				Variables.gpsStatus = 0;
				Log.v("wy", "gps Enabled");
			}

			public void onProviderDisabled(String arg0) {
				Log.v("wy", "gps Disabled");
				Variables.gpsStatus = 2;
				if (SportRecordActivity.gpsV != null) {
					Variables.gpsStatus = 0;
					SportRecordActivity.gpsV
							.setBackgroundResource(R.drawable.gps_1);
				}
			}

			// 当坐标改变时触发此函数；如果Provider传进相同的坐标，它就不会被触发
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					// lts.writeFileToSD(
					// "Location changed : Lat: " + location.getLatitude()
					// + " Lng: " + location.getLongitude(),
					// "uploadLocation");
					if (location.getAccuracy() > 200) {
						if (SportRecordActivity.gpsV != null) {
							SportRecordActivity.gpsV
									.setBackgroundResource(R.drawable.gps_1);
						}
						rank = 1;
					} else if (location.getAccuracy() > 50) {
						if (SportRecordActivity.gpsV != null) {
							SportRecordActivity.gpsV
									.setBackgroundResource(R.drawable.gps_2);
						}
						rank = 2;
					}

					else if (location.getAccuracy() > 20) {
						if (SportRecordActivity.gpsV != null) {
							SportRecordActivity.gpsV
									.setBackgroundResource(R.drawable.gps_3);
						}
						rank = 3;
					}

					else {
						if (SportRecordActivity.gpsV != null) {
							SportRecordActivity.gpsV
									.setBackgroundResource(R.drawable.gps_4);
						}
						rank = 4;
					}

					if (rank > 3) {
						loc = location;
						Variables.gpsStatus = 1;
					} else {
						Variables.gpsStatus = 0;
					}
					// lts.writeFileToSD("rank: " + rank, "uploadLocation");

				} else {
					Variables.gpsStatus = 0;
					if (SportRecordActivity.gpsV != null) {
						SportRecordActivity.gpsV
								.setBackgroundResource(R.drawable.gps_1);
					}
				}
			}
		};

		// 注册监听器 locationListener
		// 第 2 、 3个参数可以控制接收GPS消息的频度以节省电力。第 2个参数为毫秒， 表示调用 listener的周期，第 3个参数为米
		// ,表示位置移动指定距离后就调用 listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0, locationlisten);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.v("wy", "app stop");
	}

	public String getImeiCode() {
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String sImei = null;
		try {
			sImei = tm.getDeviceId();

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		Log.v("sdk", "pid" + sImei);
		return sImei;
	}

	public String getOptVer() {
		String ver = android.os.Build.VERSION.RELEASE;
		return "A_" + ver;
	}

	public String getModel() {
		String ver = android.os.Build.MODEL;
		return ver;
	}

	public String getModelVer() {
		String ver = android.os.Build.BOARD;
		return ver;
	}

	public void getPreference() {
		sharedPreferences = this.getSharedPreferences("yaopao", 0);
	}

	public static YaoPao01App getAppContext() {
		return instance;
	}

	/*
	 * public static boolean isGpsAvailable() { if (Variables.gpsStatus!=2) { if
	 * (Variables.gpsStatus==1) { return true; }else {
	 * //Toast.makeText(instance, "当前位置GPS信号较弱", Toast.LENGTH_LONG).show();
	 * return false; } }else { //Toast.makeText(instance, "请开启GPS",
	 * Toast.LENGTH_LONG).show(); return false; } // return true; }
	 */

	public static int[] cal(long second) {
		
//		int ss = second % 60;
//		int mm = (minute + second / 60) % 60;
//		int hh = hour + (minute + second / 60) / 60;

		int h = (int) (second / 3600);
		int m = (int) (second % 3600 / 60);
		int s = (int) (second % 3600 % 60);
		
//		int h = 0;
//		int m = 0;
//		int s = 0;
//		int temp = (int) (second % 3600);
//		if (second > 3600) {
//			h = (int) (second / 3600);
//			if (temp != 0) {
//				if (temp > 60) {
//					m = (int) (temp / 60);
//					if (temp % 60 != 0) {
//						s = (int) (temp % 60);
//					}
//				} else {
//					s = temp;
//				}
//			}
//		} else {
//			m = (int) (second / 60);
//			if (second % 60 != 0) {
//				s = (int) (second % 60);
//			}
//		}

		return new int[] { h, m, s };
	}

	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;

		return weekDays[w];
	}

	/**
	 * 计算平时跑步积分
	 * 
	 * @param min
	 *            每公里的配速
	 * @return
	 */
	public static int calPspeedPoints(int min) {
		int ponits = 0;

		if (min >= 12) {
			ponits = 3;
		} else if (min >= 11) {
			ponits = 4;
		} else if (min >= 10) {
			ponits = 5;
		} else if (min >= 9) {
			ponits = 6;
		} else if (min >= 8) {
			ponits = 7;
		} else if (min >= 7) {
			ponits = 8;
		} else if (min >= 6) {
			ponits = 9;
		} else if (min >= 5) {
			ponits = 10;
		} else if (min < 5) {
			ponits = 12;
		}

		return ponits;
	}

	/**
	 * 计算比赛跑步积分
	 * 
	 * @param min
	 *            每公里的配速
	 * @return
	 */
	public static int calMatchPspeedPoints(int min) {
		int ponits = 0;

		if (min >= 12) {
			ponits = 6;
		} else if (min >= 11) {
			ponits = 8;
		} else if (min >= 10) {
			ponits = 10;
		} else if (min >= 9) {
			ponits = 12;
		} else if (min >= 8) {
			ponits = 15;
		} else if (min >= 7) {
			ponits = 16;
		} else if (min >= 6) {
			ponits = 18;
		} else if (min >= 5) {
			ponits = 20;
		} else if (min < 5 && min > 0) {
			ponits = 24;
		}

		return ponits;
	}

	/**
	 * 计算平时跑步距离零头积分
	 * 
	 * @return
	 */
	public static void calDisPoints(Context context, Handler handler) {
//		YaoPao01App.lts.writeFileToSD("完成时  计算前 累计积分 : " + Variables.points,
//				"uploadLocation");
		double dis = Variables.distance % 1000;

		if (Variables.distance / 1000 < 1) {
			if (Variables.distance > 50) {
				Variables.points += 1;
			} else {
				handler.obtainMessage(1).sendToTarget();
				return;
			}
		} else if (Variables.distance / 1000 > 1) {
			if (dis >= 500) {
				Variables.points += 2;
			} else if (dis < 500 && dis > 0) {
				Variables.points += 0;
			}
		}
		handler.obtainMessage(0).sendToTarget();
		//播放语音
		if (Variables.switchVoice == 0) {
		YaoPao01App.playCompletVoice();
		}
	}

	/**
	 * 计算比赛跑步距离零头积分
	 * 
	 * @return
	 */
	public static void calMatchDisPoints() {
		double dis = Variables.distance % 1000;
		if (Variables.distance / 1000 < 0) {
			if (Variables.distance > 0) {
				Variables.points += 2;
			}
		} else if (Variables.distance / 1000 > 0) {
			if (dis >= 500) {
				Variables.points += 4;
			} else if (dis < 500 && dis > 0) {
				Variables.points += 0;
			}
		}
	}

	// ====================================获取剩余公里数据id,当目标是奇数的时候，要播放0.5公里的部分
	public static String getLeftDisCode(boolean ishalf) {
		// 获取公里语音代码
		//double leftDis =Double.parseDouble(df.format(Math.round(((Variables.runtarDis*1000-Variables.distance)/1000))));
		//double leftDis =Math.round((Variables.runtarDis*1000-Variables.distance)/1000);
		
		double leftDis =Variables.runtarDis-Math.round(Variables.distancePlayed/1000.0);
//		double leftDis =Variables.runtarDis-Math.round(Variables.distance/1000);
		
//		YaoPao01App.lts.writeFileToSD("Variables.runtarDis = " + Variables.runtarDis+" Variables.distancePlayed"+Variables.distancePlayed/1000, "uploadLocation");
//		YaoPao01App.lts.writeFileToSD("wyvoice:leftDis = " + (Variables.runtarDis-Variables.distancePlayed/1000), "uploadLocation");
		if (Variables.runtarDis%2!=0&&ishalf) {
			leftDis-=0.5;
		}
		List<Integer> dis = voice.voiceOfDouble(leftDis);
		String disStr = "";
		for (int i = 0; i < dis.size(); i++) {
			disStr += dis.get(i) + ",";
		}
		return disStr;
	}
	// 获取超过目标公里数据id
	public static String getOverDisCode() {
		// 获取公里语音代码
		//double leftDis =Double.parseDouble(df.format((double)((Variables.distance-Variables.runtarDis*1000)/1000)));
//		double leftDis =(int)(Variables.distance-Variables.runtarDis*1000)/1000;
		double leftDis =Variables.distancePlayed/1000-Variables.runtarDis;
		List<Integer> dis = voice.voiceOfDouble(leftDis);
		String disStr = "";
		for (int i = 0; i < dis.size(); i++) {
			disStr += dis.get(i) + ",";
		}
		return disStr;
	}
	
	// ====================================获取完成公里数据id，如果运动目标是时间，播放小数部分，如果目标是距离，只播放整数部分
	public static String getDisCode() {
		// 获取公里语音代码
		double leftDis =Double.parseDouble(df.format((double) (Variables.distance / 1000)));
				if (Variables.runtar!=2) {
					 leftDis =(int)(Variables.distance/1000);
				}
//				List<Integer> dis = voice.voiceOfDouble(Double.parseDouble(df.format((double) (Variables.distance / 1000))));
				List<Integer> dis = voice.voiceOfDouble(leftDis);
				String disStr = "";
				for (int i = 0; i < dis.size(); i++) {
					disStr += dis.get(i) + ",";
				}
		return disStr;
	}
	
	// 获取目标公里数据id
	public static String getTarDisCode() {
		// 获取公里语音代码
		List<Integer> dis = voice.voiceOfDouble(Variables.runtarDis);
		String disStr = "";
		for (int i = 0; i < dis.size(); i++) {
			disStr += dis.get(i) + ",";
		}
		return disStr;
	}
	//==================================== 获取整公里数据id
	public static String getPerKmCode() {
		// 获取公里语音代码
//		List<Integer> dis = voice.voiceOfDouble((int)(Variables.distance / 1000));
		List<Integer> dis = voice.voiceOfDouble(Variables.distancePlayed/1000);
		Log.v("wyvoice", " distance=" + Variables.distance+"distance=" + Variables.distance+"米"+(int)(Variables.distance / 1000)+"km"); 
//		YaoPao01App.lts.writeFileToSD("wyvoice:distance=" + Variables.distance+"distance=" + Variables.distance+"米"+(int)(Variables.distance / 1000)+"km", "uploadLocation");
		String disStr = "";
		for (int i = 0; i < dis.size(); i++) {
			disStr += dis.get(i) + ",";
		}
		return disStr;
	}

	// 获取用时数据的id
	public static String getTimeCode() {
		// 获取用时语音代码
		int[] times = YaoPao01App.cal(Variables.utime/1000);
		List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
		String timeStr = "";
		for (int i = 0; i < time.size(); i++) {
			timeStr += time.get(i) + ",";
		}
		return timeStr;
	}
	
	// 获取用时数据的id
		public static String getTimePer5Code() {
			// 获取用时语音代码
//			int[] times = YaoPao01App.cal(Variables.utime/1000);
			int[] times = YaoPao01App.cal(Variables.timePlayed);
			List<Integer> time = voice.voiceOfTime(times[0], times[1]);
			String timeStr = "";
			for (int i = 0; i < time.size(); i++) {
				timeStr += time.get(i) + ",";
			}
			return timeStr;
		}

	// 获取配速数据的id
	public static String getPspeedCode() {
		int[] speeds = YaoPao01App.cal((int) (Variables.pspeed));
		List<Integer> speed = voice
				.voiceOfTime(speeds[0], speeds[1], speeds[2]);
		String speedStr = "";
		for (int i = 0; i < speed.size(); i++) {
			speedStr += speed.get(i) + ",";
		}
		return speedStr;
	}

	// 整公里播报
	public static void playPerKmVoice() {
		StringBuffer ids = new StringBuffer();
		// ids+="120221,"+data[0]+"110041,120212,"+data[1]+"120213,"+data[2];
		long utime = Variables.utime;
		String pspeedStr = "";
		if(Variables.distance<3000){
			if (Variables.distance<2000) {
				int[] speeds = YaoPao01App.cal(Math.round(utime/1000.0));
				List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
			
				for (int i = 0; i < speed.size(); i++) {
					pspeedStr += speed.get(i) + ",";
				}
				int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
				List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
				String timeStr = "";
				for (int i = 0; i < time.size(); i++) {
					timeStr += time.get(i) + ",";
				}
				ids.append("120221,").append(getPerKmCode()).append("110041,120212,")
				.append(timeStr).append("120213,")
				.append(pspeedStr);
			}else{
				int[] speeds = YaoPao01App.cal(Math.round(utime/2000.0));
				List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
				for (int i = 0; i < speed.size(); i++) {
					pspeedStr += speed.get(i) + ",";
				}
				int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
				List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
				String timeStr = "";
				for (int i = 0; i < time.size(); i++) {
					timeStr += time.get(i) + ",";
				}
				ids.append("120221,").append(getPerKmCode()).append("110041,120212,")
				.append(timeStr).append("120213,")
				.append(pspeedStr);
			}
		}else {
			ids.append("120221,").append(getPerKmCode()).append("110041,120212,")
			.append(getTimeCode()).append("120213,")
			.append(getPspeedCode());
		}
		
		Log.v("wyvoice", "整公里上报ids =" + ids);
//		lts.writeFileToSD("整公里上报ids =" + ids, "voice");
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}

	/**
	 * 完成时上报
	 */
	public static void playCompletVoice() {

		// 获取公里语音代码
		List<Integer> dis = voice.voiceOfDouble(Double.parseDouble(df
				.format((double) (Variables.distance / 1000))));
		String disStr = "";
		for (int i = 0; i < dis.size(); i++) {
			disStr += dis.get(i) + ",";
		}
		// ids+="120204,120211,"+disStr+"110041,120212,"+timeStr+"120213,"+speedStr;
		StringBuffer ids = new StringBuffer();
		ids.append("120204,120211,").append(disStr).append("110041,120212,")
				.append(getTimeCode()).append("120213,")
				.append(getPspeedCode());
		Log.v("wyvoice", "运动完成上报ids =" + ids);
//		lts.writeFileToSD("运动完成上报ids =" + ids, "voice");
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}

	/**
	 * 距离运动目标,跑步至运动目标的一半时
	 */
	public static void playHalfDisVoice() {
//		long utime = Variables.utime;
//		String pspeedStr = "";
//		StringBuffer ids = new StringBuffer();
//		if (Variables.runtarDis>2) {
//			if(Variables.distance<3000){
//				if (Variables.distance<2000) {
//					Log.v("wyvoice", "Variables.distance = " + Variables.distance);
//					Log.v("wyvoice", "Variables.utime = " + Variables.utime);
//					Log.v("wyvoice", "Math.round(utime/1000.0) = " + Math.round(utime/1000.0));
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.distance = " + Variables.distance, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.utime = " + Variables.utime, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里Math.round(utime/1000.0) = " + Math.round(utime/1000.0), "uploadLocation");
//					int[] speeds = YaoPao01App.cal(Math.round(utime/1000.0));
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里time=" + Variables.utime + "毫秒    utime/1000=" + Variables.timePlayed + "秒 pertime="+(Variables.utime- Variables.timePlayed*1000), "uploadLocation");
//					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
//				
//					for (int i = 0; i < speed.size(); i++) {
//						pspeedStr += speed.get(i) + ",";
//					}
//					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
//					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
//					String timeStr = "";
//					for (int i = 0; i < time.size(); i++) {
//						timeStr += time.get(i) + ",";
//					}
//					ids.append("120101,120223,120222,").append(getLeftDisCode(true)).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
//				}else{
//					Log.v("wyvoice", "Variables.distance = " + Variables.distance);
//					Log.v("wyvoice", "Variables.utime = " + Variables.utime);
//					Log.v("wyvoice", "Math.round(utime/2000.0) = " + Math.round(utime/2000.0));
//					int[] speeds = YaoPao01App.cal(Math.round(utime/2000.0));
//					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
//					for (int i = 0; i < speed.size(); i++) {
//						pspeedStr += speed.get(i) + ",";
//					}
//					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
//					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
//					String timeStr = "";
//					for (int i = 0; i < time.size(); i++) {
//						timeStr += time.get(i) + ",";
//					}
//					ids.append("120101,120223,120222,").append(getLeftDisCode(true)).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
//				}
//			}else {
//				ids.append("120101,120223,120222,").append(getLeftDisCode(true)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//			}
//		}else{
//			ids.append("120101,120223,120222,").append(getLeftDisCode(true)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//		}
		
		
		
		StringBuffer ids = new StringBuffer();
		ids.append("120101,120223,120222,").append(getLeftDisCode(true)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
		Log.v("wyvoice", " 距离运动目标,跑步至运动目标的一半时ids =" + ids);
		lts.writeFileToSD("距离运动目标,跑步至运动目标的一半时ids =" + ids, "voice");
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}
	/**
	 * 运动类型是距离,距离目标小于2公里
	 */
	public static void playLess2Voice() {
//		long utime = Variables.utime;
//		String pspeedStr = "";
//		StringBuffer ids = new StringBuffer();
//		if (Variables.runtarDis>2) {
//			if(Variables.distance<3000){
//				if (Variables.distance<2000) {
//					Log.v("wyvoice", "Variables.distance = " + Variables.distance);
//					Log.v("wyvoice", "Variables.utime = " + Variables.utime);
//					Log.v("wyvoice", "Math.round(utime/1000.0) = " + Math.round(utime/1000.0));
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.distance = " + Variables.distance, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.utime = " + Variables.utime, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里Math.round(utime/1000.0) = " + Math.round(utime/1000.0), "uploadLocation");
//					int[] speeds = YaoPao01App.cal(Math.round(utime/1000.0));
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里time=" + Variables.utime + "毫秒    utime/1000=" + Variables.timePlayed + "秒 pertime="+(Variables.utime- Variables.timePlayed*1000), "uploadLocation");
//					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
//				
//					for (int i = 0; i < speed.size(); i++) {
//						pspeedStr += speed.get(i) + ",";
//					}
//					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
//					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
//					String timeStr = "";
//					for (int i = 0; i < time.size(); i++) {
//						timeStr += time.get(i) + ",";
//					}
//					ids.append("120102,120224,120222,").append(getLeftDisCode(false)).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
//				}else{
//					Log.v("wyvoice", "Variables.distance = " + Variables.distance);
//					Log.v("wyvoice", "Variables.utime = " + Variables.utime);
//					Log.v("wyvoice", "Math.round(utime/2000.0) = " + Math.round(utime/2000.0));
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.distance = " + Variables.distance, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里 Variables.utime = " + Variables.utime, "uploadLocation");
////					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里Math.round(utime/2000.0) = " + Math.round(utime/2000.0), "uploadLocation");
//					int[] speeds = YaoPao01App.cal(Math.round(utime/2000.0));
//					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
//					for (int i = 0; i < speed.size(); i++) {
//						pspeedStr += speed.get(i) + ",";
//					}
//					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
//					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
//					String timeStr = "";
//					for (int i = 0; i < time.size(); i++) {
//						timeStr += time.get(i) + ",";
//					}
//					ids.append("120102,120224,120222,").append(getLeftDisCode(false)).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
//				}
//			}else {
//				ids.append("120102,120224,120222,").append(getLeftDisCode(false)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//			}
//		}else{
//			ids.append("120102,120224,120222,").append(getLeftDisCode(false)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//		}
		
		
		StringBuffer ids = new StringBuffer();
		ids.append("120102,120224,120222,").append(getLeftDisCode(false)).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
		Log.v("wyvoice", "运动目标为距离，小于两公里上报ids =" + ids);
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}
	/**
	 * 运动类型是距离,达成目标
	 */
	public static void playAchieveGoalVoice() {
		long utime = Variables.utime;
		String pspeedStr = "";
		StringBuffer ids = new StringBuffer();
			if(Variables.distance<3000){
				if (Variables.distance<2000) {
					int[] speeds = YaoPao01App.cal(Math.round(utime/1000.0));
//					YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里time=" + Variables.utime + "毫秒    utime/1000=" + Variables.timePlayed + "秒 pertime="+(Variables.utime- Variables.timePlayed*1000), "uploadLocation");
					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
				
					for (int i = 0; i < speed.size(); i++) {
						pspeedStr += speed.get(i) + ",";
					}
					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
					String timeStr = "";
					for (int i = 0; i < time.size(); i++) {
						timeStr += time.get(i) + ",";
					}
					ids.append("120103,120226,").append(getTarDisCode()).append("110041,120227,120212,").append(timeStr).append("120213,").append(pspeedStr);
				}else{
					int[] speeds = YaoPao01App.cal(Math.round(utime/2000.0));
					List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
					for (int i = 0; i < speed.size(); i++) {
						pspeedStr += speed.get(i) + ",";
					}
					int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
					List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
					String timeStr = "";
					for (int i = 0; i < time.size(); i++) {
						timeStr += time.get(i) + ",";
					}
					ids.append("120103,120226,").append(getTarDisCode()).append("110041,120227,120212,").append(timeStr).append("120213,").append(pspeedStr);
				}
			}else {
				ids.append("120103,120226,").append(getTarDisCode()).append("110041,120227,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
			}
		
		
		
//		StringBuffer ids = new StringBuffer();
//		//ids.append("120103,120224,120222,").append(getLeftDisCode()).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//		ids.append("120103,120226,").append(getTarDisCode()).append("110041,120227,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//		Log.v("wyvoice", "运动类型是距离,达成目标ids =" + ids);
////		lts.writeFileToSD("运动类型是距离,达成目标ids =" + ids, "voice");
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}
	/**
	 * 运动类型是距离,1，跑步至整公里数时；2，超过运动目标时；
	 */
	public static void playOverGoalVoice() {
		long utime = Variables.utime;
		String pspeedStr = "";
		StringBuffer ids = new StringBuffer();
		List<Integer> disStrs = voice.voiceOfDouble(Variables.distancePlayed/1000);
		String disStr = "";
		for (int i = 0; i < disStrs.size(); i++) {
			disStr += disStrs.get(i) + ",";
		}
		if(Variables.distance<3000){
			if (Variables.distance<2000) {
				int[] speeds = YaoPao01App.cal(Math.round(utime/1000.0));
//				YaoPao01App.lts.writeFileToSD("wyvoice: 第一公里time=" + Variables.utime + "毫秒    utime/1000=" + Variables.timePlayed + "秒 pertime="+(Variables.utime- Variables.timePlayed*1000), "uploadLocation");
				List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
			
				for (int i = 0; i < speed.size(); i++) {
					pspeedStr += speed.get(i) + ",";
				}
				int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
				List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
				String timeStr = "";
				for (int i = 0; i < time.size(); i++) {
					timeStr += time.get(i) + ",";
				}
				ids.append("120221,").append(disStr).append("110041,").append("120225,").append(getOverDisCode()).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
			}else{
				int[] speeds = YaoPao01App.cal(Math.round(utime/2000.0));
				List<Integer> speed = voice.voiceOfTime(speeds[0], speeds[1], speeds[2]);
				for (int i = 0; i < speed.size(); i++) {
					pspeedStr += speed.get(i) + ",";
				}
				int[] times = YaoPao01App.cal(Math.round(utime/1000.0));
				List<Integer> time = voice.voiceOfTime(times[0], times[1], times[2]);
				String timeStr = "";
				for (int i = 0; i < time.size(); i++) {
					timeStr += time.get(i) + ",";
				}
				ids.append("120221,").append(disStr).append("110041,").append("120225,").append(getOverDisCode()).append("110041,120212,").append(timeStr).append("120213,").append(pspeedStr);
			}
		}else {
			ids.append("120221,").append(disStr).append("110041,").append("120225,").append(getOverDisCode()).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
		}
		
//		StringBuffer ids = new StringBuffer();
//		
//		List<Integer> dis = voice.voiceOfDouble(Variables.distancePlayed/1000);
//		String disStr = "";
//		for (int i = 0; i < dis.size(); i++) {
//			disStr += dis.get(i) + ",";
//		}
//		ids.append("120221,").append(disStr).append("110041,").append("120225,").append(getOverDisCode()).append("110041,120212,").append(getTimeCode()).append("120213,").append(getPspeedCode());
//		Log.v("wyvoice", "运动类型是距离,1，跑步至整公里数时；2，超过运动目标时ids =" + ids);
		PlayVoice.StartPlayVoice(ids.toString(), instance);
	}
	
	// 获取5分钟的整数倍时数据id
		public static String getPer5minCode() {
			int[] times = YaoPao01App.cal(Variables.utime/1000);
			List<Integer> time =new ArrayList<Integer>();
			if (times[2]==0) {
				time = voice.voiceOfTime(times[0],times[1]);
			}else {
				 time = voice.voiceOfTime(times[0],times[1],times[2]);
			}
//			List<Integer> time = voice.voiceOfTime(times[0],times[1],times[2]);
			StringBuffer timeStr = new StringBuffer();
			for (int i = 0; i < time.size(); i++) {
				timeStr.append(time.get(i) + ",");
			}
			return timeStr.toString();
		}
		// 获取剩余时间数据id
		public static String getLefTimeCode(boolean ishalf,boolean isTarOdd) {
			int[] times =null;
//			if (ishalf) {
//				 times = YaoPao01App.cal(Variables.runtarTime*60/2);
//			}else {
//				 times = YaoPao01App.cal(Variables.runtarTime*60-Variables.utime/1000);
//			}
			List<Integer> time =new ArrayList<Integer>();
			if (ishalf) {
				times = YaoPao01App.cal(Variables.runtarTime*30);
				if(isTarOdd){
					time = voice.voiceOfTime(times[0],times[1],times[2]);
				}else{
					time = voice.voiceOfTime(times[0],times[1]);
				}
			}else {
				times = YaoPao01App.cal(Variables.runtarTime*60-Variables.timePlayed);
				time = voice.voiceOfTime(times[0],times[1]);
			}
		
			
				
			StringBuffer timeStr = new StringBuffer();
			for (int i = 0; i < time.size(); i++) {
				timeStr.append(time.get(i) + ",");
			}
			return timeStr.toString();
		}
		// 获取超过目标时间数据id
		public static String getOverTimeCode() {
//			int[] times = YaoPao01App.cal(Variables.utime/1000-Variables.runtarTime*60);
			int[] times = YaoPao01App.cal(Variables.timePlayed-Variables.runtarTime*60);
			List<Integer> time =new ArrayList<Integer>();
				time = voice.voiceOfTime(times[0],times[1]);
			StringBuffer timeStr = new StringBuffer();
			for (int i = 0; i < time.size(); i++) {
				timeStr.append(time.get(i) + ",");
			}
			return timeStr.toString();
		}
		// 获取目标时间数据id
		public static String getGoalTimeCode() {
			int[] times = YaoPao01App.cal(Variables.runtarTime*60);
			List<Integer> time =new ArrayList<Integer>();
//			if (times[2]==0) {
				time = voice.voiceOfTime(times[0],times[1]);
//			}else {
//				 time = voice.voiceOfTime(times[0],times[1],times[2]);
//			}
//			List<Integer> time = voice.voiceOfTime(times[0],times[1],times[2]);
			StringBuffer timeStr = new StringBuffer();
			for (int i = 0; i < time.size(); i++) {
				timeStr.append(time.get(i) + ",");
			}
			return timeStr.toString();
		}
		/**
		 * 时间运动目标,跑步至运动目标的一半时
		 */
		public static void playHalfTimeVoice() {
			StringBuffer ids = new StringBuffer();
			//真棒！你已完成目标的一半，还剩XX分钟，运动距离XX.XX公里，平均速度每公里XX分XX秒。
			ids.append("120101,120223,120222,").append(getLefTimeCode(true,((Variables.runtarTime%2))!=0)).append("120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			Log.v("wyvoice", "时间运动目标,跑步至运动目标的一半时ids =" + ids);
//			lts.writeFileToSD("时间运动目标,跑步至运动目标的一半时ids = " + ids, "voice");
			PlayVoice.StartPlayVoice(ids.toString(), instance);
		}
		/**
		 * 时间运动目标,跑步至5分钟的整数倍时；2，距离目标小于等于10分钟时；
		 */
		public static void playLess10minVoice() {
			StringBuffer ids = new StringBuffer();
			//加油！你就快达成目标了，还剩XX分钟，运动距离XX.XX公里，平均速度每公里XX分XX秒。
			ids.append("120102,120224,120222,").append(getLefTimeCode(false,false)).append("120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			Log.v("wyvoice", "时间运动目标，小于等于10分钟时上报ids =" + ids);
//			lts.writeFileToSD("时间运动目标，小于等于10分钟时上报ids = " + ids, "voice");
			PlayVoice.StartPlayVoice(ids.toString(), instance);
		}
		/**
		 *  时间运动目标,达成目标
		 */
		public static void playAchieveTimeGoalVoice() {
			StringBuffer ids = new StringBuffer();
			//恭喜你！你已达成了XX分钟的目标，运动距离XX.XX公里，平均速度每公里XX分XX秒。
			ids.append("120103,120226,").append(getGoalTimeCode()).append("120227,120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			Log.v("wyvoice", "时间运动目标,达成目标ids =" + ids);
//			lts.writeFileToSD("时间运动目标,达成目标ids = " + ids, "voice");
			PlayVoice.StartPlayVoice(ids.toString(), instance);
		}
		/**
		 *  时间运动目标,1，跑步至5分钟的整数倍时；2，超过运动目标时
		 */
		public static void playOverTimeGoalVoice() {
			StringBuffer ids = new StringBuffer();
			//你已完成XX分钟，超过你的目标XX分钟，运动距离XX.XX公里，平均速度每公里XX分XX秒。
//			ids.append("120221,").append(getTimeCode()).append("120225,").append(getOverTimeCode()).append("120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			ids.append("120221,").append(getTimePer5Code()).append("120225,").append(getOverTimeCode()).append("120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			Log.v("wyvoice", "时间运动目标,1，跑步至5分钟的整数倍时；2，超过运动目标时ids =" + ids);
//			lts.writeFileToSD("时间运动目标,1，跑步至5分钟的整数倍时；2，超过运动目标时ids = " + ids, "voice");
			PlayVoice.StartPlayVoice(ids.toString(), instance);
		}
		
		/**
		 * 跑步至5分钟的整数倍时；
		 */
		public static void playPer5minVoice() {
			StringBuffer ids = new StringBuffer();
			//你已完成XX分钟，运动距离XX.XX公里，平均速度每公里XX分XX秒。
			ids.append("120221,").append(getTimePer5Code()).append("120211,").append(getDisCode()).append("110041,120213,").append(getPspeedCode());
			Log.v("wyvoice", "跑步至5分钟的整数倍时ids =" + ids);
//			lts.writeFileToSD("跑步至5分钟的整数倍时ids = " + ids, "voice");
			PlayVoice.StartPlayVoice(ids.toString(), instance);
		}
		
		public static void palyOpenGps(){
			PlayVoice.StartPlayVoice(openGps, instance);
		}
		public static void palyWeekGps(){
			PlayVoice.StartPlayVoice(weekGps, instance);
		}
		public static void palyOpenNetwork(){
			PlayVoice.StartPlayVoice(openNetwork, instance);
		}
		public static void isPlayVoice(){
			if (Variables.switchVoice==1) {
				
			}
		}
		
	    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {  
	           
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	        	if ("unregister".equals(intent.getExtras().getString("data"))) {
	        		  if (Variables.sportStatus!=0) {
							if(locationManager!=null){
								locationManager.removeUpdates(locationlisten);
								locationlisten=null;
								locationManager=null;
							}
						}
	        		
				}else if("register".equals(intent.getExtras().getString("data"))){
					 if (Variables.sportStatus!=0) {
							initGPS();
						}
				}
	        }  
	    };  
}
