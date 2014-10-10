package net.yaopao.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
public class MatchMainActivity extends BaseActivity implements OnTouchListener {
	private ImageView mapV;
	private ImageView avatarV;
	private TextView nameV;
	private TextView teamNameV;
	private TextView nextArea;
	private ImageView teamV;
	private ImageView batonV;
	private ImageView d1V;
	private ImageView d2V;
	private ImageView d3V;
	private ImageView d4V;

	private ImageView t1V;
	private ImageView t2V;
	private ImageView t3V;
	private ImageView t4V;
	private ImageView t5V;
	private ImageView t6V;

	private ImageView s1V;
	private ImageView s2V;
	private ImageView s3V;
	private ImageView s4V;

//	private TrackData trackData;
//	private LonLatEncryption lonLatEncryption;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_run);
		initView();
		

//		lonLatEncryption = new LonLatEncryption();
//		trackData = new TrackData();
		//trackData.read("TrackData.properties");
		//测试代码
//		trackData.read("DaXingTest2.properties");
		
	}

	private void initView() {
		mapV = (ImageView) findViewById(R.id.match_map);
		teamV = (ImageView) findViewById(R.id.match_team);
		batonV = (ImageView) findViewById(R.id.match_run_baton);
		nameV = (TextView) findViewById(R.id.match_username);
		teamNameV = (TextView) findViewById(R.id.match_team_name);
		
		
		mapV.setOnTouchListener(this);
		teamV.setOnTouchListener(this);
		batonV.setOnTouchListener(this);

		d1V = (ImageView) findViewById(R.id.match_recoding_dis1);
		d2V = (ImageView) findViewById(R.id.match_recoding_dis2);
		d3V = (ImageView) findViewById(R.id.match_recoding_dis3);
		d4V = (ImageView) findViewById(R.id.match_recoding_dis4);

		t1V = (ImageView) findViewById(R.id.match_recoding_time_h1);
		t2V = (ImageView) findViewById(R.id.match_recoding_time_h2);
		t3V = (ImageView) findViewById(R.id.match_recoding_time_m1);
		t4V = (ImageView) findViewById(R.id.match_recoding_time_m2);
		t5V = (ImageView) findViewById(R.id.match_recoding_time_s1);
		t6V = (ImageView) findViewById(R.id.match_recoding_time_s2);

		s1V = (ImageView) findViewById(R.id.match_recoding_speed1);
		s2V = (ImageView) findViewById(R.id.match_recoding_speed2);
		s3V = (ImageView) findViewById(R.id.match_recoding_speed3);
		s4V = (ImageView) findViewById(R.id.match_recoding_speed4);
		
	}


//	private void updateUI() {
//		
//		int d1 = (int) distance / 10000;
//		int d2 = (int) (distance % 10000) / 1000;
//		int d3 = (int) (distance % 1000) / 100;
//		int d4 = (int) (distance % 100) / 10;
//		
//		int[] time = YaoPao01App.cal(totalTime);
//		int t1 = time[0] / 10;
//		int t2 = time[0] % 10;
//		int t3 = time[1] / 10;
//		int t4 = time[1] % 10;
//		int t5 = time[2] / 10;
//		int t6 = time[2] % 10;
//
//		int[] speed = YaoPao01App.cal((int) ((1000 / distance) * totalTime));
//		int s1 = speed[1] / 10;
//		int s2 = speed[1] % 10;
//		int s3 = speed[2] / 10;
//		int s4 = speed[2] % 10;
//
//		update(d1, d1V);
//		update(d2, d2V);
//		update(d3, d3V);
//		update(d4, d4V);
//
//		update(t1, t1V);
//		update(t2, t2V);
//		update(t3, t3V);
//		update(t4, t4V);
//		update(t5, t5V);
//		update(t6, t6V);
//
//		update(s1, s1V);
//		update(s2, s2V);
//		update(s3, s3V);
//		update(s4, s4V);
//
//	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_map:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mapV.setBackgroundResource(R.drawable.button_map_h);
				break;
			case MotionEvent.ACTION_UP:
				mapV.setBackgroundResource(R.drawable.button_map);
				Intent intent = new Intent(MatchMainActivity.this,
						MatchMapActivity.class);
				startActivity(intent);
				// MatchRunActivity.this.finish();

				break;
			}
			break;
		case R.id.match_team:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchMainActivity.this,
						MatchScoreListActivity.class);
				startActivity(intent);
				// MatchRunActivity.this.finish();

				break;
			}
			break;
		case R.id.match_run_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchMainActivity.this,
						MatchRelayActivity.class);
				startActivity(intent);

				break;
			}
			break;
		}
		return true;
	}





	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// DialogTool.quit(MainActivity.this);
		}
		return false;
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
