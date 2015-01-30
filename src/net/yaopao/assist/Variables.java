package net.yaopao.assist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import com.alibaba.fastjson.JSONObject;

/**
 * @author dell 供全局使用的一些变量
 */
@SuppressLint("SimpleDateFormat")
public class Variables {

	public static String pid;

	public static String ua;

	public static int islogin;// 0-未登录，1-登录成功，2-正在登录,3-用户在其他设别上登录
	
	public static int uid = 0;
	
//	//头像URL,完整地址
	public static String headUrl = "";
	
	public static JSONObject userinfo=null;
	
	//比赛信息
	public static JSONObject matchinfo =null;
	
	//public static int gpsLevel = 1;

	public static int network = 0;// 0-网络不可用，1-网络可用

	public static int gpsStatus = 0;// 0-gps不可用，1-gps可用，2-未开启

	public static int gpsListener = 0;// 0-gps未监听，1-gps已监听
	
	public static boolean isActive = true;// 应用是否在前台
	
	public static int sportStatus = 1; // 0-运动状态，1-暂停状态,2-未运动；处于比赛期间，此值一直为0

	public static int switchTime = 0; //是否显示倒计时  0-开，1-关
	
	public static int switchVoice = 0; // 0-开，1-关

	public static String tarinfo;// 运动目标详情
//	public static String remarks;// 个人备注
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

	//public static int runtar=0;// 自由：0，距离：1，时间：2
    //public static int runty=1; // 步行：1，跑步：2，自行车骑行：3
	
	public static int runTargetType=1;// 自由：1，距离：2，时间：3
	public static int runTargetDis = 5000;// 目标距离,米
	public static int runTargetTime = 1800000;// 目标时间，毫秒
	public static int runType=1;// 跑步：1，步行：2，自行车骑行：3， 默认跑步


//	public static int mind; // 心情
//	public static int runway;// 跑道
	public static int averageHeart;// 平均心率
	public static String clientImagePathsSmall;// 客户端缩略图路径
	public static String clientImagePaths;// 客户端图片路径
	public static String clientBinaryFilePath;// 客户端二进制文件路径
	public static String serverImagePathsSmall;// 服务端缩略图路径
	public static String serverImagePaths;// 服务端图路径
	public static String serverBinaryFilePath;// 服务端二进制文件路径
	public static int maxHeart;// 最高心率
	public static int weather;// 天气代码
	public static int temp;// 温度
	public static int heat;// 热量 卡路里
	public static int imageCount;
	public static int stamp;

	public static double distance;// 距离 m

	public static Bitmap avatar;//头像
	public static Bitmap avatar_default;//默认头像
	public static int toUserInfo;//0-主页进入个人信息，1-设置进入个人信息
	
	
	public static int isMatch=0;//比赛
	
	public static String jsonParam="";//预留字段
	
	public static String gpsString="";//
	
	
	public static String sport_pho;//跑步拍照图片的名字
	
	public static int timePlayed=0;//已经播报的时间,单位秒，
	public static double distancePlayed=0;//已经播报的距离,单位米，ssss
	
	public static int intervalTime=300;//语音播报间隔时间，单位秒，5分
	
	
	// 获取rid
	public static String getRid() {
		return uid +"_"+ new Date().getTime() + "";
	}
	public static String activityOnFront="";
	
	public static Map<String,String> countryData;
	
	public static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
	public static SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
	//验证码获取倒计时
	public static int verifyTime=60;
	//获得年月目录
		public static String  getPhoDir(){
			Date date = new Date();
			return sdf1.format(date)+"/"+sdf2.format(date)+"/";
		}
	
	
	public static boolean isTest=true;//是否是测试客户端

}
