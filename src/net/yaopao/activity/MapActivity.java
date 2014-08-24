package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.widget.SliderRelativeLayout;
import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

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

/**
 */
public class MapActivity extends Activity implements LocationSource,
		AMapLocationListener,OnTouchListener {
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;
	SliderRelativeLayout slider;
	static TextView doneV;
	static TextView resumeV;
	static TextView sliderIconV;
	static TextView sliderTextV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		lonLatEncryption = new LonLatEncryption();
		slider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
		sliderTextV = (TextView) findViewById(R.id.slider_text);
		
		doneV = (TextView) findViewById(R.id.slider_done);
		resumeV = (TextView) findViewById(R.id.slider_resume);
		sliderIconV = (TextView) findViewById(R.id.slider_icon);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);
//		if (Variables.sportStatus == 0) {
//			sliderTextV.setText("滑动暂停");
//		} else {
//			sliderTextV.setText("滑动恢复");
//		}
		slider.setMainHandler(slipHandler);
		init();
		startTimer();
	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				@Override
				public void onCameraChangeFinish(CameraPosition cameraPosition) {
					System.out.println("zoom level is:" + cameraPosition.tilt);
				}

				@Override
				public void onCameraChange(CameraPosition arg0) {

				}
			});
		}

		aMap.getUiSettings().setZoomControlsEnabled(false);

		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// ����С�����ͼ��
		myLocationStyle.strokeColor(Color.BLACK);// ����Բ�εı߿���ɫ
		myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
		// myLocationStyle.anchor(int,int)//����С�����ê��
		myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		// aMap.setMyLocationType()
		int pointCount = SportRecordActivity.points.size();
		if (pointCount==0) {
			return;
		}else if (pointCount<2) {
			lastDrawPoint = SportRecordActivity.points.get(SportRecordActivity.points.size() - 1);
			return;
		}
		
		List<Integer> indexList = new ArrayList<Integer>();
//		Log.v("wygps", "map pointsIndex="+SportRecordActivity.pointsIndex);
		indexList.addAll(SportRecordActivity.pointsIndex);
//		Log.v("wygps", "indexList.size="+indexList.size());
		if (indexList.size()==0) {	
			aMap.addPolyline((new PolylineOptions()).addAll(initPoints(SportRecordActivity.points))	.color(Color.RED));
			lastDrawPoint = SportRecordActivity.points.get(SportRecordActivity.points.size() - 1);
			return;
		}
//		Log.v("wygps", "indexList.size="+indexList);
		indexList.add(pointCount - 1);
		int linesCount = indexList.size();
		int j = 0;
		int i = 0;
		int n = 0;
		for (j = 0; j < linesCount - 1; j++) {
			List<LatLng> oneLinePoints = new ArrayList<LatLng>();
			int startIndex = indexList.get(j);
			int endIndex = indexList.get(j + 1);

			if (endIndex - startIndex + 1 < 2)
				continue;
			for (i = startIndex, n = 0; i <= endIndex; i++, n++) {
				GpsPoint gpsPoint = SportRecordActivity.points.get(i);
				oneLinePoints.add(new LatLng(
						lonLatEncryption.encrypt(gpsPoint).lat,
						lonLatEncryption.encrypt(gpsPoint).lon));
			}

			GpsPoint gpsPointEnd = SportRecordActivity.points
					.get(oneLinePoints.size() - 1);
			if (gpsPointEnd.status == 0) {
				aMap.addPolyline((new PolylineOptions()).addAll(oneLinePoints)
						.color(Color.RED));
			} else {
				aMap.addPolyline((new PolylineOptions()).addAll(oneLinePoints)
						.color(Color.BLACK).setDottedLine(true));
			}
		}
		lastDrawPoint = SportRecordActivity.points.get(SportRecordActivity.points.size() - 1);
		
	}

	private void drawNewLine() {
		
		if (SportRecordActivity.points.size()<2) {
			return;
		}
		
		GpsPoint newPoint = SportRecordActivity.points
				.get(SportRecordActivity.points.size() - 1);
		if (newPoint.lon != lastDrawPoint.lon
				|| newPoint.lat != lastDrawPoint.lat) {// 5�����λ�����ƶ�
			int count = 2;
			List<LatLng> newLine = new ArrayList<LatLng>();
			newLine.add(new LatLng(lonLatEncryption.encrypt(newPoint).lat,lonLatEncryption.encrypt(newPoint).lon));
			newLine.add(new LatLng(lonLatEncryption.encrypt(lastDrawPoint).lat,lonLatEncryption.encrypt(lastDrawPoint).lon));
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

	Handler slipHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
					SportRecordActivity.stopTimer();
					doneV.setVisibility(View.VISIBLE);
					resumeV.setVisibility(View.VISIBLE);
					sliderIconV.setVisibility(View.GONE);
					sliderTextV.setVisibility(View.GONE);
			}
		};
	};

	/**
	 * ����������д
	 */
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

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		stopTimer();
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
		for (int i = 0; i < SportRecordActivity.points.size(); i++) {
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

	 Handler timer = new Handler();
	 Runnable drawTask = new Runnable() {
		@Override
		public void run() {
			drawNewLine();
			timer.postDelayed(this, 3000);
		}

	};
	public  void startTimer() {
		timer.postDelayed(drawTask, 3000);
	}

	public  void stopTimer() {
		timer.removeCallbacks(drawTask);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.slider_done:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
						 DialogTool.doneSport(MapActivity.this);
				
				break;
			}
			break;
		case R.id.slider_resume:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportRecordActivity.startTimer();
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
}
