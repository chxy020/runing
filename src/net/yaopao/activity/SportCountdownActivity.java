package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

public class SportCountdownActivity extends BaseActivity implements OnTouchListener {
	private RelativeLayout time;
	private ImageView time1;
	private ImageView time2;

	private int countTime = 10;
	private Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countdown);
		time = (RelativeLayout) findViewById(R.id.sport_time_layout);
		time1 = (ImageView) findViewById(R.id.sport_time1);
		time2 = (ImageView) findViewById(R.id.sport_time2);
		time1.setVisibility(View.VISIBLE);
		time.setOnTouchListener(this);
		timer.schedule(task, 0, 1000);
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.sport_time_layout:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent myIntent = new Intent();
				myIntent = new Intent(SportCountdownActivity.this,
						SportRunMainActivity.class);
				startActivity(myIntent);
				SportCountdownActivity.this.finish();
				break;
			}
			break;
		}
		return true;
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					if (countTime == 10) {
						update(1, time1);
						update(0, time2);
					} else {
						time1.setVisibility(View.GONE);
						update(countTime, time2);

					}

					countTime--;
					if (countTime < 0) {
						timer.cancel();
						update(0, time1);
						update(0, time2);
						Intent intent = new Intent(SportCountdownActivity.this,
								SportRunMainActivity.class);
						startActivity(intent);
						SportCountdownActivity.this.finish();
					}

				}
			});
		}
	};

	protected void update(int i, ImageView view) {
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.r_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.r_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.r_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.r_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.r_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.r_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.r_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.r_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.r_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.r_9);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return false;
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
