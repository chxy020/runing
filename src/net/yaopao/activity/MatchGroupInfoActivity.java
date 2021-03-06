package net.yaopao.activity;

import java.io.InputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNLonLat;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
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
public class MatchGroupInfoActivity extends BaseActivity implements OnTouchListener,OnMapClickListener,LocationSource ,AMapLocationListener {
	private MapView mapView;
	private AMap aMap;
	private RelativeLayout button_list;
	private RelativeLayout button_message;
	private RelativeLayout button_me;
	private RelativeLayout match_get_baton_layout;
	private RelativeLayout mapContainer;
	private RelativeLayout titleBar;
	private RelativeLayout bottombar;
	private ImageView button_relay; 
	private ImageView backV;
	private ImageView match_map_loc ;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	
	Resources resource; 
	
//	private ImageView button_relay;
	
	String from;
	Timer timer_refresh_data = null;
	TimerTask_request task_request;
	Marker annotation;
	String imagePath;
	Bitmap avatarImage;
	double lon;//最新位置
	double lat;//最新位置
	private LonLatEncryption lonLatEncryption;
	
	private ImageView image_avatar,imageview_dot,d1V,d2V,d3V,d4V,d5V,d6V,dot,km;
	private TextView label_uname,label_tName,button_back,label_date,label_time,label_pspeed,label_avr_speed;
	
	private LoadingDialog loadingDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_watch);
		if(getIntent().getExtras() != null){
			from = getIntent().getExtras().getString("from");
		}
		resource = getResources();
		mapView = (MapView) findViewById(R.id.match_watch_map);
		mapView.onCreate(savedInstanceState);
		mapView.setOnTouchListener(this);
		lonLatEncryption = new LonLatEncryption();
		init();
		Log.v("zc","CNAppDelegate.hasFinishTeamMatch is "+CNAppDelegate.hasFinishTeamMatch);
		if(CNAppDelegate.hasFinishTeamMatch){
			button_message.setVisibility(View.GONE);
//			button_relay.setVisibility(View.GONE);
			match_get_baton_layout.setVisibility(View.GONE);
	    }else{
	    	button_message.setVisibility(View.VISIBLE);
//			button_relay.setVisibility(View.VISIBLE);
	    	match_get_baton_layout.setVisibility(View.VISIBLE);
	    }
	    if("main".equals(from)){
	        button_back.setVisibility(View.VISIBLE);
	        button_back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					MatchGroupInfoActivity.this.finish();
					
				}
			});
	    }else{
	        button_back.setVisibility(View.GONE);
	    }
	    label_uname.setText(Variables.userinfo.getString("nickname"));
	    label_tName.setText(CNAppDelegate.matchDic.getString("groupname"));
	    if(Variables.avatar != null){
	        image_avatar.setImageBitmap(Variables.avatar);
	    }
	    initMileage(0);
	    drawTrack();//画赛道
	    drawTakeOverZone();//画接力区
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
	private void init() {
		setUpMap();
		button_list = (RelativeLayout) findViewById(R.id.match_watch_score);
		button_message = (RelativeLayout) findViewById(R.id.match_watch_message);
		button_me = (RelativeLayout) findViewById(R.id.match_watch_user);
		match_get_baton_layout = (RelativeLayout) findViewById(R.id.match_get_baton_layout);
		mapContainer = (RelativeLayout)findViewById(R.id.match_watch_map_layout);
		titleBar = (RelativeLayout)findViewById(R.id.match_watch_top_bar);
		bottombar = (RelativeLayout)findViewById(R.id.match_bottom);
		
		backV = (ImageView) findViewById(R.id.match_full_map_back);
		match_map_loc = (ImageView) findViewById(R.id.match_map_loc);
		backV.setOnTouchListener(this);
		match_map_loc.setOnTouchListener(this);
		
		button_relay = (ImageView) findViewById(R.id.match_get_baton);
		image_avatar = (ImageView) findViewById(R.id.match_watch_head);	
		imageview_dot = (ImageView) findViewById(R.id.message_red_dot);
		
		label_uname = (TextView) findViewById(R.id.username);
		label_tName = (TextView) findViewById(R.id.match_watch_title);
		button_back = (TextView) findViewById(R.id.button_back);
		label_date = (TextView) findViewById(R.id.match_watch_date);
		label_time = (TextView) findViewById(R.id.match_watch_time);
		label_pspeed = (TextView) findViewById(R.id.match_watch_pspeed);
		label_avr_speed = (TextView) findViewById(R.id.match_watch_avg_speed);
		
		dot = (ImageView) this.findViewById(R.id.list_sport_dot);
		dot.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_dot));
		km =  (ImageView) this.findViewById(R.id.list_sport_km);
		km.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_km));
		
		d1V = (ImageView) this.findViewById(R.id.list_sport_num1);
		d2V = (ImageView) this.findViewById(R.id.list_sport_num2);
		d3V = (ImageView) this.findViewById(R.id.list_sport_num3);
		d4V = (ImageView) this.findViewById(R.id.list_sport_num4);
		d5V = (ImageView) this.findViewById(R.id.list_sport_dec1);
		d6V = (ImageView) this.findViewById(R.id.list_sport_dec2);

		button_list.setOnTouchListener(this);
		button_message.setOnTouchListener(this);
		button_me.setOnTouchListener(this);
		button_relay.setOnTouchListener(this);
		
		loadingDialog= new LoadingDialog(this);
		loadingDialog.setCancelable(false);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
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
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setScrollGesturesEnabled(false);
		aMap.getUiSettings().setZoomGesturesEnabled(false);
		aMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
//				Intent intent = new Intent(MatchGroupInfoActivity.this,
//						MatchNoRunMapActivity.class);
//				startActivity(intent);
				displaymap("big");
			}
		});
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
		if(CNAppDelegate.hasMessage){
	        imageview_dot.setVisibility(View.VISIBLE);
	    }else{
	    	imageview_dot.setVisibility(View.GONE);
	    }
		task_request = new TimerTask_request();
		timer_refresh_data = new Timer();
		timer_refresh_data.schedule(task_request, 0, CNAppDelegate.kMatchReportInterval*1000);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}
	class TimerTask_request extends TimerTask{
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
	void requestData(){
		displayLoading();
		new RequestTask().execute("");
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		MobclickAgent.onPause(this);
		if(timer_refresh_data != null){
			timer_refresh_data.cancel();
			timer_refresh_data = null;
			if(task_request != null){
				task_request.cancel();
				task_request = null;
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_full_map_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				displaymap("small");
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
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(myloc.getLatitude(), myloc.getLongitude())));
				}

				break;
			}
			break;
		case R.id.match_watch_score:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_list.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				button_list.setBackgroundResource(R.color.white);
				Intent intent = new Intent(MatchGroupInfoActivity.this,
						MatchGroupListActivity.class);
				startActivity(intent);
				break;
			}
			break;
		case R.id.match_watch_message:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_message.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				button_message.setBackgroundResource(R.color.white);
				Intent messageIntent = new Intent(this,WebViewActivity.class);
				messageIntent.putExtra("net.yaopao.activity.PageUrl",
							"message_index.html");
				startActivity(messageIntent);
				break;
			}
			break;
		case R.id.match_watch_user:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_me.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				button_me.setBackgroundResource(R.color.white);
				Intent recIntent = new Intent(MatchGroupInfoActivity.this,
						SportListActivity.class);
				startActivity(recIntent);
				break;
			}
			break;
		case R.id.match_get_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_relay.setBackgroundResource(R.drawable.match_baton_h);
				break;
			case MotionEvent.ACTION_UP:
				button_relay.setBackgroundResource(R.drawable.match_baton);
				if(CNAppDelegate.istest){
					Intent intent = new Intent(MatchGroupInfoActivity.this,
	        				MatchNotRunTransmitRelayActivity.class);
	        		startActivity(intent);
				}else{
					if(YaoPao01App.loc == null){
						Toast.makeText(this, "没有定上位，请稍后重试", Toast.LENGTH_LONG).show();
						return true;
					}
					CNLonLat wgs84Point = new CNLonLat(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude());
					CNLonLat encryptionPoint = lonLatEncryption.encrypt(wgs84Point);
					int isInTakeOverZone;
					isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(encryptionPoint.getLon(),encryptionPoint.getLat());
		            if(isInTakeOverZone != -1){
		            	Intent intent = new Intent(MatchGroupInfoActivity.this,
		        				MatchNotRunTransmitRelayActivity.class);
		        		startActivity(intent);
		            }else{
		            	Intent intent = new Intent(MatchGroupInfoActivity.this,
		            			MatchNotInActivity.class);
		        		startActivity(intent);
		            }
				}
				break;
			}
			break;
		}
		return true;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		Intent intent = new Intent(MatchGroupInfoActivity.this,
				MatchNoRunMapActivity.class);
		startActivity(intent);
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
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.smallMapPage,MatchGroupInfoActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				if(resultDic.getJSONObject("announcement")!=null&&!resultDic.getJSONObject("announcement").isEmpty()){
		            JSONObject messageDic = resultDic.getJSONObject("announcement");
		            int isann = messageDic.getIntValue("isann");
		            if(isann == 0){
		            	CNAppDelegate.hasMessage = false;
		            }else{
		            	CNAppDelegate.hasMessage = true;
		            }
		        }
				
				
				double distance = resultDic.getDoubleValue("distancegr");
			    
				initMileage(distance+5);
				JSONObject infoDic = resultDic.getJSONObject("longitude");
				if(infoDic.isEmpty()){
					return;
				}
				long time = infoDic.getLongValue("uptime");//毫秒
				Date date = new Date(time);
				label_date.setText(DateFormat.getDateInstance(DateFormat.FULL).format(date));
				
		        int duringTime = (int)(time/1000-CNAppDelegate.match_start_timestamp);//豪秒
		        Log.v("zc","duringTime:"+duringTime);
		        initTime(duringTime);
		        if(distance>1){
		            int speed_second = (int) (1000*(duringTime/distance));//秒
		            initPspeed(speed_second);
		            initavgSpeed(speed_second);
		        }
			    lon = infoDic.getDoubleValue("slon");
			    lat = infoDic.getDoubleValue("slat");
			    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));
			    
			    JSONObject runnerDic = resultDic.getJSONObject("runner");
			    imagePath = runnerDic.getString("imgpath");
			    //
			    avatarImage =  Variables.avatar_default;
			    if(imagePath == null){
			    	addAnnotation();
			    }else{
			        Bitmap image = CNAppDelegate.avatarDic.get(imagePath);
			        if(image != null){//缓存中有
			        	Log.v("zc","缓存中有");
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
		Bitmap popBitmap = CNAppDelegate.avatarDic.get(imagePath+"pop");
		if(popBitmap == null){//缓存中没有
			popBitmap = getViewBitmap(getView(avatarImage));
			CNAppDelegate.avatarDic.put(imagePath+"pop", popBitmap);
		}else{
			Log.v("zc","缓存已经存在气泡图片");
		}
		annotation = aMap.addMarker(new MarkerOptions()
		.position(new LatLng(lat, lon))
		.icon(BitmapDescriptorFactory.fromBitmap(popBitmap))		
		.anchor(0.5f, 0.5f));
		aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lon)));
		aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
		aMap.invalidate();
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
//	    disableAllButton();
//	}
//	void hideLoading(){
//	    enableAllButton();
//	}
//	void disableAllButton(){
//	}
//	void enableAllButton(){
//	}
	void displayLoading(){
		loadingDialog.show();
	}
	void hideLoading(){
		loadingDialog.dismiss();
	}
	private void initMileage(double distance) {
		d1V.setVisibility(View.GONE);
		d2V.setVisibility(View.GONE);
		d3V.setVisibility(View.GONE);
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
//		if (d1 > 0) {
//			d1V.setVisibility(View.VISIBLE);
//		}
//		if (d2 > 0) {
//			d2V.setVisibility(View.VISIBLE);
//		}
//		if (d3 > 0) {
//			d3V.setVisibility(View.VISIBLE);
//		}
		if (d1 > 0) {
			d1V.setVisibility(View.VISIBLE);
			d2V.setVisibility(View.VISIBLE);
			d3V.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			d2V.setVisibility(View.VISIBLE);
			d3V.setVisibility(View.VISIBLE); 
		}
		if (d3 > 0) {
			d3V.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateRedNum(
				new int[] { d1, d2, d3, d4, d5, d6 }, new ImageView[] { d1V,
						d2V, d3V, d4V, d5V, d6V });
	}
	private void initTime(long utime) {
		int[] time = YaoPao01App.cal(utime);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
//		label_time.setText(time[0] + ":" + time[1] + ":" +  time[2]);
		label_time.setText(t1 + "" + t2 + ":" + t3 + "" + t4 + ":"	+ t5 + "" + t6);
	}
	private void initPspeed(int pspeed) {
		int[] speed = YaoPao01App.cal(pspeed);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		label_pspeed.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" );
	}
	@SuppressLint("NewApi")
	private void initavgSpeed(int pspeed) {
		
		double avgSpeed =(float)pspeed/3600.0;
		avgSpeed =1.0/avgSpeed;
		DecimalFormat  df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.UP);
		label_avr_speed.setText(df.format(avgSpeed)+" km/h");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
			if (!"main".equals(from)) {
				DialogTool.quit(MatchGroupInfoActivity.this);
			}
		}
		return false;
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
	
	/**
	 * * 在view布局文件中中显示文字
	 * */
	public View getView(Bitmap avatar) {
		Log.v("zc","getview!!!");
		View view = getLayoutInflater().inflate(R.layout.marker_avatar, null);
		ImageView avatarInside = (ImageView) view.findViewById(R.id.marker_avatar);
		avatarInside.setImageBitmap(avatar);
		return view;
	}
	public void displaymap(String type){
		if(type.equals("big")){
			titleBar.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapContainer.getLayoutParams();   //取控件aaa当前的布局参数
			layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;    
			layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT; //当控件的高强制设成365象素
			mapContainer.setLayoutParams(layoutParams);
			bottombar.setVisibility(View.VISIBLE);
			setBigMap();
		}else if(type.equals("small")){
			titleBar.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapContainer.getLayoutParams();   //取控件aaa当前的布局参数
			layoutParams.height = (int) resource.getDimension(R.dimen.recording_save_pho_height);
			Log.v("zc","height="+layoutParams.height);
			layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT; //当控件的高强制设成365象素
			mapContainer.setLayoutParams(layoutParams);
			bottombar.setVisibility(View.GONE);
			setUpMap();
		}
	}
	
	private void setBigMap() {
		aMap.setOnMapClickListener(null);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.getUiSettings().setScrollGesturesEnabled(true);
		aMap.getUiSettings().setZoomGesturesEnabled(true);

	}
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
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
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);
		}
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
}
