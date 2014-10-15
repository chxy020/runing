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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



/**
 */
public class MatchNotInTakeOverActivity extends BaseActivity implements OnTouchListener {
	
	private ImageView imageview_avatar;
	private TextView label_uname;
	private TextView relay_end;
	private TextView button_back;
	private ImageView image_gps;
	Timer checkInTakeOver;
	TimerTask_check task_check;
	private LonLatEncryption lonLatEncryption;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		
		button_back = (TextView) findViewById(R.id.out_delay_tip_back);
		
		button_back.setOnTouchListener(this);
		relay_end.setOnTouchListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
		checkInTakeOver = new Timer();
		task_check = new TimerTask_check();
		
		checkInTakeOver.schedule(task_check, 1000, 1000);
	}
	class TimerTask_check extends TimerTask{
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					if (YaoPao01App.loc != null) {
						CNLonLat wgs84Point = new CNLonLat(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude());
						CNLonLat encryptionPoint = lonLatEncryption.encrypt(wgs84Point);
					    int isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(encryptionPoint.getLon(),encryptionPoint.getLat());
					    if(isInTakeOverZone != -1){
					        Intent intent = new Intent(MatchNotInTakeOverActivity.this,
			        				MatchGiveRelayActivity.class);
					        startActivity(intent);
					        finish();
					    }
					}
					
				}
			});
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if(checkInTakeOver!=null){
			checkInTakeOver.cancel();
			checkInTakeOver = null;
			if(task_check!=null){
				task_check.cancel();
				task_check = null;
			}
		}
		
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
				quit1();
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
	public void quit1() {
		new AlertDialog.Builder(MatchNotInTakeOverActivity.this).setTitle(R.string.app_name).setIcon(R.drawable.icon_s)
				.setMessage("结束跑队赛程意味着跑队比赛结束，成绩截止，其他队友也将无法继续参赛。您是否确认提前结束跑队的比赛？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						
						quit2();
						dialog.cancel();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();
	}
	public void quit2() {
		new AlertDialog.Builder(MatchNotInTakeOverActivity.this).setTitle(R.string.app_name).setIcon(R.drawable.icon_s)
		.setMessage("请再次确认提前结束跑队的比赛？")
		.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();				
			}
		})
		.setNegativeButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				finishMatch();
			}
		}).show();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}
 }
