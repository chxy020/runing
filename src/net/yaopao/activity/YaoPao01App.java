package net.yaopao.activity;

import java.util.Calendar;
import java.util.Date;

import net.yaopao.assist.LogtoSD;
import net.yaopao.assist.Variables;
import net.yaopao.db.DBManager;
import net.yaopao.listener.NetworkStateReceiver;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

	@Override
	public void onCreate() {

		instance = this;
		db = new DBManager(this);
		Log.v("wy", "app");
		Variables.pid = getImeiCode();
		Variables.ua = this.getOptVer() + ",a_2.2.5,a_2.2.5,a_2.2.5,a_2.2.5";
		Log.v("wy", "pid=" + Variables.pid + " ua=" + Variables.ua);
		getPreference();
		initGPS();
	};

	private void initGPS() {
		Log.v("wy", "initGPS");
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationlisten = new LocationListener() {

			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.v("wy", "gps StatusChanged");
				  if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE){
					  if (SportRecordActivity.gpsV != null) {
						  Variables.gpsStatus = 0;
							SportRecordActivity.gpsV.setBackgroundResource(R.drawable.gps_1);
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
						SportRecordActivity.gpsV.setBackgroundResource(R.drawable.gps_1);
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
						SportRecordActivity.gpsV.setBackgroundResource(R.drawable.gps_1);
					}
				}
			}
		};

		// 注册监听器 locationListener
		// 第 2 、 3个参数可以控制接收GPS消息的频度以节省电力。第 2个参数为毫秒， 表示调用 listener的周期，第 3个参数为米
		// ,表示位置移动指定距离后就调用 listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, locationlisten);

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

	public static boolean isGpsAvailable() {
		/* if (Variables.gpsStatus!=2) {
		 if (Variables.gpsStatus==1) {
		 return true;
		 }else {
		 Toast.makeText(instance, "当前位置GPS信号较弱", Toast.LENGTH_LONG).show();
		 return false;
		 }
		 }else {
		 Toast.makeText(instance, "请开启GPS", Toast.LENGTH_LONG).show();
		 return false;
		 }*/
    return true;
	}

	public static int[] cal(long second) {
		int h = 0;
		int m = 0;
		int s = 0;
		int temp = (int)(second % 3600);
		if (second > 3600) {
			h =(int) (second / 3600);
			if (temp != 0) {
				if (temp > 60) {
					m = (int) (temp / 60);
					if (temp % 60 != 0) {
						s = (int)(temp % 60);
					}
				} else {
					s = temp;
				}
			}
		} else {
			m =(int) (second / 60);
			if (second % 60 != 0) {
				s = (int)(second % 60);
			}
		}

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
}
