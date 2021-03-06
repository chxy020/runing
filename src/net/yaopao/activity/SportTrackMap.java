package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import net.yaopao.engine.manager.GpsPoint;
import net.yaopao.engine.manager.OneKMInfo;
import net.yaopao.engine.manager.RunManager;
import net.yaopao.engine.manager.binaryIO.BinaryIOManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.umeng.analytics.MobclickAgent;

public class SportTrackMap extends BaseActivity{
	private MapView mapView;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
	public GpsPoint curPopPoint;
	public GpsPoint lastPopPoint;

	private DecimalFormat df;
	private String title = "";
	private TextView timeV;
	private TextView pspeedV;
	private TextView ponitsV;
	private TextView dateV;
	private TextView date1V;
	private TextView date2V;
	private TextView date3V;
	private TextView date4V;
	private TextView titleV;
	private TextView disV;
	private ImageView typeV;
	private TextView backV;
	double distance_add = 0;
	long time_one_km = 0;
	int targetDis = 1000;
	GpsPoint lastSportPoint = null;
	private RunManager runManager;
	@SuppressLint("NewApi")
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
		ponitsV = (TextView) findViewById(R.id.full_points);
//		titleV = (TextView) findViewById(R.id.full_title);
		dateV = (TextView) findViewById(R.id.full_date);
		date1V = (TextView) findViewById(R.id.full_date1);
		date2V = (TextView) findViewById(R.id.full_date2);
		date3V = (TextView) findViewById(R.id.full_date3);
		date4V = (TextView) findViewById(R.id.full_date4);
		disV = (TextView) findViewById(R.id.full_dis);
		typeV = (ImageView) findViewById(R.id.full_type);
//		backV.setOnTouchListener(this);
		backV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SportTrackMap.this.finish();
				
			}
		});
//		sdf1 = new SimpleDateFormat("MM");
//		sdf2 = new SimpleDateFormat("dd");
//		sdf3 = new SimpleDateFormat("HH:mm");
//		sdf4 = new SimpleDateFormat("yyyy");
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		initMap();
	}

	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

		Intent intent = getIntent();
		int id = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(id);
		initSportData(oneSport.getDistance(), oneSport.getHowToMove(),
				oneSport.getFeeling(), oneSport.getRunway(),
				oneSport.getRemark(), oneSport.getDuration(),
				oneSport.getSecondPerKm(), oneSport.getScore(),
				oneSport.getGenerateTime());
//		List<GpsPoint> pointsArray = JSONArray.parseArray(oneSport.getRuntra(),
//				GpsPoint.class);
		runManager = BinaryIOManager.readBinary(oneSport.getClientBinaryFilePath());
		if (oneSport.getIsMatch() == 1) {
			//Log.v("wysport", "oneSport.getRuntra() =" + oneSport.getRuntra());
			//String[] str = ((String)oneSport.getRuntra()).split(",");
		//	drawRunTrack(str);

		} else {
			drawConmmenTrack(runManager.GPSList);
		}
		
		
		
	}

	private void drawConmmenTrack(List<GpsPoint> pointsArray) {
		if (pointsArray.size()==0) {
			return;
		}
		//从增强还原成全量
		/*GpsPoint befor = null;
		GpsPoint curr = null;
//		YaoPao01App.lts.writeFileToSD("track 取出的数组: " +pointsArray, "uploadLocation");
		for (int i = 0; i < pointsArray.size(); i++) {
			if (i==0) {
				befor=pointsArray.get(0);
				continue;
			}
			befor =pointsArray.get(i-1);
			curr = pointsArray.get(i);
			curr.setLat(curr.getLat()+befor.getLat());
			curr.setLon(curr.getLon()+befor.getLon());
			curr.setTime(curr.time+befor.time);
			pointsArray.set(i, curr);
		}*/
//		YaoPao01App.lts.writeFileToSD("track 转换后的数组: " +pointsArray, "uploadLocation");
		GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));
		GpsPoint end = lonLatEncryption.encrypt(pointsArray.get(pointsArray
				.size() - 1));
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(end.getLat(), end.getLon()))
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(end())))
				.anchor(0.5f, 0.5f));
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(start.getLat(), start.getLon()))
				.icon(BitmapDescriptorFactory
						.fromBitmap(getViewBitmap(start()))).anchor(0.5f, 0.5f));
		drawLine(pointsArray);
		draw1KmMaker(runManager.dataKm);
	}

	private void drawLine(List<GpsPoint> pointsArray) {
		GpsPoint firstPoint = lonLatEncryption.encrypt(pointsArray.get(0));
		double min_lat = firstPoint.getLat();
		double max_lat = firstPoint.getLat();
		double min_lon = firstPoint.getLon();
		double max_lon = firstPoint.getLon();
		List<LatLng> runPoints =new ArrayList<LatLng>();
		GpsPoint crrPoint =null;
		// 先绘制黑色底线和灰色线
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.BLACK).width(13f));
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.GRAY).width(11f));
		lastSportPoint = pointsArray.get(0);
		for (int i = 0; i < pointsArray.size(); i++) {
			crrPoint = pointsArray.get(i);
			GpsPoint  encryptPoint = lonLatEncryption.encrypt(crrPoint);
			if (encryptPoint.getLon() < min_lon) {
				min_lon = encryptPoint.getLon();
			}
			if (encryptPoint.getLat() < min_lat) {
				min_lat = encryptPoint.getLat();
			}
			if (encryptPoint.getLon() > max_lon) {
				max_lon = encryptPoint.getLon();
			}
			if (encryptPoint.getLat() > max_lat) {
				max_lat = encryptPoint.getLat();
			}
			if (crrPoint.getStatus()==1) {
				LatLng latlon = new LatLng( lonLatEncryption.encrypt(crrPoint).getLat(), lonLatEncryption.encrypt(crrPoint).getLon());
				runPoints.add(latlon);
			}else if(crrPoint.getStatus()==2){
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints).color(Color.GREEN).width(11f));
				runPoints = new ArrayList<LatLng>();
			}if (i==(pointsArray.size()-1)) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints).color(Color.GREEN).width(11f));
			}
			lastSportPoint= crrPoint;
			}
		// 移动到中心
		LatLng latlon1 = new LatLng(min_lat, min_lon);
		LatLng latlon2 = new LatLng(max_lat, max_lon);
		LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
	}
	private void  draw1KmMaker(List<OneKMInfo> infos){
		for (int i = 0; i < infos.size(); i++) {
			OneKMInfo info = infos.get(i);
			int a = info.getDuring();
			GpsPoint onekm = lonLatEncryption.encrypt(new GpsPoint(info.getLon(),info.getLat()));
			aMap.addMarker(new MarkerOptions()
			.position(new LatLng(onekm.getLat(), onekm.getLon()))
			.icon(BitmapDescriptorFactory.fromBitmap(
					getViewBitmap(getView("第"+ info.getNumber()+ "公里", info.getDuring() / 1000 / 60+ "'" + (info.getDuring() / 1000)
							% 60 + "\""))))
			.anchor(0.5f, 0.5f));
		}
		
	}
	private void initSportData(double distance, int runty, int mind,
			int runway, String remarks, int utime, int pspeed, int points,
			long addtime) {

		int[] time = YaoPao01App.cal(utime/1000);
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
		pspeedV.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" );
		ponitsV.setText("+ " + points);
		disV.setText(df.format(distance / 1000) + " km");
		Date date = new Date(addtime);
		dateV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date1V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date2V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date3V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date4V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		initType(runty);
		
		SimpleDateFormat df = new SimpleDateFormat("M月d日");
		
		titleV.setText(df.format(date) + title);

	}

	private void initType(int type) {
		switch (type) {
		case 1:
			typeV.setBackgroundResource(R.drawable.runtype_run_big);
			title = "的跑步";
			break;
		case 2:
			typeV.setBackgroundResource(R.drawable.runtype_walk_big);
			title = "的步行";
			break;
		case 3:
			typeV.setBackgroundResource(R.drawable.runtype_ride_big);
			title = "的自行车骑行";
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		MobclickAgent.onPause(this);
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
		for (int i = 0; i < list.size(); i++) {
			GpsPoint one = lonLatEncryption.encrypt(list.get(i));
			ponit = new LatLng(one.getLat(), one.getLon());
			points.add(ponit);
		}
		return points;

	}

	/**
	 * * 在view布局文件中中显示文字
	 * */
	public View getView(String title, String text) {
		View view = getLayoutInflater().inflate(R.layout.marker, null);
		TextView text_title = (TextView) view.findViewById(R.id.marker_title);
		TextView text_text = (TextView) view.findViewById(R.id.marker_text);
		text_title.setText(title);
		text_title.setTextColor(Color.BLACK);
		text_text.setText(text);
		text_text.setTextColor(Color.BLACK);
		return view;
	}

	public View start() {
		return getLayoutInflater().inflate(R.layout.marker_s, null);
	}

	public View end() {
		return getLayoutInflater().inflate(R.layout.marker_e, null);
	}

	/**
	 * 把一个view转化成bitmap对象
	 * */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	// 画比赛运动记录的轨迹
	void drawRunTrack(String[] match_pointList) {
		// 把CNAppDelegate.match_pointList画到地图上，如果遇到0,0则分段
		Log.v("wysport", "match_pointList =" + match_pointList);
		int j = 0;
		int i = 0;
		int n = 0;
		
		double min_lat = 0;
		double max_lat = 0;
		double min_lon = 0;
		double max_lon = 0;
		// int pointCount = CNAppDelegate.match_pointList.size();
		int pointCount = match_pointList.length;
		if (pointCount < 2) {
			return;
		}
//		double gpsLat = 0;
//		double gpsLon = 0;
		double slat= Double.parseDouble(match_pointList[0].split(" ")[1]);
        double slon = Double.parseDouble(match_pointList[0].split(" ")[0]);
        double elat= Double.parseDouble(match_pointList[match_pointList.length-1].split(" ")[1]);
        double elon = Double.parseDouble(match_pointList[match_pointList.length-1].split(" ")[0]);
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(elat, elon))
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(end())))
				.anchor(0.5f, 0.5f));
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(slat,slon))
				.icon(BitmapDescriptorFactory
						.fromBitmap(getViewBitmap(start()))).anchor(0.5f, 0.5f));
		
		
		max_lon = min_lon = Double.parseDouble(match_pointList[0].split(" ")[0]);
        max_lat = min_lat = Double.parseDouble(match_pointList[0].split(" ")[1]);
        //先画黑边
		for (i = 0; i < pointCount; i++) {
			double gpsLat =Double.parseDouble(match_pointList[i].split(" ")[1]);
			double gpsLon =Double.parseDouble(match_pointList[i].split(" ")[0]);
			if (gpsLon < 0.01 || i == pointCount - 1) {
				List<LatLng> points = new ArrayList<LatLng>();
				for (j = 0; j < i - n; j++) {
		            double lat2 = Double.parseDouble(match_pointList[n+j].split(" ")[1]);
		            double lon2 = Double.parseDouble(match_pointList[n+j].split(" ")[0]);
					points.add(new LatLng(lat2,lon2));
				}
				Log.v("wysport", "points =" + points);

				aMap.addPolyline((new PolylineOptions()).addAll(points)
						.color(Color.BLACK).width(13f));
				n = i + 1;// n为下一个起点
			}
		}
        
		 j = 0;
		 i = 0;
		 n = 0;
		for (i = 0; i < pointCount; i++) {
			double gpsLat =Double.parseDouble(match_pointList[i].split(" ")[1]);
			double gpsLon =Double.parseDouble(match_pointList[i].split(" ")[0]);
			if (gpsLon < 0.01 || i == pointCount - 1) {
				List<LatLng> points = new ArrayList<LatLng>();
				for (j = 0; j < i - n; j++) {
					if(gpsLon > 0.01){
						if(gpsLon < min_lon){
			                min_lon = gpsLon;
			            }
			            if(gpsLat < min_lat){
			                min_lat = gpsLat;
			            }
			            if(gpsLon > max_lon){
			                max_lon = gpsLon;
			            }
			            if(gpsLat > max_lat){
			                max_lat = gpsLat;
			            }
					}
		            double lat2 = Double.parseDouble(match_pointList[n+j].split(" ")[1]);
		            double lon2 = Double.parseDouble(match_pointList[n+j].split(" ")[0]);
					points.add(new LatLng(lat2,lon2));
				}
				Log.v("wysport", "points =" + points);

				aMap.addPolyline((new PolylineOptions()).addAll(points)
						.color(Color.GREEN).width(11f));
				n = i + 1;// n为下一个起点
			}
		}
		 LatLng latlon1 = new LatLng(min_lat, min_lon);
			LatLng latlon2 = new LatLng(max_lat, max_lon);
			LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
	}
	
//	@Override
//	public boolean onTouch(View view, MotionEvent event) {
//		int action = event.getAction();
//		switch (view.getId()) {
//		case R.id.full_back:
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				break;
//			case MotionEvent.ACTION_UP:
//				SportTrackMap.this.finish();
//				break;
//			}
//			break;
//
//		}
//		return true;
//	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { } return false; }
	 */
}
