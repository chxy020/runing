package net.yaopao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.CNLonLat;
import net.yaopao.assist.Constants;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;



/**
 */
public class MatchNotInTakeOverActivity extends BaseActivity implements OnTouchListener {
	
	private ImageView imageview_avatar;
	
	private TextView label_uname;
	
	private TextView relay_end;
	
	
	private ImageView button_back;
	
	private ImageView image_gps;
	
	Timer checkInTakeOver;
	private LonLatEncryption lonLatEncryption;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_not_in_take_over);
		lonLatEncryption = new LonLatEncryption();
		init();
		if(Variables.avatar != null){
			imageview_avatar.setImageBitmap(Variables.avatar);
	    }
	    label_uname.setText(Variables.userinfo.getString("nickname"));
	}
	private void init() {
		label_uname = (TextView) findViewById(R.id.relay_nickname);
		relay_end = (TextView) findViewById(R.id.relay_end);
		relay_end.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		imageview_avatar = (ImageView) findViewById(R.id.relay_head);
		
		image_gps = (ImageView) findViewById(R.id.relay_gps_status);
		
		button_back = (ImageView) findViewById(R.id.out_delay_tip_back);
		
		button_back.setOnTouchListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
		TimerTask task_check = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						CNLonLat wgs84Point = new CNLonLat(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude());
						CNLonLat encryptionPoint = lonLatEncryption.encrypt(wgs84Point);
					    int isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(encryptionPoint.getLon(),encryptionPoint.getLat());
					    if(isInTakeOverZone != -1){
					        finish();
					        Intent intent = new Intent(MatchNotInTakeOverActivity.this,
			        				MatchGiveRelayActivity.class);
					        startActivity(intent);
					    }
					}
				});
			}
		};
		checkInTakeOver.schedule(task_check, 1000, 1000);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		checkInTakeOver.cancel();
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.out_delay_tip_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				MatchNotInTakeOverActivity.this.finish();
				break;
			}
			break;
		case R.id.relay_end:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				//提前结束处理
				//needwy
				break;
			}
			break;
		}
		return true;
	}
	void finishMatch(){
		CNAppDelegate.saveMatchToRecord();
		CNAppDelegate.cancelMatchTimer();
	    CNAppDelegate.match_deleteHistoryPlist();
	    new FinishMatchTask().execute("");
	}
	private class FinishMatchTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			List<Map<String,String>> pointList = new ArrayList<Map<String,String>>();
		    Map<String,String> onepoint = new HashMap<String,String>();
		    CNGPSPoint4Match gpsPoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
		    onepoint.put("uptime", ""+gpsPoint.getTime()*1000);
		    onepoint.put("distanceur", ""+CNAppDelegate.match_totaldis);
		    onepoint.put("inrunway", ""+gpsPoint.getIsInTrack());
		    onepoint.put("slat", ""+gpsPoint.getLat());
		    onepoint.put("slon", ""+gpsPoint.getLon());
		    onepoint.put("mstate", "3");
		    pointList.add(onepoint);
		    String pointJson =JSON.toJSONString(pointList);
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&longitude=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,pointJson);
		    Log.v("zc","结束比赛参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.endMatch, request_params);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.endMatch,MatchNotInTakeOverActivity.this);
				CNAppDelegate.ForceGoMatchPage("finish");
			} else {
			}
		}

	}
 }
