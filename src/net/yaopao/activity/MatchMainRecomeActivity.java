package net.yaopao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.activity.MatchMainActivity.TimerTask_one_point;
import net.yaopao.activity.MatchMainActivity.TimerTask_report;
import net.yaopao.activity.MatchMainActivity.TimerTask_secondplusplus;
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
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
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
	
	private ImageView dotV;
	private ImageView colon1V;
	private ImageView colon2V;
	private ImageView minV;
	private ImageView secV;
	
	private RelativeLayout view_distance;
	private RelativeLayout view_offtrack;
	
	private LonLatEncryption lonLatEncryption;
	double nextDis;
	boolean isIn = false;
	int tryCount = 0;
	long lastKMTime;
	
	TimerTask_one_point task_one_point = null;
	TimerTask_secondplusplus task_secondplusplus = null;
	TimerTask_report task_report = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("zc","进崩溃页面");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_recome_run);
		lonLatEncryption = new LonLatEncryption();
		initMatch();
		initView();	
		initStartPage();
		double this_dis = (CNAppDelegate.match_currentLapDis - CNAppDelegate.match_startdis)+CNAppDelegate.match_countPass*CNAppDelegate.geosHandler.claimedLength;
		CNAppDelegate.match_totaldis = this_dis+CNAppDelegate.match_historydis;
	    initMileage(CNAppDelegate.match_totaldis+5);
	    int speed_second = (int) (1000*(CNAppDelegate.match_historySecond/CNAppDelegate.match_totaldis));
	    initPspeed(speed_second);
	    nameV.setText(Variables.userinfo.getString("nickname"));
		teamNameV.setText(CNAppDelegate.matchDic.getString("groupname"));
	    if(Variables.avatar != null){
	    	avatarV.setImageBitmap(Variables.avatar);
	    }
	    startTimer();
	}
	void initMatch(){
		String historyDate = CNAppDelegate.match_readHistoryPlist();
		Log.v("zc","historyDate is "+historyDate);
		JSONObject dic = JSON.parseObject(historyDate);
		CNAppDelegate.match_pointList = new ArrayList<CNGPSPoint4Match>();
	    //把已经跑过的距离作为基准值
		CNAppDelegate.match_historydis = dic.getDouble("match_historydis");
		CNAppDelegate.match_totalDisTeam = dic.getDouble("match_totalDisTeam");
		CNAppDelegate.match_targetkm = dic.getIntValue("match_targetkm");
		CNAppDelegate.match_historySecond = dic.getIntValue("match_historySecond");
		CNAppDelegate.match_startdis = dic.getDouble("match_startdis");
		CNAppDelegate.match_currentLapDis = dic.getDouble("match_currentLapDis");
		CNAppDelegate.match_countPass = dic.getIntValue("match_countPass");
		CNAppDelegate.match_time_last_in_track = dic.getIntValue("match_time_last_in_track");
		CNAppDelegate.match_score = dic.getIntValue("match_score");
		CNAppDelegate.match_km_target_personal = dic.getIntValue("match_km_target_personal");
		CNAppDelegate.match_km_start_time = dic.getIntValue("match_km_start_time");
	}
	class TimerTask_one_point extends TimerTask{
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					pushOnePoint();
					
				}
			});
		}
	}
	class TimerTask_secondplusplus extends TimerTask{
		@Override
		public void run() {

			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					displayTime();
					
				}
			});
		}
	}
	class TimerTask_report extends TimerTask{
		@Override
		public void run() {
			matchReport();
		}
	}
	void startTimer(){
		//先存一下，这个和ios不一样
		CNAppDelegate.match_save2plist();
		
		task_one_point = new TimerTask_one_point();
		CNAppDelegate.timer_one_point = new Timer();
		CNAppDelegate.timer_one_point.schedule(task_one_point, CNAppDelegate.kMatchInterval*1000, CNAppDelegate.kMatchInterval*1000);
		task_secondplusplus = new TimerTask_secondplusplus();
		CNAppDelegate.timer_secondplusplus = new Timer();
		CNAppDelegate.timer_secondplusplus.schedule(task_secondplusplus, 1000, 1000);
		CNAppDelegate.match_timer_report = new Timer();
		task_report = new TimerTask_report();
		CNAppDelegate.match_timer_report.schedule(task_report, CNAppDelegate.kMatchReportInterval*1000, CNAppDelegate.kMatchReportInterval*1000); 
	}
	CNGPSPoint4Match getOnePoint() {
		if(CNAppDelegate.istest){
			CNAppDelegate.testIndex++;
		    if(CNAppDelegate.testIndex == CNAppDelegate.matchtestdatalength-1){
		    	CNAppDelegate.testIndex = 0;
		    }
		    return CNAppDelegate.test_getOnePoint();
		}else{
			CNGPSPoint4Match point = null;
			if (YaoPao01App.loc != null) {
				CNLonLat wgs84Point = new CNLonLat(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude());
				CNLonLat encryptionPoint = lonLatEncryption.encrypt(wgs84Point);
				long time = CNAppDelegate.getNowTimeDelta();
				point = new CNGPSPoint4Match(time,encryptionPoint.getLon(),encryptionPoint.getLat(),1);
			}
			return point;
		}
	}
	void pushOnePoint(){
		//得到新的一个点压入数组
	    CNGPSPoint4Match gpsPoint = getOnePoint();
	    boolean isInTheTracks = CNAppDelegate.geosHandler.isInTheTracks(gpsPoint.getLon(), gpsPoint.getLat());
	    int timeFromLastInTrack = (int)(gpsPoint.getTime() - CNAppDelegate.match_time_last_in_track);
	    if(isInTheTracks){//在赛道内
	    	//在赛道内
	        if(view_distance.getVisibility() == View.GONE){
	        	view_distance.setVisibility(View.VISIBLE);
	        }
	        if(view_offtrack.getVisibility() == View.VISIBLE){
//	            [kApp.voiceHandler voiceOfapp:@"match_come_back" :nil];needwy
	        	YaoPao01App.matchReturnTrack();
	            view_offtrack.setVisibility(View.GONE);
	        }
	    	CNAppDelegate.match_time_last_in_track = gpsPoint.getTime();
	    	GeometryLocation gl = CNAppDelegate.geosHandler.match(gpsPoint.getLon(), gpsPoint.getLat(), 0);
	        double point2Dis = CNAppDelegate.geosHandler.getRunningDistance(gl);
	        //将点的坐标改为匹配坐标
	        gpsPoint.setLon(gl.getCoordinate().x);
	        gpsPoint.setLat(gl.getCoordinate().y);
	        gpsPoint.setIsInTrack(1);
	        if(timeFromLastInTrack < CNAppDelegate.kBoundary1*60){//小于十分钟
	            if(point2Dis > CNAppDelegate.match_currentLapDis){
	                if(point2Dis - CNAppDelegate.match_currentLapDis>CNAppDelegate.geosHandler.claimedLength/2){//大的很多，证明是往回跑，并且经过起点了
	                	Log.v("zc","往回跑经过起点");
	                }else{//大的不多，正常向前跑
	                    Log.v("zc","正常向前跑");
	                    CNAppDelegate.match_currentLapDis = point2Dis;
	                }
	            }else{
	                if(CNAppDelegate.match_currentLapDis - point2Dis > CNAppDelegate.geosHandler.claimedLength/2){//小的太多，证明往前跑并且跨圈了
	                    Log.v("zc","往前跑并且跨圈了");
	                    CNAppDelegate.match_currentLapDis = point2Dis;
	                    CNAppDelegate.match_countPass++;
	                }else{//小的不是很多，证明往回跑了，什么都不干
	                    Log.v("zc","往回跑，没过起点");
	                }
	            }
	            double this_dis = (CNAppDelegate.match_currentLapDis - CNAppDelegate.match_startdis)+CNAppDelegate.match_countPass*CNAppDelegate.geosHandler.claimedLength;
	            CNAppDelegate.match_totaldis = this_dis+CNAppDelegate.match_historydis;
	            if(CNAppDelegate.match_totalDisTeam + CNAppDelegate.match_totaldis > CNAppDelegate.match_targetkm*CNAppDelegate.kkmInterval){
	                //整公里上报
	                oneKmReport(gpsPoint.getTime());
	                //播报语音
	                onekmvoice();
	                new Handler().postDelayed(new Runnable(){  
	        	        public void run() {  
	        	        	//execute the task
	        	        	CNAppDelegate.match_targetkm ++;
	        	        }  
	        	     }, 1000);
	            }
	            initMileage(CNAppDelegate.match_totaldis+5);
	            Log.v("zc","跑了多少米:"+CNAppDelegate.match_totaldis+5);
	            //计算配速
	            if(CNAppDelegate.match_totaldis > 1){
	                int speed_second = (int) (1000*(CNAppDelegate.match_historySecond/CNAppDelegate.match_totaldis));
	                initPspeed(speed_second);
	            }
	            //距离下一交接区
	            nextDis = CNAppDelegate.geosHandler.getDistanceToNextTakeOverZone(point2Dis);
	            nextArea.setText(String.format("距离下一交接区还有:%.2f公里", (nextDis+5)/1000.0));
	            
	            //算积分
	            if(CNAppDelegate.match_totaldis > 1000*CNAppDelegate.match_km_target_personal){
	                int minute = (CNAppDelegate.match_historySecond - CNAppDelegate.match_km_start_time)/60;
	                CNAppDelegate.match_score += score4speed(minute);
	                CNAppDelegate.match_km_target_personal++;
	                CNAppDelegate.match_km_start_time = CNAppDelegate.match_historySecond;
	            }
	        }else{//大于十分钟，但是回来了
	            Log.v("zc","超过10分钟回来");
	            CNAppDelegate.match_historydis += (CNAppDelegate.match_currentLapDis - CNAppDelegate.match_startdis)+CNAppDelegate.match_countPass*CNAppDelegate.geosHandler.claimedLength;
	            //通过该点初始化比赛信息，意味着重新开始一段新的跑步
	            CNAppDelegate.match_startdis = point2Dis;
	            CNAppDelegate.match_currentLapDis = point2Dis;
	            CNAppDelegate.match_countPass = 0;
	            CNGPSPoint4Match point = new CNGPSPoint4Match(0,0,0,0);
	            CNAppDelegate.match_pointList.add(point);
	        }
	    }else{//不在赛道内
	        Log.v("zc","偏离赛道");
	        if(view_distance.getVisibility() == View.VISIBLE){
	        	view_distance.setVisibility(View.GONE);
	        }
	        if(view_offtrack.getVisibility() == View.GONE){
//	            [kApp.voiceHandler voiceOfapp:@"match_off_track" :nil];
	        	YaoPao01App.matchDeviateTrack();
	            view_offtrack.setVisibility(View.VISIBLE);
	        }
	        gpsPoint.setIsInTrack(0);
	        if(timeFromLastInTrack >= CNAppDelegate.kBoundary2*60){//已经偏离了1个小时了
	            Log.v("zc","偏离赛道1小时");
	            //结束比赛
	            finishMatch();
	        }
	    }
	    CNAppDelegate.match_pointList.add(gpsPoint);//在不在赛道内都压入数组
	    
	    int isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpsPoint.getLon(),gpsPoint.getLat());
	    if(isInTakeOverZone != -1){//在交接区
	        if(isIn == false){
	            isIn = true;
	            //播放语音
	            YaoPao01App.matchRunningInTakeOver();
	        }
	    }else{//不在交接区
	        isIn = false;
	    }
	    
	    //将总距离存储到文件
	    if(CNAppDelegate.match_pointList.size()%5 == 0){
	        CNAppDelegate.match_save2plist();
	    }
	}
	void displayTime(){
		CNAppDelegate.match_historySecond++;
//	    self.tiv.time = kApp.match_historySecond;
//	    [self.tiv fitToSize];needwy
		initTime(CNAppDelegate.match_historySecond);
	}
	void matchReport(){
		new ReportPointTask().execute("");
	}
	void oneKmReport(long time){
		lastKMTime = time;
	    tryCount++;
	    new ReportKmTask().execute(""+time*1000);
	}
	void onekmvoice(){
		//播报语音
	    CNGPSPoint4Match gpspoint = getOnePoint();
	    int isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpspoint.getLon(),gpspoint.getLat());
	    if(isInTakeOverZone != -1){//在交接区
//	        NSMutableDictionary* voice_params = [[NSMutableDictionary alloc]init];
//	        [voice_params setObject:[NSString stringWithFormat:@"%i",kApp.match_targetkm] forKey:@"km"];
//	        [kApp.voiceHandler voiceOfapp:@"match_one_km_and_not_in_take_over" :voice_params];needwy
	    	//用哪个数据
	    	YaoPao01App.matchOneKmTeam(CNAppDelegate.match_totalDisTeam);
	    //	YaoPao01App.matchOneKmAndNotInTakeOver(CNAppDelegate.match_totalDisTeam,(nextDis+5)/1000.0);
	    }else{//不在交接区
//	        NSMutableDictionary* voice_params = [[NSMutableDictionary alloc]init];
//	        [voice_params setObject:[NSString stringWithFormat:@"%f",self.nextDis] forKey:@"distanceFromTakeOver"];
//	        [voice_params setObject:[NSString stringWithFormat:@"%i",kApp.match_targetkm] forKey:@"km"];
//	        NSLog(@"self.nextDis is %f",self.nextDis);
//	        NSLog(@"kApp.match_targetkm is %i",kApp.match_targetkm);
//	        [kApp.voiceHandler voiceOfapp:@"match_one_km_and_not_in_take_over" :voice_params];needwy
	    	//用哪个数据
	    	YaoPao01App.matchOneKmAndNotInTakeOver(CNAppDelegate.match_totalDisTeam,(nextDis+5)/1000.0);
	    	
	    }
	}
	int score4speed(int minute){
		if(minute < 5){
	        return 24;
	    }
	    if(minute < 6){
	        return 20;
	    }
	    if(minute < 7){
	        return 18;
	    }
	    if(minute < 8){
	        return 16;
	    }
	    if(minute < 9){
	        return 14;
	    }
	    if(minute < 10){
	        return 12;
	    }
	    if(minute < 11){
	        return 10;
	    }
	    if(minute < 12){
	        return 8;
	    }
	    if(minute < 13){
	        return 6;
	    }
	    return 0;
	}
	void finishMatch(){
		CNAppDelegate.saveMatchToRecord();
		CNAppDelegate.cancelMatchTimer();
	    CNAppDelegate.match_deleteHistoryPlist();
	    new FinishMatchTask().execute("");
	}
	private void initView() {
		mapV = (ImageView) findViewById(R.id.match_recome_map);
		teamV = (ImageView) findViewById(R.id.match_recome_team);
		avatarV = (ImageView) findViewById(R.id.match_recome_head);
		batonV = (ImageView) findViewById(R.id.match_recome_run_baton);
		nameV = (TextView) findViewById(R.id.match_recome_username);
		teamNameV = (TextView) findViewById(R.id.match_recome_team_name);
		nextArea = (TextView) findViewById(R.id.match_recome_next_area);
		
		view_distance=(RelativeLayout)findViewById(R.id.match_recome_mileage);
		view_offtrack=(RelativeLayout)findViewById(R.id.match_recome_off_track);
		
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
		
		dotV = (ImageView) findViewById(R.id.match_recome_recoding_dis_d);
		colon1V = (ImageView) findViewById(R.id.match_recome_recoding_time_d1);
		colon2V = (ImageView) findViewById(R.id.match_recome_recoding_time_d2);
		minV = (ImageView) findViewById(R.id.match_recome_recoding_speed_d1);
		secV = (ImageView) findViewById(R.id.match_recome_recoding_speed_d2);
		
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
				CNGPSPoint4Match gpspoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
				int isInTakeOverZone;
				if(CNAppDelegate.istest){
					isInTakeOverZone = 0;
				}else{
					isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpspoint.getLon(),gpspoint.getLat());
				}
				if(isInTakeOverZone != -1){
					Intent intent = new Intent(MatchMainRecomeActivity.this,
							MatchGiveRelayActivity.class);
					startActivity(intent);
	            }else{
	            	Intent intent = new Intent(MatchMainRecomeActivity.this,
	            			MatchNotInTakeOverActivity.class);
					startActivity(intent);
	            }

				break;
			}
			break;
		}
		return true;
	}


	private class ReportPointTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			List<Map<String,String>> pointList = new ArrayList<Map<String,String>>();
		    Map<String,String> onepoint = new HashMap<String,String>();
		    CNGPSPoint4Match gpsPoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
		    onepoint.put("uptime", ""+gpsPoint.getTime()*1000);
		    onepoint.put("distanceur", ""+CNAppDelegate.match_totaldis);
		    onepoint.put("inrunway", ""+gpsPoint.getIsInTrack());
		    onepoint.put("slat", ""+gpsPoint.getLat());
		    onepoint.put("slon", ""+gpsPoint.getLon());
		    if(CNAppDelegate.match_totalDisTeam < 1){//是第一棒
		        onepoint.put("mstate", "1");
		    }else{
		    	onepoint.put("mstate", "1");
		    }
		    pointList.add(onepoint);
		    String pointJson =JSON.toJSONString(pointList);
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&longitude=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,pointJson);
		    Log.v("zc","上报点数据参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.matchReport, request_params);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchMainRecomeActivity.this);
			} else {
				
			}
		}

	}
	private class ReportKmTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String timestr = params[0];
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&km=%d&uptime=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,CNAppDelegate.match_targetkm,timestr);
		    Log.v("zc","整公里上报参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.matchOnekm, request_params);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				tryCount = 0;
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchMainRecomeActivity.this);
			} else {
				if(tryCount < 4){
			        oneKmReport(lastKMTime);
			    }
			}
		}

	}
	private class FinishMatchTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			List<Map<String,String>> pointList = new ArrayList<Map<String,String>>();
		    Map<String,String> onepoint = new HashMap<String,String>();
		    CNGPSPoint4Match gpsPoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
		    onepoint.put("uptime", ""+gpsPoint.getTime()*1000);
		    onepoint.put("distanceur", ""+CNAppDelegate.match_totaldis);
		    onepoint.put("inrunway", ""+gpsPoint.getIsInTrack());
		    onepoint.put("slat", ""+gpsPoint.getLat());
		    onepoint.put("slon", ""+gpsPoint.getLon());
		    onepoint.put("mstate", "3");
		    pointList.add(onepoint);
		    String pointJson =JSON.toJSONString(pointList);
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&longitude=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,pointJson);
		    Log.v("zc","结束比赛参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.endMatch, request_params);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.endMatch,MatchMainRecomeActivity.this);
				CNAppDelegate.ForceGoMatchPage("finish");
			} else {
			}
		}

	}



//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			// DialogTool.quit(MainActivity.this);
//		}
//		return false;
//	}
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
//		int d1 = (int) Variables.distance / 10000;
//		int d2 = (int) (Variables.distance % 10000) / 1000;
//		int d3 = (int) (Variables.distance % 1000) / 100;
//		int d4 = (int) (Variables.distance % 100) / 10;
		int d1 = (int) (distance % 100000) / 10000;
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
		
		private void initStartPage(){
			initMileage(0);
			initPspeed(0);
			initTime(0);
			initinitSymbol();
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_HOME) {
				// Toast.makeText(SportRecordActivity.this, "", duration)
			}
			return false;
		}
}
