package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.activity.R.drawable;
import net.yaopao.assist.CNAppDelegate;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class MatchCountdownActivity extends BaseActivity  {
	private ImageView time1;
	private ImageView time2;
	private ImageView time3;
	private TextView  countdownTip;
	
	private int startSecond = 3;
	Timer timer_countdown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_countdown);
		time1 = (ImageView) findViewById(R.id.match_countdown_1);
		time2 = (ImageView) findViewById(R.id.match_countdown_2);
		time3 = (ImageView) findViewById(R.id.match_countdown_3);
		countdownTip = (TextView) findViewById(R.id.countdown_tip);
		
//	    self.niv = [[CNNumImageView alloc]initWithFrame:CGRectMake(-27.5, 180, 375, 120)];
//	    [self.view addSubview:self.niv];
//	    self.niv.num = self.startSecond;
//	    self.niv.color = @"red";
//	    [self.niv fitToSize];needwy

		
	}

	



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		TimerTask task_countdown = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						//不在出发区提示:
					    if(CNAppDelegate.isInStartZone() == false){
					    	countdownTip.setText("你尚未进入出发区，请于倒计时结束前进入出发区，否则将无法开始比赛!");
					    }else{
					        countdownTip.setText("已经进入出发区!");
					    }
					    startSecond--;
					    if(startSecond >= 0){
//					        self.niv.num = self.startSecond;
//					        self.niv.color = @"red";
//					        [self.niv fitToSize];needwy
					    }
					    if(startSecond == 0){
					        timer_countdown.cancel();
					        if(CNAppDelegate.isInStartZone()){//在出发区
					        	Intent intent = new Intent(MatchCountdownActivity.this,MatchMainActivity.class);
					    		startActivity(intent);
					        }else{//不在出发区
					        	CNAppDelegate.canStartButNotInStartZone = true;
//					            [CNAppDelegate popupWarningNotInStartZone];needwy
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
	
	
}
