package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.Variables;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchMapActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnTouchListener {

	private AMap aMap;
	private MapView mapView;
	private ImageView avatarV;
	private ImageView match_map_loc;
	private ImageView match_baton;
	private ImageView image_gps;
	
	private TextView nameV;
	private TextView teamNameV;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private ImageView backV;
	CNGPSPoint4Match lastDrawPoint;
	
	class TimerTask_drawLine extends TimerTask{
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					drawIncrementLine();
				}
			});
		}
	}
	Timer timer_match_map = null;
	TimerTask_drawLine task_drawLine = null;
	private int isFollow = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_match_map);
		mapView = (MapView) findViewById(R.id.match_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		nameV = (TextView) findViewById(R.id.match_map_username);
		teamNameV = (TextView) findViewById(R.id.match_map_team);
		avatarV = (ImageView) findViewById(R.id.match_map_head);
		nameV.setText(Variables.userinfo.getString("nickname"));
		teamNameV.setText(CNAppDelegate.matchDic.getString("groupname"));
	    if(Variables.avatar != null){
	    	avatarV.setImageBitmap(Variables.avatar);
	    }
		init();
		registerReceiver(gpsStateReceiver, new IntentFilter(YaoPao01App.gpsState));
//		nameV.setText(Variables.userinfo.getString("nickname"));
//		teamNameV.setText(CNAppDelegate.matchDic.getString("groupname"));
//	    if(Variables.avatar != null){
//	    	avatarV.setImageBitmap(Variables.avatar);
//	    }needwy
		drawTrack();//画赛道
	    drawStratZone();//画出发区
	    drawTakeOverZone();//画接力区
	    drawRunTrack();//画已经跑得轨迹
	    if(CNAppDelegate.match_pointList.size() > 0){
	    	lastDrawPoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
	    }
	}
	void drawTrack(){
		//polygon字符串：CNAppDelegate.match_stringTrackZone,可能多个用;分隔
		double min_lon = 0;
	    double min_lat = 0;
	    double max_lon = 0;
	    double max_lat = 0;
	    String[] tracklist = CNAppDelegate.match_stringTrackZone.split(":");
	    for(int i=0;i<tracklist.length;i++){
	    	String[] oneTrackStrlist = tracklist[i].split(", ");
			List<LatLng> points = new ArrayList<LatLng>();
			for(int j=0;j<oneTrackStrlist.length;j++){
				String[] lonlat = oneTrackStrlist[j].split(" ");
				double lon = Double.parseDouble(lonlat[0]);
				double lat = Double.parseDouble(lonlat[1]);
				if(i == 0 && j == 0){
					max_lon = min_lon = lon;
	                max_lat = min_lat = lat;
				}
				if(lon < min_lon){
	                min_lon = lon;
	            }
	            if(lat < min_lat){
	                min_lat = lat;
	            }
	            if(lon > max_lon){
	                max_lon = lon;
	            }
	            if(lat > max_lat){
	                max_lat = lat;
	            }
				points.add(new LatLng(lat, lon));
			}
			aMap.addPolygon(new PolygonOptions()
			    .addAll(points)
			    .fillColor(Color.argb(50, 0, 0, 1)).strokeColor(Color.TRANSPARENT).strokeWidth(0));
	    }
//	    LatLng latlon1 = new LatLng(min_lat, min_lon);
//		LatLng latlon2 = new LatLng(max_lat, max_lon);
//		LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
//		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
		
	}
	void drawStratZone(){
		//polygon字符串：CNAppDelegate.match_stringStartZone只有一个
		String[] tracklist = CNAppDelegate.match_stringStartZone.split(", ");
		List<LatLng> points = new ArrayList<LatLng>();
		for(int i=0;i<tracklist.length;i++){
			String[] lonlat = tracklist[i].split(" ");
			points.add(new LatLng(Double.parseDouble(lonlat[1]), Double.parseDouble(lonlat[0])));
		}
		aMap.addPolygon(new PolygonOptions()
		    .addAll(points)
		    .fillColor(Color.TRANSPARENT).strokeColor(Color.argb(255, 255, 165, 0)).strokeWidth(3));
	}
	void drawTakeOverZone(){
		//polygon字符串：CNAppDelegate.match_takeover_zone只有一个
		String[] tracklist = CNAppDelegate.match_takeover_zone.split(", ");
		List<LatLng> points = new ArrayList<LatLng>();
		for(int i=0;i<tracklist.length;i++){
			String[] lonlat = tracklist[i].split(" ");
			points.add(new LatLng(Double.parseDouble(lonlat[1]), Double.parseDouble(lonlat[0])));
		}
		aMap.addPolygon(new PolygonOptions()
		    .addAll(points)
		    .fillColor(Color.argb(130, 255, 0, 0)).strokeColor(Color.TRANSPARENT).strokeWidth(0));
	}
	void drawRunTrack(){
		//把CNAppDelegate.match_pointList画到地图上，如果遇到0,0则分段
		int j = 0;
	    int i = 0;
	    int n = 0;
	    int pointCount = CNAppDelegate.match_pointList.size();
	    if(pointCount < 2){
	    	return;
	    }
	    for(i=0;i<pointCount;i++){
	        CNGPSPoint4Match gpsPoint = CNAppDelegate.match_pointList.get(i);
	        if (gpsPoint.getLon() < 0.01 || i == pointCount-1) {
	            List<LatLng> points = new ArrayList<LatLng>();
	            for(j=0;j<i-n;j++){
	                CNGPSPoint4Match gpsPoint2 = CNAppDelegate.match_pointList.get(n+j);
	                points.add(new LatLng(gpsPoint2.getLat(),gpsPoint2.getLon()));
	            }
	            aMap.addPolyline((new PolylineOptions()).addAll(points).color(Color.GREEN).width(13f));
	            n = i+1;//n为下一个起点
	        }
	    }
	}
	void drawIncrementLine(){
		if(CNAppDelegate.match_pointList.size() < 2)return;
		CNGPSPoint4Match newPoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);//取得当前最新点
		//连接地图上最后画的一个点和newPoint
		if(lastDrawPoint == null){
			lastDrawPoint = newPoint;
			return;
		}
		if(newPoint.getLon() != lastDrawPoint.getLon() || newPoint.getLat() != lastDrawPoint.getLat()){//5秒后点的位置有移动
	        List<LatLng> points = new ArrayList<LatLng>();
	        points.add(new LatLng(lastDrawPoint.getLat(),lastDrawPoint.getLon()));
	        points.add(new LatLng(newPoint.getLat(),newPoint.getLon()));
	        aMap.addPolyline((new PolylineOptions()).addAll(points).color(Color.GREEN).width(13f));
	        lastDrawPoint = newPoint;
	        aMap.invalidate();
	    }
	    
	}
	/**
	 * 初始化AMap对象
	 */
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
					Log.v("wymap", "移动地图，isfollow="+isFollow);
				}
			});
			setUpMap();
		}

		backV = (ImageView) findViewById(R.id.match_map_back);
		match_map_loc = (ImageView) findViewById(R.id.match_map_loc);
		match_baton = (ImageView) findViewById(R.id.match_baton);
		image_gps = (ImageView) findViewById(R.id.match_map_gps_status);
		
		match_map_loc.setOnTouchListener(this);
		match_baton.setOnTouchListener(this);
		backV.setOnTouchListener(this);
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
		task_drawLine = new TimerTask_drawLine();
		timer_match_map = new Timer();
		timer_match_map.schedule(task_drawLine, 2000, CNAppDelegate.kMatchInterval*1000);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
		MobclickAgent.onPause(this);
		if(timer_match_map != null){
			timer_match_map.cancel();
			timer_match_map = null;
			if(task_drawLine != null){
				task_drawLine.cancel();
				task_drawLine = null;
			}
		}
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
			mListener.onLocationChanged(aLocation);
			if (isFollow == 1) {
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aLocation.getLatitude(), aLocation.getLongitude())));
			}
			Log.v("wymap", "位置变化，isfollow="+isFollow);
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
		case R.id.match_map_loc:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				match_map_loc.setBackgroundResource(R.drawable.button_position_h);
				break;
			case MotionEvent.ACTION_UP:
				match_map_loc.setBackgroundResource(R.drawable.button_position);
				Location myloc = aMap.getMyLocation();
				if (myloc != null) {
					isFollow = 1;
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(myloc.getLatitude(), myloc.getLongitude())));
				}

				break;
			}
			break;
			
		case R.id.match_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				match_baton.setBackgroundResource(R.drawable.match_baton_h);
				break;
			case MotionEvent.ACTION_UP:
				match_baton.setBackgroundResource(R.drawable.match_baton);
				CNGPSPoint4Match gpspoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
				int isInTakeOverZone;
				if(CNAppDelegate.istest){
					isInTakeOverZone = 0;
				}else{
					isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpspoint.getLon(),gpspoint.getLat());
				}
				if(isInTakeOverZone != -1){
					Intent intent = new Intent(MatchMapActivity.this,
							MatchGiveRelayActivity.class);
					startActivity(intent);
	            }else{
	            	Intent intent = new Intent(MatchMapActivity.this,
	            			MatchGiveRelayActivity.class);
					startActivity(intent);
	            }
				break;
			}
			break;
		}
		return true;
	}


/**
 * 设置一些amap的属性
 */
private void setUpMap() {
	aMap.getUiSettings().setZoomControlsEnabled(false);
	aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//	aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
	aMap.setLocationSource(this);// 设置定位监听
	aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

}
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_HOME) {
		// Toast.makeText(SportRecordActivity.this, "", duration)
	}
	return false;
}

//gps状态接收广播
private BroadcastReceiver gpsStateReceiver = new BroadcastReceiver() {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		unregisterReceiver(this);
		int rank = intent.getExtras().getInt("state");
		switch (rank) {
		case 1:
			image_gps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_1));
			break;
		case 2:
			image_gps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_2));
			break;
		case 3:
			image_gps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_3));
			break;
		case 4:
			image_gps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_4));
			break;

		default:
			image_gps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_1));
			break;
		}
	}
};
}
