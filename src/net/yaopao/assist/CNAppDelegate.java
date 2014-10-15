package net.yaopao.assist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.yaopao.activity.LoginActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.match.track.TrackData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

//有关比赛的变量和方法byzc
public class CNAppDelegate {
	//一些常量
	
	//测试比赛用
	public static final boolean istest = true;
	public static int matchtestdatalength;
	public static int testIndex= 0; 
	
	public static final String kTrackName = "LunYang";//使用赛道
	public static final String kStartTime = "2014-10-15 16:20:00";//比赛开始时间
	public static final int kDuringMinute = 24*60;//比赛持续时间


	public static final int kMatchReportInterval = 30;//gps上报时间以及观众刷新时间
	public static final int kkmInterval = 1000;//每1000米上报整公里
	public static final int kMatchInterval = 2;//两秒取一个点
	public static final int kBoundary1 = 10;//偏离赛道界限1，10分钟
	public static final int kBoundary2 = 60;//偏离赛道界限2，60分钟
	public static final int kScanTransmitinterval = 10;//交接棒扫描间隔
	public static final int kShortTime = 3000;//和服务器同步时间要小于
	//一些变量
	//地图数据
	public static String match_track_line;//赛道
	public static String match_takeover_zone;//交接区
	public static String match_stringTrackZone;//赛道区域
	public static String match_stringStartZone;//出发区
	//队员信息
	public static JSONObject matchDic;//参赛信息
	public static int isMatch = 0;//是否参赛
	public static int isbaton = 0;//是否持棒
	public static String uid;
	public static String gid;
	public static String mid;
	public static boolean hasMessage = false;
	public static int gstate = 0;//是否已经结束比赛
	public static boolean loginSucceedAndNext = false;//表示已经登录成功准备进行下一步判断
	//一些时间属性
	public static int deltaTime;
	public static long match_before5min_timestamp;
	public static long match_start_timestamp;
	public static long match_end_timestamp;
	//一次跑步
	public static List<CNGPSPoint4Match> match_pointList;//比赛用存点数组
	public static int match_isLogin = 0;//比赛是否登录过
	public static Map<String,Bitmap> avatarDic = new HashMap<String,Bitmap>();//记录下载过的各种头像，以后通过url访问
	//和单次跑有关
	public static double match_startdis;//开始比赛，起跑时距离起点的距离
	public static double match_currentLapDis;//已经跑过的距离（当前圈）
	public static int match_countPass;//本次跑步已经经过了起点的次数
	public static double match_historydis;//开启这次跑步时已经跑了的距离数，在显示距离的时候应该加上这个数
	public static int match_historySecond;//已经跑了的时间，在现实时间的时候加上这个数
	public static double match_totaldis;//本次一共跑得距离，如果上次崩溃了会加上上次的
	public static int match_targetkm;//已第几公里为目标
	public static double match_totalDisTeam;//整个跑队的公里数
	public static int match_score;//比赛积分
	public static int match_km_target_personal;//记录个人要跑向第几公里
	public static int match_km_start_time;//这一公里开始跑时，记录下时间
	public static long match_time_last_in_track;//最后一次在赛道内的点的时间
	public static boolean canStartButNotInStartZone;//可以开始比赛但是由于没有进入出发区而不能开始
	public static boolean hasFinishTeamMatch;//是否已经结束比赛
	public static boolean match_inMatch;//在比赛中
	public static boolean hasCheckTimeFromServer;//已经和服务器同步时间
	//一些timer
	public static Timer match_timer_report = new Timer();//上报timer
	public static Timer match_timer_check_countdown = new Timer();//如果比赛前进入软件，启动定时器判断何时开始比赛开始
	public static Timer timer_one_point = new Timer();//每个2秒去一个点的timer
	public static Timer timer_secondplusplus = new Timer();//比赛显示时间的timer
	
	public static TrackData geosHandler;
	
	//全局方法
	//获取系统时间——秒
	public static long getNowTime(){
		return System.currentTimeMillis()/1000;
	}
	//获取系统时间——毫秒
	public static long getNowTime1000(){
		return System.currentTimeMillis();
	}
	//获取与服务器矫正过后的系统时间——秒
	public static long getNowTimeDelta(){
		return System.currentTimeMillis()/1000+CNAppDelegate.deltaTime;
	}
	public static String getMatchStage(){
	    long nowTimeSecond = CNAppDelegate.getNowTimeDelta();
	    if(nowTimeSecond < CNAppDelegate.match_before5min_timestamp){
	        return "beforeMatch";
	    }else if(nowTimeSecond >= CNAppDelegate.match_before5min_timestamp&&nowTimeSecond<CNAppDelegate.match_start_timestamp){
	        return "closeToMatch";
	    }else if(nowTimeSecond >= CNAppDelegate.match_start_timestamp&&nowTimeSecond<=CNAppDelegate.match_end_timestamp){
	        return "isMatching";
	    }else{
	        return "afterMatch";
	    }
	}
	public static void finishThisRun(){//结束这次跑步
		CNAppDelegate.isbaton = 0;
	    CNAppDelegate.saveMatchToRecord();
	    CNAppDelegate.cancelMatchTimer();
	    CNAppDelegate.match_deleteHistoryPlist();
	}
	public static void match_save2plist(){//每隔几秒写plist
		Map<String,String> dic = new HashMap<String,String>();
		dic.put("match_historydis", ""+CNAppDelegate.match_historydis);
		dic.put("match_totalDisTeam", ""+CNAppDelegate.match_totalDisTeam);
		dic.put("match_targetkm", ""+CNAppDelegate.match_targetkm);
		dic.put("match_historySecond", ""+CNAppDelegate.match_historySecond);
		dic.put("match_startdis", ""+CNAppDelegate.match_startdis);
		dic.put("match_currentLapDis", ""+CNAppDelegate.match_currentLapDis);
		dic.put("match_countPass", ""+CNAppDelegate.match_countPass);
		if(CNAppDelegate.match_time_last_in_track < 1){
	    	CNAppDelegate.match_time_last_in_track = CNAppDelegate.getNowTimeDelta();
	    }
		dic.put("match_time_last_in_track", ""+CNAppDelegate.match_time_last_in_track);
		dic.put("match_score", ""+CNAppDelegate.match_score);
		dic.put("match_km_target_personal", ""+CNAppDelegate.match_km_target_personal);
		dic.put("match_km_start_time", ""+CNAppDelegate.match_km_start_time);
		String historyJson = JSON.toJSONString(dic);
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("match_historydis", historyJson);
		editor.commit();
	}
	public static String match_readHistoryPlist(){//读取preference
		return YaoPao01App.sharedPreferences.getString("match_historydis", "");
	}
	public static void match_deleteHistoryPlist(){//删除记录的preference
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("match_historydis", "");
		editor.commit();
	}
	public static void ForceGoMatchPage(String target){//强制跳转到某个界面
		YaoPao01App.instance.ForceGoMatchPage(target);
	}
	public static int whatShouldIdo(){//启动手机后应该干嘛
		//先判断时间
	    String matchstage = CNAppDelegate.getMatchStage();
	    Log.v("zc","matchstage is"+matchstage);
	    if(matchstage.equals("beforeMatch")){//赛前5分钟还要之前
	    	TimerTask task_check_start_match = new TimerTask() {
				@Override
				public void run() {
					check_start_match();
				}
			};
			CNAppDelegate.match_timer_check_countdown.schedule(task_check_start_match, 1000, 1000);
	    }else if(matchstage.equals("closeToMatch")){//赛前5分钟到比赛正式开始
	        if(CNAppDelegate.isbaton == 1){//第一棒
	            CNAppDelegate.ForceGoMatchPage("countdown");
	        }else{//不是第一棒
	            CNAppDelegate.ForceGoMatchPage("matchWatch");
	        }
	    }else if(matchstage.equals("isMatching")){//正式比赛时间
	        if(CNAppDelegate.isbaton == 1){//正在跑
	            //通过plist文件判断是否是崩溃重进
	            //needwy
	        	String fileexist = CNAppDelegate.match_readHistoryPlist();
	            if("".equals(fileexist)){//没有这个文件，则说明上次不是比赛中闪退的
	                if(CNAppDelegate.isbaton == 1 && CNAppDelegate.match_totalDisTeam < 1){//如果是第一棒有个特殊条件才能启动，就是必须在出发区
	                    if(CNAppDelegate.isInStartZone()){
	                        CNAppDelegate.ForceGoMatchPage("matchRun_normal");
	                    }else{
	                    	CNAppDelegate.canStartButNotInStartZone = true;
	                    	return 100;
	                    }
	                }else{
	                    CNAppDelegate.ForceGoMatchPage("matchRun_normal");
	                }
	            }else{
	            	CNAppDelegate.ForceGoMatchPage("matchRun_crash");
	            }
	        }else{//没再跑
	        	CNAppDelegate.ForceGoMatchPage("matchWatch");
	        }
	    }else{//赛后
	    	Variables.sportStatus = 1;
	    }
	    return 1;
	}
	public static void saveMatchToRecord(){//把比赛记到记录里
		YaoPao01App.db.saveOneSportMatch();
	}
	public static void check_start_match(){
		if(CNAppDelegate.getNowTimeDelta() >= CNAppDelegate.match_before5min_timestamp){//进入了赛前5分钟
	        CNAppDelegate.match_timer_check_countdown.cancel();
	        if(CNAppDelegate.isbaton == 1){//第一棒
	            CNAppDelegate.ForceGoMatchPage("countdown");
	        }else{//不是第一棒
	            CNAppDelegate.ForceGoMatchPage("matchWatch");
	        }
	    }
	}
	public static boolean isInStartZone(){//判断是否在出发区
		if(istest){
			return true;
		}else{
			if(YaoPao01App.loc == null){
				Log.v("zc","没定上位，不在出发区");
				return false;
			}else{
				CNLonLat wgs84Point = new CNLonLat(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude());
				LonLatEncryption lonLatEncryption = new LonLatEncryption();
				CNLonLat encryptionPoint = lonLatEncryption.encrypt(wgs84Point);
				return CNAppDelegate.geosHandler.isInTheStartZone(encryptionPoint.getLon(),encryptionPoint.getLat());
			}
		}
	}
	//比赛网络请求响应的一个过滤器，判断两点：第一是否已经结束比赛，第二是否账号在其他设备登录
	public static void matchRequestResponseFilter(String responseJson,String requestType,Context context){
		JSONObject result = JSON.parseObject(responseJson);
		int gstate = result.getIntValue("gstate");
	    if(gstate == 2){
	        if(CNAppDelegate.hasFinishTeamMatch == false){
	        	CNAppDelegate.cancelMatchTimer();
	            if(!requestType.equals(Constants.endMatch)){
	                //跳转
	                CNAppDelegate.ForceGoMatchPage("finishTeam");
	            }
	            CNAppDelegate.hasFinishTeamMatch = true;
	        }
	    }
	    JSONObject stateDic = result.getJSONObject("state");
	    if(stateDic == null)return;
	    int code = stateDic.getIntValue("code");
	    if(code == -7){//用户已经在其他手机登录
	    	Intent intent = new Intent(context,LoginActivity.class);
			Variables.islogin=3;
			DataTool.setUid(0);
			Variables.headUrl="";
			if (Variables.avatar!=null) {
				Variables.avatar=null;
			}
			Variables.userinfo = null;
			Variables.matchinfo = null;
			context.startActivity(intent);
	    }
	}
	public static void cancelMatchTimer(){
		if(CNAppDelegate.timer_one_point != null){
			CNAppDelegate.timer_one_point.cancel();
			CNAppDelegate.timer_one_point = null;
		}
		if(CNAppDelegate.timer_secondplusplus != null){
			CNAppDelegate.timer_secondplusplus.cancel();
			CNAppDelegate.timer_secondplusplus = null;
		}
		if(CNAppDelegate.match_timer_report != null){
			CNAppDelegate.match_timer_report.cancel();
			CNAppDelegate.match_timer_report = null;
		}
	}
	public static CNGPSPoint4Match test_getOnePoint(){
		List<CNGPSPoint4Match> testlist = new ArrayList<CNGPSPoint4Match>();
	    String[] pointlist = match_track_line.split(", ");
	    matchtestdatalength = pointlist.length;
	    int i=0;
	    for(i=0;i<pointlist.length;i++){
	    	String[] lonlats = pointlist[i].split(" ");
	        CNGPSPoint4Match point = new CNGPSPoint4Match(getNowTimeDelta(),Double.parseDouble(lonlats[0]),Double.parseDouble(lonlats[1]),0);
	        testlist.add(point);
	    }
	    CNGPSPoint4Match testpoint = testlist.get(testIndex);
	    return testpoint;
	}
}
