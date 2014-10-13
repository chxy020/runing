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

	private LonLatEncryption lonLatEncryption;
	double nextDis;
	boolean isIn = false;
	boolean isStart = false;
	int tryCount = 0;
	long lastKMTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v("zc","进正常页面");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_run);
		lonLatEncryption = new LonLatEncryption();
		initMatch();
		initView();
		
		nameV.setText(Variables.userinfo.getString("nickname"));
		teamNameV.setText(CNAppDelegate.matchDic.getString("groupname"));
	    if(Variables.avatar != null){
	    	avatarV.setImageBitmap(Variables.avatar);
	    }
		startTimer();		
		
	}
	void initMatch(){
		CNAppDelegate.match_pointList = new ArrayList<CNGPSPoint4Match>();
		CNAppDelegate.match_historydis = 0;
		CNAppDelegate.match_historySecond = 0;
		CNAppDelegate.match_startdis = 0;
	    CNAppDelegate.match_currentLapDis = 0;
	    CNAppDelegate.match_countPass = 0;
	    CNAppDelegate.match_time_last_in_track = CNAppDelegate.getNowTime();
	    CNAppDelegate.match_targetkm = ((int)CNAppDelegate.match_totalDisTeam)/CNAppDelegate.kkmInterval+1;//只有match_totalDisTeam没有在这赋值，交接棒会对它赋值
	    CNAppDelegate.match_score = 0;
	    CNAppDelegate.match_km_target_personal = 1;
	    CNAppDelegate.match_km_start_time = 0;
	}
	private void initView() {
		mapV = (ImageView) findViewById(R.id.match_map);
		teamV = (ImageView) findViewById(R.id.match_team);
		batonV = (ImageView) findViewById(R.id.match_run_baton);
		nameV = (TextView) findViewById(R.id.match_username);
		teamNameV = (TextView) findViewById(R.id.match_team_name);
		nextArea=(TextView)findViewById(R.id.match_next_area);
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
	void startTimer(){
		//正常进入数组必为空，先往数组里放一个点
	    CNGPSPoint4Match gpsPoint = getOnePoint();
	    CNAppDelegate.match_pointList.add(gpsPoint);
	    CNAppDelegate.match_time_last_in_track = gpsPoint.getTime();
	    //初始化比赛一些参数,正常进入第一个点一定会在赛道上
	    if(CNAppDelegate.isbaton == 1 && CNAppDelegate.match_totalDisTeam < 1){//是第一棒
	    }else{
	        //通过gpsPoint初始化
	        double point2Dis = CNAppDelegate.geosHandler.getRunningDistance(CNAppDelegate.geosHandler.match(gpsPoint.getLon(), gpsPoint.getLat(), 0));
	        CNAppDelegate.match_startdis = point2Dis;
	        CNAppDelegate.match_currentLapDis = point2Dis;
	        CNAppDelegate.match_countPass = 0;
	    }
	    CNAppDelegate.match_save2plist();
	    
	    TimerTask task_one_point = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						pushOnePoint();
						
					}
				});
			}
			
		};
		CNAppDelegate.timer_one_point.schedule(task_one_point, CNAppDelegate.kMatchInterval*1000, CNAppDelegate.kMatchInterval*1000);
		TimerTask task_secondplusplus = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						displayTime();
						
					}
				});
			}
		};
		CNAppDelegate.timer_secondplusplus.schedule(task_secondplusplus, 1000, 1000);
	    int delay = new Random().nextInt(10) + 1;
	    new Handler().postDelayed(new Runnable(){  
	        public void run() {  
	        	//execute the task
	        	startReportGPS();
	        }  
	     }, delay*1000);  
	}
	void startReportGPS(){
		TimerTask task_report = new TimerTask() {
			@Override
			public void run() {
				matchReport();
			}
		};
		CNAppDelegate.match_timer_report.schedule(task_report, CNAppDelegate.kMatchReportInterval*1000, CNAppDelegate.kMatchReportInterval*1000);
	}
	void pushOnePoint(){
		//得到新的一个点压入数组
	    CNGPSPoint4Match gpsPoint = getOnePoint();
	    boolean isInTheTracks = CNAppDelegate.geosHandler.isInTheTracks(gpsPoint.getLon(), gpsPoint.getLat());
	    int timeFromLastInTrack = (int)(gpsPoint.getTime() - CNAppDelegate.match_time_last_in_track);
	    if(isInTheTracks){//在赛道内
//	        if(self.view_distance.hidden == YES){
//	            self.view_distance.hidden = NO;
//	        }
//	        if(self.view_offtrack.hidden == NO){
//	            [kApp.voiceHandler voiceOfapp:@"match_come_back" :nil];
//	            self.view_offtrack.hidden = YES;
//	        }needwy
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
//	            self.big_div.distance = (kApp.match_totaldis+5)/1000.0;
//	            [self.big_div fitToSize];needwy
	            //计算配速
	            if(CNAppDelegate.match_totaldis > 1){
	                int speed_second = (int) (1000*(CNAppDelegate.match_historySecond/CNAppDelegate.match_totaldis));
//	                self.siv.time = speed_second;
//	                [self.siv fitToSize];needwy
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
//	        if(self.view_distance.hidden == NO){
//	            self.view_distance.hidden = YES;
//	        }
//	        if(self.view_offtrack.hidden == YES){
//	            self.view_offtrack.hidden = NO;
//	            [kApp.voiceHandler voiceOfapp:@"match_off_track" :nil];
//	        }//needwy
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
//	            [kApp.voiceHandler voiceOfapp:@"match_running_in_take_over" :nil];needwy
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
	    CNGPSPoint4Match gpspoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
	    int isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpspoint.getLon(),gpspoint.getLat());
	    if(isInTakeOverZone != -1){//在交接区
//	        NSMutableDictionary* voice_params = [[NSMutableDictionary alloc]init];
//	        [voice_params setObject:[NSString stringWithFormat:@"%i",kApp.match_targetkm] forKey:@"km"];
//	        [kApp.voiceHandler voiceOfapp:@"match_one_km_and_not_in_take_over" :voice_params];needwy
	    }else{//不在交接区
//	        NSMutableDictionary* voice_params = [[NSMutableDictionary alloc]init];
//	        [voice_params setObject:[NSString stringWithFormat:@"%f",self.nextDis] forKey:@"distanceFromTakeOver"];
//	        [voice_params setObject:[NSString stringWithFormat:@"%i",kApp.match_targetkm] forKey:@"km"];
//	        NSLog(@"self.nextDis is %f",self.nextDis);
//	        NSLog(@"kApp.match_targetkm is %i",kApp.match_targetkm);
//	        [kApp.voiceHandler voiceOfapp:@"match_one_km_and_not_in_take_over" :voice_params];needwy
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
						MatchGroupListActivity.class);
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
				CNGPSPoint4Match gpspoint = CNAppDelegate.match_pointList.get(CNAppDelegate.match_pointList.size()-1);
				int isInTakeOverZone;
				if(CNAppDelegate.istest){
					isInTakeOverZone = 0;
				}else{
					isInTakeOverZone = CNAppDelegate.geosHandler.isInTheTakeOverZones(gpspoint.getLon(),gpspoint.getLat());
				}
				if(isInTakeOverZone != -1){
					Intent intent = new Intent(MatchMainActivity.this,
							MatchGiveRelayActivity.class);
					startActivity(intent);
	            }else{
	            	Intent intent = new Intent(MatchMainActivity.this,
	            			MatchGiveRelayActivity.class);
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
		        if(isStart == false){
		            isStart = true;
		            onepoint.put("mstate", "0");
		        }else{
		        	onepoint.put("mstate", "1");
		        }
		    }else{
		    	onepoint.put("mstate", "1");
		    }
		    pointList.add(onepoint);
		    String pointJson =JSON.toJSONString(pointList);
		    String request_params = String.format("uid=%s&mid=%s&gid=%s&longitude=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid,pointJson);
		    Log.v("zc","上报点数据参数 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.matchReport, request_params);
		    Log.v("zc","上报点数据返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchMainActivity.this);
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
		    Log.v("zc","整公里上报返回 is "+responseJson);
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
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchMainActivity.this);
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
		    Log.v("zc","结束比赛返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.endMatch,MatchMainActivity.this);
				CNAppDelegate.ForceGoMatchPage("finish");
			} else {
			}
		}

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
