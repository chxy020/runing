package net.yaopao.assist;


import net.yaopao.activity.YaoPao01App;
import android.content.SharedPreferences;

public class DataTool {
	// 用户信息
//	public static JSONObject getUserInfo() {
//		JSONObject rt = JSON.parseObject(YaoPao01App.sharedPreferences
//				.getString("userInfo", null));
//		JSONObject userInfo = rt.getJSONObject("userinfo");
//		if (userInfo == null) {
//			return rt;
//		}
//		return userInfo;
//	}

//	public static void setUserInfo(String data) {
//		Log.v("wyuser", "要存储的=" + data);
//		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
//		editor.putString("userInfo", data);
//		editor.putInt("uid", Variables.uid);
//		editor.commit();
//
//	}

//	// 头像
//	public static String getHeadUrl() {
//		JSONObject rt = JSON.parseObject(YaoPao01App.sharedPreferences
//				.getString("head", null));
//		return Constants.endpoints + rt.getString("path120");
//	}

//
//	public static void setHead(String data) {
//		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
//		editor.putString("head", data);
//		editor.commit();
//
//	}
//
//	// 电话号码
//	public static String getPhone() {
//		return YaoPao01App.sharedPreferences.getString("phone", "");
//	}
//
//	public static void setPhone(String phone) {
//		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
//		editor.putString("phone", phone);
//		editor.commit();
//	}

	// uid
	public static int getUid() {
		return YaoPao01App.sharedPreferences.getInt("uid", 0);
	}
	public static void setUid(int uid) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putInt("uid", uid);
		editor.commit();
	}
	
	// 区分是否需要验证手机标志 0-未验证，1-已验证
	public static int getIsPhoneVerfied() {
		return YaoPao01App.sharedPreferences.getInt("isPhoneVerfied", 0);
	}
	public static void setIsPhoneVerfied(int isPhoneVerfied) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putInt("isPhoneVerfied", isPhoneVerfied);
		editor.commit();
	}
	
	
//	public static void initUserInfo(JSONObject rt,String rtjson) {
//		JSONObject userInfo= rt.getJSONObject("userinfo");
//		JSONObject match= rt.getJSONObject("match");
//		Variables.islogin = 1;
//		Variables.uid =userInfo.getInteger("uid");
//		Variables.utype = userInfo.getInteger("utype");
//		Variables.userName = userInfo.getString("uname")!=null?userInfo.getString("uname"):"";
//		Variables.nikeName = userInfo.getString("nickname")!=null?userInfo.getString("nickname"):"";
//		Variables.headPath=userInfo.getString("imgpath");
//		
//		if (Variables.headPath != null	&& !"".equals(Variables.headPath)) {
//			// 下载头像
//			Variables.headUrl = Constants.endpoints_img +Variables.headPath;
//			Log.v("wyuser", "头像======="+Variables.headUrl);
//			try {
//					Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
//			} catch (Exception e) {
//				Log.v("wyuser", "下载头像异常="+e.toString());
//				e.printStackTrace();
//			}
//		}
//		
//		//DataTool.setUserInfo(rtjson);
//		Log.v("wyuser", "rtjson = " + rtjson);
//		//是否有比赛
//		if ("1".equals(match.getString("ismatch"))) {
//			Variables.mid=match.getInteger("mid");
//		}
//		//是否报名
//		if ("1".equals(match.getString("issign"))) {
//			Variables.isSigned=true;
//			Variables.bid="1";
//		}
//		//是否组队
//		if ("1".equals(match.getString("isgroup"))) {
//			Variables.gid=match.getString("gid");
//		}
//		//是否队长
//		Variables.isLeader=match.getString("isleader");
//		//是否是头棒
//		if ("4".equals(match.getString("isgroup"))) {
//			Variables.isBaton="1";
//		}
//		
//		
//	}
//	public static void initMatchInfo(JSONObject rt,String rtjson) {
//		JSONObject userInfo= rt.getJSONObject("userinfo");
//		JSONObject match= rt.getJSONObject("match");
//		Variables.islogin = 1;
//		Variables.uid =userInfo.getInteger("uid");
//		Variables.utype = userInfo.getInteger("utype");
//		Variables.userName = userInfo.getString("uname")!=null?userInfo.getString("uname"):"";
//		Variables.nikeName = userInfo.getString("nickname")!=null?userInfo.getString("nickname"):"";
//		Variables.headPath=userInfo.getString("imgpath");
//		
//		if (Variables.headPath != null	&& !"".equals(Variables.headPath)) {
//			// 下载头像
//			Variables.headUrl = Constants.endpoints_img +Variables.headPath;
//			Log.v("wyuser", "头像======="+Variables.headUrl);
//			try {
//				Variables.avatar = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));
//			} catch (Exception e) {
//				Log.v("wyuser", "下载头像异常="+e.toString());
//				e.printStackTrace();
//			}
//		}
//		
//		//DataTool.setUserInfo(rtjson);
//		Log.v("wyuser", "rtjson = " + rtjson);
//		//是否有比赛
//		if ("1".equals(match.getString("ismatch"))) {
//			Variables.mid=match.getInteger("mid");
//		}
//		//是否报名
//		if ("1".equals(match.getString("issign"))) {
//			Variables.isSigned=true;
//			Variables.bid="1";
//		}
//		//是否组队
//		if ("1".equals(match.getString("isgroup"))) {
//			Variables.gid=match.getString("gid");
//		}
//		//是否队长
//		Variables.isLeader=match.getString("isleader");
//		//是否是头棒
//		if ("4".equals(match.getString("isgroup"))) {
//			Variables.isBaton="1";
//		}
//		
//		
//	}
	
	/**
	 * Get image from newwork
	 * 
	 * @param path
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
//	public static InputStream getImageStream(String path) throws Exception {
//		URL url = new URL(path);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		conn.setConnectTimeout(5 * 1000);
//		conn.setRequestMethod("GET");
//		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//			return conn.getInputStream();
//		}
//		return null;
//	}

}
