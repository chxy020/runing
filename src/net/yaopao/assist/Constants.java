package net.yaopao.assist;


import android.os.Environment;

/**
 * @author zc 常量
 */
public class Constants {

	public static final int SPLASH_DISPLAY_LENGHT = 4000; // 延迟四秒
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

	public static  String endpoints = "";// 线上服务地址
	public static  String endpoints_img = "";// 图片下载地址
	public static  String endpoints1 = "http://appservice.yaopao.net/chSports";// 当友盟参数获取不到的时候，使用这个参数
	public static  String endpoints2 = "http://image.yaopao.net";// 图片下载地址，当友盟参数获取不到的时候，使用这个参数
	public static  String endpoints3 = "http://appservice.yaopao.net/";// 给web页面传的参数
	//测试地址
//	public static  String endpoints1 = "http://182.92.97.144:8080/chSports";// 当友盟参数获取不到的时候，使用这个参数
//	public static  String endpoints3 = "http://182.92.97.144:8080/";// 给web页面传的参数
	

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

	public static final String MAPKEY = "edb052cf66b6d7f82477334887205ff7";// 高德地图key
	
	public static final int  offset =10;// 数据库查询分页条数

}
