package net.yaopao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchGiveRelayActivity extends BaseActivity implements OnTouchListener {
	private ImageView backV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_relay);
		init();
	}

	private void init() {
		backV = (ImageView) findViewById(R.id.relay_wait_head);
		backV.setOnTouchListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
		case R.id.relay_wait_head:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent();
				intent = new Intent(MatchGiveRelayActivity.this,
						MatchMainActivity.class);
				startActivity(intent);
				finish();
				break;
			}
			break;
		}
		return true;
	}

}
