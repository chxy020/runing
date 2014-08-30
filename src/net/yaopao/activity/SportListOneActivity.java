package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.bean.SportBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
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
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;

public class SportListOneActivity extends Activity implements OnTouchListener, LocationSource,AMapLocationListener {
	private TextView backV;
	private MapView mapView;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	public GpsPoint lastDrawPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list_one);
		mapView = (MapView) findViewById(R.id.one_map);
		mapView.onCreate(savedInstanceState);
		initLayout();
	}

	private void initLayout() {
		
		backV = (TextView) findViewById(R.id.recording_one_back);
		backV .setOnTouchListener(this);
		lonLatEncryption = new LonLatEncryption();
		Intent intent = getIntent();
		 int id=Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(id);
		List<GpsPoint> pointsArray = JSONArray.parseArray(oneSport.getRuntra(),GpsPoint.class);
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
		if (pointCount!=0) {
			GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));
			
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(start.lat, start.lon), 16));
		}else{
			aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		}
		
		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		
		
		if (pointCount==0) {
			return;
		}else if (pointCount<2) {
			lastDrawPoint = (GpsPoint) pointsArray.get(pointsArray.size() - 1);
			return;
		}
		
		if (indexArray.size()==0) {	
			aMap.addPolyline((new PolylineOptions()).addAll(initPoints(pointsArray)).color(Color.RED));
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

			GpsPoint gpsPointEnd = (GpsPoint) pointsArray
					.get(oneLinePoints.size() - 1);
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

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
	 */
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
