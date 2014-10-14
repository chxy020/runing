package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolygonOptions;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchNoRunMapActivity extends BaseActivity implements OnTouchListener {

	private ImageView backV;
	private MapView mapView;
	private AMap aMap;
	
	Timer timer_refresh_data;
	TimerTask_reqeust task_request;
	String imagePath;
	double lon;//最新位置
	double lat;//最新位置
	Bitmap avatarImage;
	Marker annotation;
	private LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_watch_map);
		mapView = (MapView) findViewById(R.id.match_full_map);
		mapView.onCreate(savedInstanceState);
		init();
		drawTrack();//画赛道
		drawTakeOverZone();//画接力区
		task_request = new TimerTask_reqeust();
		timer_refresh_data = new Timer();
		timer_refresh_data.schedule(task_request, 0, CNAppDelegate.kMatchReportInterval*1000);
	}
	class TimerTask_reqeust extends TimerTask{
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					requestData();
					
				}
			});
		}
	}
	private void init() {
		backV = (ImageView) findViewById(R.id.match_full_map_back);
		backV.setOnTouchListener(this);
		loadingDialog= new LoadingDialog(this);
		loadingDialog.setCancelable(false);
		setUpMap();
	}
	void drawTrack(){
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
	    LatLng latlon1 = new LatLng(min_lat, min_lon);
		LatLng latlon2 = new LatLng(max_lat, max_lon);
		LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
		aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
	}
	void drawTakeOverZone(){
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
	void requestData(){
		displayLoading();
		new RequestTask().execute("");
	}
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
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
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
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
		if(timer_refresh_data != null){
			timer_refresh_data.cancel();
			timer_refresh_data = null;
			if(task_request != null){
				task_request.cancel();
				task_request = null;
			}
		}
		
		MobclickAgent.onPause(this);
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_full_map_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				finish();
				break;
			}
			break;
		}
		return true;
	}
	private class RequestTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","查询跑步队员位置参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.smallMapPage, request_params);
		    Log.v("zc","查询跑步队员位置返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideLoading();
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchNoRunMapActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				JSONObject infoDic = resultDic.getJSONObject("longitude");
				if(infoDic.isEmpty()){
					return;
				}
			    lon = infoDic.getDoubleValue("slon");
			    lat = infoDic.getDoubleValue("slat");
			    JSONObject runnerDic = resultDic.getJSONObject("runner");
			    imagePath = runnerDic.getString("imgpath");
			    avatarImage =  BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null);
			    if(imagePath == null){
			    	addAnnotation();
			    }else{
			        Bitmap image = CNAppDelegate.avatarDic.get("imagePath");
			        if(image != null){//缓存中有
			        	avatarImage = image;
			        	addAnnotation();
			        }else{//下载
			        	downloadImage();
			        }
			    }
			} else {
				
			}
		}
	}
	void addAnnotation(){
		if(annotation != null){
			annotation.remove();
		}
		annotation = aMap.addMarker(new MarkerOptions()
		.position(new LatLng(lat, lon))
		.icon(BitmapDescriptorFactory.fromBitmap(avatarImage))
		.anchor(0.5f, 0.5f));
	}
	
	void downloadImage(){
		displayLoading();
		new RequestImageTask().execute("");
	}
	private class RequestImageTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Bitmap image = null;
			try{
				image = BitmapFactory.decodeStream(getImageStream(Constants.endpoints_img+imagePath));
			}catch(Exception e){
				e.printStackTrace();
			}
			if(image != null){
				CNAppDelegate.avatarDic.put(imagePath, image);
		        avatarImage = image;
		        return true;
		    }else{
		    	return false;
		    }
			
			
		}
		@Override
		protected void onPostExecute(Boolean result) {
			hideLoading();
			addAnnotation();
		}
	}
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}
//	void displayLoading(){
//    disableAllButton();
//}
//void hideLoading(){
//    enableAllButton();
//}
//void disableAllButton(){
//}
//void enableAllButton(){
//}
void displayLoading(){
	loadingDialog.show();
}
void hideLoading(){
	loadingDialog.show();
}
}
