package net.yaopao.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import net.yaopao.engine.manager.RunManager;
import net.yaopao.engine.manager.binaryIO.BinaryIOManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.umeng.analytics.MobclickAgent;

public class SportListOneActivity extends BaseActivity {
	private TextView backV;
	private TextView timeV;
	private TextView pspeedV;
	private TextView ponitV;
	private TextView dateV;
//	private TextView desV;
	private EditText desV;
	private TextView titleV;
	private TextView shareV;
	
	private TextView date1V;
	private TextView date2V;
	private TextView date3V;
	private TextView date4V;
	
	private ImageView typeV;
	private ImageView mindV;
	private ImageView wayV;
	private ImageView d1V;
	private ImageView d2V;
	private ImageView d3V;
	private ImageView d4V;
	/** 消息内容 */
	private ViewPager mPager = null;
	/** Tab页面列表 */
	private List<View> mListViews;
	/** 推送消息数据适配器 */
	private MessagePagerAdapter mMessageAdapter = null;
	/** 容器加载 */
	private LayoutInflater mInflater = null;

	private MapView mapView;
	private View mapLayout;
	private View phoLayout;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
//	public static RunManager runManager;
	String title = "";
	int recordId = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list_one);
		Intent intent = getIntent();
		recordId = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(recordId);
		initViewPager();
		mapView = (MapView) mapLayout.findViewById(R.id.one_map);
		mapView.onCreate(savedInstanceState);
		initLayout();

	}

	private void initViewPager() {

		this.mPager = (ViewPager) this.findViewById(R.id.oneVPager);
		this.mListViews = new ArrayList<View>();
		this.mInflater = this.getLayoutInflater();
		mapLayout = mInflater.inflate(R.layout.sport_one_slider_map, null);

		if (oneSport.getClientImagePaths()!=null&&!"".equals(oneSport.getClientImagePaths())) {
			phoLayout = mInflater.inflate(R.layout.sport_one_slider_pho, null);
			ImageView phoV = (ImageView) phoLayout.findViewById(R.id.one_pho_v);
			phoV.setScaleType(ScaleType.CENTER_CROP);
//			phoV.setImageBitmap(getImg(Constants.sportPho+ oneSport.getClientImagePaths()));
			phoV.setImageBitmap(getImg(oneSport.getClientImagePaths()));
		}

		this.mListViews.add(mapLayout);
		if (phoLayout != null) {
			this.mListViews.add(phoLayout);
		}
		this.mMessageAdapter = new MessagePagerAdapter(mListViews);
		this.mPager.setAdapter(this.mMessageAdapter);
		this.mPager.setOnPageChangeListener(new MessageOnPageChangeListener());
	}

	private void initLayout() {

		backV = (TextView) findViewById(R.id.recording_one_back);
		timeV = (TextView) findViewById(R.id.one_time);
		pspeedV = (TextView) findViewById(R.id.one_pspeed);
		ponitV = (TextView) findViewById(R.id.one_ponit);
		titleV = (TextView) findViewById(R.id.recording_one_title);
		shareV = (TextView) findViewById(R.id.recording_one_share);
		dateV = (TextView) findViewById(R.id.one_date);
		date1V = (TextView) findViewById(R.id.one_date1);
		date2V = (TextView) findViewById(R.id.one_date2);
		date3V = (TextView) findViewById(R.id.one_date3);
		date4V = (TextView) findViewById(R.id.one_date4);
		desV = (EditText) findViewById(R.id.one_desc);
		// disV = (TextView) findViewById(R.id.one_dis);
		typeV = (ImageView) findViewById(R.id.one_type);
		mindV = (ImageView) findViewById(R.id.one_mind);
		wayV = (ImageView) findViewById(R.id.one_way);
		backV = (TextView) findViewById(R.id.recording_one_back);

		d1V = (ImageView) findViewById(R.id.list_sport_num1);
		d2V = (ImageView) findViewById(R.id.list_sport_num2);
		d3V = (ImageView) findViewById(R.id.list_sport_dec1);
		d4V = (ImageView) findViewById(R.id.list_sport_dec2);
		initinitSymbol();
		backV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				YaoPao01App.db.updateRemark(recordId, desV.getText().toString());
				SportListOneActivity.this.finish();

			}
		});

		shareV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent();
				// 分享页面
				myIntent = new Intent(SportListOneActivity.this,
						SportShareActivity.class);
				myIntent.putExtra("id", recordId + "");
				startActivity(myIntent);
			}
		});
		initMap();
	}

	@SuppressLint("NewApi")
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setScrollGesturesEnabled(false);
		aMap.getUiSettings().setZoomGesturesEnabled(false);
		aMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(SportListOneActivity.this,
						SportTrackMap.class);
				intent.putExtra("id", recordId + "");
				startActivity(intent);
			}
		});

		lonLatEncryption = new LonLatEncryption();
		 Intent intent = getIntent();
		 recordId = Integer.parseInt(intent.getStringExtra("id"));
		 oneSport = YaoPao01App.db.queryForOne(recordId);
		initSportData(oneSport.getDistance(), oneSport.getHowToMove(),
				oneSport.getFeeling(), oneSport.getRunway(),
				oneSport.getRemark(), oneSport.getDuration(),
				oneSport.getSecondPerKm(), oneSport.getScore(),
				oneSport.getGenerateTime());
		
		if (oneSport.getIsMatch() == 1) {
			//画比赛的轨迹，本次不支持
//			Log.v("wysport", "oneSport.getRuntra() =" + oneSport.getRuntra());
//			
//			String[] str = ((String)oneSport.getRuntra()).split(",");
//			drawRunTrack(str);

		} else {
			RunManager runManager = BinaryIOManager.readBinary(oneSport.getClientBinaryFilePath());
			drawConmmenTrack(runManager.GPSList);
		}
		

		
	}

	// 画比赛运动记录的轨迹
	void drawRunTrack(String[] match_pointList) {
		// 把CNAppDelegate.match_pointList画到地图上，如果遇到0,0则分段
		Log.v("wysport", "match_pointList =" + match_pointList);
		int j = 0;
		int i = 0;
		int n = 0;
		
		double min_lon = 0;
	    double min_lat = 0;
	    double max_lon = 0;
	    double max_lat = 0;
		
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
		max_lon = min_lon = Double.parseDouble(match_pointList[0].split(" ")[0]);
        max_lat = min_lat = Double.parseDouble(match_pointList[0].split(" ")[1]);
        
        j = 0;
		 i = 0;
		 n = 0;
		for (i = 0; i < pointCount; i++) {
			double gpsLat =Double.parseDouble(match_pointList[i].split(" ")[1]);
			double gpsLon =Double.parseDouble(match_pointList[i].split(" ")[0]);
			
			Log.v("wysport", "gpsLat =" + gpsLat+" gpsLon="+gpsLon);
			if (gpsLon < 0.01 || i == pointCount - 1) {
				List<LatLng> points = new ArrayList<LatLng>();
				for (j = 0; j < i - n; j++) {
					if(gpsLon > 0.01){
						if (gpsLon < min_lon) {
							min_lon = gpsLon;
						}
						if (gpsLat < min_lat) {
							min_lat = gpsLat;
						}
						if (gpsLon > max_lon) {
							max_lon = gpsLon;
						}
						if (gpsLat > max_lat) {
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

	// 画普通运动记录的轨迹
	@SuppressLint("NewApi")
	void drawConmmenTrack(List<GpsPoint> pointsArray) {
		if (pointsArray.size() == 0) {
			return;
		}
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(6);
		df.setRoundingMode(RoundingMode.DOWN);

		String gpsStr = df.format(pointsArray.get(0).getLon()) + " "
				+ df.format(pointsArray.get(0).getLat());
		for (int i = 1; i < pointsArray.size(); i++) {
			gpsStr += "," + df.format(pointsArray.get(i).getLon()) + " "
					+ df.format(pointsArray.get(i).getLat());
		}

		YaoPao01App.lts.writeFileToSD("运动记录: " + gpsStr, "gps");
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
	}

	private void drawLine(List<GpsPoint> pointsArray) {
		GpsPoint firstPoint = lonLatEncryption.encrypt(pointsArray.get(0));
		double min_lat = firstPoint.getLat();
		double max_lat = firstPoint.getLat();
		double min_lon = firstPoint.getLon();
		double max_lon = firstPoint.getLon();
		List<LatLng> runPoints = new ArrayList<LatLng>();
		GpsPoint crrPoint = null;
		// 先绘制黑色底线和灰色线
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.BLACK).width(13f));
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.GRAY).width(11f));
		for (int i = 0; i < pointsArray.size(); i++) {
			crrPoint = pointsArray.get(i);
			GpsPoint encryptPoint = lonLatEncryption.encrypt(crrPoint);
			if(encryptPoint.getLon() > 0.01){
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
			}
			
			if (crrPoint.getStatus() == 1) {
				LatLng latlon = new LatLng(
						lonLatEncryption.encrypt(crrPoint).getLat(),
						lonLatEncryption.encrypt(crrPoint).getLon());
				runPoints.add(latlon);
			} else if (crrPoint.getStatus() == 2) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints)
						.color(Color.GREEN).width(11f));
				runPoints = new ArrayList<LatLng>();
			}
			if (i == (pointsArray.size() - 1)) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints)
						.color(Color.GREEN).width(11f));
			}
			// 移动到中心
			LatLng latlon1 = new LatLng(min_lat, min_lon);
			LatLng latlon2 = new LatLng(max_lat, max_lon);
			LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
		}
	}

	private void initSportData(double distance, int runty, int mind,
			int runway, String remarks, int utime, int pspeed, int ponit,
			long addtime) {

		int[] time = YaoPao01App.cal(utime / 1000);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
		Log.v("wysport", "utime =" + utime);

		 timeV.setText(t1 + "" + t2 + ":" + t3 + "" + t4 + ":" + t5 + "" + t6);
//		timeV.setText(time[0] + ":" + time[1] + ":" + time[2]);
		Log.v("wysport", "time =" + t1 + "" + t2 + ":" + t3 + "" + t4 + ":"
				+ t5 + "" + t6);
		int[] speed = YaoPao01App.cal(pspeed);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		pspeedV.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"");
		ponitV.setText("+ " + ponit);
		initDis(distance);
		desV.setText(remarks);
		Date date = new Date(addtime);
		dateV.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date1V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date2V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date3V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		date4V.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
		initType(runty);
		initMind(mind);
		initWay(runway);

		SimpleDateFormat df = new SimpleDateFormat("M月d日");

		titleV.setText(df.format(date) + title);

	}

	private void initDis(double distance) {
		int d1 = (int) (distance % 100000) / 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1V.setVisibility(View.VISIBLE);
//			update(d1, d1V);
			YaoPao01App.graphicTool.updateRedNum(d1, d1V);
		}

//		update(d2, d2V);
//		update(d3, d3V);
//		update(d4, d4V);
		YaoPao01App.graphicTool.updateRedNum(new int[] { d2, d3, d4 },
				new ImageView[] { d2V, d3V, d4V });

	}

	protected void update(int i, ImageView view) {
		if (i > 9) {
			i = i % 10;
		}
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.r_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.r_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.r_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.r_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.r_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.r_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.r_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.r_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.r_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.r_9);
			break;

		default:
			break;
		}
	}

	private void initMind(int mind) {
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

	private void initWay(int way) {
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

	// @Override
	// public boolean onTouch(View view, MotionEvent event) {
	// int action = event.getAction();
	// switch (view.getId()) {
	// case R.id.recording_one_back:
	// SportListOneActivity.this.finish();
	// break;
	// }
	// return true;
	// }

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront = this.getClass().getSimpleName();
		Variables.activityOnFront = this.getClass().getSimpleName();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		MobclickAgent.onPause(this);
		// deactivate();
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
	 * ViewPager适配器
	 */
	protected class MessagePagerAdapter extends PagerAdapter {
		public List<View> views;

		public MessagePagerAdapter(List<View> mListViews) {
			this.views = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return this.views == null ? 0 : this.views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(this.views.get(arg1), 0);
			return this.views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	/**
	 * 消息切换监听
	 */
	protected class MessageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			// arg0是表示你当前选中的页面，这事件是在你页面跳转完毕的时候调用的。
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做，就是停在那。
			if (1 == arg0) {
				// 停止自动轮播
			} else if (0 == arg0) {
				// 走完2之后,完全停下来之后就会走0,所以在最后重新启动轮播
				// 重新启动自动轮播
			}
		}
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

	public Bitmap getImg(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			// options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			// bitmap = BitmapFactory.decodeStream(fis);
			bitmap = BitmapFactory.decodeStream(fis, null, options);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return bitmap;
	}

	private void initinitSymbol() {
		ImageView dot = (ImageView) this.findViewById(R.id.list_sport_dot);
		ImageView km = (ImageView) this.findViewById(R.id.list_sport_km);
		dot.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_dot));
		km.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_km));
	}
}
