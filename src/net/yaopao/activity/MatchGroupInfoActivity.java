package net.yaopao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
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
	
	private ImageView image_avatar;
	private TextView label_uname,label_tName,button_back,label_date,label_time,label_pspeed,label_avr_speed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_watch);
		mapView = (MapView) findViewById(R.id.match_watch_map);
		mapView.onCreate(savedInstanceState);
		mapView.setOnTouchListener(this);
		init();
	}

	private void init() {
		setUpMap();
		button_list = (RelativeLayout) findViewById(R.id.match_watch_score);
		button_message = (RelativeLayout) findViewById(R.id.match_watch_message);
		button_me = (RelativeLayout) findViewById(R.id.match_watch_user);
		
		button_relay = (ImageView) findViewById(R.id.main_start);
		image_avatar = (ImageView) findViewById(R.id.match_watch_head);		
		
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
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setOnMapClickListener(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
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
				Intent intent = new Intent(MatchGroupInfoActivity.this,
						MatchGiveRelayActivity.class);
				startActivity(intent);
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

}
