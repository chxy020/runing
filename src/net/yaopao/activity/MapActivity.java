package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
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
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.a.am;
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
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.PolylineOptions;

/**
 */
public class MapActivity extends Activity implements LocationSource,
		AMapLocationListener, OnTouchListener {
	public static final String closeAction = "close.action";
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
	private SliderRelativeLayout slider;
	static TextView doneV;
	static TextView resumeV;
	static ImageView sliderIconV;
	static TextView sliderTextV;
	private ImageView backV;
	private ImageView locV;
	private int isFollow = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		lonLatEncryption = new LonLatEncryption();
		slider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
		sliderTextV = (TextView) findViewById(R.id.slider_text);

		doneV = (TextView) findViewById(R.id.slider_done);
		resumeV = (TextView) findViewById(R.id.slider_resume);
		sliderIconV = (ImageView) findViewById(R.id.slider_icon);
		backV = (ImageView) findViewById(R.id.map_back);
		locV = (ImageView) findViewById(R.id.map_loc);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);
		backV.setOnTouchListener(this);
		locV.setOnTouchListener(this);
		slider.setMainHandler(slipHandler);
		init();
		startTimer();
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChangeFinish(CameraPosition cameraPosition) {

				}

				@Override
				public void onCameraChange(CameraPosition arg0) {
					isFollow = 0;
				}
			});
		}

		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		drawLine(SportRecordActivity.points);
	}

	private void drawLine(List<GpsPoint> pointsArray) {
		List<LatLng> runPoints = new ArrayList<LatLng>();
		GpsPoint crrPoint = null;
		// 先绘制灰色线
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.GRAY).width(13f));
		for (int i = 0; i < pointsArray.size(); i++) {
			crrPoint = pointsArray.get(i);
			if (crrPoint.status == 0) {
				LatLng latlon = new LatLng(
						lonLatEncryption.encrypt(crrPoint).lat,
						lonLatEncryption.encrypt(crrPoint).lon);
				runPoints.add(latlon);
			} else if (crrPoint.status == 1) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints)
						.color(Color.GREEN).width(13f));
				runPoints = new ArrayList<LatLng>();
				lastDrawPoint =crrPoint;
				aMap.invalidate();
			}
			if (i == (pointsArray.size() - 1)) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints)
						.color(Color.GREEN).width(13f));
				lastDrawPoint =crrPoint;
				aMap.invalidate();
			}
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
			if (newPoint.status == 0) {
				aMap.addPolyline((new PolylineOptions()).addAll(newLine).color(
						Color.GREEN));
				aMap.invalidate();
			} else {
				aMap.addPolyline((new PolylineOptions()).addAll(newLine).color(
						Color.GRAY));
				aMap.invalidate();
			}
			lastDrawPoint = newPoint;
		}
	}

	Handler slipHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
//				SportRecordActivity.stopTimer();
				SportRecordActivity.timerListenerHandler.obtainMessage(2).sendToTarget();
				doneV.setVisibility(View.VISIBLE);
				resumeV.setVisibility(View.VISIBLE);
				sliderIconV.setVisibility(View.GONE);
				sliderTextV.setVisibility(View.GONE);
			}
		};
	};

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
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
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		stopTimer();
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
			GpsPoint one = lonLatEncryption.encrypt(list.get(i));
			ponit = new LatLng(one.lat, one.lon);
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
			mListener.onLocationChanged(aLocation);
			if (isFollow == 1) {
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						aLocation.getLatitude(), aLocation.getLongitude()), 16));
			}
		}
	}

	Handler timer = new Handler();
	Runnable drawTask = new Runnable() {
		@Override
		public void run() {
			drawNewLine();
			timer.postDelayed(this, 3000);
		}

	};

	public void startTimer() {
		timer.postDelayed(drawTask, 3000);
	}

	public void stopTimer() {
		timer.removeCallbacks(drawTask);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.map_loc:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				locV.setBackgroundResource(R.drawable.button_position_h);
				break;
			case MotionEvent.ACTION_UP:
				locV.setBackgroundResource(R.drawable.button_position);
				Location myloc = aMap.getMyLocation();
				if (myloc != null) {
					isFollow = 1;
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(myloc.getLatitude(), myloc
									.getLongitude()), 16));
				}

				break;
			}
			break;
		case R.id.map_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				backV.setBackgroundResource(R.drawable.button_back_h);
				break;
			case MotionEvent.ACTION_UP:
				backV.setBackgroundResource(R.drawable.button_back);
				MapActivity.this.finish();

				break;
			}
			break;
		case R.id.slider_done:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				doneV.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				doneV.setBackgroundResource(R.color.red);
				final Handler sliderHandler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 0) {
							Intent intent = new Intent(MapActivity.this,
									SportSaveActivity.class);
//							SportRecordActivity.stopRecordGps();
							SportRecordActivity.gpsListenerHandler.obtainMessage(4).sendToTarget();
							MapActivity.this.startActivity(intent);
							MapActivity.this.finish();
							
						}else if(msg.what == 1){
							//运动距离小于50米
							Toast.makeText(MapActivity.this, "您运动距离也太短了吧！这次就不给您记录了，下次一定要加油", Toast.LENGTH_LONG).show();
							Variables.utime = 0;
							Variables.pspeed = 0;
							Variables.distance = 0;
							Variables.points=0;
							if (SportRecordActivity.points!=null) {
								SportRecordActivity.points=null;
							}
							MapActivity.this.finish();
						}
						super.handleMessage(msg);
					}
				};
				Intent closeintent = new Intent(closeAction);
				closeintent.putExtra("data", "close");
				sendBroadcast(closeintent);
				DialogTool.doneSport(MapActivity.this, sliderHandler);
				break;
			}
			break;
		case R.id.slider_resume:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				resumeV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				resumeV.setBackgroundResource(R.color.blue_dark);
//				SportRecordActivity.startTimer();
				SportRecordActivity.timerListenerHandler.obtainMessage(1).sendToTarget();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}
}
