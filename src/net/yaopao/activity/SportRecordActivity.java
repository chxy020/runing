package net.yaopao.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;
import net.yaopao.widget.SliderRelativeLayout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

public class SportRecordActivity extends Activity implements OnTouchListener {
	public static List<GpsPoint> points;
	public static List<Integer> pointsIndex;
	public static SimpleDateFormat formatterM;
	public static SimpleDateFormat formatterS;
	public static TextView timeV;
	public static TextView sliderIconV;
	public static TextView sliderTextV;
	public static TextView doneV;
	public static TextView resumeV;
	
	private ImageView mapV;
	private SliderRelativeLayout slider;
	private static TextView distanceV;
	private static TextView speedV;
	
	private DecimalFormat df;
//	private double distance = 0;
	//测试代码
	// public static double lon = 116.402894;
	// public double lat = 39.923433;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_recording);
		distanceV = (TextView) findViewById(R.id.sport_recoding_mileage);
		timeV = (TextView) findViewById(R.id.sport_recoding_time);
		speedV = (TextView) findViewById(R.id.sport_recoding_speed);
		doneV = (TextView) findViewById(R.id.slider_done);
		resumeV = (TextView) findViewById(R.id.slider_resume);
		mapV = (ImageView) findViewById(R.id.sport_map);
		sliderIconV = (TextView) findViewById(R.id.slider_icon);
		slider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
		sliderTextV = (TextView) findViewById(R.id.slider_text);
		if (Variables.sportStatus == 0) {
			sliderIconV.setVisibility(View.VISIBLE);
			sliderTextV.setVisibility(View.VISIBLE);
			sliderTextV.setText("滑动暂停");
			doneV.setVisibility(View.GONE);
			resumeV.setVisibility(View.GONE);
		} else {
			doneV.setVisibility(View.VISIBLE);
			resumeV.setVisibility(View.VISIBLE);
			sliderIconV.setVisibility(View.GONE);
			sliderTextV.setVisibility(View.GONE);
		}
		distanceV.setOnTouchListener(this);
		timeV.setOnTouchListener(this);
		speedV.setOnTouchListener(this);
		mapV.setOnTouchListener(this);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);
		// switchV.setOnTouchListener(this);

		points = new ArrayList<GpsPoint>();
		//
		// points.add(new GpsPoint(116.403694,39.906744));
		// points.add(new GpsPoint(116.413755,39.91516));
		// points.add(new GpsPoint(116.413755,39.91616));
		// points.add(new GpsPoint(116.413755,39.91716));
		// points.add(new GpsPoint(116.413755,39.91816));
		// points.add(new GpsPoint(116.413755,39.91916));

		formatterM = new SimpleDateFormat("mm");//
		formatterS = new SimpleDateFormat("ss");//
		df = new java.text.DecimalFormat("#.##");
		slider.setMainHandler(slipHandler);
		pointsIndex = new ArrayList<Integer>();
		pointsIndex.add(0);
		startTimer();
		startRecordGps();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopTimer();
		Log.v("wydb", "points=" + SportRecordActivity.points);
		// points.clear();
		// pointsIndex.clear();
		// Log.v("wygps", "des pointsIndex=" + pointsIndex);
		
	}

	@Override
	protected void onResume() {
		if (Variables.sportStatus == 0) {
			sliderIconV.setVisibility(View.VISIBLE);
			sliderTextV.setVisibility(View.VISIBLE);
			sliderTextV.setText("滑动暂停");
			doneV.setVisibility(View.GONE);
			resumeV.setVisibility(View.GONE);
		} else {
			doneV.setVisibility(View.VISIBLE);
			resumeV.setVisibility(View.VISIBLE);
			sliderIconV.setVisibility(View.GONE);
			sliderTextV.setVisibility(View.GONE);
		}
		super.onResume();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.sport_time_layout:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			break;
		case R.id.sport_map:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(SportRecordActivity.this,
						MapActivity.class);
				SportRecordActivity.this.startActivity(intent);
				break;
			}
			break;
		case R.id.slider_done:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 0) {
							Intent intent = new Intent(SportRecordActivity.this,
									SportSaveActivity.class);
							stopRecordGps();
							SportRecordActivity.this.startActivity(intent);
							SportRecordActivity.this.finish();
						}
						super.handleMessage(msg);
					}
				};
				DialogTool.doneSport(SportRecordActivity.this, handler);
				
				break;
			}
			break;
		case R.id.slider_resume:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				startTimer();
				sliderIconV.setVisibility(View.VISIBLE);
				sliderTextV.setVisibility(View.VISIBLE);
				sliderTextV.setText("滑动暂停");
				doneV.setVisibility(View.GONE);
				resumeV.setVisibility(View.GONE);
				break;
			}
			break;
		}
		return true;
	}

	Handler slipHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (Variables.sportStatus == 0) {
					stopTimer();
					doneV.setVisibility(View.VISIBLE);
					resumeV.setVisibility(View.VISIBLE);
					sliderIconV.setVisibility(View.GONE);
					sliderTextV.setVisibility(View.GONE);
				}
			}
		};
	};

	public static GpsPoint getOnePoint() {
		GpsPoint point = null;
		// Log.v("wy", "YaoPao01App.loc: " + YaoPao01App.loc);
		if (YaoPao01App.loc != null) {
			point = new GpsPoint();
			point.lon = YaoPao01App.loc.getLongitude();
			point.lat = YaoPao01App.loc.getLatitude();
			point.time = YaoPao01App.loc.getTime();
			point.altitude = YaoPao01App.loc.getAltitude();
			point.course = YaoPao01App.loc.getBearing();
			point.speed = YaoPao01App.loc.getSpeed();
			point.status = Variables.sportStatus;
		}
		return point;

	}

	public static boolean pushOnePoint() {

		double meter = 0;
		GpsPoint last = null;
		GpsPoint point = getOnePoint();
		if (point != null) {
			if (points.size() == 0) {
				points.add(point);
				last = point;
				return false;
			}

			meter = getDistanceFrom2ponit(points.get(points.size() - 1), point);
			if (meter == 0) {
				return false;
			} else {
				if (point.status == 0) {
					last = points.get(points.size() - 1);
					if (last.status == 0) {
						meter = getDistanceFrom2ponit(last, point);
						Variables.distance += meter;
						points.add(point);
						return true;
					} else {
						points.add(point);
						return false;
					}

				} else {
					points.add(point);
					return false;
				}
			}
		}
		return false;
	}

	final static Handler handler = new Handler();
	static Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// 测试代码
			// lat=lat+0.001;
			// if (points.size()==0) {
			// points.add(new GpsPoint(lon,lat,Variables.sportStatus));
			// }else {
			// points.add(new GpsPoint(lon,lat,Variables.sportStatus));
			// }

			if (pushOnePoint()) {
				//distanceV.setText(Variables.distance);
				//speedV.setText(Variables.pspeed);
				updateUI();
			}
			handler.postDelayed(this, 3000);
		}

	};

	final static Handler timer = new Handler();
	static Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			Variables.utime += 1;
			timeV.setText(formatterM.format(Variables.utime * 1000) + "'"
					+ formatterS.format(Variables.utime * 1000) + "\"");
			timer.postDelayed(this, 1000);
		}

	};

	public static void startTimer() {
		timer.postDelayed(timerTask, 1000);
		Variables.sportStatus = 0;
		if (points.size() > 0) {
			pointsIndex.add(points.size() - 1);
			// Log.v("wygps", "start pointsIndex=" + pointsIndex);
		}

	}

	public static void stopTimer() {
		timer.removeCallbacks(timerTask);
		Variables.sportStatus = 1;
		if (points.size() > 0) {
			pointsIndex.add(points.size() - 1);
		}

	}

	public static void startRecordGps() {
		handler.postDelayed(runnable, 1000);
	}

	//
	public static void stopRecordGps() {
		handler.removeCallbacks(runnable);
	}

	private static double getDistanceFrom2ponit(GpsPoint before, GpsPoint now) {
		return AMapUtils.calculateLineDistance(new LatLng(now.lat, now.lon),
				new LatLng(before.lat, before.lon));

	}
	
	private static void updateUI() {
		
		int d1 = (int) Variables.distance / 10000;
		int d2 = (int) (Variables.distance % 10000) / 1000;
		int d3 = (int) (Variables.distance % 1000) / 100;
		int d4 = (int) (Variables.distance % 100) / 10;
		
		int[] time = YaoPao01App.cal(Variables.utime);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;

		int[] speed = YaoPao01App.cal((int) ((1000 / Variables.distance) * Variables.utime));
		Variables.pspeed =(int) ((1000 / Variables.distance) * Variables.utime);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		distanceV.setText(d1+""+d2+"."+d3+""+d4);
		speedV.setText( s1+""+s2+"'"+s3+""+s4+"\"");
		Log.v("wy", "Variables.utime="+Variables.utime);
		Log.v("wy", "distance1="+Variables.distance);
		Log.v("wy", "distance2="+d1+""+d2+"."+d3+""+d4);
		Log.v("wy", "speed1="+(1000 / Variables.distance) * Variables.utime);
		Log.v("wy", "Variables.pspeed="+Variables.pspeed);
		Log.v("wy", "speed3="+s1+""+s2+"'"+s3+""+s4+"\"");
		
		
//		update(d1, d1V);
//		update(d2, d2V);
//		update(d3, d3V);
//		update(d4, d4V);
//
//		update(t1, t1V);
//		update(t2, t2V);
//		update(t3, t3V);
//		update(t4, t4V);
//		update(t5, t5V);
//		update(t6, t6V);
//
//		update(s1, s1V);
//		update(s2, s2V);
//		update(s3, s3V);
//		update(s4, s4V);

	}
}
