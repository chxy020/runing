package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;

public class SportListOneActivity extends Activity implements OnTouchListener,
		LocationSource, AMapLocationListener {
	private TextView backV;
	private TextView timeV;
	private TextView pspeedV;
	private TextView avgspeedV;
	private TextView dateV;
	private TextView desV;
	private TextView titleV;
	private TextView disV;
	private ImageView typeV;
	private ImageView mindV;
	private ImageView wayV;
	
	
	private MapView mapView;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	public GpsPoint lastDrawPoint;
	
	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private SimpleDateFormat sdf4;
	private DecimalFormat df;
	String title = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list_one);
		mapView = (MapView) findViewById(R.id.one_map);
		mapView.onCreate(savedInstanceState);
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
		sdf4 = new SimpleDateFormat("yyyy");
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		initLayout();
	}

	private void initLayout() {

		backV = (TextView) findViewById(R.id.recording_one_back);
		timeV = (TextView) findViewById(R.id.one_time);
		pspeedV = (TextView) findViewById(R.id.one_pspeed);
		avgspeedV = (TextView) findViewById(R.id.one_avgspeed);
		titleV = (TextView) findViewById(R.id.recording_one_title);
		dateV = (TextView) findViewById(R.id.one_date);
		desV = (TextView) findViewById(R.id.one_desc);
		disV = (TextView) findViewById(R.id.one_dis);
		typeV= (ImageView) findViewById(R.id.one_type);
		mindV= (ImageView) findViewById(R.id.one_mind);
		wayV= (ImageView) findViewById(R.id.one_way);
		backV = (TextView) findViewById(R.id.recording_one_back);
		backV = (TextView) findViewById(R.id.recording_one_back);
		backV.setOnTouchListener(this);
		initMap();
	}
	

	private void initMap(){
		
		lonLatEncryption = new LonLatEncryption();
		Intent intent = getIntent();
		int id = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(id);
		initSportData(oneSport.getDistance(),oneSport.getRunty(),oneSport.getMind(),oneSport.getRunway(),oneSport.getRemarks(),
				oneSport.getUtime(),oneSport.getPspeed(),oneSport.getHspeed(),oneSport.getAddtime());
		Log.v("wydb", " utime =" + oneSport.getUtime());
		Log.v("wydb", " hspeed =" + oneSport.getHspeed() );
		Log.v("wydb", " remarks =" + oneSport.getRemarks());
		List<GpsPoint> pointsArray = JSONArray.parseArray(oneSport.getRuntra(),
				GpsPoint.class);
		int pointCount = pointsArray.size();
		JSONArray indexArray = JSONArray.parseArray(oneSport.getStatusIndex());
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChangeFinish(CameraPosition cameraPosition) {

				}

				@Override
				public void onCameraChange(CameraPosition arg0) {

				}
			});
		}

		aMap.getUiSettings().setZoomControlsEnabled(false);
		if (pointCount != 0) {
			GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));

			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					start.lat, start.lon), 16));
		} else {
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		}

		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
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
	
	private void initSportData(double distance, int runty, int mind,
			int runway, String remarks, int utime, int pspeed, double hspeed,long addtime) {
		
		int[] time = YaoPao01App.cal(utime);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
		timeV.setText(t1+""+t2+":"+t3+""+t4+":"+t5+""+t6);
		
		int[] speed = YaoPao01App.cal(pspeed);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		pspeedV.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" + "/km");
		avgspeedV.setText(hspeed+"km/h");
		disV.setText( df.format(distance/1000) +" km");
		desV.setText(remarks);
		Date date = new Date(addtime);
		dateV.setText(sdf4.format(date) + "年" + sdf1.format(date) + "月" + sdf2.format(date) + "日 "+ YaoPao01App.getWeekOfDate(date) + " " + sdf3.format(date));
		
		initType(runty);
		initMind(mind);
		initWay(runway);
		titleV.setText( YaoPao01App.getWeekOfDate(date)+title);

		
	}
	private void initMind(int mind){
		switch (mind) {
		case 1:
			mindV.setBackgroundResource(R.drawable.mood1_h);
			break;
		case 2:
			mindV.setBackgroundResource(R.drawable.mood2_h);
			break;
		case 3:
			mindV.setBackgroundResource(R.drawable.mood3_h);
			break;
		case 4:
			mindV.setBackgroundResource(R.drawable.mood4_h);
			break;
		case 5:
			mindV.setBackgroundResource(R.drawable.mood5_h);
			break;

		default:
			break;
		}
	}
	private void initType(int type){
		switch (type) {
		case 1:
			typeV.setBackgroundResource(R.drawable.runtype_walk);
			title="的步行";
			break;
		case 2:
			typeV.setBackgroundResource(R.drawable.runtype_run);
			title="的跑步";
			break;
			
		default:
			break;
		}
	}
	private void initWay(int way){
		switch (way) {
		case 1:
			wayV.setBackgroundResource(R.drawable.way1_h);
			break;
		case 2:
			wayV.setBackgroundResource(R.drawable.way2_h);
			break;
		case 3:
			wayV.setBackgroundResource(R.drawable.way3_h);
			break;
		case 4:
			wayV.setBackgroundResource(R.drawable.way4_h);
			break;
		case 5:
			wayV.setBackgroundResource(R.drawable.way5_h);
			break;
			
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.recording_one_back:
			SportListOneActivity.this.finish();
			break;
		}
		return true;
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
		deactivate();
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

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	private List<LatLng> initPoints(List<GpsPoint> list) {
		List points = new ArrayList<LatLng>();
		LatLng ponit = null;
		for (int i = 0; i < list.size(); i++) {
			ponit = new LatLng(list.get(i).lat, list.get(i).lon);
			points.add(ponit);
		}
		return points;

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
		}
	}

}
