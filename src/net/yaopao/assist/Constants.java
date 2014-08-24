package net.yaopao.assist;

import com.amap.api.maps2d.model.LatLng;

import android.graphics.Bitmap;
import android.os.Environment;
	
/**
 * @author zc
 * 常量
 */
public class Constants {
	
	public static final int SPLASH_DISPLAY_LENGHT = 4000; // 延迟四秒  
	public static final int RET_CAMERA = 101 ;
	public static final int RET_GALLERY = 102 ; 
	public static final int RET_CROP = 103;
	public static final String IMAGE_UNSPECIFIED = "image/*";
//	public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/temp.jpg";//拍照图片存放路径
	public static final String imagePath = Environment.getExternalStorageDirectory().toString() + "/YaoPao/head.jpg"; 
	public static final String sportPho = Environment.getExternalStorageDirectory().toString() + "/YaoPao/sportPho/"; 
	
	public static final String endpoints="http://182.92.97.144:8080/chSports";//线上服务地址   
	
	public static final String reg="/login/useregister.htm";//注册   
//	public static final String reg="/testlogin/testuseregister.htm";//注册   
	
	public static final String login="/login/loginbyphone.htm";//登录
	
	public static final String vcode="/login/getvcode.htm";//获取验证码    
	
	public static final String autoLogin="/login/autologin.htm";//自动登录   
	
	public static final String saveUserInfo="/login/entryregister.htm";//保存修改的用户信息   
	
	public static final String upImg="/sys/upimage.htm";//上传图片
	
	public static final String userInfo="/login/viewregister.htm";//获取用户信息
	
	public static final String forgetPwdCode="/login/mopasscode.htm";//忘记密码,获取验证码
	
	public static final String modifyPwd="/login/modifypass.htm";//修改密码   
	
	public static final String upData="/login/runstar.htm";//上报数据 

	public static final String MAPKEY="edb052cf66b6d7f82477334887205ff7";//高德地图key
	
	
	public static final LatLng BEIJING = new LatLng(39.90403, 116.407525);// 北京市经纬度
	public static final LatLng ZHONGGUANCUN = new LatLng(39.983456, 116.3154950);// 北京市中关村经纬度
	public static final LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度
	public static final LatLng FANGHENG = new LatLng(39.989614, 116.481763);// 方恒国际中心经纬度
	public static final LatLng CHENGDU = new LatLng(30.679879, 104.064855);// 成都市经纬度
	public static final LatLng XIAN = new LatLng(34.341568, 108.940174);// 西安市经纬度
	public static final LatLng ZHENGZHOU = new LatLng(34.7466, 113.625367);// 郑州市经纬度
}
