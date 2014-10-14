package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Variables;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class MatchCountdownActivity extends BaseActivity {
	private ImageView time1;
	private ImageView time2;
	private ImageView time3;
	private TextView countdownTip;

	private int startSecond = (int) (CNAppDelegate.match_start_timestamp - CNAppDelegate
			.getNowTimeDelta());;
	Timer timer_countdown = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_countdown);
		time1 = (ImageView) findViewById(R.id.match_countdown_1);
		time2 = (ImageView) findViewById(R.id.match_countdown_2);
		time3 = (ImageView) findViewById(R.id.match_countdown_3);
		countdownTip = (TextView) findViewById(R.id.countdown_tip);
		initTime();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
		MobclickAgent.onResume(this);
		TimerTask task_countdown = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						// 不在出发区提示:
						if (CNAppDelegate.isInStartZone() == false) {
							countdownTip
									.setText("你尚未进入出发区，请于倒计时结束前进入出发区，否则将无法开始比赛!");
						} else {
							countdownTip.setText("已经进入出发区!");
						}
						startSecond--;
						Log.v("zc", "startSecond is " + startSecond);
						if (startSecond >= 0) {
							initTime();
						}
						if (startSecond <= 0) {
							timer_countdown.cancel();
							timer_countdown = null;
							if (CNAppDelegate.isInStartZone()) {// 在出发区
								Intent intent = new Intent(
										MatchCountdownActivity.this,
										MatchMainActivity.class);
								startActivity(intent);
							} else {// 不在出发区
								CNAppDelegate.canStartButNotInStartZone = true;
								finish();
							}
						}

					}
				});
			}
		};
		timer_countdown.schedule(task_countdown, 1000, 1000);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initTime() {

		int t1 = startSecond / 100;
		int t2 = (startSecond % 100) / 10;
		int t3 = startSecond % 10;
		if (t1 > 0) {
			time1.setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(new int[] { t1 },
					new ImageView[] { time1 });
		}

		if (t2 > 0) {
			time2.setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(new int[] { t2 },
					new ImageView[] { time2 });
		}
		if (t1==0) {
			time1.setVisibility(View.GONE);
		}
		if (t2==0) {
			if (t1 == 0) {
				time2.setVisibility(View.GONE);
			}
		}
		YaoPao01App.graphicTool.updateRedNum(new int[] { t3 },
				new ImageView[] { time3 });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}

}
