package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



/**
 */
public class MatchNotRunTransmitRelayActivity extends BaseActivity implements OnTouchListener {
	
	private ImageView image_myavatar;
	private ImageView image_run_user;
//	private ImageView view_cartoon1;
//	private ImageView view_cartoon2;
	
	
	private TextView label_name;
	private TextView lable_run_user;
	
	private RelativeLayout view_back;
	private RelativeLayout view_run_user;
	private RelativeLayout relay_main;
	
	private TextView button_back;
	private ImageView image_gps;
	
	private ImageView relayAnim;
	private AnimationDrawable animationDrawable;  
	
	Timer timer_transmit;
	String imagePath_runner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_match_wait_relay);
		init();
		label_name.setText(Variables.userinfo.getString("nickname"));
	    if(Variables.avatar != null){
	    	image_myavatar.setImageBitmap(Variables.avatar);
	    }
	}
	private void init() {
		image_myavatar = (ImageView) findViewById(R.id.relay_wait_head);
		label_name = (TextView) findViewById(R.id.relay_wait_nickname);
		view_back = (RelativeLayout) findViewById(R.id.relay_wait_back_layout);
		
		image_run_user = (ImageView) findViewById(R.id.relay_head);
		lable_run_user = (TextView) findViewById(R.id.relay_nickname);
		view_run_user = (RelativeLayout) findViewById(R.id.relay_head_layout);
		
		image_gps = (ImageView) findViewById(R.id.relay_wait_gps_status);
		button_back = (TextView) findViewById(R.id.relay_wait_back);
		
		
		relayAnim = (ImageView) findViewById(R.id.relay_anim);
		relay_main = (RelativeLayout)findViewById(R.id.relay_main);
        animationDrawable = (AnimationDrawable) relayAnim.getDrawable();  
		button_back.setOnTouchListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
		TimerTask task_request = new TimerTask() {
			@Override
			public void run() {
				requestTransmit();
			}
		};
		timer_transmit = new Timer();
		timer_transmit.schedule(task_request, 0, CNAppDelegate.kScanTransmitinterval*1000);
	}
	void requestTransmit(){
		new RequestTask().execute("");
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		timer_transmit.cancel();
		timer_transmit = null;
	}

	private class RequestTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","交接棒扫描 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.transmitRelay, request_params);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchNotRunTransmitRelayActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				Log.v("zc","我要接棒返回 is "+responseJson);
				JSONArray array = resultDic.getJSONArray("list");
			    if(array!= null&&array.size()<1){
			        view_back.setVisibility(View.VISIBLE);
			        view_run_user.setVisibility(View.GONE);
			        JSONObject longitude = resultDic.getJSONObject("longitude");
			        if(longitude!=null&&!longitude.isEmpty()){//被确认接棒
			        	CNAppDelegate.match_totalDisTeam = longitude.getDoubleValue("distancegr");
			            startAnimation();
			            startmatch();
			        }else{//没有搜索到人
			            view_back.setVisibility(View.VISIBLE);
			            view_run_user.setVisibility(View.GONE);
			        }
			    }else{
			        view_back.setVisibility(View.GONE);
			        view_run_user.setVisibility(View.VISIBLE);
			        JSONObject run_user_dic = array.getJSONObject(0);
			        lable_run_user.setText(run_user_dic.getString("nickname"));
			        imagePath_runner = run_user_dic.getString("imgpath");
			        if(imagePath_runner == null){
			            image_run_user.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			        }else{
			            Bitmap image = CNAppDelegate.avatarDic.get("imagePath_runner");
			            if(image != null){//缓存中有
			                image_run_user.setImageBitmap(image);
			            }else{//下载
			                downloadImage();
			            }
			        }
			    }
			} else {
				
			}
		}
	}
	void startmatch(){
		Intent intent = new Intent(this,MatchMainActivity.class);
		startActivity(intent);
	}
	void downloadImage(){
        new RequestImageTask().execute("");
	}
	private class RequestImageTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;
		Bitmap image = null;
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			
			try{
				image = BitmapFactory.decodeStream(getImageStream(Constants.endpoints_img+imagePath_runner));
			}catch(Exception e){
				e.printStackTrace();
			}
			if(image != null){
				CNAppDelegate.avatarDic.put(imagePath_runner, image);
		        return true;
		    }else{
		    	return false;
		    }
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				image_run_user.setImageBitmap(image);
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
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.relay_wait_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_back.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				button_back.setBackgroundResource(R.color.blue_dark);
				MatchNotRunTransmitRelayActivity.this.finish();
				break;
			}
			break;
		}
		return true;
	}

	private void startAnimation(){
//		try {
//			Thread.currentThread().sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		[kApp.voiceHandler voiceOfapp:@"match_wait_get_relay" :nil];//needwy
		relayAnim.setVisibility(View.VISIBLE);
		relay_main.setVisibility(View.GONE);
		animationDrawable.start();
		relayAnim.setVisibility(View.GONE);
		relay_main.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}
 }
