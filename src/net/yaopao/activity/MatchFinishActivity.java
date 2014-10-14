package net.yaopao.activity;


import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Variables;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
public class MatchFinishActivity extends BaseActivity implements OnTouchListener {
	
	
	private ImageView image_avatar,image_gps;
	private TextView label_username,label_teamname,buton_back;
	

	private ImageView d1V;
	private ImageView d2V;
	private ImageView d3V;
	private ImageView d4V;
	private ImageView dotV;
	

	private ImageView t1V;
	private ImageView t2V;
	private ImageView t3V;
	private ImageView t4V;
	private ImageView t5V;
	private ImageView t6V;
	private ImageView colon1V;
	private ImageView colon2V;

	private ImageView s1V;
	private ImageView s2V;
	private ImageView s3V;
	private ImageView s4V;
	private ImageView minV;
	private ImageView secV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_finish);
		initView();
		initinitSymbol();
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
	   
	    initMileage(match_totaldis+5);
	    initPspeed(speed_second);
	    initTime(CNAppDelegate.match_historySecond);
	}
	private void initView() {
		image_avatar = (ImageView) findViewById(R.id.match_head);
		image_gps = (ImageView) findViewById(R.id.match_gps_status);
		
		label_username = (TextView) findViewById(R.id.match_username);
		label_teamname = (TextView) findViewById(R.id.match_team_name);
		buton_back = (TextView) findViewById(R.id.match_bcak);
		buton_back.setOnTouchListener(this);

		d1V = (ImageView) findViewById(R.id.match_finish_dis1);
		d2V = (ImageView) findViewById(R.id.match_finish_dis2);
		d3V = (ImageView) findViewById(R.id.match_finish_dis3);
		d4V = (ImageView) findViewById(R.id.match_finish_dis4);
		dotV = (ImageView) findViewById(R.id.match_finish_dis_d);

		t1V = (ImageView) findViewById(R.id.match_finish_time_h1);
		t2V = (ImageView) findViewById(R.id.match_finish_time_h2);
		t3V = (ImageView) findViewById(R.id.match_finish_time_m1);
		t4V = (ImageView) findViewById(R.id.match_finish_time_m2);
		t5V = (ImageView) findViewById(R.id.match_finish_time_s1);
		t6V = (ImageView) findViewById(R.id.match_finish_time_s2);
		colon1V = (ImageView) findViewById(R.id.match_finish_time_d1);
		colon2V = (ImageView) findViewById(R.id.match_finish_time_d2);

		s1V = (ImageView) findViewById(R.id.match_finish_speed1);
		s2V = (ImageView) findViewById(R.id.match_finish_speed2);
		s3V = (ImageView) findViewById(R.id.match_finish_speed3);
		s4V = (ImageView) findViewById(R.id.match_finish_speed4);
		minV = (ImageView) findViewById(R.id.match_finish_speed_d1);
		secV = (ImageView) findViewById(R.id.match_finish_speed_d2);
		
	}
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
	
	private void initinitSymbol() {
		dotV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.w_dot));
		minV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.w_min));
		secV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.w_sec));
		 colon1V.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.w_colon));
		 colon2V.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.w_colon));
		 minV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
					.get(R.drawable.w_min));
		 secV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				 .get(R.drawable.w_sec));
		 
	}
	private void initMileage(double distance) {
		// distance = 549254;
		int d1 = (int) distance / 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1V.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateWhiteNum(new int[]{d1,d2,d3,d4},new ImageView[]{d1V,d2V,d3V,d4V});
		
		
		
	}
	// 初始化平均配速
	private void initPspeed(int pspeed) {

		int[] speed = YaoPao01App.cal(pspeed);

		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;

		YaoPao01App.graphicTool.updateWhiteNum(new int[] { s1, s2, s3, s4 },
				new ImageView[] { s1V, s2V, s3V, s4V });

	}
	
		//初始化滑动页面-总时间
		private void initTime(long total){
			int[] time = YaoPao01App.cal(total);
			int t1 = time[0] / 10;
			int t2 = time[0] % 10;
			int t3 = time[1] / 10;
			int t4 = time[1] % 10;
			int t5 = time[2] / 10;
			int t6 = time[2] % 10;	
			
			YaoPao01App.graphicTool.updateWhiteNum(new int[]{t1,t2,t3,t4,t5,t6},new ImageView[]{t1V,t2V,t3V,t4V,t5V,t6V,});
		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_HOME) {
				// Toast.makeText(SportRecordActivity.this, "", duration)
			}
			return false;
		}
}
