package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

/**
 */

public class MatchGiveRelayActivity extends BaseActivity implements
		OnTouchListener {
	private TextView label_back, label_finish, confirm, cancel, label_user1,
			label_user2, label_user3, label_user4, label_myname, label_test,label_giveWho;
	private ImageView button_user1, button_user2, button_user3, button_user4, image_me,
			image_gps;


	private RelativeLayout relay_wait_header_layout1,relay_wait_header_layout2,text_button,view_user1,view_user2,view_user3;
	
	String joinid;
	String joinid1;
	String joinid2;
	String joinid3;
	String avatarurl1;
	String avatarurl2;
	String avatarurl3;
	Timer timer_look_submit = null;
	TimerTask_scan task_look_submit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_give_relay);
		init();
	    if(Variables.avatar != null){
	    	image_me.setImageBitmap(Variables.avatar);
	    }
	    label_myname.setText(Variables.userinfo.getString("nickname"));
	}

	private void init() {
		label_back = (TextView) findViewById(R.id.relay_continue);
		label_back.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		label_finish = (TextView) findViewById(R.id.relay_end);
		label_finish.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		confirm = (TextView) findViewById(R.id.relay_chosen_confirm);
		cancel = (TextView) findViewById(R.id.relay_chosen_cancel);
		label_test = (TextView) findViewById(R.id.out_delay_tip1);

		label_user1 = (TextView) findViewById(R.id.relay_wait_nickname1);
		label_user2 = (TextView) findViewById(R.id.relay_wait_nickname2);
		label_user3 = (TextView) findViewById(R.id.relay_wait_nickname3);
		label_user4 = (TextView) findViewById(R.id.relay_chosen_nickname);
		label_myname = (TextView) findViewById(R.id.relay_nickname);
		label_giveWho = (TextView) findViewById(R.id.relay_chosen_name);

		button_user1 = (ImageView) findViewById(R.id.relay_wait_head1);
		button_user2 = (ImageView) findViewById(R.id.relay_wait_head2);
		button_user3 = (ImageView) findViewById(R.id.relay_wait_head3);
		button_user4 = (ImageView) findViewById(R.id.relay_chosen_head);
		
		view_user1 =  (RelativeLayout) findViewById(R.id.relay_wait_head_layout1);
		view_user2 =  (RelativeLayout) findViewById(R.id.relay_wait_head_layout2);
		view_user3 =  (RelativeLayout) findViewById(R.id.relay_wait_head_layout3);
		image_me = (ImageView) findViewById(R.id.relay_head);

		relay_wait_header_layout1 = (RelativeLayout) findViewById(R.id.relay_wait_head_layout);
		relay_wait_header_layout2 = (RelativeLayout) findViewById(R.id.relay_wait_header_layout2);
		text_button = (RelativeLayout) findViewById(R.id.text_button);

		label_back.setOnTouchListener(this);
		view_user1.setOnTouchListener(this);
		view_user2.setOnTouchListener(this);
		view_user3.setOnTouchListener(this);

		label_finish.setOnTouchListener(this);
		label_back.setOnTouchListener(this);
		confirm.setOnTouchListener(this);
		cancel.setOnTouchListener(this);

	}
	class TimerTask_scan extends TimerTask{
		  @Override
		  public void run() {
			  requestTransmit();
		  }
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		task_look_submit = new TimerTask_scan();
		timer_look_submit = new Timer();
		timer_look_submit.schedule(task_look_submit, 0, CNAppDelegate.kScanTransmitinterval*1000);
		
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}
	void requestTransmit(){
		new ScanTransmitTask().execute("");
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if(timer_look_submit != null){
			timer_look_submit.cancel();
			timer_look_submit = null;
			if(task_look_submit != null){
				task_look_submit.cancel();
				task_look_submit = null;
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
		// 点击不交棒
		case R.id.relay_continue:
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				new CancelTransmitTask().execute("");
				finish();
				break;
			}
			break;
		// 点击中间的头像
		case R.id.relay_wait_head_layout1:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(CNAppDelegate.avatarDic.get(avatarurl1),label_user1.getText().toString());
				label_giveWho.setText(label_user1.getText().toString());
				joinid = joinid1;
				break;
			}
			break;
		// 点击左侧的头像
		case R.id.relay_wait_head_layout2:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(CNAppDelegate.avatarDic.get(avatarurl2),label_user2.getText().toString());
				label_giveWho.setText(label_user2.getText().toString());
				joinid = joinid2;
				break;
			}
			break;
		// 点击右侧头像
		case R.id.relay_wait_head_layout3:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(CNAppDelegate.avatarDic.get(avatarurl3),label_user3.getText().toString());
				label_giveWho.setText(label_user3.getText().toString());
				joinid = joinid3;
				break;
			}
			break;

		// 点击确认交棒
		case R.id.relay_chosen_confirm:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				new ComfirmTransmitTask().execute("");
				break;
			}
			break;
		// 点击取消交棒
		case R.id.relay_chosen_cancel:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout2Dismiss();
				break;
			}
			break;
			
		case R.id.relay_end:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Toast.makeText(MatchGiveRelayActivity.this,"super ="+super.activityOnFront+" this= "+this.getClass().getSimpleName()+"v ="+Variables.activityOnFront, Toast.LENGTH_LONG).show();
				quit();
				break;
			}
			break;
			
		//needwy 加上结束的按钮事件弹两次
		}
		return true;
	}

	private void headerLayout1Dismiss(Bitmap avatar,String name) {
		relay_wait_header_layout1.setVisibility(View.GONE);
		relay_wait_header_layout2.setVisibility(View.VISIBLE);
		text_button.setVisibility(View.GONE);
		if (avatar!=null) {
			button_user4.setImageBitmap(avatar);
		}
		label_user4.setText(name);
		
	}

	private void headerLayout2Dismiss() {
		relay_wait_header_layout1.setVisibility(View.VISIBLE);
		label_test.setVisibility(View.VISIBLE);
		relay_wait_header_layout2.setVisibility(View.GONE);
	}

	// 搜索到队员后隐藏正在搜索提示
	private void tipDismiss() {
		label_test.setVisibility(View.GONE);
		relay_wait_header_layout1.setVisibility(View.VISIBLE);
		//设置接棒队员的头像昵称
	}
	void downloadImage(int index){
	    String imagePath = "";
	    if(index == 1){
	        imagePath = avatarurl1;
	    }else if(index == 2){
	        imagePath = avatarurl2;
	    }else if(index == 3){
	        imagePath = avatarurl3;
	    }
	    RequestImageTask requestTask = new RequestImageTask();
    	requestTask.index = index;
    	requestTask.avatarUrl = imagePath;
    	requestTask.execute("");
	}
	private class ScanTransmitTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","持棒选手交接棒参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.transmitRelay, request_params);
		    Log.v("zc","持棒选手交接棒扫描返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchGiveRelayActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				JSONArray array = resultDic.getJSONArray("list");
			    if(array.size()<1){
			        label_test.setText("正在搜索接棒队员，请稍候...");
			    }else{
			        label_test.setText(String.format("搜索到%d个人", array.size()));
			        
			        view_user1.setVisibility(View.GONE);
			        view_user2.setVisibility(View.GONE);
			        view_user3.setVisibility(View.GONE);
			        //必有用户1
			        view_user1.setVisibility(View.VISIBLE);
			        JSONObject run_user_dic = array.getJSONObject(0);
			        label_user1.setText(run_user_dic.getString("nickname"));
			        joinid1 = run_user_dic.getString("uid");
			        avatarurl1 = run_user_dic.getString("imgpath");
			        if(avatarurl1 == null){
			            button_user1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			        }else{
			            Bitmap image = CNAppDelegate.avatarDic.get(avatarurl1);
			            if(image != null){//缓存中有
			                button_user1.setImageBitmap(image);
			            }else{//下载
			                downloadImage(1);
			            }
			        }
			        if(array.size()>1){
			        	view_user2.setVisibility(View.VISIBLE);
				        run_user_dic = array.getJSONObject(1);
				        label_user2.setText(run_user_dic.getString("nickname"));
				        joinid2 = run_user_dic.getString("uid");
				        avatarurl2 = run_user_dic.getString("imgpath");
				        if(avatarurl2 == null){
				            button_user2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
				        }else{
				            Bitmap image = CNAppDelegate.avatarDic.get(avatarurl2);
				            if(image != null){//缓存中有
				                button_user2.setImageBitmap(image);
				            }else{//下载
				                downloadImage(2);
				            }
				        }
			        }
			        if(array.size()>2){
			        	view_user3.setVisibility(View.VISIBLE);
				        run_user_dic = array.getJSONObject(2);
				        label_user3.setText(run_user_dic.getString("nickname"));
				        joinid3 = run_user_dic.getString("uid");
				        avatarurl3 = run_user_dic.getString("imgpath");
				        if(avatarurl3 == null){
				            button_user3.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
				        }else{
				            Bitmap image = CNAppDelegate.avatarDic.get(avatarurl3);
				            if(image != null){//缓存中有
				                button_user3.setImageBitmap(image);
				            }else{//下载
				                downloadImage(3);
				            }
				        }
			        }
			    }
			}
		}
	}
	private class RequestImageTask extends AsyncTask<String, Void, Boolean> {
		public int index;
		public String avatarUrl;
		Bitmap image = null;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			
			try{
				image = BitmapFactory.decodeStream(getImageStream(Constants.endpoints_img+avatarUrl));
			}catch(Exception e){
				e.printStackTrace();
			}
			if(image != null){
				CNAppDelegate.avatarDic.put(avatarUrl, image);
		        return true;
		    }else{
		    	return false;
		    }
			
			
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				if(index == 1){
					button_user1.setImageBitmap(image);
				}else if(index == 2){
					button_user2.setImageBitmap(image);
				}else if(index == 3){
					button_user3.setImageBitmap(image);
				}
			}
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
	private class ComfirmTransmitTask extends AsyncTask<String, Void, Boolean> {
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
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&joinid=%s&longitude=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,joinid,pointJson);
		    Log.v("zc","确认交棒参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.confirmTransmit, request_params);
		    Log.v("zc","确认交棒返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.isbaton = 0;
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.endMatch,MatchGiveRelayActivity.this);
				if(timer_look_submit != null){
					timer_look_submit.cancel();
					timer_look_submit = null;
				}
				CNAppDelegate.cancelMatchTimer();
				
				startAnimation();
				CNAppDelegate.finishThisRun();
			    //播放语音
//			    NSMutableDictionary* voice_params = [[NSMutableDictionary alloc]init];
//			    [voice_params setObject:[NSString stringWithFormat:@"%f",kApp.match_totaldis] forKey:@"distance"];
//			    [voice_params setObject:[NSString stringWithFormat:@"%i",kApp.match_historySecond] forKey:@"second"];
//			    [kApp.voiceHandler voiceOfapp:@"match_running_transmit_relay" :voice_params];//needwy
			    Intent intent = new Intent(MatchGiveRelayActivity.this,MatchFinishActivity.class);
				startActivity(intent);
			} else {
				
			}
		}

	}
	void startAnimation(){
		//needwy
	}
	private class CancelTransmitTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","取消交接棒参数是 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.cancelTransmit, request_params);
		    Log.v("zc","取消交接棒返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchGiveRelayActivity.this);
			}
		}
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
		    Log.v("zc","结束比赛返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) { 
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.endMatch,MatchGiveRelayActivity.this);
				CNAppDelegate.ForceGoMatchPage("finish");
			} else {
			}
		}
	}
	public void quit() {
		new AlertDialog.Builder(MatchGiveRelayActivity.this).setTitle(R.string.app_name).setIcon(R.drawable.icon_s)
				.setMessage("确认提前结束比赛？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						finishMatch();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
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
