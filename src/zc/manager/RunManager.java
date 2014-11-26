package zc.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

import android.util.Log;
import android.widget.Toast;

import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;


/**
 * @author zhangchi
 * 核心控制模块
 * 基本思想：
 * 1.一次跑步对应一个该类的实例（单例），在开始跑步的时候初始化该类，所以构造函数调用时间就是该次跑步的开始时间
 * 2.（最初的想法是manager自己维护一个gps模块取值，这样甚至可以打一个jar包，但是现在还是通过外界gps模块获取，
 *    因为第一，这样manager也要维护一个gps模块，全局有两个gps模块更加费电，
 * 	  第二，最理想的是一初始化manager立刻有gps数据，如果自己维护一个gps模块达不到该效果）
 *    构造函数直接传递取gps间隔，因为认为gps是最主要的输入，一但初始化该类，则调用它自己的timer，定时取点，并刷新自己的值。
 * 3.数据的更新时机分两种：一种是和gps相关的，更新的时机是取得一个新的gps；一种是和时间相关的，需要使用的时候立刻取得。
 * 	 前者设置为成员变量，后者通过方法得到。原因就是时间需要取得一个实时的值。
 *   例如外界想获取跑步的距离：则调用manager.distance,外界想获取运动的总时间,则调用manager.runLast()函数
 * 4.主要逻辑在于timertask的实现，该task取gps来更新成员变量值。
 * 5.另外我觉得语音的逻辑不应该包含在该manager中。原因是语音和ui现实密不可分，并不是我真的跑了一公里就播放一公里的语音，而是界面上显示了一公里才播放
 * 	 一公里的语音（当然这两个基本不会差多少），所以：更新ui是外部的事，那么播放语音也是外部的事。
 * 6.比赛和日常跑步本来想分开为两个manager，但是像输出记录这种想统一管理，最后还是觉得用一个，无非有些变量用不上
 */
public class RunManager {
	//一些自用的变量（供自己使用，不会直接被外部所用）
	private int runType;//跑步类型：1：日常跑步,2:比赛
	private int howToMove;//如何移动：1：跑步，2：步行，3：自行车
	private int runStatus;//跑步状态：1：运动，2：暂停 3：赛道内，4：偏离赛道
	private int way;//跑道1，2，3，4，5
	private int feeling;//心情1，2，3，4，5
	private int targetType;//设定的目标类型：1-自由；2-距离；3-时间
	private int targetValue;//设定的目标对应的值，毫秒或者米 
	
	private int gpsIntervalSec;//取gps时间间隔，初始化的时候赋值，以后自己更新自己
	private Timer timerUpdate;//取一个gps，更新数据的timer
	private TimerTask timerTask;
	
	private long startTimeStamp;//初始化RunManager的时间戳（毫秒），初始化意味着开始跑步
	private long startPauseTimeStamp;//开始暂停时间戳
	private int pauseSecond;//总的暂停时间（毫秒）
	private int targetKM;//下一个要到达的公里数，为了计算整公里的数据
	
	//下面这些变量是在timer中每次根据新的gps刷新值,外部可以访问
	public double distance;//个人距离(米)
	public int paceKm;//每公里配速（秒）
	public int paceMile;//每英里配速
	public int averSpeedKm;//平均速度（km/h）
	public int averSpeedMile;//平均速度（mile/h)
	public int score;//积分
	public float completePercent;//完成目标百分比
	
	public List<OneKMInfo> dataKm;//每公里的数据记录
	// 这个对象应该包括，每公里的位置、配速、步数、消耗的卡路里⋯⋯
	public List<Object> dataMile;//每公里的数据记录
	// 这个对象应该包括，每英里的位置、配速、步数、消耗的卡路里⋯⋯
	public List<Object> data5Min;//每5分钟的数据记录
	// 这个对象应该包括，位置、跑过的公里数、配速、步数、消耗的卡路里⋯⋯
    public List<ZCGPSPoint> GPSList;//gps序列,每个点有状态，运动/暂停，赛道内/赛道外
	
	public double match_team_dis;//队伍总成绩
	public boolean match_is_in_track;//是否在赛道内
	public double match_next_dis;//距离下一交界区
	
	/**
	 * @author zhangchi
	 * 日常跑步更更新数据task
	 */
	class TimerTaskEveryday extends TimerTask{
		@Override
		public void run() {
			updateDate();
		}
	}
	/**
	 * 默认构造函数，则创建一个平时跑步的实例manager
	 * @param second 取gps点的时间间隔（秒）
	 * @param howToMove 跑步、步行or骑车
	 * @param targetType 设定的目标类型
	 * @param targetValue 目标值毫秒或者米
	 */
	public RunManager(int second,int howToMove,int targetType,int targetValue){
		//判断gps模块是否有数据
		if(!Variables.isTest && YaoPao01App.loc == null){
			Log.v("zc","gps模块没有数据");
			return;
		}
		this.runType = 1;
		this.runStatus = 1;
		this.howToMove = howToMove;
		this.targetType = targetType;
		this.targetValue = targetValue;
		this.startTimeStamp = System.currentTimeMillis();
		//初始化变量
		this.GPSList = new ArrayList<ZCGPSPoint>();
		this.dataKm = new ArrayList<OneKMInfo>();
		this.distance = 0;
		this.paceKm = 0;
		this.paceMile = 0;
		this.averSpeedKm = 0;
		this.averSpeedMile = 0;
		this.score = 0;
		this.targetKM = 1;
		
		//根据second启动timer。
		timerTask = new TimerTaskEveryday();
		timerUpdate = new Timer();
		timerUpdate.schedule(timerTask, 0, second*1000);
	}
	/**
	 * 构造一个为比赛服务的manager实例
	 * @param match_team_dis 队伍已经跑的距离（米）
	 * @param second 取gps点的时间间隔（秒）
	 * @param match_map 赛道名称
	 */
	public RunManager(double match_team_dis,int second,String match_map){
		this.runType = 2;//比赛
		this.match_team_dis = match_team_dis;
		this.startTimeStamp = System.currentTimeMillis();
		//根据match_map初始化赛道
		//根据second启动timer。
	}
	
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public int getFeeling() {
		return feeling;
	}
	public void setFeeling(int feeling) {
		this.feeling = feeling;
	}
	/**
	 * 结束一次运动
	 */
	public void FinishOneRun(){
		if(timerUpdate != null){
			timerUpdate.cancel();
			timerUpdate = null;
			if(timerTask != null){
				timerTask.cancel();
				timerTask = null;
			}
		}
		//计算一下积分的零头
		if(this.distance < 1000){
	        this.score = 1;
	    }else{
	        int meter = (int)this.distance % 1000;
	        if(meter > 500){
	            this.score += 2;
	        }
	    }
	}
	/**
	 * 将这次的数据保存到数据库
	 */
	public void saveToDB(){
		
	}
	/**
	 * 返回总的运动时间
	 * @return
	 */
	public int during(){
		int second = (int)(System.currentTimeMillis()-this.startTimeStamp)-pauseSecond;
		return second >= 0 ? second : 0 ;
	}
	/**
	 * 改变运动状态
	 * @param status 状态
	 */
	public void changeRunStatus(int status){
		if(this.runType == 1){//如果是日常跑步，计算pauseSecond；
			if(this.runStatus == 2 && status == 1){//由暂停变运动
				this.pauseSecond += System.currentTimeMillis() - this.startPauseTimeStamp;
			}else if(this.runStatus == 1 && status == 2){//由运动变暂停
				this.startPauseTimeStamp = System.currentTimeMillis();
			}
		}
		this.runStatus = status;
	}
	
	int testnum = 0;
	private ZCGPSPoint getOnePoint(){
		if(Variables.isTest){
			testnum ++ ;
			return new ZCGPSPoint(116.390053,39.968191+0.001*testnum,this.runStatus,System.currentTimeMillis());
		}else{
			return new ZCGPSPoint(YaoPao01App.loc.getLongitude(),YaoPao01App.loc.getLatitude(),this.runStatus,System.currentTimeMillis());
		}
	}
	private void updateDate(){
		ZCGPSPoint gpsPoint = getOnePoint();
		if(this.GPSList.isEmpty()){
			this.GPSList.add(gpsPoint);
			return;
		}
		//不是第一个点
		ZCGPSPoint lastPoint = GPSList.get(GPSList.size()-1);
		double meter = getDistanceFrom2ponit(gpsPoint, lastPoint);
		if(meter < 5){//离得特别近
	        if(gpsPoint.getStatus() == lastPoint.getStatus()){//两点状态一样
	            //不保存这个点，算一下配速和进度条
	            if(gpsPoint.getStatus() == 1){//运动中，计算
//	                kApp.totalSecond = kApp.alreadySecond + (int)([CNUtil getNowTime] - kApp.startTime);
	                
	                //计算一下平均配速：
	            	if(this.distance < 1){//距离太短
	            		this.paceKm = 0;
	            	}else{
	            		this.paceKm = (int)(during()/distance);
	            	}
	            }
	            lastPoint.setTime(gpsPoint.getTime());//就不入数组了，而是更新时间
	        }else{//两点状态不一样，要计算配速、进度条和距离
	            if(gpsPoint.getStatus() == 1){//运动中，计算
//	                kApp.totalSecond = kApp.alreadySecond + (int)([CNUtil getNowTime] - kApp.startTime);
	                //计算一下平均配速：
	                this.paceKm = (int)(during()/distance);
	                this.distance += meter;
	            }
	            GPSList.add(gpsPoint);
	        }
	    }else{
	        if(gpsPoint.getStatus() == 1){
//	            kApp.totalSecond = kApp.alreadySecond + (int)([CNUtil getNowTime] - kApp.startTime);
	            //计算一下平均配速：
	        	this.distance += meter;
	        	this.paceKm = (int)(during()/distance);
	        }
	        GPSList.add(gpsPoint);
	    }
		//算一下完成目标百分比
		switch(this.targetType){
			case 1://自由
			{
				this.completePercent = 1;
				break;
			}
			case 2://距离
			{
				if(this.distance <= this.targetValue){
					this.completePercent = (float)(this.distance/this.targetValue);
				}else{
					this.completePercent = 1;
				}
				break;
			}
			case 3://时间
			{
				if(this.during() <= this.targetValue){
					this.completePercent = (float)(this.during()/this.targetValue);
				}else{
					this.completePercent = 1;
				}
				break;
			}
			default:break;
		}
		//如果是刚好到达整公里，则计算一下相关数据
		if(this.distance > this.targetKM*1000){//刚到达整公里
			int thisKmDuring = 0;
			if(this.dataKm.isEmpty()){
				thisKmDuring = during();
			}else{
				thisKmDuring = during() - dataKm.get(dataKm.size()-1).getDuring();
			}
			this.score += score4speed(thisKmDuring/60000);//计算积分
			dataKm.add(new OneKMInfo(targetKM,gpsPoint.getLon(),gpsPoint.getLat(),during(),thisKmDuring/1000));
			this.targetKM++;
		}
		Log.v("zc","lon:"+gpsPoint.getLon()+" lat:"+gpsPoint.getLat());
		Log.v("zc","distance:"+this.distance);
		Log.v("zc","paceKm:"+this.paceKm);
		Log.v("zc","score:"+this.score);
		Log.v("zc","completePercent:"+this.completePercent);
		
	}
	private double getDistanceFrom2ponit(ZCGPSPoint point1, ZCGPSPoint point2) {
		return AMapUtils.calculateLineDistance(new LatLng(point1.getLat(), point1.getLon()),
				new LatLng(point2.getLat(), point2.getLon()));

	}
	private int score4speed(int minute){
		if(minute < 5){
	        return 12;
	    }
	    if(minute < 6){
	        return 10;
	    }
	    if(minute < 7){
	        return 9;
	    }
	    if(minute < 8){
	        return 8;
	    }
	    if(minute < 9){
	        return 7;
	    }
	    if(minute < 10){
	        return 6;
	    }
	    if(minute < 11){
	        return 5;
	    }
	    if(minute < 12){
	        return 4;
	    }
	    if(minute < 13){
	        return 3;
	    }
	    return 0;
	}
}
