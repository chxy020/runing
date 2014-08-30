package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;

/**
 */
public class MatchMapActivity extends Activity implements LocationSource,
		AMapLocationListener, OnTouchListener {

	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	private ImageView backV;

	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_map);
		mapView = (MapView) findViewById(R.id.match_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		lonLatEncryption = new LonLatEncryption();
		init();
		startTimer();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		
		backV = (ImageView) findViewById(R.id.match_map_back);
		backV.setOnTouchListener(this);
		drawLine();
	}

	private void drawLine() {
		if (MatchRunActivity.points.size()<1) {
			return;
		}
		List<LatLng> oneLinePoints = new ArrayList<LatLng>();
		for (int i=0;i<MatchRunActivity.points.size();i++) {
			GpsPoint gpsPoint = MatchRunActivity.points.get(i);
			oneLinePoints.add(new LatLng(lonLatEncryption.encrypt(gpsPoint).lat,
					lonLatEncryption.encrypt(gpsPoint).lon));
		}
		lastDrawPoint = MatchRunActivity.points.get(oneLinePoints.size() - 1);
		aMap.addPolyline((new PolylineOptions()).addAll(oneLinePoints)
				.color(Color.RED));
	}
	public  void startTimer() {
		timer.postDelayed(drawTask, 3000);
	}

	public  void stopTimer() {
		timer.removeCallbacks(drawTask);
	}
	 Handler timer = new Handler();
	 Runnable drawTask = new Runnable() {
		@Override
		public void run() {
			drawNewLine();
			timer.postDelayed(this, 3000);
		}

	};
	private void drawNewLine() {
		
		if (MatchRunActivity.points.size()<2) {
			return;
		}
		
		GpsPoint newPoint = MatchRunActivity.points
				.get(MatchRunActivity.points.size() - 1);
		if (newPoint.lon != lastDrawPoint.lon
				|| newPoint.lat != lastDrawPoint.lat) {
			List<LatLng> newLine = new ArrayList<LatLng>();
			newLine.add(new LatLng(lonLatEncryption.encrypt(newPoint).lat,lonLatEncryption.encrypt(newPoint).lon));
			newLine.add(new LatLng(lonLatEncryption.encrypt(lastDrawPoint).lat,lonLatEncryption.encrypt(lastDrawPoint).lon));
				aMap.addPolyline((new PolylineOptions()).addAll(newLine).color(
						Color.RED));
				aMap.invalidate();
			lastDrawPoint = newPoint;
		}
	}	
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			 mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_map_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				backV.setBackgroundResource(R.drawable.button_back_h);
				break;
			case MotionEvent.ACTION_UP:
				backV.setBackgroundResource(R.drawable.button_back);
				MatchMapActivity.this.finish();

				break;
			}
			break;
		}
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//DialogTool.quit(MainActivity.this);
		}
		return false;
	}
}
