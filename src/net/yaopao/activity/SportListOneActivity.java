package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.yaopao.activity.SportListActivity.MessageOnPageChangeListener;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.bean.SportBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
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
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolylineOptions;

public class SportListOneActivity extends Activity implements OnTouchListener {
	private TextView backV;
	private TextView timeV;
	private TextView pspeedV;
	private TextView ponitV;
	private TextView dateV;
	private TextView desV;
	private TextView titleV;
	private TextView disV;
	private ImageView typeV;
	private ImageView mindV;
	private ImageView wayV;
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

	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private SimpleDateFormat sdf4;
	private DecimalFormat df;
	String title = "";
	int recordId=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list_one);
		initViewPager();
		mapView = (MapView) mapLayout.findViewById(R.id.one_map);
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

	private void initViewPager() {
		
		this.mPager = (ViewPager) this.findViewById(R.id.oneVPager);
		this.mListViews = new ArrayList<View>();
		this.mInflater = this.getLayoutInflater();
		mapLayout = mInflater.inflate(R.layout.sport_one_slider_map, null);
		phoLayout = mInflater.inflate(R.layout.sport_one_slider_pho, null);
		this.mListViews.add(mapLayout);
		this.mListViews.add(phoLayout);
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
		dateV = (TextView) findViewById(R.id.one_date);
		desV = (TextView) findViewById(R.id.one_desc);
		disV = (TextView) findViewById(R.id.one_dis);
		typeV = (ImageView) findViewById(R.id.one_type);
		mindV = (ImageView) findViewById(R.id.one_mind);
		wayV = (ImageView) findViewById(R.id.one_way);
		backV = (TextView) findViewById(R.id.recording_one_back);
		backV.setOnTouchListener(this);
		initMap();
	}

	private void initMap() {

		lonLatEncryption = new LonLatEncryption();
		Intent intent = getIntent();
		recordId = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(recordId);
		initSportData(oneSport.getDistance(), oneSport.getRunty(),
				oneSport.getMind(), oneSport.getRunway(),
				oneSport.getRemarks(), oneSport.getUtime(),
				oneSport.getPspeed(), oneSport.getPoints(),
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
		aMap.getUiSettings().setScrollGesturesEnabled(false);
		aMap.getUiSettings().setZoomGesturesEnabled(false);
		aMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(SportListOneActivity.this,SportTrackMap.class);
				intent.putExtra("id", recordId + "");
				startActivity(intent);
			}
		});
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
			int runway, String remarks, int utime, int pspeed, int ponit,
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
		ponitV.setText("+ "+ ponit);
		disV.setText(df.format(distance / 1000) + " km");
		desV.setText(remarks);
		Date date = new Date(addtime);
		dateV.setText(sdf4.format(date) + "年" + sdf1.format(date) + "月"
				+ sdf2.format(date) + "日 " + YaoPao01App.getWeekOfDate(date)
				+ " " + sdf3.format(date));

		initType(runty);
		initMind(mind);
		initWay(runway);
		titleV.setText(YaoPao01App.getWeekOfDate(date) + title);

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
			typeV.setBackgroundResource(R.drawable.runtype_walk);
			title = "的步行";
			break;
		case 2:
			typeV.setBackgroundResource(R.drawable.runtype_run);
			title = "的跑步";
			break;
		case 3:
			typeV.setBackgroundResource(R.drawable.runtype_ride);
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
//		deactivate();
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
			ponit = new LatLng(list.get(i).lat, list.get(i).lon);
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
}
