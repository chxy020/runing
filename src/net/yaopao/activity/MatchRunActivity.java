package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MatchRunActivity extends Activity implements OnTouchListener {
	private ImageView mapV;
	private ImageView teamV;
	private ImageView batonV;
	private ImageView d1V;
	private ImageView d2V;
	private ImageView d3V;
	private ImageView d4V;

	private ImageView t1V;
	private ImageView t2V;
	private ImageView t3V;
	private ImageView t4V;
	private ImageView t5V;
	private ImageView t6V;

	private ImageView s1V;
	private ImageView s2V;
	private ImageView s3V;
	private ImageView s4V;
	Timer timer = new Timer();
	public static List<GpsPoint> points;
	public double distance = 0;
	public String speed = "";
	public static int totalTime = 0;

	public static double lon = 116.403694;
	public static double lat = 39.906744;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_run);
		mapV = (ImageView) findViewById(R.id.match_map);
		teamV = (ImageView) findViewById(R.id.match_team);
		batonV = (ImageView) findViewById(R.id.match_run_baton);
		mapV.setOnTouchListener(this);
		teamV.setOnTouchListener(this);
		batonV.setOnTouchListener(this);
		
		d1V = (ImageView) findViewById(R.id.match_recoding_dis1);
		d2V = (ImageView) findViewById(R.id.match_recoding_dis2);
		d3V = (ImageView) findViewById(R.id.match_recoding_dis3);
		d4V = (ImageView) findViewById(R.id.match_recoding_dis4);

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
		
		points = new ArrayList<GpsPoint>();
		timer.schedule(task, 0, 1000);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					totalTime++;
					updateUI();
					if (pushOnePoint()) {
						// 这里计算数据，更新ui
						// updateUI();
						Log.v("wyrun", "points =" + points);
					}
				}

			});
		}
	};

	private void updateUI() {
		int[] time = cal(totalTime);
		int d1 = (int) distance / 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;

		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;

		int[] speed = cal((int) ((1000 / distance) * totalTime));
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;

		update(d1, d1V);
		update(d2, d2V);
		update(d3, d3V);
		update(d4, d4V);

		update(t1, t1V);
		update(t2, t2V);
		update(t3, t3V);
		update(t4, t4V);
		update(t5, t5V);
		update(t6, t6V);

		update(s1, s1V);
		update(s2, s2V);
		update(s3, s3V);
		update(s4, s4V);

		// Log.v("wyrun", "dis = "+distance);
		// Log.v("wyrun", "d1 = "+d1);
		// Log.v("wyrun", "d2 = "+d2);
		// Log.v("wyrun", "d3 = "+d3);
		// Log.v("wyrun", "d4 = "+d4);
		Log.v("wyrun", "dis = " + distance);
		Log.v("wyrun", "time = " + totalTime);
		YaoPao01App.lts.writeFileToSD("time = " + totalTime, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time1 = " + t1, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time2 = " + t2, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time3 = " + t3, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time4 = " + t4, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time5 = " + t5, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("time6 = " + t6, "uploadLocation");
		YaoPao01App.lts.writeFileToSD("====================================",
				"uploadLocation");
		// Log.v("wyrun", "s0 = "+s0);
		//
		// Log.v("wyrun", "s1 = "+s1);
		// Log.v("wyrun", "s2 = "+s2);
		// Log.v("wyrun", "s3 = "+s3);
		// Log.v("wyrun", "s4 = "+s4);
	}

	public int[] cal(int second) {
		int h = 0;
		int m = 0;
		int s = 0;
		int temp = second % 3600;
		if (second > 3600) {
			h = second / 3600;
			if (temp != 0) {
				if (temp > 60) {
					m = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else {
			m = second / 60;
			if (second % 60 != 0) {
				s = second % 60;
			}
		}

		return new int[] { h, m, s };
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_map:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mapV.setBackgroundResource(R.drawable.button_map_h);
				break;
			case MotionEvent.ACTION_UP:
				mapV.setBackgroundResource(R.drawable.button_map);
				Intent intent = new Intent(MatchRunActivity.this,
						MatchMapActivity.class);
				startActivity(intent);
				// MatchRunActivity.this.finish();

				break;
			}
			break;
		case R.id.match_team:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchRunActivity.this,
						MatchScoreListActivity.class);
				startActivity(intent);
				// MatchRunActivity.this.finish();
				
				break;
			}
			break;
		case R.id.match_run_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchRunActivity.this,
						MatchRelayActivity.class);
				startActivity(intent);
				// MatchRunActivity.this.finish();
				
				break;
			}
			break;
		}
		return true;
	}

	public GpsPoint getOnePoint() {
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

	public boolean pushOnePoint() {

		double meter = 0;
		GpsPoint last = null;
		GpsPoint point = null;
		// ceshi
		// lat = lat + 0.00001;
		// point = new GpsPoint(lon, lat);
		// if (points.size() == 0) {
		// points.add(point);
		// return false;
		// }
		// meter = getDistanceFrom2ponit(points.get(points.size() - 1), point);
		// distance += meter;
		// points.add(point);
		// ceshi

		if (getOnePoint() != null) {
			point = getOnePoint();
			if (points.size() == 0) {
				points.add(point);
				last = point;
				return false;
			}
			
			meter = getDistanceFrom2ponit(points.get(points.size() - 1), point);
			if (meter == 0) {
				return false;
			} else {
				last = points.get(points.size() - 1);
				meter = getDistanceFrom2ponit(last, point);
				distance += meter;
				points.add(point);
				return true;
			}
		}
		return false;
	}

	private double getDistanceFrom2ponit(GpsPoint before, GpsPoint now) {
		return AMapUtils.calculateLineDistance(new LatLng(now.lat, now.lon),
				new LatLng(before.lat, before.lon));

	}

	protected void update(int i, ImageView view) {
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

}
