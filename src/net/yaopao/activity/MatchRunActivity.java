package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.match.track.TrackData;

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
	private Timer timer = new Timer();

	private TrackData trackData;
	private LonLatEncryption lonLatEncryption;
	public static List<GpsPoint> points;
	public double distance = 0;
	public static int totalTime = 0;

	// 测试数据
	// public static double lon = 116.403694;
	// public static double lat = 39.906744;
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
		lonLatEncryption = new LonLatEncryption();
		trackData = new TrackData();
		trackData.read("TrackData.properties");
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
		int[] time = YaoPao01App.cal(totalTime);
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

		int[] speed = YaoPao01App.cal((int) ((1000 / distance) * totalTime));
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
			boolean isInTrack = trackData.isInTheTracks(
					lonLatEncryption.encrypt(point).lon,
					lonLatEncryption.encrypt(point).lat);
			// Log.v("wytrack",
			// "isInTrack ="+isInTrack+" claimedLength="+trackData.claimedLength+" name"+trackData.name+" pgTracks"+trackData.pgTracks);
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
