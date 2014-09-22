package net.yaopao.assist;

import java.util.Date;

import android.graphics.Bitmap;

/**
 * @author dell 供全局使用的一些变量
 */
public class Variables {

	public static String pid;

	public static String ua;

	public static int islogin;// 0-未登录，1-登录成功，2-正在登录,3-用户在其他设别上登录
	
//	public static int userStatus;// 0-未登录，1-登录成功，2-正在登录
	
	public static int uid = 0;

	public static int utype = 0;// 1-会员，0-未注册

	public static int network = 0;// 0-网络不可用，1-网络可用

	public static int gpsStatus = 0;// 0-gps不可用，1-gps可用，2-未开启
	
	//报名ID
	public static String bid = "";
	//组队ID
	public static String gid = "";
	//昵称
	public static String nikeName = "";
	//组名
	public static String groupName = "";
	//是否领队,"1"/"0"
	public static String isLeader = "0";
	//是否第一棒,"1"/"0"
	public static String isBaton = "0";
	//比赛ID
	public static int mid = 1;
	
	public static int gpsListener = 0;// 0-gps未监听，1-gps已监听
	public static boolean isActive = true;// 应用是否在前台
	// public static long lon = 0;// 当前经度

	// public static long lat = 0;// 当前纬度
	public static String headUrl = "";// 线上服务地址
	public static int sportStatus = 1; // 0-运动状态，1-暂停状态,2-未运动

	public static int switchTime = 0; // 0-开，1-关
	public static int switchVoice = 0; // 0-开，1-关

	public static String tarinfo;// 运动目标详情
	public static String remarks;// 个人备注
	public static String runtra;// 一条轨迹轨迹--点的集合格式：rentra=
	// [{slon:"1100",slat:"1110"},{slon:"2100",slat:"2110"}]
	// 一个点的 属性
	public static int addtime; // 毫秒数
	public static int slon; // 经度 WGS84坐标系，保留6位小数，乘以1000000取整
	public static int slat;// 纬度 WGS84坐标系，保留6位小数，乘以1000000取整
	public static int speed;// 米/秒
	public static int orient;// 方向 正北为0度，顺时针方向增加值359度
	public static int height; // gps高度，米，保留1位小数，乘以10取整
	public static int state; // 运动中：0，暂停中：1

	public static int runtar=0;// 自由：0，距离：1，时间：2
	public static int runtarDis = 5;// 目标距离,千米
	public static int runtarTime = 30;// 目标时间，分钟

	public static int runty=1;// 步行：1，跑步：2，自行车骑行：3
	public static int mind; // 心情
	public static int runway;// 跑道
	public static int aheart;// 平均心率
	public static int mheart;// 最高心率
	public static int weather;// 天气代码
	public static int temp;// 温度
	public static int utime;// 所用时间 秒数
	public static int heat;// 热量 卡路里
	public static int imageCount;
	public static int stamp;

	public static double distance;// 距离 m
	public static int pspeed;// 配速 s
	public static String hspeed;// 时速 km/h

	public static double stateIndex;//
	public static int points;//积分
	public static Bitmap avatar;//头像
	public static int toUserInfo;//0-主页进入个人信息，1-设置进入个人信息
	
	public static int sportty=0;//跑步类型0-日常，1-比赛
	public static int hassportpho=0;//跑步是否拍照0-无，1-有
	public static String sport_pho;//跑步拍照图片的名字
	
	// 获取rid
	public static String getRid() {
		return uid + new Date().getTime() + "";
	}

}
