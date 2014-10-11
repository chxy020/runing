package net.yaopao.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
	
	private ImageView button_back;
	private ImageView image_gps;
	
	private ImageView relayAnim;
	private AnimationDrawable animationDrawable;  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_wait_relay);
		init();
	}
	private void init() {
		image_myavatar = (ImageView) findViewById(R.id.relay_wait_head);
		label_name = (TextView) findViewById(R.id.relay_wait_nickname);
		view_back = (RelativeLayout) findViewById(R.id.relay_wait_back_layout);
		
		image_run_user = (ImageView) findViewById(R.id.relay_head);
		lable_run_user = (TextView) findViewById(R.id.relay_nickname);
		view_run_user = (RelativeLayout) findViewById(R.id.relay_head_layout);
		
		image_gps = (ImageView) findViewById(R.id.relay_wait_gps_status);
		button_back = (ImageView) findViewById(R.id.relay_wait_back);
		
        animationDrawable = (AnimationDrawable) relayAnim.getDrawable();  
		button_back.setOnTouchListener(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
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
		case R.id.relay_wait_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
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
		relayAnim.setVisibility(View.VISIBLE);
		relay_main.setVisibility(View.GONE);
		animationDrawable.start();
		relayAnim.setVisibility(View.GONE);
		relay_main.setVisibility(View.VISIBLE);
	}

 }
