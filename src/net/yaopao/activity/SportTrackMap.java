package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import net.yaopao.widget.SliderRelativeLayout;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.amap.api.a.am;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;

/**
 */
public class SportTrackMap extends Activity implements  OnTouchListener {
	private MapView mapView;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
	public GpsPoint curPopPoint;
	public GpsPoint lastPopPoint;

	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private SimpleDateFormat sdf4;
	private DecimalFormat df;
	private String title = "";
	private TextView timeV;
	private TextView pspeedV;
	private TextView avgspeedV;
	private TextView dateV;
	private TextView titleV;
	private TextView disV;
	private ImageView typeV;
	private TextView backV;
	int oneDis=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list_one_full);
		mapView = (MapView) findViewById(R.id.full_map);
		mapView.onCreate(savedInstanceState);
		lonLatEncryption = new LonLatEncryption();
		backV = (TextView) findViewById(R.id.full_back);
		titleV = (TextView) findViewById(R.id.full_title);
		timeV = (TextView) findViewById(R.id.full_time);
		pspeedV = (TextView) findViewById(R.id.full_pspeed);
		avgspeedV = (TextView) findViewById(R.id.full_avgspeed);
		titleV = (TextView) findViewById(R.id.full_title);
		dateV = (TextView) findViewById(R.id.full_date);
		disV = (TextView) findViewById(R.id.full_dis);
		typeV = (ImageView) findViewById(R.id.full_type);
		backV.setOnTouchListener(this);
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
		sdf4 = new SimpleDateFormat("yyyy");
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		initLayout();
		initMap();
	}

	private void initLayout() {
		// TODO Auto-generated method stub
		
	}

	private void initMap() {
		lonLatEncryption = new LonLatEncryption();
		Intent intent = getIntent();
		int id = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(id);
		initSportData(oneSport.getDistance(), oneSport.getRunty(),
				oneSport.getMind(), oneSport.getRunway(),
				oneSport.getRemarks(), oneSport.getUtime(),
				oneSport.getPspeed(), oneSport.getHspeed(),
				oneSport.getAddtime());
		List<GpsPoint> pointsArray = JSONArray.parseArray(oneSport.getRuntra(),
				GpsPoint.class);
		int pointCount = pointsArray.size();
		JSONArray indexArray = JSONArray.parseArray(oneSport.getStatusIndex());
		if (aMap == null) {
			aMap = mapView.getMap();
//			aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
//				@Override
//				public void onCameraChangeFinish(CameraPosition cameraPosition) {
//
//				}
//
//				@Override
//				public void onCameraChange(CameraPosition arg0) {
//
//				}
//			});
		}

		
		if (pointCount != 0) {
			GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));

			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					start.lat, start.lon), 16));
		} else {
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		if (pointCount == 0) {
			return;
		} else if (pointCount < 2) {
			lastDrawPoint = (GpsPoint) pointsArray.get(pointsArray.size() - 1);
			return;
		}

		if (indexArray.size() == 0) {
			aMap.addPolyline((new PolylineOptions()).addAll(
					initPoints(pointsArray)).color(Color.RED));
			lastDrawPoint = (GpsPoint) pointsArray.get(pointsArray.size() - 1);
			return;
		}
		indexArray.add(pointCount - 1);
		int linesCount = indexArray.size();
		int j = 0;
		int i = 0;
		int n = 0;
		GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));
		GpsPoint end = lonLatEncryption.encrypt(pointsArray.get(pointsArray.size()-1));
//		aMap.addMarker(new MarkerOptions().position(new LatLng(start.lat, start.lon))
//		.icon(BitmapDescriptorFactory.fromResource(R.drawable.pop)).anchor(0f, 1f));
		
//		aMap.addMarker(new MarkerOptions().position(new LatLng(end.lat, end.lon))
//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.pop)).anchor(1f, 1f));
		
		
		aMap.addMarker(new MarkerOptions().position(new LatLng(39.908156,116.397572))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.pop)).anchor(0.5f, 0.5f));

		for (j = 0; j < linesCount - 1; j++) {
			
			List<LatLng> oneLinePoints = new ArrayList<LatLng>();
			int startIndex = (Integer) indexArray.get(j);
			int endIndex = (Integer) indexArray.get(j + 1);

			if (endIndex - startIndex + 1 < 2)
				continue;
			for (i = startIndex, n = 0; i <= endIndex; i++, n++) {
				GpsPoint gpsPoint = (GpsPoint) pointsArray.get(i);
				oneLinePoints.add(new LatLng(
						lonLatEncryption.encrypt(gpsPoint).lat,
						lonLatEncryption.encrypt(gpsPoint).lon));
			}

			GpsPoint gpsPointEnd = (GpsPoint) pointsArray.get(oneLinePoints
					.size() - 1);
			if (gpsPointEnd.status == 0) {
				aMap.addPolyline((new PolylineOptions()).addAll(oneLinePoints)
						.color(Color.RED));
			} else {
				aMap.addPolyline((new PolylineOptions()).addAll(oneLinePoints)
						.color(Color.BLACK).setDottedLine(true));
			}
		}
		lastDrawPoint = (GpsPoint) pointsArray.get(pointsArray.size() - 1);
	}
	private void addPop() {
	}

	private void initSportData(double distance, int runty, int mind,
			int runway, String remarks, int utime, int pspeed, String hspeed,
			long addtime) {

		int[] time = YaoPao01App.cal(utime);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
		Log.v("wysport", "utime =" + utime);

		timeV.setText(t1 + "" + t2 + ":" + t3 + "" + t4 + ":" + t5 + "" + t6);
		Log.v("wysport", "time =" + t1 + "" + t2 + ":" + t3 + "" + t4 + ":"
				+ t5 + "" + t6);
		int[] speed = YaoPao01App.cal(pspeed);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		pspeedV.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" + "/km");
		avgspeedV.setText(hspeed + " km/h");
		disV.setText(df.format(distance / 1000) + " km");
		Date date = new Date(addtime);
		dateV.setText(sdf4.format(date) + "年" + sdf1.format(date) + "月"
				+ sdf2.format(date) + "日 " + YaoPao01App.getWeekOfDate(date)
				+ " " + sdf3.format(date));

		initType(runty);
		titleV.setText(YaoPao01App.getWeekOfDate(date) + title);

	}
	private void initType(int type) {
		switch (type) {
		case 1:
			typeV.setBackgroundResource(R.drawable.runtype_walk);
			title = "的步行";
			break;
		case 2:
			typeV.setBackgroundResource(R.drawable.runtype_run);
			title = "的跑步";
			break;

		default:
			break;
		}
	}
	private void drawNewLine() {

		if (SportRecordActivity.points.size() < 2) {
			return;
		}

		GpsPoint newPoint = SportRecordActivity.points
				.get(SportRecordActivity.points.size() - 1);
		Log.v("wy", "SportRecordActivity.points=" + SportRecordActivity.points);
		if (newPoint.lon != lastDrawPoint.lon
				|| newPoint.lat != lastDrawPoint.lat) {// 5秒后点的位置有移动
			int count = 2;
			List<LatLng> newLine = new ArrayList<LatLng>();
			newLine.add(new LatLng(lonLatEncryption.encrypt(newPoint).lat,
					lonLatEncryption.encrypt(newPoint).lon));
			newLine.add(new LatLng(lonLatEncryption.encrypt(lastDrawPoint).lat,
					lonLatEncryption.encrypt(lastDrawPoint).lon));
			if (lastDrawPoint.status == 0) {
				aMap.addPolyline((new PolylineOptions()).addAll(newLine).color(
						Color.RED));
			} else {
				aMap.addPolyline((new PolylineOptions()).addAll(newLine)
						.color(Color.BLACK).setDottedLine(true));
			}
			lastDrawPoint = newPoint;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}



	private List<LatLng> initPoints(List<GpsPoint> list) {
		List points = new ArrayList<LatLng>();
		LatLng ponit = null;
		for (int i = 0; i < SportRecordActivity.points.size(); i++) {
			ponit = new LatLng(list.get(i).lat, list.get(i).lon);
			points.add(ponit);
		}
		return points;

	}





	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.full_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportTrackMap.this.finish();
				break;
			}
			break;
		
	}return true;
		}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}
}
