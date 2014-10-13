package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNLonLat;
import net.yaopao.assist.Constants;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchGroupInfoActivity extends BaseActivity implements OnTouchListener,
		OnMapClickListener {
	private MapView mapView;
	private AMap aMap;
	private RelativeLayout button_list;
	private RelativeLayout button_message;
	private RelativeLayout button_me;
	private ImageView button_relay;
	
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
	
	private ImageView image_avatar,imageview_dot;
	private TextView label_uname,label_tName,button_back,label_date,label_time,label_pspeed,label_avr_speed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_watch);
		if(getIntent().getExtras() != null){
			from = getIntent().getExtras().getString("from");
		}
		mapView = (MapView) findViewById(R.id.match_watch_map);
		mapView.onCreate(savedInstanceState);
		mapView.setOnTouchListener(this);
		lonLatEncryption = new LonLatEncryption();
		init();
		Log.v("zc","CNAppDelegate.hasFinishTeamMatch is "+CNAppDelegate.hasFinishTeamMatch);
		if(CNAppDelegate.hasFinishTeamMatch){
			button_message.setVisibility(View.GONE);
			button_relay.setVisibility(View.GONE);
	    }else{
	    	button_message.setVisibility(View.VISIBLE);
			button_relay.setVisibility(View.VISIBLE);
	    }
	    if("main".equals(from)){
	        button_back.setVisibility(View.VISIBLE);
	    }else{
	        button_back.setVisibility(View.GONE);
	    }
	    label_uname.setText(Variables.userinfo.getString("nickname"));
	    label_tName.setText(CNAppDelegate.matchDic.getString("groupname"));
	    if(Variables.avatar != null){
	        image_avatar.setImageBitmap(Variables.avatar);
	    }
//	    self.div = [[CNDistanceImageView alloc]initWithFrame:CGRectMake(5, 200+IOS7OFFSIZE, 130, 32)];
//	    self.div.distance = 0;
//	    self.div.color = @"red";
//	    [self.div fitToSizeLeft];
//	    [self.view addSubview:self.div];
//	    self.image_km = [[UIImageView alloc]initWithFrame:CGRectMake(self.div.frame.origin.x+self.div.frame.size.width, 200+IOS7OFFSIZE,26, 32)];
//	    self.image_km.image = [UIImage imageNamed:@"redkm.png"];
//	    [self.view addSubview:self.image_km];needwy
	    drawTrack();//画赛道
	    drawTakeOverZone();//画接力区
	}
	void drawTrack(){
		
	}
	void drawTakeOverZone(){
		
	}
	private void init() {
		setUpMap();
		button_list = (RelativeLayout) findViewById(R.id.match_watch_score);
		button_message = (RelativeLayout) findViewById(R.id.match_watch_message);
		button_me = (RelativeLayout) findViewById(R.id.match_watch_user);
		
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
		

		button_list.setOnTouchListener(this);
		button_message.setOnTouchListener(this);
		button_me.setOnTouchListener(this);
		button_relay.setOnTouchListener(this);
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
				Intent intent = new Intent(MatchGroupInfoActivity.this,
						MatchNoRunMapActivity.class);
				startActivity(intent);
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
	}
	class TimerTask_request extends TimerTask{
		@Override
		public void run() {
			requestData();
		}
	}
	void requestData(){
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
		case R.id.match_watch_score:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchGroupInfoActivity.this,
						MatchGroupListActivity.class);
				startActivity(intent);
				break;
			}
			break;
		case R.id.match_watch_message:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
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
				break;
			case MotionEvent.ACTION_UP:
				Intent recIntent = new Intent(MatchGroupInfoActivity.this,
						SportListActivity.class);
				startActivity(recIntent);
				break;
			}
			break;
		case R.id.match_get_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
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
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchGroupInfoActivity.this);
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
			    
//			    self.div.distance = (distance+5)/1000.0;
//			    [self.div fitToSizeLeft];
//			    self.image_km.frame = CGRectMake(self.div.frame.origin.x+self.div.frame.size.width, 200+IOS7OFFSIZE,26, 32);needwy
				
				JSONObject infoDic = resultDic.getJSONObject("longitude");
				if(infoDic.isEmpty()){
					return;
				}
				long time = infoDic.getLongValue("uptime");//毫秒
//		        self.label_date.text = [CNUtil dateStringFromTimeStamp:time];needwy
		        int duringTime = (int)(time-CNAppDelegate.match_start_timestamp);//秒
//		        self.label_time.text = [CNUtil duringTimeStringFromSecond:duringTime];needwy
		        if(distance>1){
		            int speed_second = (int) (1000*(duringTime/distance));//秒
//		            self.label_pspeed.text = [CNUtil pspeedStringFromSecond:speed_second];//needwy
//		            float perspeed = [CNUtil speedFromPspeed:speed_second];
//		            self.label_avr_speed.text = [NSString stringWithFormat:@"%0.2f",perspeed];//needwy
		        }
			    lon = infoDic.getDoubleValue("slon");
			    lat = infoDic.getDoubleValue("slat");
			    //needwy 将地图中心点移动到lon，lat，level=16
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
//		[self.mapView removeAnnotation:self.annotation];
//	    self.annotation = [[MAPointAnnotation alloc] init];
//	    self.annotation.coordinate = CLLocationCoordinate2DMake(self.lat, self.lon);
//	    [self.mapView addAnnotation:self.annotation];needwy:lon,lat,avatarImage
	}
	
	void downloadImage(){
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
	void displayLoading(){
	    disableAllButton();
	}
	void hideLoading(){
	    enableAllButton();
	}
	void disableAllButton(){
	}
	void enableAllButton(){
	}
}
