package net.yaopao.activity;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.CNGPSPoint4Match;
import net.yaopao.assist.CNLonLat;
import net.yaopao.assist.Constants;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
public class MatchFinishActivity extends BaseActivity implements OnTouchListener {
	
	
	private ImageView image_avatar,image_gps;
	private TextView label_username,label_teamname,buton_back;
	

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
		setContentView(R.layout.activity_match_finish);
		initView();
	    if(Variables.avatar != null){
	    	image_avatar.setImageBitmap(Variables.avatar);
	    }
	    label_username.setText(Variables.userinfo.getString("nickname"));
	    label_teamname.setText(CNAppDelegate.matchDic.getString("groupname"));
	    
	    double this_dis = (CNAppDelegate.match_currentLapDis - CNAppDelegate.match_startdis)+CNAppDelegate.match_countPass*CNAppDelegate.geosHandler.claimedLength;
	    double match_totaldis = this_dis+CNAppDelegate.match_historydis;
	    int speed_second;
	    if(match_totaldis < 1){
	        speed_second = 0;
	    }else{
	        speed_second = (int) (1000*(CNAppDelegate.match_historySecond/match_totaldis));
	    }
//	    CNDistanceImageView* big_div = [[CNDistanceImageView alloc]initWithFrame:CGRectMake(-2.5, 138, 325, 80)];
//	    big_div.distance = (match_totaldis+5)/1000.0;
//	    big_div.color = @"white";
//	    [big_div fitToSize];
//	    [self.view addSubview:big_div];
//	    
//	    CNTimeImageView* tiv = [[CNTimeImageView alloc]initWithFrame:CGRectMake(10, 245+IOS7OFFSIZE, 140, 32)];
//	    tiv.time = CNAppDelegate.match_historySecond;
//	    tiv.color = @"white";
//	    [tiv fitToSize];
//	    [self.view addSubview:tiv];
//	    
//	    CNSpeedImageView* siv = [[CNSpeedImageView alloc]initWithFrame:CGRectMake(190, 245+IOS7OFFSIZE, 100, 32)];
//	    siv.time = speed_second;
//	    siv.color = @"white";
//	    [siv fitToSize];
//	    [self.view addSubview:siv];needwy
	}
	private void initView() {
		image_avatar = (ImageView) findViewById(R.id.match_head);
		image_gps = (ImageView) findViewById(R.id.match_gps_status);
		
		label_username = (TextView) findViewById(R.id.match_username);
		label_teamname = (TextView) findViewById(R.id.match_team_name);
		buton_back = (TextView) findViewById(R.id.match_bcak);
		buton_back.setOnTouchListener(this);

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
		case R.id.match_bcak:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if(CNAppDelegate.hasFinishTeamMatch){//比赛已经结束
			        Intent intent = new Intent(MatchFinishActivity.this,
	        				MatchFinishTeamActivity.class);
	        		startActivity(intent);
			    }else{
			        Intent intent = new Intent(MatchFinishActivity.this,MatchGroupInfoActivity.class);
	        		startActivity(intent);
			    }
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
