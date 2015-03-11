package net.yaopao.assist;


import android.os.Environment;

/**
 * @author zc 常量
 */
public class Constants {

	public static final String  VERSION = "a_1.0.6"; //版本号
	public static final int SPLASH_DISPLAY_LENGHT_SHORT = 1000; // 延迟1秒
	public static final int SPLASH_DISPLAY_LENGHT = 3000; // 延迟四秒
	public static final int RET_CAMERA = 101;
	public static final int RET_GALLERY = 102;
	public static final int RET_CROP = 103;
	
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final String avatarPath = Environment.getExternalStorageDirectory().toString() + "/YaoPao/";
	public static final String avatarName = "avatar.jpg";
	public static final String tempPath = Environment.getExternalStorageDirectory().toString();
	public static final String tempImage = "temp.jpg";
	public static final String sportPho = Environment.getExternalStorageDirectory().toString() + "/YaoPao/sportPho/";
	public static final String sportPho_s = Environment.getExternalStorageDirectory().toString() + "/YaoPao/sportPho/small/";
	
	
	//mob sms 
 //测试key
//	public static final String SMS_KEY = "328a52117e94";
//	public static final String SMS_SECRET = "7833f56c649d87aeb53fe434a72bb86e";
	//正式key
	public static final String SMS_KEY = "3289fdd0ca3b";
	public static final String SMS_SECRET = "78b2977ac2193fe84a48b76595e1267d";
	
	public static final String SMS_CN_CODE = "86";
	public static final String SMS_DEF_CONUTRY = "中国";
	public static final String SMS_CN_ID = "42";
	

	public static  String endpoints = "";// 线上服务地址
	public static  String endpoints_img = "";// 图片下载地址
	
//	public static  String endpoints1 = "http://appservice.yaopao.net/chSports";// 当友盟参数获取不到的时候，使用这个参数
	public static  String endpoints2 = "http://image.yaopao.net";// 图片下载地址，当友盟参数获取不到的时候，使用这个参数
//	public static  String endpoints3 = "http://appservice.yaopao.net/";// 给web页面传的参数
	//测试地址
	public static  String endpoints1 = "http://182.92.97.144:8080/chSports";// 当友盟参数获取不到的时候，使用这个参数
//	public static  String endpoints2 ="http://yaopaotest.oss-cn-beijing.aliyuncs.com";
	public static  String endpoints3 = "http://182.92.97.144:8080/";// 给web页面传的参数
	

	public static final String reg = "/login/useregister.htm";// 注册

	public static final String login = "/login/loginbyphone.htm";// 登录

	public static final String vcode = "/login/getvcode.htm";// 获取验证码

	public static final String autoLogin = "/login/autologin.htm";// 自动登录

	public static final String saveUserInfo = "/login/entryregister.htm";// 保存修改的用户信息

	public static final String upImg = "/sys/upimage.htm";// 上传图片

	public static final String userInfo = "/login/viewregister.htm";// 获取用户信息

	public static final String forgetPwdCode = "/login/mopasscode.htm";// 忘记密码,获取验证码

	public static final String modifyPwd = "/login/modifypass.htm";// 修改密码

	public static final String upData = "/login/runstar.htm";// 上报数据
	
	
	public static final String matchReport = "/matchstart/gpsreport.htm";// 比赛上报gps
	public static final String matchOnekm = "/matchstart/kilometrereport.htm";// 整公里上报
	public static final String smallMapPage = "/matchstart/showmapgps.htm";// 比赛概况
	public static final String transmitRelay = "/matchstart/applysuccession.htm";// 交接扫描
	public static final String confirmTransmit = "/matchstart/confirmsuccession.htm";// 确认交棒
	public static final String cancelTransmit = "/matchstart/cancelsuccession.htm";//取消交棒
	public static final String listKM = "/matchstart/matchendkilometre.htm";// 按里程查看比赛数据
	public static final String listPersonal = "/matchstart/matchendshow.htm";// 按个人查看比赛数据
	public static final String checkServerTime = "/matchstart/returntime.htm";// 与服务器同步时间
	public static final String endMatch = "/matchstart/advancefinish.htm";// 提前完赛
	

	public static final String MAPKEY = "edb052cf66b6d7f82477334887205ff7";// 高德地图key
	
	public static final int  offset =10;// 数据库查询分页条数
	
	
	

}
