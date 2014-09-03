package net.yaopao.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;
import net.yaopao.widget.SliderRelativeLayout;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

public class SportRecordActivity extends Activity implements OnTouchListener {
	public static List<GpsPoint> points;
	public static List<Integer> pointsIndex;
	public static SimpleDateFormat formatterM;
	public static SimpleDateFormat formatterS;
	public static ImageView sliderIconV;
	public static TextView sliderTextV;
	public static TextView doneV;
	public static TextView resumeV;
	public static ImageView gpsV;
	private static ImageView d1v;
	private static ImageView d2v;
	private static ImageView d3v;
	private static ImageView d4v;
	private static ImageView mapV;
	private SliderRelativeLayout slider;
	private static ProgressBar progressHorizontal;
	private static ImageView t1V;
	private static ImageView t2V;
	private static ImageView t3V;
	private static ImageView t4V;
	private static ImageView t5V;
	private static ImageView t6V;

	private static ImageView s1V;
	private static ImageView s2V;
	private static ImageView s3V;
	private static ImageView s4V;
	// 记录ProgressBar的完成进度
	private static int status = 0;
	private static int target = 0;
	private static int speedPerKm=0;
	private static double disPerKm=0;
	private static int timePerKm=0;
	

	// 测试代码
	// public static double lon = 116.402894;
	// public static double lat = 39.923433;

	// 以上测试代码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_recording);
		doneV = (TextView) findViewById(R.id.slider_done);
		resumeV = (TextView) findViewById(R.id.slider_resume);
		mapV = (ImageView) findViewById(R.id.sport_map);
		sliderIconV = (ImageView) findViewById(R.id.slider_icon);
		slider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
		sliderTextV = (TextView) findViewById(R.id.slider_text);
		gpsV = (ImageView) findViewById(R.id.sport_gps_status);
		d1v = (ImageView) this.findViewById(R.id.match_recoding_dis1);
		d2v = (ImageView) this.findViewById(R.id.match_recoding_dis2);
		d3v = (ImageView) this.findViewById(R.id.match_recoding_dis3);
		d4v = (ImageView) this.findViewById(R.id.match_recoding_dis4);
		t1V = (ImageView) findViewById(R.id.match_recoding_time_h1);
		t2V = (ImageView) findViewById(R.id.match_recoding_time_h2);
		t3V = (ImageView) findViewById(R.id.match_recoding_time_m1);
		t4V = (ImageView) findViewById(R.id.match_recoding_time_m2);
		t5V = (ImageView) findViewById(R.id.match_recoding_time_s1);
		t6V = (ImageView) findViewById(R.id.match_recoding_time_s2);

		s1V = (ImageView) findViewById(R.id.match_recoding_speed1);
		s2V = (ImageView) findViewById(R.id.match_recoding_speed2);
		s3V = (ImageView) findViewById(R.id.match_recoding_speed3);
		s4V = (ImageView) findViewById(R.id.match_recoding_speed4);
		progressHorizontal = (ProgressBar) findViewById(R.id.recoding_process);
		if (Variables.runtar == 1) {
			progressHorizontal.setMax(Variables.runtarDis * 100);
			target = Variables.runtarDis * 100;// 由于计算的时候距离是double
		} else if (Variables.runtar == 2) {
			progressHorizontal.setMax(Variables.runtarTime * 60);
			target = Variables.runtarTime * 60;
		}
		Log.v("wysport", "target  = " + target);
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
		// timeV.setOnTouchListener(this);
		// speedV.setOnTouchListener(this);
		mapV.setOnTouchListener(this);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);

		points = new ArrayList<GpsPoint>();
		points.add(new GpsPoint(116.403694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.404694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.405694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.406694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.407694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.408694, 39.906744, 1409643285776L));
		points.add(new GpsPoint(116.409694, 39.906744, 1409643285776L));
//		points.add(new GpsPoint(116.403694, 39.906824, 1409643295776L));
//		points.add(new GpsPoint(116.404694, 39.906934, 1409643305776L));
//		points.add(new GpsPoint(116.403694, 39.907044, 1409643375776L));
//		points.add(new GpsPoint(116.403794, 39.907154, 1409643485776L));
//		points.add(new GpsPoint(116.403894, 39.907264, 140964435776L));
//		points.add(new GpsPoint(116.403994, 39.907374, 1409645285776L));
//		points.add(new GpsPoint(116.405694, 39.907484, 1409646285776L));
//		points.add(new GpsPoint(116.406694, 39.908584, 1409646105776L));
//		points.add(new GpsPoint(116.407694, 39.908684, 1409646105776L));
		slider.setMainHandler(slipHandler);
		pointsIndex = new ArrayList<Integer>();
		pointsIndex.add(0);
		startTimer();
		startRecordGps();
		IntentFilter filter = new IntentFilter(MapActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopTimer();
		unregisterReceiver(broadcastReceiver);
		// Log.v("wydb", "points=" + SportRecordActivity.points);
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
							YaoPao01App.calDisPoints();
							Intent intent = new Intent(
									SportRecordActivity.this,
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

	// 返回true，更新ui，false不更新ui
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
			last = points.get(points.size() - 1);
			// meter = getDistanceFrom2ponit(points.get(points.size() - 1),
			// point);
			meter = getDistanceFrom2ponit(last, point);
			// 判断，如果距离小于5，并且两点状态相同，抛掉当前点，如果状态不同，记录，算距离，更新ui
			if (meter < 5) {
				if (last.status == point.status) {
					last.time = point.time;
					return false;
				} else {
					points.add(point);
					return false;
				}

			} else {
				if (point.status == 0) {
					if (last.status == 0) {
						// meter = getDistanceFrom2ponit(last, point);
						Variables.distance += meter;
						disPerKm += meter;
						timePerKm += Variables.utime;
				        if(disPerKm > 1000){
				            int minute = timePerKm/60;
				            Variables.points += YaoPao01App.calPspeedPoints(minute);
				            disPerKm = 0;
				            timePerKm = 0;
				        }

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
			// if (meter < 5) {
			// if (last.status==point.status) {
			// return false;
			// }
			//
			// } else {
			// if (point.status == 0) {
			// // last = points.get(points.size() - 1);
			// if (last.status == 0) {
			// meter = getDistanceFrom2ponit(last, point);
			// Variables.distance += meter;
			// points.add(point);
			// return true;
			// } else {
			// points.add(point);
			// return false;
			// }
			//
			// } else {
			// points.add(point);
			// return false;
			// }
			// }

		}
		return false;
	}

	final static Handler handler = new Handler();
	static Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// 测试代码
			// lat = lat + 0.001;
			// double meter = 0;
			// GpsPoint point = new GpsPoint(lon, lat, Variables.sportStatus);
			// if (points.size() == 0) {
			// points.add(point);
			// } else {
			// meter = getDistanceFrom2ponit(points.get(points.size() - 1),
			// point);
			// points.add(point);
			// }
			// Variables.distance += meter;
			/*
			 * Variables.distance += 55; if (Variables.runtar != 0) { if (status
			 * < target) { if (Variables.runtar == 1) { status = (int)
			 * (Variables.distance * 100); } else if (Variables.runtar == 2) {
			 * status = Variables.utime; } // 发送消息到Handler Message m = new
			 * Message(); m.what = 0x111; // 发送消息 procesHandler.sendMessage(m);
			 * } }
			 */
			// 以上测试代码

			updateUI();

			if (pushOnePoint()) {
				updateUI();
				if (Variables.runtar != 0) {
					if (status < target) {
						if (Variables.runtar == 1) {
							status = (int) (Variables.distance * 100);
						} else if (Variables.runtar == 2) {
							status = Variables.utime;
						}
						// 发送消息到Handler
						Message m = new Message();
						m.what = 0x111;
						// 发送消息
						procesHandler.sendMessage(m);
					}
				}
			}
			handler.postDelayed(this, 3000);
		}

	};

	final static Handler timer = new Handler();
	static Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			Variables.utime += 1;
			int[] time = YaoPao01App.cal(Variables.utime);
			int t1 = time[0] / 10;
			int t2 = time[0] % 10;
			int t3 = time[1] / 10;
			int t4 = time[1] % 10;
			int t5 = time[2] / 10;
			int t6 = time[2] % 10;
			update(t1, t1V);
			update(t2, t2V);
			update(t3, t3V);
			update(t4, t4V);
			update(t5, t5V);
			update(t6, t6V);
			timer.postDelayed(this, 1000);
		}

	};

	public static void startTimer() {
		timer.postDelayed(timerTask, 1000);
		Variables.sportStatus = 0;
		if (points.size() > 0) {
			pointsIndex.add(points.size() - 1);
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
		Log.v("wygps", "distance =" + Variables.distance);
		int d1 = (int) Variables.distance / 10000;
		int d2 = (int) (Variables.distance % 10000) / 1000;
		int d3 = (int) (Variables.distance % 1000) / 100;
		int d4 = (int) (Variables.distance % 100) / 10;
		if (d1 > 0) {
			d1v.setVisibility(View.VISIBLE);
		}
		update(d1, d1v);
		update(d2, d2v);
		update(d3, d3v);
		update(d4, d4v);

		int[] speed = YaoPao01App
				.cal((int) ((1000 / Variables.distance) * Variables.utime));
		Variables.pspeed = (int) ((1000 / Variables.distance) * Variables.utime);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		update(s1, s1V);
		update(s2, s2V);
		update(s3, s3V);
		update(s4, s4V);

	}

	protected static void update(int i, ImageView view) {
		if (i > 9) {
			i = i % 10;
		}
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.w_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.w_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.w_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.w_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.w_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.w_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.w_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.w_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.w_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.w_9);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// DialogTool.quit(MainActivity.this);
		}
		return false;
	}

	// 控制进度条
	final static Handler procesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 表明消息是由该程序发送的。
			if (msg.what == 0x111) {
				progressHorizontal.setProgress(status);
			}
		}
	};
	// 在地图页面关闭此页面
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("close".equals(intent.getExtras().getString("data"))) {
				SportRecordActivity.this.finish();
			}
		}
	};
}
