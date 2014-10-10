package net.yaopao.assist;

import java.util.List;
import java.util.Map;
import java.util.Timer;

import android.graphics.Bitmap;

//有关比赛的变量和方法byzc
public class CNAppDelegate {
	//一些常量
	public static final String kTrackName = "LongWan";//使用赛道
	public static final String kStartTime = "2014-10-18 15:00:00";//比赛开始时间
	public static final int kDuringMinute = 24*60;//比赛持续时间
	public static final int kMatchReportInterval = 30;//gps上报时间以及观众刷新时间
	public static final int kkmInterval = 1000;//每1000米上报整公里
	//一些变量
	//地图数据
	public static String match_track_line;//赛道
	public static String match_takeover_zone;//交接区
	public static String match_stringTrackZone;//赛道区域
	public static String match_stringStartZone;//出发区
	//队员信息
	public static int isMatch = 0;//是否参赛
	public static int isbaton = 0;//是否持棒
	public static String gid;
	public static String mid;
	//一些时间属性
	public static int deltaTime;
	public static long match_before5min_timestamp;
	public static long match_start_timestamp;
	public static long match_end_timestamp;
	//一次跑步
	public static List<CNGPSPoint4Match> match_pointList;//比赛用存点数组
	public static int match_isLogin;//比赛是否登录过
	public static Map<String,Bitmap> avatarDic;//记录下载过的各种头像，以后通过url访问
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
	    long nowTimeSecond = CNAppDelegate.getNowTime();
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
		
	}
	public static void match_save2plist(){//每隔几秒写plist
	
	}
	public static void ForceGoMatchPage(String target){//强制跳转到某个界面
		
	}
	public static void whatShouldIdo(){//启动手机后应该干嘛
		
	}
	public static void saveMatchToRecord(){//把比赛记到记录里
		
	}
	public static void check_start_match(){
		
	}
	public static boolean isInStartZone(){//判断是否在出发区
		return true;
	}
}
