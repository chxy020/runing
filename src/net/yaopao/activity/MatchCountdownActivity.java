package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class MatchCountdownActivity extends BaseActivity {
	private ImageView time1;
	private ImageView time2;
	private ImageView time3;
	private int time = 3;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_countdown);
		time1 = (ImageView) findViewById(R.id.match_countdown_1);
		time2 = (ImageView) findViewById(R.id.match_countdown_2);
		time3 = (ImageView) findViewById(R.id.match_countdown_3);
		timer.schedule(task, 0, 1000);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					time--;
					int i = time / 100;
					int j = (time % 100) / 10;
					int k = (time % 100) % 10;
					Log.v("wytime", "i=" + i + " j=" + j + " k=" + k);
					YaoPao01App.graphicTool.updateRedNum(new int[]{i,j,k},new ImageView[]{time1,time2,time3});

					if (time == 0) {
						Intent intent = new Intent(MatchCountdownActivity.this,
								MatchMainActivity.class);
						startActivity(intent);
						MatchCountdownActivity.this.finish();
						Toast.makeText(MatchCountdownActivity.this, "stop",	Toast.LENGTH_LONG).show();
					}

				}
			});
		}
	};



	@Override
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
