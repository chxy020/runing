package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.activity.MatchNotInTakeOverActivity.TimerTask_check;
import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNLonLat;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;



/**
 */
public class MatchNotInActivity extends BaseActivity implements OnTouchListener {
	
	private ImageView imageview_avatar;
	
	private TextView label_uname;
	
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
		setContentView(R.layout.activity_match_not_in);
		lonLatEncryption = new LonLatEncryption();
		init();
	}
	private void init() {
		label_uname = (TextView) findViewById(R.id.out_relay_nickname);
		
		imageview_avatar = (ImageView) findViewById(R.id.out_relay_head);
		
		image_gps = (ImageView) findViewById(R.id.out_relay_gps_status);
		
		button_back = (TextView) findViewById(R.id.out_relay_tip_back);
		
		if (Variables.avatar!=null) {
			imageview_avatar.setImageBitmap(Variables.avatar);
		}
		
		label_uname.setText(Variables.userinfo.getString("nickname"));
		
		button_back.setOnTouchListener(this);
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
					        Intent intent = new Intent(MatchNotInActivity.this,
					        		MatchNotRunTransmitRelayActivity.class);
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
		case R.id.out_relay_tip_back:
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_back.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				button_back.setBackgroundResource(R.color.blue_dark);
				MatchNotInActivity.this.finish();
				break;
			}
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}
 }
