package net.yaopao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.RelativeLayout;

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
public class MatchWatchActivity extends Activity implements OnTouchListener,
		OnMapClickListener {
	private MapView mapView;
	private AMap aMap;
	private RelativeLayout scoreV;
	private RelativeLayout batonV;

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
		scoreV = (RelativeLayout) findViewById(R.id.match_watch_score);
		batonV = (RelativeLayout) findViewById(R.id.match_watch_baton);

		scoreV.setOnTouchListener(this);
		batonV.setOnTouchListener(this);
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
				Intent intent = new Intent(MatchWatchActivity.this,
						MatchScoreListActivity.class);
				startActivity(intent);
				break;
			}
			break;
		case R.id.match_watch_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchWatchActivity.this,
						MatchRelayActivity.class);
				startActivity(intent);
				break;
			}
			break;
		}
		return true;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		Intent intent = new Intent(MatchWatchActivity.this,
				MatchWatchMapActivity.class);
		startActivity(intent);
	}

}
