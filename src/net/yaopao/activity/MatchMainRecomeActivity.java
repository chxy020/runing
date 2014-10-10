package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.match.track.TrackData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.umeng.analytics.MobclickAgent;
public class MatchMainRecomeActivity extends BaseActivity implements OnTouchListener {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_recome_run);
		initView();
		

		
	}

	private void initView() {
		mapV = (ImageView) findViewById(R.id.match_recome_map);
		teamV = (ImageView) findViewById(R.id.match_recome_team);
		batonV = (ImageView) findViewById(R.id.match_recome_run_baton);
		nameV = (TextView) findViewById(R.id.match_recome_username);
		teamNameV = (TextView) findViewById(R.id.match_recome_team_name);
		nextArea = (TextView) findViewById(R.id.match_recome_next_area);
		
		
		mapV.setOnTouchListener(this);
		teamV.setOnTouchListener(this);
		batonV.setOnTouchListener(this);

		d1V = (ImageView) findViewById(R.id.match_recome_recoding_dis1);
		d2V = (ImageView) findViewById(R.id.match_recome_recoding_dis2);
		d3V = (ImageView) findViewById(R.id.match_recome_recoding_dis3);
		d4V = (ImageView) findViewById(R.id.match_recome_recoding_dis4);

		t1V = (ImageView) findViewById(R.id.match_recome_recoding_time_h1);
		t2V = (ImageView) findViewById(R.id.match_recome_recoding_time_h2);
		t3V = (ImageView) findViewById(R.id.match_recome_recoding_time_m1);
		t4V = (ImageView) findViewById(R.id.match_recome_recoding_time_m2);
		t5V = (ImageView) findViewById(R.id.match_recome_recoding_time_s1);
		t6V = (ImageView) findViewById(R.id.match_recome_recoding_time_s2);

		s1V = (ImageView) findViewById(R.id.match_recome_recoding_speed1);
		s2V = (ImageView) findViewById(R.id.match_recome_recoding_speed2);
		s3V = (ImageView) findViewById(R.id.match_recome_recoding_speed3);
		s4V = (ImageView) findViewById(R.id.match_recome_recoding_speed4);
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_recome_map:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mapV.setBackgroundResource(R.drawable.button_map_h);
				break;
			case MotionEvent.ACTION_UP:
				mapV.setBackgroundResource(R.drawable.button_map);
				Intent intent = new Intent(MatchMainRecomeActivity.this,
						MatchMapActivity.class);
				startActivity(intent);

				break;
			}
			break;
		case R.id.match_recome_team:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchMainRecomeActivity.this,
						MatchGroupListActivity.class);
				startActivity(intent);

				break;
			}
			break;
		case R.id.match_recome_run_baton:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent intent = new Intent(MatchMainRecomeActivity.this,
						MatchGiveRelayActivity.class);
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
