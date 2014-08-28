//package net.yaopao.assist;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import net.yaopao.activity.SplashActivity;
//import net.yaopao.activity.YaoPao01App;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//public class AutoLogin extends AsyncTask<String, Void, Boolean> {
//	private String loginJson;
//	
//	@Override
//	protected void onPreExecute() {
//	}
//
//	@Override
//	protected Boolean doInBackground(String... params) {
//		loginJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.autoLogin, "uid=" + YaoPao01App.sharedPreferences.getInt("uid", 0));
//		if (loginJson!=null&&!"".equals(loginJson)) {
//			return true;
//		}else {
//			return false;
//		}
//		
//	}
//
//	@Override
//	protected void onPostExecute(Boolean result) {
//		if (result) {
//			
//			JSONObject rt = JSON.parseObject(loginJson);
//			int rtCode = rt.getJSONObject("state").getInteger("code");
//			switch (rtCode) {
//			case 0:
//				Variables.islogin=1;
//				Variables.uid=rt.getJSONObject("userinfo").getInteger("uid");
//				Variables.utype=rt.getJSONObject("userinfo").getInteger("utype");
//				//下载头像
//				Variables.headUrl=Constants.endpoints+rt.getJSONObject("userinfo").getString("imgpath");
//				if (Variables.headUrl!=null&&!"".equals(Variables.headUrl)) {
//					SplashActivity.messageHandler.obtainMessage(0).sendToTarget();  
//				}
//				DataTool.setUserInfo(loginJson);
//				Log.v("wyuser","loginJson = "+ loginJson);
//				break;
//			default:
//				break;
//			}
//		}else {
//			Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试", Toast.LENGTH_LONG).show();
//		}
//	}
//	
//}