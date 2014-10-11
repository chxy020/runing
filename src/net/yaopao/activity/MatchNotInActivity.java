package net.yaopao.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;



/**
 */
public class MatchNotInActivity extends BaseActivity implements OnTouchListener {
	
	private ImageView imageview_avatar;
	
	private TextView label_uname;
	
	private ImageView button_back;
	
	private ImageView image_gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_not_in);
		init();
	}
	private void init() {
		label_uname = (TextView) findViewById(R.id.relay_wait_nickname);
		
		imageview_avatar = (ImageView) findViewById(R.id.relay_wait_head);
		
		image_gps = (ImageView) findViewById(R.id.relay_wait_gps_status);
		
		button_back = (ImageView) findViewById(R.id.out_delay_tip_back);
		
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
		case R.id.out_delay_tip_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				MatchNotInActivity.this.finish();
				break;
			}
			break;
		}
		return true;
	}


 }